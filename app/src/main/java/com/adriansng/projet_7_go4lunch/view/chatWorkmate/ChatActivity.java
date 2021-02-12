package com.adriansng.projet_7_go4lunch.view.chatWorkmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Message;
import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.view.BaseActivity;
import com.adriansng.projet_7_go4lunch.view.detailRestaurant.DetailRestaurantActivity;
import com.adriansng.projet_7_go4lunch.viewModel.Injection;
import com.adriansng.projet_7_go4lunch.viewModel.chatActivity.ViewModelChatActivity;
import com.adriansng.projet_7_go4lunch.viewModel.chatActivity.ViewModelFactoryChatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.adriansng.projet_7_go4lunch.R.string.not_chosen_restaurant_workmate;
import static com.adriansng.projet_7_go4lunch.R.string.toast_choose_image;

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

    // FOR DESIGN
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_recycler_view)
    RecyclerView recyclerView;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_text_input)
    TextInputEditText editTextMessage;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_image_chosen_preview)
    ImageView imageViewPreview;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_add_file_btn)
    ImageView addFileBtn;

    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_toolbar)
    Toolbar toolbar;

    // FOR DATA
    private ChatAdapter chatAdapter;

    private List<Workmate> listWorkmates;
    private List<String> listFavoriteWorkmateSender;

    @Nullable
    private Workmate workmateReceiver;
    private String restaurantWorkmateReceiver;
    private Workmate workmateSender;
    private Uri uriImageSelected;

    private ViewModelChatActivity viewModelChatActivity;

    // STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.chat_activty);
        ButterKnife.bind(this);
        this.getWorkmateReceiver();
        this.getWorkmateSender();
        this.configViewModel();
        this.configureRecyclerView();
        this.configureToolbar();
        this.onClickAddFile();
    }

    // ------------------
    // WORKMATE
    // ------------------

    private void getWorkmateReceiver() {
        Intent intent = getIntent();
        workmateReceiver = intent.getParcelableExtra("Workmate");
    }

    private void getWorkmateSender() {
        workmateSender = new Workmate(getUidUser(), getNameUser(), getAvatarUser().toString());
    }

    // ------------------
    // VIEW MODEL
    // ------------------

    private void configViewModel() {
        ViewModelFactoryChatActivity viewModelFactoryChatActivity = Injection.viewModelFactoryChatActivity();
        viewModelChatActivity = new ViewModelProvider(this, viewModelFactoryChatActivity).get(ViewModelChatActivity.class);
        viewModelChatActivity.initWorkmate(workmateSender.getUid());
        configObsWorkmates();
    }

    // ------------------
    // OBSERVER WORKMATES
    // ------------------

    // --- FOR LIST PLACES ---

    private void configObsWorkmates() {
        viewModelChatActivity.getWorkmates().observe(this, this::configObsListFav);
    }

    private void configObsListFav(List<Workmate> workmates) {
        viewModelChatActivity.getFavoriteRestaurantList().observe(this, list -> {
            configObsIdValidatePlace();
            listFavoriteWorkmateSender = list;
            listWorkmates = workmates;
        });
    }

    // --- FOR LAUNCH DETAIL PLACE ---

    private void configObsIdValidatePlace() {
        viewModelChatActivity.getIdValidatedPlace(Objects.requireNonNull(workmateReceiver).getUid()).observe(this, uidPlaces -> {
            if (!uidPlaces.matches("")) {
                restaurantWorkmateReceiver = uidPlaces;
            } else {
                restaurantWorkmateReceiver = null;
            }
        });
    }

    private void configObsGetValidatedPlace() {
        viewModelChatActivity.getValidatedPlace().observe(this, this::clickForDetail);
    }


    // --------------------
    // ACTIONS
    // --------------------

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.chat_send_btn)
    public void onClickSendMessage() {
        if (!TextUtils.isEmpty(Objects.requireNonNull(editTextMessage).getText()) && workmateReceiver != null) {
            // Check if the ImageView is set
            if (Objects.requireNonNull(this.imageViewPreview).getDrawable() == null) {
                // SEND A TEXT MESSAGE
                viewModelChatActivity.createMessage(Objects.requireNonNull(editTextMessage.getText()).toString(), workmateSender, workmateReceiver.getUid()).addOnFailureListener(this.onFailureListener());
                this.editTextMessage.setText("");
            } else {
                // SEND A IMAGE + TEXT IMAGE
                this.uploadPhotoInFirebaseAndSendMessage(Objects.requireNonNull(editTextMessage.getText()).toString());
                this.editTextMessage.setText("");
                this.imageViewPreview.setImageDrawable(null);
            }
        }
    }


    public void onClickAddFile() {
        Objects.requireNonNull(addFileBtn).setOnClickListener(v -> chooseImageFromPhone());
    }

    // ------------------
    // REST REQUESTS
    // ------------------

    private void uploadPhotoInFirebaseAndSendMessage(final String message) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        // A - UPLOAD TO GCS
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        mImageRef.putFile(this.uriImageSelected)
                .addOnSuccessListener(this, taskSnapshot -> {
                    String pathImageSavedInFirebase = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString();
                    // B - SAVE MESSAGE IN FIRE STORE
                    viewModelChatActivity.createMessageWithImage(pathImageSavedInFirebase, message, workmateSender, Objects.requireNonNull(workmateReceiver).getUid())
                            .addOnFailureListener(onFailureListener());
                })
                .addOnFailureListener(this.onFailureListener());
    }

    // ------------------
    // FILE MANAGEMENT
    // ------------------

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RC_CHOOSE_PHOTO) {
            chooseImageFromPhone();
        }
    });

    private void chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_perms), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(i);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(Objects.requireNonNull(this.imageViewPreview));
            } else {
                Toast.makeText(this, toast_choose_image, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }


    // ------------------
    // UI
    // ------------------

    // --- TOOLBAR ---

    protected void configureToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(toolbar).setTitle(Objects.requireNonNull(workmateReceiver).getNameWorkmate());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_restaurant, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_detail_lunch) {
            if (restaurantWorkmateReceiver != null) {
                viewModelChatActivity.initValidatedPlace(restaurantWorkmateReceiver, listWorkmates, listFavoriteWorkmateSender);
                configObsGetValidatedPlace();
            } else {
                Toast.makeText(this, not_chosen_restaurant_workmate, Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void clickForDetail(Restaurant restaurant) {
        Intent intent = new Intent(this, DetailRestaurantActivity.class);
        intent.putExtra("Restaurant", restaurant);
        this.startActivity(intent);
    }

    // --- RECYCLER VIEW ---

    private void configureRecyclerView() {
        this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(viewModelChatActivity
                .getAllMessages(getUidUser(), Objects.requireNonNull(workmateReceiver).getUid())), Glide.with(this), this, Objects.requireNonNull(this.getCurrentUser()).getUid());
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Objects.requireNonNull(recyclerView).smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        Objects.requireNonNull(recyclerView).setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.chatAdapter);
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // ------------------
    // CALLBACK
    // ------------------

    @Override
    public void onDataChanged() {
        Objects.requireNonNull(textViewRecyclerViewEmpty).setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
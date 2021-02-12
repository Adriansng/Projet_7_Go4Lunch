package com.adriansng.projet_7_go4lunch.view.detailRestaurant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.view.BaseActivity;
import com.adriansng.projet_7_go4lunch.viewModel.Injection;
import com.adriansng.projet_7_go4lunch.viewModel.detailActivity.ViewModelDetailActivity;
import com.adriansng.projet_7_go4lunch.viewModel.detailActivity.ViewModelFactoryDetailActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailRestaurantActivity extends BaseActivity {

    // --- FOR DATA  ---

    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_avatar_restaurant)
    ImageView detailAvatar;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_name_restaurant)
    TextView detailName;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_address_restaurant)
    TextView detailAddress;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_star_like)
    ImageView detailStarLike;

    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_like_btn)
    ImageView detailLikeBtn;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_phone_btn)
    ImageView detailPhoneBtn;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_phone_txt)
    TextView detailPhoneTxt;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_web_btn)
    ImageView detailWebBtn;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_web_txt)
    TextView detailWebTxt;

    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_validate_restaurant)
    ImageView detailButtonValidate;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_toolbar_layout)
    CollapsingToolbarLayout toolBarLayout;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_recycler_view_workmates)
    RecyclerView mRecyclerView;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.detail_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;

    private ViewModelDetailActivity viewModelDetailActivity;
    private Restaurant restaurant;
    private List<String> listFavorite;
    private List<Workmate> workmates;

    private String validatedRestaurant;
    private String placeId;
    private String nameRestaurant;
    private String addressRestaurant;
    private String uidUser;

    // ------------------
    // TO CREATE
    // ------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_restaurant_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(toolBarLayout).setTitleEnabled(false);

        configRestaurant();

        configViewModel();

        configObsFavorite();
        configObsValidate();
        configObsWorkmates();
    }

    // ------------------
    // VIEW MODEL
    // ------------------

    private void configViewModel() {
        ViewModelFactoryDetailActivity viewModelFactoryDetailActivity = Injection.viewModelFactoryDetailActivity();
        viewModelDetailActivity = new ViewModelProvider(this, viewModelFactoryDetailActivity).get(ViewModelDetailActivity.class);
    }

    // ------------------
    // RESTAURANT
    // ------------------

    private void configRestaurant() {
        Intent intent = getIntent();
        restaurant = intent.getParcelableExtra("Restaurant");
        placeId = restaurant.getPlaceId();
        nameRestaurant = restaurant.getName();
        addressRestaurant = restaurant.getAddress();
        uidUser = getUidUser();

        configUIRestaurant();

        configCall();
        configWebsite();
    }

    // ------------------
    // VALIDATED RESTAURANT
    // ------------------

    private void configObsValidate() {
        viewModelDetailActivity.getValidatedRestaurant(getUidUser()).observe(this, restaurant -> {
            validatedRestaurant = restaurant;
            configValidate(validatedRestaurant);
            clickValidate(validatedRestaurant);
        });
    }
    // ------------------
    // FAVORITE RESTAURANTS
    // ------------------

    private void configObsFavorite() {
        viewModelDetailActivity.getFavoriteRestaurantList(getUidUser()).observe(this, list -> {
            listFavorite = list;
            configStarLike(listFavorite);
            clickStarLike(listFavorite);
        });
    }

    // ------------------
    // LIST WORKMATES
    // ------------------

    private void configObsWorkmates() {
        viewModelDetailActivity.getWorkmates(placeId).observe(this, workmateList -> {
            workmates = workmateList;
            configRecyclerView(workmates);
        });
    }

    // ------------------
    // UI
    // ------------------

    // --- INFO RESTAURANT ---

    private void configUIRestaurant() {
        if (!restaurant.getPhoto().isEmpty()) {
            Glide.with(Objects.requireNonNull(detailAvatar).getContext())
                    .load(restaurant.getPhoto())
                    .centerCrop()
                    .into(detailAvatar);
        }
        Objects.requireNonNull(detailName).setText(nameRestaurant);
        Objects.requireNonNull(detailAddress).setText(restaurant.getAddress());
    }

    // --- BUTTON CALL ---

    private void configCall() {
        if (restaurant.getPhoneNumber() != null) {
            Objects.requireNonNull(detailPhoneBtn).setVisibility(View.VISIBLE);
            Objects.requireNonNull(detailPhoneTxt).setVisibility(View.VISIBLE);
            detailPhoneBtn.setOnClickListener(v -> {
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                intentCall.setData(Uri.parse("tel:" + restaurant.getPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    startActivity(intentCall);
                }
            });
        } else {
            Objects.requireNonNull(detailPhoneBtn).setVisibility(View.INVISIBLE);
            Objects.requireNonNull(detailPhoneTxt).setVisibility(View.INVISIBLE);
        }
    }

    // --- BUTTON WEBSITE ---

    @SuppressLint("QueryPermissionsNeeded")
    private void configWebsite() {
        if (restaurant.getWebsite() != null) {
            Objects.requireNonNull(detailWebBtn).setVisibility(View.VISIBLE);
            Objects.requireNonNull(detailWebTxt).setVisibility(View.VISIBLE);
            detailWebBtn.setOnClickListener(v -> {
                Intent intentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.getWebsite()));
                if (intentWeb.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentWeb);
                }
            });
        } else {
            Objects.requireNonNull(detailWebBtn).setVisibility(View.INVISIBLE);
            Objects.requireNonNull(detailWebTxt).setVisibility(View.INVISIBLE);
        }
    }

    // --- BUTTON LIKE ---

    private void configStarLike(List<String> restaurantsFavorite) {
        if (viewModelDetailActivity.checkRestaurantIsFavorite(placeId, restaurantsFavorite)) {
            Objects.requireNonNull(detailStarLike).setVisibility(View.VISIBLE);
        } else {
            Objects.requireNonNull(detailStarLike).setVisibility(View.INVISIBLE);
        }
    }

    private void clickStarLike(List<String> restaurantsFavorite) {
        Objects.requireNonNull(detailLikeBtn).setOnClickListener(v -> {
            if (viewModelDetailActivity.checkRestaurantIsFavorite(placeId, restaurantsFavorite)) {
                viewModelDetailActivity.deleteFavoriteRestaurantToWorkmate(uidUser, placeId);
                Objects.requireNonNull(detailStarLike).setVisibility(View.INVISIBLE);
            } else {
                viewModelDetailActivity.addFavoriteRestaurant(uidUser, placeId);
                Objects.requireNonNull(detailStarLike).setVisibility(View.VISIBLE);
            }
            configObsFavorite();
        });
    }

    // --- BUTTON VALIDATE ---

    private void configValidate(String validated) {
        if (viewModelDetailActivity.checkRestaurantIsValidate(validated, placeId)) {
            Objects.requireNonNull(detailButtonValidate).setImageResource(R.drawable.ic_baseline_cancel_24);
        } else {
            Objects.requireNonNull(detailButtonValidate).setImageResource(R.drawable.fui_ic_check_circle_black_128dp);
        }
    }

    private void clickValidate(String validated) {
        Objects.requireNonNull(detailButtonValidate).setOnClickListener(v -> {
            if (viewModelDetailActivity.checkRestaurantIsValidate(validated, placeId)) {
                viewModelDetailActivity.updateWorkmateChooseRestaurant(uidUser, null, null, null);
                detailButtonValidate.setImageResource(R.drawable.fui_ic_check_circle_black_128dp);
            } else {
                viewModelDetailActivity.updateWorkmateChooseRestaurant(uidUser, placeId, nameRestaurant, addressRestaurant);
                detailButtonValidate.setImageResource(R.drawable.ic_baseline_cancel_24);
            }
            configObsValidate();
        });
    }

    // --- RECYCLER VIEW ---

    private void configRecyclerView(List<Workmate> workmateList) {
        Objects.requireNonNull(mRecyclerView).addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        DetailRestaurantAdapter detailAdapter = new DetailRestaurantAdapter(workmateList);
        Objects.requireNonNull(textViewRecyclerViewEmpty).setVisibility(detailAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(detailAdapter);
    }
}
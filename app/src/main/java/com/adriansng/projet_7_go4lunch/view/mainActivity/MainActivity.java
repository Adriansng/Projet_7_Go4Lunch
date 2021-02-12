package com.adriansng.projet_7_go4lunch.view.mainActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.service.notification.ControllerWorkerManager;
import com.adriansng.projet_7_go4lunch.view.BaseActivity;
import com.adriansng.projet_7_go4lunch.view.LoginActivity;
import com.adriansng.projet_7_go4lunch.view.detailRestaurant.DetailRestaurantActivity;
import com.adriansng.projet_7_go4lunch.view.mainActivity.fragment.MapFragment;
import com.adriansng.projet_7_go4lunch.view.mainActivity.fragment.restaurantList.RestaurantListFragment;
import com.adriansng.projet_7_go4lunch.view.mainActivity.fragment.workmates.WorkmatesFragment;
import com.adriansng.projet_7_go4lunch.viewModel.Injection;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModeMainActivity;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModelFactoryMainActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.adriansng.projet_7_go4lunch.R.string.not_chosen_restaurant;

public class MainActivity extends BaseActivity {

    // --- BUTTER KNIFE VIEW ---

    @SuppressLint("NonConstantResourceId") @BindView(R.id.nav_view)
    BottomNavigationView bottomNavigationView;
    @SuppressLint("NonConstantResourceId") @BindView(R.id.nav_view_menu)
    NavigationView navigationDrawer;
    @SuppressLint("NonConstantResourceId") @BindView(R.id.toolbar)
    Toolbar toolbar;
    @SuppressLint("NonConstantResourceId") @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.nav_header_name_txt)
    TextView name;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.nav_header_email_txt)
    TextView email;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.nav_header_image_view)
    ImageView avatar;

    // --- FOR DATA ---

    private ViewModeMainActivity viewModelMainActivity;

    private List<Workmate> workmates = new ArrayList<>();
    private boolean changedNumberWorkmateChoose = true;

    private List<String> listFavorite = new ArrayList<>();
    private String idPlace = "";
    private String uidUser;
    private Restaurant validateRestaurant = null;
    private Restaurant autocompleteRestaurant = null;

    // ------------------
    // TO CREATE
    // ------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        // CONFIGURATION
        this.configToolbar();
        this.configOpenNavDrawer(drawerLayout);
        this.configBottomNavigation();
        this.configNavigationDrawer();
        ControllerWorkerManager.initNotification();
        this.updateFragment();
        this.uidUser = getUidUser();
        this.configViewModel();
    }

    // ------------------
    // VIEW MODEL
    // ------------------

    private void configViewModel() {
        ViewModelFactoryMainActivity viewModelFactoryMainActivity = Injection.viewModelFactoryMainActivity();
        viewModelMainActivity = new ViewModelProvider(this, viewModelFactoryMainActivity).get(ViewModeMainActivity.class);
        viewModelMainActivity.initWorkmate(uidUser);
        configObsWorkmates();
    }

    // ------------------
    // OBSERVER WORKMATES
    // ------------------

    // --- FOR LIST PLACES ---

    private void configObsWorkmates() {
        viewModelMainActivity.getWorkmates().observe(this, workmateList -> {
            changedNumberWorkmateChoose = viewModelMainActivity.checkNumberWorkmateChoose(workmates, workmateList);
            workmates = workmateList;
            configObsListFav(workmates, changedNumberWorkmateChoose);
        });
    }

    private void configObsListFav(List<Workmate> workmates, boolean changedNumberWorkmateChoose) {
        viewModelMainActivity.getFavoriteRestaurantList().observe(this, list -> {
            if (listFavorite.size() != list.size() || changedNumberWorkmateChoose) {
                listFavorite = list;
                viewModelMainActivity.initPlaces(this, this, workmates, listFavorite);
            }
            configObsIdValidatePlace(workmates, listFavorite);
        });
    }

    // --- FOR LAUNCH DETAIL PLACE SETTING ---

    private void configObsIdValidatePlace(List<Workmate> workmates, List<String> listFavorite) {
        viewModelMainActivity.getIdValidatedPlace(uidUser).observe(this, uidPlaces -> {
            if (!idPlace.matches(uidPlaces)) {
                idPlace = uidPlaces;
                if (!idPlace.matches("")) {
                    viewModelMainActivity.initValidatedPlace(idPlace, workmates, listFavorite);
                    configObsGetValidatedPlace();
                } else {
                    validateRestaurant = null;
                }
            }
        });
    }

    private void configObsGetValidatedPlace() {
        viewModelMainActivity.getValidatedPlace().observe(this, restaurant -> {
            if (!idPlace.matches("")) {
                validateRestaurant = restaurant;
            }
        });
    }

    // --- FOR LAUNCH DETAIL PLACE AUTOCOMPLETE ---

    private void configObsGetAutocompletePlace() {
        viewModelMainActivity.getAutocompletePlace().observe(this, restaurant -> {
            autocompleteRestaurant = restaurant;
            clickForDetail(autocompleteRestaurant);
        });
    }

    // ------------------
    // UI
    // ------------------

    // --- TOOLBAR ---

    private void configToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_list_24);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main_activity, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_toolbar_search:
                launchAutocomplete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // --- NAVIGATION BOTTOM BAR ---

    private void configBottomNavigation() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    // --- FRAGMENTS  ---

    private void getSupportFragmentManager(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private void updateFragment() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_workmates:
                    getSupportFragmentManager(WorkmatesFragment.newInstance());
                    this.toolbar.setTitle(R.string.title_fragment_workmates);
                    this.toolbar.getMenu().findItem(R.id.menu_toolbar_search).setVisible(false);
                    return true;
                case R.id.navigation_list_view:
                    getSupportFragmentManager(RestaurantListFragment.newInstance());
                    this.toolbar.setTitle(R.string.top_title);
                    this.toolbar.getMenu().findItem(R.id.menu_toolbar_search).setVisible(true);
                    return true;
                case R.id.navigation_map_view:
                    getSupportFragmentManager(MapFragment.newInstance());
                    this.toolbar.setTitle(R.string.top_title);
                    this.toolbar.getMenu().findItem(R.id.menu_toolbar_search).setVisible(true);
                    return true;
                default:
                    return false;
            }
        });
    }


    // --- NAVIGATION DRAWER ---

    @SuppressLint("NonConstantResourceId")
    private void configNavigationDrawer() {
        navigationDrawer.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_setting_lunch:
                    clickForDetail(validateRestaurant);
                    return true;
                case R.id.menu_setting_settings:
                    drawerLayout.close();
                    settingPopup();
                    return true;
                case R.id.menu_setting_logout:
                    logOut();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void configOpenNavDrawer(DrawerLayout drawerView) {
        drawerView.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                configProfile();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                configProfile();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }


    // --- SETTING --

    private void settingPopup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.setting_title_popup);
        dialog.setMessage(R.string.notification_delete_account);
        dialog.setPositiveButton(R.string.delete, (dialog1, which) -> {
            deleteAccount();
            finish();
        });
        dialog.setNegativeButton(R.string.setting_no, (dialog2, which) -> dialog2.dismiss());
        dialog.create().show();
    }

    private void clickForDetail(Restaurant restaurant) {
        if (restaurant != null) {
            Intent intent = new Intent(this, DetailRestaurantActivity.class);
            intent.putExtra("Restaurant", restaurant);
            this.startActivity(intent);
        } else {
            Toast.makeText(this, not_chosen_restaurant, Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------
    // CURRENT USER
    // ------------------

    // --- PROFILE ---

    private void configProfile() {
        ButterKnife.bind(this);
        if (name != null) name.setText(getNameUser());
        if (email != null) email.setText(getEmailUser());
        if (this.avatar != null)
            Glide.with(this).load(getAvatarUser()).circleCrop().into(this.avatar);
    }

    // --- LOGOUT ---

    private void logOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, task -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
    }

    // --- DELETE ACCOUNT ---

    private void deleteAccount() {
        viewModelMainActivity.deleteWorkmate(uidUser);
        AuthUI.getInstance()
                .delete(this)
                .addOnSuccessListener(this, task -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
    }

    // ------------------
    // AUTOCOMPLETE PLACE
    // ------------------

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            launchAutocomplete();
        }
    });

    private void launchAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setHint(getString(R.string.title_search_bar))
                .build(getApplicationContext());
        launcher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
            String idPlace = place.getId();
            viewModelMainActivity.initAutocompletePlace(idPlace, workmates, listFavorite);
            configObsGetAutocompletePlace();
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ------------------
    // LIFE ACTIVITY
    // ------------------

    @Override
    public void onBackPressed() {
    }
}
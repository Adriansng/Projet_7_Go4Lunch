package com.adriansng.projet_7_go4lunch.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.view.mainActivity.MainActivity;
import com.adriansng.projet_7_go4lunch.viewModel.Injection;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModeMainActivity;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModelFactoryMainActivity;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

public class LoginActivity extends BaseActivity {

    // --- FOR DATA ---

    private ViewModeMainActivity viewModeMainActivity;

    // ------------------
    // TO CREATE
    // ------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        this.configViewModel();
        loginForLaunch();
    }

    // ------------------
    // VIEW MODEL
    // ------------------

    private void configViewModel() {
        ViewModelFactoryMainActivity viewModelFactoryMainActivity = Injection.viewModelFactoryMainActivity();
        viewModeMainActivity = new ViewModelProvider(this, viewModelFactoryMainActivity).get(ViewModeMainActivity.class);
    }

    // ------------------
    // WORKMATE
    // ------------------

    private void configWorkmateList() {
        viewModeMainActivity.initWorkmates();
        viewModeMainActivity.getWorkmates().observe(this, workmates -> {
            if (!viewModeMainActivity.checkWorkmateIsToExist(getUidUser(), workmates)) {
                viewModeMainActivity.createWorkmate(getUidUser(), getNameUser(), getAvatarUser().toString(), getEmailUser());
            }
            loginForLaunch();
        });
    }

    // ------------------
    // LAUNCHER LOGIN
    // ------------------

    private void loginForLaunch() {
        if (!this.isCurrentUserLogged()) {
            this.startSignInActivity();
        } else {
            launchMainActivity();
        }
    }

    private void startSignInActivity() {
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                configWorkmateList();
            }
        });
        launcher.launch(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                        Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.meal_v3_final)
                .build());
    }

    // ------------------
    // LAUNCHER MAIN ACTIVITY
    // ------------------

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

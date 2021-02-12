package com.adriansng.projet_7_go4lunch.view;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adriansng.projet_7_go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    // ------------------
    // TO CREATE
    // ------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                    getString(R.string.maps_api_key));
        }
    }

    // ------------------
    // INFORMATION PROFILE
    // ------------------

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    protected String getUidUser() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    protected String getNameUser() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
    }

    protected Uri getAvatarUser() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl();
    }

    protected String getEmailUser() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    }

    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
    }
}

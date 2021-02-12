package com.adriansng.projet_7_go4lunch.viewModel.mainActivity;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.adriansng.projet_7_go4lunch.repositories.PlacesRepository;
import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;

import static android.content.ContentValues.TAG;

public class ViewModelFactoryMainActivity implements ViewModelProvider.Factory {

    private final PlacesRepository placesRepository;

    private final WorkmatesRepository workmatesRepository;

    public ViewModelFactoryMainActivity(PlacesRepository placesRepository, WorkmatesRepository workmatesRepository) {
        this.placesRepository = placesRepository;
        this.workmatesRepository = workmatesRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel>
    T create(@NonNull Class<T> modelClass) {
        Log.i(TAG, "MainViewModelProviderFactory: ");
        if (modelClass.isAssignableFrom(ViewModeMainActivity.class)) {
            Log.i(TAG, "create: is assignable");
            return (T) new ViewModeMainActivity(placesRepository, workmatesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }


}

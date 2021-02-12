package com.adriansng.projet_7_go4lunch.viewModel.detailActivity;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;

import static android.content.ContentValues.TAG;

public class ViewModelFactoryDetailActivity implements ViewModelProvider.Factory {

    private final WorkmatesRepository workmatesRepository;

    public ViewModelFactoryDetailActivity(WorkmatesRepository workmatesRepository) {
        this.workmatesRepository = workmatesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        Log.i(TAG, "DetailActivityModelProviderFactory: ");
        if (modelClass.isAssignableFrom(ViewModelDetailActivity.class)) {
            Log.i(TAG, "create: is assignable");
            return (T) new ViewModelDetailActivity(workmatesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

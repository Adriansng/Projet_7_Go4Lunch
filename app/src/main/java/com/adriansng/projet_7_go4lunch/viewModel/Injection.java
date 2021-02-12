package com.adriansng.projet_7_go4lunch.viewModel;

import com.adriansng.projet_7_go4lunch.repositories.PlacesRepository;
import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;
import com.adriansng.projet_7_go4lunch.viewModel.chatActivity.ViewModelFactoryChatActivity;
import com.adriansng.projet_7_go4lunch.viewModel.detailActivity.ViewModelFactoryDetailActivity;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModelFactoryMainActivity;

public class Injection {

    // --- REPOSITORIES ---

    private static PlacesRepository createPlaceRepository() {
        return new PlacesRepository();
    }

    private static WorkmatesRepository createWorkmateRepositories() {
        return new WorkmatesRepository();
    }

    // --- VIEW MODEL FACTORY ---

    public static ViewModelFactoryMainActivity viewModelFactoryMainActivity() {
        return new ViewModelFactoryMainActivity(createPlaceRepository(), createWorkmateRepositories());
    }

    public static ViewModelFactoryDetailActivity viewModelFactoryDetailActivity() {
        return new ViewModelFactoryDetailActivity(createWorkmateRepositories());
    }

    public static ViewModelFactoryChatActivity viewModelFactoryChatActivity() {
        return new ViewModelFactoryChatActivity(createPlaceRepository(), createWorkmateRepositories());
    }
}

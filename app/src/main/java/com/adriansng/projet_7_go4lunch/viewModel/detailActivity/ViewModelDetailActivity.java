package com.adriansng.projet_7_go4lunch.viewModel.detailActivity;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;

import java.util.List;

public class ViewModelDetailActivity extends ViewModel {

    // --- DATA ---

    private final MutableLiveData<List<Workmate>> listWorkmatesMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<String>> favoriteRestaurant = new MutableLiveData<>();
    private final MutableLiveData<String> validatedRestaurant = new MutableLiveData<>();

    // --- REPOSITORIES ---

    private final WorkmatesRepository workmatesRepository;

    // --- CONSTRUCTOR ---

    public ViewModelDetailActivity(WorkmatesRepository workmatesRepository) {
        this.workmatesRepository = workmatesRepository;
    }

    // ------------------
    // WORKMATES
    // ------------------

    public MutableLiveData<List<Workmate>> getWorkmates(String placeId) {
        this.workmatesRepository.setWorkmateListForThisRestaurant(listWorkmatesMutableLiveData, placeId);
        return listWorkmatesMutableLiveData;
    }

    // --- UPDATE WORKMATE ---

    public void updateWorkmateChooseRestaurant(String uid, String chooseRestaurant, String nameChooseRestaurant, String addressChooseRestaurant) {
        this.workmatesRepository.updateWorkmateChooseRestaurant(uid, chooseRestaurant, nameChooseRestaurant, addressChooseRestaurant);
    }

    // ------------------
    // VALIDATED RESTAURANT
    // ------------------

    // --- GET RESTAURANT VALIDATE ---

    public MutableLiveData<String> getValidatedRestaurant(String idWorkmate) {
        this.workmatesRepository.getValidatedRestaurant(idWorkmate, validatedRestaurant);
        return validatedRestaurant;
    }

    // --- CHECK VALIDATE ---

    public boolean checkRestaurantIsValidate(String validateRestaurant, String placeId) {
        return this.workmatesRepository.checkRestaurantIsValidate(validateRestaurant, placeId);
    }

    // ------------------
    // FAVORITE RESTAURANT
    // ------------------

    // --- GET LIST FAVORITE TO WORKMATE ---

    public MutableLiveData<List<String>> getFavoriteRestaurantList(String idWorkmate) {
        this.workmatesRepository.getListFavoriteRestaurantToWorkmate(idWorkmate, favoriteRestaurant);
        return favoriteRestaurant;
    }

    // --- CHECK FAVORITE ---

    public boolean checkRestaurantIsFavorite(String uid, List<String> favoriteList) {
        return this.workmatesRepository.checkRestaurantIsFavorite(favoriteList, uid);
    }

    // --- ADD FAVORITE RESTAURANTS --

    public void addFavoriteRestaurant(String idWorkmate, String uid) {
        this.workmatesRepository.addFavoriteRestaurantToWorkmate(idWorkmate, uid);
    }

    // --- DELETE WORKMATE ---

    public void deleteFavoriteRestaurantToWorkmate(String idWorkmate, String uid) {
        this.workmatesRepository.deleteFavoriteRestaurantToWorkmate(idWorkmate, uid);
    }
}

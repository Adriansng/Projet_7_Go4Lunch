package com.adriansng.projet_7_go4lunch.viewModel.mainActivity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.repositories.PlacesRepository;
import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;
import com.adriansng.projet_7_go4lunch.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ViewModeMainActivity extends ViewModel  {

    // --- DATA ---

    private MutableLiveData<List<Restaurant>> listPlaces;
    private MutableLiveData<Boolean> chargedList = new MutableLiveData<>(false);
    private MutableLiveData<List<String>> favoritePlace = new MutableLiveData<>();

    private final MutableLiveData<String> idValidatedPlace = new MutableLiveData<>();
    private MutableLiveData<Restaurant> validatedPlace = new MutableLiveData<>();

    private MutableLiveData<Restaurant> autocompletePlace = new MutableLiveData<>();

    private MutableLiveData<List<Workmate>> listWorkmates = new MutableLiveData<>();

    // --- REPOSITORIES ---

    private final PlacesRepository placesRepository;
    private final WorkmatesRepository workmatesRepository;

    // --- CONSTRUCTOR ---

    public ViewModeMainActivity(PlacesRepository placesRepository, WorkmatesRepository workmatesRepository) {
        this.placesRepository = placesRepository;
        this.workmatesRepository = workmatesRepository;
    }

    // ------------------
    // INIT
    // ------------------

    public void initWorkmate(String idWorkmate) {
        workmatesRepository.getListFavoriteRestaurantToWorkmate(idWorkmate, favoritePlace);
        setListFavorite(favoritePlace);
        initWorkmates();
    }

    public void initWorkmates() {
        workmatesRepository.setWorkmateListMutableLiveData(listWorkmates);
        setListWorkmates(listWorkmates);
    }

    public void initPlaces(Context context, Activity activity, List<Workmate> workmates, List<String> listFav) {
        Location mLocation = Utils.getLastKnowLocation(context, activity);
        if (mLocation != null) {
            if (listPlaces == null) {
                setListPlaces(placesRepository.getListPlaces(mLocation, workmates, listFav));
            } else {
                setListPlaces(placesRepository.getListPlacesWithoutCall(mLocation, workmates, listFav));
            }
            chargedList.setValue(true);
            setChargedList(chargedList);
        }
    }

    public void initValidatedPlace(String placeId, List<Workmate> workmates, List<String> favList) {
        setValidatedPlace(placesRepository.getDetailRestaurant(placeId, workmates, favList));
    }

    public void initAutocompletePlace(String placeId, List<Workmate> workmates, List<String> favList) {
        setAutocompletePlace(placesRepository.getDetailRestaurant(placeId, workmates, favList));
    }

    // ------------------
    // REFRESH LIST
    // ------------------

    public void refreshList(Context context, Activity activity) {
        Location mLocation = Utils.getLastKnowLocation(context, activity);
        if (mLocation != null) {
            setListPlaces(placesRepository.getListPlaces(mLocation, getWorkmates().getValue(), getFavoriteRestaurantList().getValue()));
            chargedList.setValue(true);
            setChargedList(chargedList);
        }
    }

    // ------------------
    // RESTAURANTS PLACES
    // ------------------

    public MutableLiveData<List<Restaurant>> getListPlaces() {
        return listPlaces;
    }

    private void setListPlaces(MutableLiveData<List<Restaurant>> listPlaces) {
        this.listPlaces = listPlaces;
    }

    public MutableLiveData<Boolean> IsChargedList() {
        return chargedList;
    }

    public void setChargedList(MutableLiveData<Boolean> isChargedList) {
        this.chargedList = isChargedList;
    }

    // ------------------
    // FAVORITE RESTAURANT
    // ------------------

    public MutableLiveData<List<String>> getFavoriteRestaurantList() {
        return favoritePlace;
    }

    private void setListFavorite(MutableLiveData<List<String>> listFav) {
        this.favoritePlace = listFav;
    }

    // ------------------
    // VALIDATED RESTAURANT
    // ------------------

    // --- GET ID RESTAURANT VALIDATE ---

    public MutableLiveData<String> getIdValidatedPlace(String idWorkmate) {
        this.workmatesRepository.getValidatedRestaurant(idWorkmate, idValidatedPlace);
        return idValidatedPlace;
    }

    // --- GET RESTAURANT VALIDATE ---

    public MutableLiveData<Restaurant> getValidatedPlace() {
        return validatedPlace;
    }

    private void setValidatedPlace(MutableLiveData<Restaurant> restaurant) {
        this.validatedPlace = restaurant;
    }

    // ------------------
    // AUTOCOMPLETE RESTAURANT
    // ------------------

    public MutableLiveData<Restaurant> getAutocompletePlace() {
        return autocompletePlace;
    }

    private void setAutocompletePlace(MutableLiveData<Restaurant> autocompleteRestaurant) {
        this.autocompletePlace = autocompleteRestaurant;
    }

    // ------------------
    // WORKMATES
    // ------------------

    public MutableLiveData<List<Workmate>> getWorkmates() {
        return listWorkmates;
    }

    private void setListWorkmates(MutableLiveData<List<Workmate>> listWorkmates) {
        this.listWorkmates = listWorkmates;
    }

    public void deleteWorkmateLoggedToList(List<Workmate> workmates){
        int indexDelete = -1;
        String uidWorkmateLogged = FirebaseAuth.getInstance().getUid();
        int size = workmates.size();
        for (int i = 0; i < size; i++) {
            String id = workmates.get(i).getUid();
            if(id.equals(uidWorkmateLogged)){
               indexDelete = i;
            }
        }
        if(indexDelete != -1) {
            workmates.remove(indexDelete);
        }
    }

    // --- CHECK VALIDATE ---

    public boolean checkWorkmateForTheRestaurant(List<Workmate> workmateList, String placeId) {
        return this.workmatesRepository.checkWorkmatesForTheRestaurant(workmateList, placeId);
    }

    public boolean checkNumberWorkmateChoose(List<Workmate> workmates, List<Workmate> newWorkmate) {
        return this.workmatesRepository.checkNumberWorkmateChoose(workmates, newWorkmate);
    }

    // --- CHECK WORKMATE ---

    public boolean checkWorkmateIsToExist(String uid, List<Workmate> workmates) {
        return this.workmatesRepository.checkWorkmateIsToExist(uid, workmates);
    }

    // --- CREATE WORKMATE ---

    public void createWorkmate(String uid, String name, String avatar, String email) {
        this.workmatesRepository.createWorkmate(uid, name, avatar, email);
    }

    // --- DELETE WORKMATE ---

    public void deleteWorkmate(String uid) {
        this.workmatesRepository.deleteWorkmate(uid);
    }
}

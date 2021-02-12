package com.adriansng.projet_7_go4lunch.repositories;

import androidx.lifecycle.MutableLiveData;

import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.List;

interface WorkmateRepositoryInterface {

    // ------------------
    // WORKMATE
    // ------------------

    // --- GET LIST WORKMATES ---
    void setWorkmateListMutableLiveData(MutableLiveData<List<Workmate>> listWorkmatesMutableLiveData);

    void setWorkmateListForThisRestaurant(MutableLiveData<List<Workmate>> listWorkmatesMutableLiveData, String placeID);

    // --- CHECK NUMBER WORKMATE CHOOSE --
    boolean checkNumberWorkmateChoose(List<Workmate> workmates, List<Workmate> newWorkmate);

    // --- CHECK WORKMATE EXIST ---
    boolean checkWorkmateIsToExist(String uid, List<Workmate> workmates);

    // --- CREATE WORKMATE ---
    void createWorkmate(String uid, String name, String avatar, String email);

    // --- UPDATE WORKMATE ---
    void updateWorkmateChooseRestaurant(String uid, String chooseRestaurant, String nameChooseRestaurant, String addressChooseRestaurant);

    // --- DELETE WORKMATE ---
    void deleteWorkmate(String uid);

    // ------------------
    // VALIDATE RESTAURANT
    // ------------------

    // --- GET RESTAURANT VALIDATE ---
    void getValidatedRestaurant(String uid, MutableLiveData<String> placeIdValidate);

    // --- CHECK VALIDATE ---
    boolean checkRestaurantIsValidate(String validateRestaurant, String placeID);

    // ------------------
    // FAVORITE RESTAURANT
    // ------------------

    // --- GET LIST FAVORITE TO WORKMATE ---
    void getListFavoriteRestaurantToWorkmate(String idWorkmate, MutableLiveData<List<String>> restaurants);

    // --- CHECK FAVORITE ---
    boolean checkRestaurantIsFavorite(List<String> restaurants, String uid);

    // --- ADD FAVORITE RESTAURANTS --
    void addFavoriteRestaurantToWorkmate(String idWorkmate, String uid);

    // --- DELETE WORKMATE ---
    void deleteFavoriteRestaurantToWorkmate(String idWorkmate, String uid);

    // ------------------
    // MESSAGE
    // ------------------

    // --- GET ALL MESSAGE ---
    Query getAllMessage(String idWorkmateSender, String idWorkmateReceiver);

    // --- CREATE MESSAGE ---
    Task<DocumentReference> createMessage(String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver);

    // --- CREATE MESSAGE WITH IMAGE ---
    Task<DocumentReference> createMessageWithImage(String urlImage, String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver);
}

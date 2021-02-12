package com.adriansng.projet_7_go4lunch.repositories;

import androidx.lifecycle.MutableLiveData;

import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.service.helper.HelperChat;
import com.adriansng.projet_7_go4lunch.service.helper.HelperWorkmate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkmatesRepository implements WorkmateRepositoryInterface {

    // ------------------
    // WORKMATE
    // ------------------

    // --- GET LIST WORKMATES ---

    @Override
    public void setWorkmateListMutableLiveData(MutableLiveData<List<Workmate>> listWorkmatesMutableLiveData) {
        HelperWorkmate.getListWorkmates().addSnapshotListener((queryDocumentSnapShots, e) -> {
            if (queryDocumentSnapShots != null) {
                List<DocumentSnapshot> workmateList = queryDocumentSnapShots.getDocuments();
                List<Workmate> workmates = new ArrayList<>();
                int size = workmateList.size();
                for (int i = 0; i < size; i++) {
                    Workmate workmate = workmateList.get(i).toObject(Workmate.class);
                    {
                        workmates.add(workmate);
                    }
                }
                listWorkmatesMutableLiveData.setValue(workmates);
            }
        });
    }

    @Override
    public void setWorkmateListForThisRestaurant(MutableLiveData<List<Workmate>> listWorkmatesMutableLiveData, String placeID) {
        HelperWorkmate.getListWorkmates().addSnapshotListener((queryDocumentSnapShots, e) -> {
            if (queryDocumentSnapShots != null) {
                List<DocumentSnapshot> workmateList = queryDocumentSnapShots.getDocuments();
                List<Workmate> workmates = new ArrayList<>();
                int size = workmateList.size();
                for (int i = 0; i < size; i++) {
                    Workmate workmate = workmateList.get(i).toObject(Workmate.class);
                    if (Objects.requireNonNull(workmate).getChooseRestaurant() != null) {
                        if (workmate.getChooseRestaurant().matches(placeID)) {
                            workmates.add(workmate);
                        }
                    }
                }
                listWorkmatesMutableLiveData.setValue(workmates);
            }
        });
    }

    // --- CHECK WORKMATE FOR RESTAURANT ---

    public boolean checkWorkmatesForTheRestaurant(List<Workmate> workmateList, String placeId) {
        boolean check = false;
        int size = workmateList.size();
        for (int i = 0; i < size; i++) {
            if (workmateList.get(i).getChooseRestaurant() != null) {
                if (workmateList.get(i).getChooseRestaurant().matches(placeId)) {
                    check = true;
                }
            }
        }
        return check;
    }

    // --- CHECK NUMBER WORKMATE CHOOSE ---

    @Override
    public boolean checkNumberWorkmateChoose(List<Workmate> workmates, List<Workmate> newWorkmate) {
        return getNumberWorkmateChoose(newWorkmate) == 0 || getNumberWorkmateChoose(workmates) != getNumberWorkmateChoose(newWorkmate);
    }

    public int getNumberWorkmateChoose(List<Workmate> workmates) {
        int numberWorkmateChoose = 0;
        int size = workmates.size();
        for (int i = 0; i < size; i++) {
            String workmate = workmates.get(i).getChooseRestaurant();
            if (workmate != null) {
                if (!workmate.matches("")) {
                    numberWorkmateChoose++;
                }
            }
        }
        return numberWorkmateChoose;
    }

    // --- CHECK WORKMATE EXIST ---

    @Override
    public boolean checkWorkmateIsToExist(String uid, List<Workmate> workmates) {
        boolean check = false;
        int size = workmates.size();
        for (int i = 0; i < size; i++) {
            String workmateId = workmates.get(i).getUid();
            if (workmateId.matches(uid)) {
                check = true;
            }
        }
        return check;
    }


    // --- CREATE WORKMATE ---

    @Override
    public void createWorkmate(String uid, String name, String avatar, String email) {
        HelperWorkmate.createWorkmate(uid, name, avatar, email);
    }

    // --- UPDATE WORKMATE ---

    @Override
    public void updateWorkmateChooseRestaurant(String uid, String chooseRestaurant, String nameChooseRestaurant, String addressChooseRestaurant) {
        HelperWorkmate.updateWorkmateChooseRestaurant(uid, chooseRestaurant);
        HelperWorkmate.updateWorkmateNameChooseRestaurant(uid, nameChooseRestaurant);
        HelperWorkmate.updateWorkmateAddressChooseRestaurant(uid, addressChooseRestaurant);
    }


    // --- DELETE WORKMATE ---

    @Override
    public void deleteWorkmate(String uid) {
        HelperWorkmate.deleteWorkmate(uid);
    }

    // ------------------
    // VALIDATE RESTAURANT
    // ------------------

    // --- GET RESTAURANT VALIDATE ---

    public void getValidatedRestaurant(String uid, MutableLiveData<String> placeIdValidate) {
        HelperWorkmate.getWorkmateCollection(uid).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Workmate workmate = documentSnapshot.toObject(Workmate.class);
                String chooseRestaurant = Objects.requireNonNull(workmate).getChooseRestaurant();
                if (chooseRestaurant != null) {
                    placeIdValidate.setValue(chooseRestaurant);
                } else {
                    placeIdValidate.setValue("");
                }
            }
        });
    }

    // --- CHECK VALIDATE ---

    @Override
    public boolean checkRestaurantIsValidate(String validateRestaurant, String placeID) {
        boolean check = false;
        if (validateRestaurant.matches(placeID)) {
            check = true;
        }
        return check;
    }

    // ------------------
    // FAVORITE RESTAURANT
    // ------------------

    // --- RECOVER LIST FAVORITE TO WORKMATE ---

    @Override
    public void getListFavoriteRestaurantToWorkmate(String idWorkmate, MutableLiveData<List<String>> data) {
        HelperWorkmate.getListFavoriteRestaurant(idWorkmate).addSnapshotListener((queryDocument, e) -> {
            if (queryDocument != null) {
                List<DocumentSnapshot> restaurantList = queryDocument.getDocuments();
                List<String> restaurants = new ArrayList<>();
                int size = restaurantList.size();
                for (int i = 0; i < size; i++) {
                    String idRestaurant = String.valueOf(restaurantList.get(i).get("placeId"));
                    restaurants.add(idRestaurant);
                }
                if (size == restaurants.size()) {
                    data.setValue(restaurants);
                }
            }
        });
    }

    // --- CHECK FAVORITE ---

    @Override
    public boolean checkRestaurantIsFavorite(List<String> restaurants, String uid) {
        boolean check = false;
        int size = restaurants.size();
        for (int i = 0; i < size; i++) {
            String uidRestaurant = restaurants.get(i);
            if (uidRestaurant.matches(uid)) {
                check = true;
            }
        }
        return check;
    }
    // --- ADD FAVORITE RESTAURANTS --

    @Override
    public void addFavoriteRestaurantToWorkmate(String idWorkmate, String uid) {
        HelperWorkmate.addRestaurantFavoriteToWorkmate(idWorkmate, uid);
    }

    // --- DELETE FAVORITE ---

    @Override
    public void deleteFavoriteRestaurantToWorkmate(String idWorkmate, String uid) {
        HelperWorkmate.deleteRestaurantFavorite(idWorkmate, uid);
    }

    // ------------------
    // MESSAGE
    // ------------------

    @Override
    public Query getAllMessage(String idWorkmateSender, String idWorkmateReceiver) {
        return HelperChat.getAllMessageForChat(idWorkmateSender, idWorkmateReceiver);
    }

    @Override
    public Task<DocumentReference> createMessage(String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver) {
        return HelperChat.createMessageForChat(textMessage, idWorkmateSender, idWorkmateReceiver);
    }

    @Override
    public Task<DocumentReference> createMessageWithImage(String urlImage, String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver) {
        return HelperChat.createMessageWithImageForChat(urlImage, textMessage, idWorkmateSender, idWorkmateReceiver);
    }
}

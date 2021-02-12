package com.adriansng.projet_7_go4lunch.service.helper;

import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class HelperWorkmate {

    private static final String COLLECTION_NAME_WORKMATES = "Workmates";
    private static final String COLLECTION_NAME_RESTAURANT = "Restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getWorkmateCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_WORKMATES);
    }

    // ------------------
    // WORKMATE
    // ------------------

    // --- CREATE ---

    public static void createWorkmate(String uid, String name, String avatar, String email) {
        Workmate workmateToCreate = new Workmate(uid, name, avatar, email);
        HelperWorkmate.getWorkmateCollection().document(uid).set(workmateToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getWorkmateCollection(String uid) {
        return HelperWorkmate.getWorkmateCollection().document(uid).get();
    }

    public static Query getListWorkmates() {
        return getWorkmateCollection()
                .orderBy("nameWorkmate");
    }

    // --- UPDATE ---

    public static void updateWorkmateChooseRestaurant(String uid, String chooseRestaurant) {
        getWorkmateCollection().document(uid).update("chooseRestaurant", chooseRestaurant);
    }

    public static void updateWorkmateNameChooseRestaurant(String uid, String nameChooseRestaurant) {
        getWorkmateCollection().document(uid).update("nameChooseRestaurant", nameChooseRestaurant);
    }

    public static void updateWorkmateAddressChooseRestaurant(String uid, String addressChooseRestaurant) {
        getWorkmateCollection().document(uid).update("addressChooseRestaurant", addressChooseRestaurant);
    }


    // --- DELETE ---

    public static void deleteWorkmate(String uid) {
        HelperWorkmate.getWorkmateCollection().document(uid).delete();
    }

    // ------------------
    // FAVORITE RESTAURANT
    // ------------------

    // --- ADD RESTAURANT ---

    public static void addRestaurantFavoriteToWorkmate(String idWorkmate, String idPlace) {
        Map<String, Object> db = new HashMap<>();
        db.put("placeId", idPlace);
        getWorkmateCollection()
                .document(idWorkmate)
                .collection(COLLECTION_NAME_RESTAURANT)
                .document(idPlace)
                .set(db);
    }

    // --- GET ---

    public static Query getListFavoriteRestaurant(String idWorkmate) {
        return getWorkmateCollection().document(idWorkmate).collection(COLLECTION_NAME_RESTAURANT);
    }

    // --- DELETE ---

    public static void deleteRestaurantFavorite(String idWorkmate, String uid) {
        getWorkmateCollection()
                .document(idWorkmate)
                .collection(COLLECTION_NAME_RESTAURANT)
                .document(uid)
                .delete();
    }
}




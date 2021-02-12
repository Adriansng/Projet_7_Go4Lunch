package com.adriansng.projet_7_go4lunch.viewModel.chatActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.repositories.PlacesRepository;
import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ViewModelChatActivity extends ViewModel {

    // --- FOR DATA ---

    private MutableLiveData<List<Workmate>> listWorkmates = new MutableLiveData<>();

    private MutableLiveData<List<String>> favoritePlace = new MutableLiveData<>();
    private final MutableLiveData<String> idValidatedPlace = new MutableLiveData<>();
    private MutableLiveData<Restaurant> validatedPlace = new MutableLiveData<>();

    // --- REPOSITORIES ---

    private final PlacesRepository placesRepository;
    private final WorkmatesRepository workmatesRepository;

    public ViewModelChatActivity(PlacesRepository placesRepository, WorkmatesRepository workmatesRepository) {
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

    public void initValidatedPlace(String placeId, List<Workmate> workmates, List<String> favList) {
        setValidatedPlace(placesRepository.getDetailRestaurant(placeId, workmates, favList));
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
    // WORKMATES
    // ------------------

    public MutableLiveData<List<Workmate>> getWorkmates() {
        return listWorkmates;
    }

    private void setListWorkmates(MutableLiveData<List<Workmate>> listWorkmates) {
        this.listWorkmates = listWorkmates;
    }

    // ------------------
    // MESSAGE
    // ------------------

    // --- GET ALL MESSAGES ---

    public Query getAllMessages(String idWorkmateSender, String idWorkmateReceiver) {
        return workmatesRepository.getAllMessage(idWorkmateSender, idWorkmateReceiver);
    }

    // --- CREATE ---

    public Task<DocumentReference> createMessage(String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver) {
        return workmatesRepository.createMessage(textMessage, idWorkmateSender, idWorkmateReceiver);
    }

    public Task<DocumentReference> createMessageWithImage(String urlImage, String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver) {
        return workmatesRepository.createMessageWithImage(urlImage, textMessage, idWorkmateSender, idWorkmateReceiver);
    }
}

package com.adriansng.projet_7_go4lunch.repositories;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;

import java.util.List;

interface PlacesRepositoryInterface {

    MutableLiveData<List<Restaurant>> getListPlaces(Location location, List<Workmate> workmates, List<String> listFavorite);

    MutableLiveData<List<Restaurant>> getListPlacesWithoutCall(Location myLocation, List<Workmate> workmates, List<String> listFavorite);

    MutableLiveData<Restaurant> getDetailRestaurant(String placeId, List<Workmate> workmates, List<String> listFavorite);
}

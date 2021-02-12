package com.adriansng.projet_7_go4lunch.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.adriansng.projet_7_go4lunch.BuildConfig;
import com.adriansng.projet_7_go4lunch.model.POJO.DetailPlace.Location;
import com.adriansng.projet_7_go4lunch.model.POJO.DetailPlace.PlaceDetailPOJO;
import com.adriansng.projet_7_go4lunch.model.POJO.NearbyPlace.PlacePOJO;
import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.service.GoogleApiService;
import com.adriansng.projet_7_go4lunch.service.RetrofitService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesRepository implements PlacesRepositoryInterface {

    // --- FOR DATA ---

    private static final String type = "restaurant";
    private static final String radius = "2000";
    private GoogleApiService mGoogleApiService;
    private static final String apiKey = BuildConfig.MAPS_API_KEY;

    private final List<Response<PlaceDetailPOJO>> detailPOJOResponseList = new ArrayList<>();

    // --- REPOSITORY INSTANCE ---

    public PlacesRepository() {
        mGoogleApiService = RetrofitService.getInterface();
    }

    // ------------------
    // API PLACE
    // ------------------

    /**
     * Have the list of restaurants thanks to the API PLACE call
     */

    @Override
    public MutableLiveData<List<Restaurant>> getListPlaces(android.location.Location myLocation, List<Workmate> workmates, List<String> listFavorite) {
        mGoogleApiService = RetrofitService.getInterface();
        MutableLiveData<List<Restaurant>> listPlaces = new MutableLiveData<>();
        Double latLocation = myLocation.getLatitude();
        double longLocation = myLocation.getLongitude();
        String location = latLocation + "," + longLocation;
        mGoogleApiService.getNearbyRestaurants(type, location, radius, apiKey).enqueue(new Callback<PlacePOJO>() {
            @Override
            public void onResponse(@NonNull Call<PlacePOJO> call, @NonNull Response<PlacePOJO> response) {
                if (response.body() != null) {
                    int size = response.body().getResults().size();
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        String placeIdRes = response.body().getResults().get(i).getPlaceId();
                        getDetailPlaceList(placeIdRes, listPlaces, restaurants, size, myLocation, workmates, listFavorite);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacePOJO> call, @NonNull Throwable t) {
                listPlaces.setValue(null);
            }
        });
        return listPlaces;
    }

    @Override
    public MutableLiveData<List<Restaurant>> getListPlacesWithoutCall(android.location.Location myLocation, List<Workmate> workmates, List<String> listFavorite) {
        MutableLiveData<List<Restaurant>> listPlaces = new MutableLiveData<>();
        int size = detailPOJOResponseList.size();
        List<Restaurant> restaurants = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String placeIdRes = Objects.requireNonNull(detailPOJOResponseList.get(i).body()).getResult().getPlaceId();
            getDetailPlaceWithoutCall(placeIdRes, listPlaces, restaurants, size, myLocation, workmates, listFavorite, i);
        }
        return listPlaces;
    }

    // ------------------
    // API PLACE DETAIL
    // ------------------

    // --- GET DETAIL WITH CALL API ---

    private void getDetailPlaceList(String placeId, MutableLiveData<List<Restaurant>> liveData, List<Restaurant> restaurants, int size, android.location.Location myLocation, List<Workmate> workmates, List<String> listFavorite) {
        mGoogleApiService.getDetailRestaurants(placeId, apiKey).enqueue(new Callback<PlaceDetailPOJO>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetailPOJO> call, @NonNull Response<PlaceDetailPOJO> res) {
                if (res.body() != null) {
                    Restaurant restaurant = getDetail(res, placeId, myLocation, workmates, listFavorite);
                    if (restaurant != null) {
                        restaurants.add(restaurant);
                        detailPOJOResponseList.add(res);
                    }
                    if (restaurants.size() == size) {
                        liveData.setValue(restaurants);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetailPOJO> call, @NonNull Throwable t) {
                liveData.setValue(null);
            }
        });
    }

    // --- GET DETAIL WITHOUT CALL API ---

    public void getDetailPlaceWithoutCall(String placeId, MutableLiveData<List<Restaurant>> liveData, List<Restaurant> restaurants, int size, android.location.Location myLocation, List<Workmate> workmates, List<String> listFavorite, int i) {
        Restaurant restaurant = getDetail(detailPOJOResponseList.get(i), placeId, myLocation, workmates, listFavorite);
        if (restaurant != null) {
            restaurants.add(restaurant);
        }
        if (restaurants.size() == size) {
            liveData.setValue(restaurants);
        }
    }

    // --- DETAIL FOR LIST PLACE BY LOCATION ---

    private Restaurant getDetail(Response<PlaceDetailPOJO> res, String placeId, android.location.Location myLocation, List<Workmate> workmates, List<String> listFavorite) {
        Restaurant restaurant = null;
        if (res.body() != null) {
            String photo;
            if (res.body().getResult().getPhotos() != null) {
                photo = getPhoto(res.body().getResult().getPhotos().get(0).getPhotoReference());
            } else {
                photo = res.body().getResult().getIcon();
            }
            Boolean openHours;
            if (res.body().getResult().getOpeningHours() != null) {
                openHours = res.body().getResult().getOpeningHours().getOpenNow();
            } else {
                return null;
            }
            Location placeLocation = res.body().getResult().getGeometry().getLocation();
            restaurant = new Restaurant(
                    res.body().getResult().getPlaceId(),
                    res.body().getResult().getName(),
                    photo,
                    res.body().getResult().getVicinity(),
                    res.body().getResult().getInternationalPhoneNumber(),
                    res.body().getResult().getWebsite(),
                    getRating(res.body().getResult().getRating()),
                    placeLocation,
                    openHours,
                    getDistance(myLocation, placeLocation),
                    checkHowManyWorkmate(placeId, workmates),
                    checkIsFavorite(placeId, listFavorite));
        }
        return restaurant;
    }

    // --- DETAIL FOR ONE PLACE ---

    @Override
    public MutableLiveData<Restaurant> getDetailRestaurant(String placeId, List<Workmate> workmates, List<String> listFavorite) {
        MutableLiveData<Restaurant> place = new MutableLiveData<>();
        mGoogleApiService.getDetailRestaurants(placeId, apiKey).enqueue(new Callback<PlaceDetailPOJO>() {
            @Override
            public void onResponse(@NotNull Call<PlaceDetailPOJO> call, @NotNull Response<PlaceDetailPOJO> res) {
                if (res.body() != null) {
                    String photo;
                    if (res.body().getResult().getPhotos() != null) {
                        photo = getPhoto(res.body().getResult().getPhotos().get(0).getPhotoReference());
                    } else {
                        photo = res.body().getResult().getIcon();
                    }
                    Restaurant restaurant = new Restaurant(
                            res.body().getResult().getPlaceId(),
                            res.body().getResult().getName(),
                            photo,
                            res.body().getResult().getVicinity(),
                            res.body().getResult().getInternationalPhoneNumber(),
                            res.body().getResult().getWebsite(),
                            checkHowManyWorkmate(placeId, workmates),
                            checkIsFavorite(placeId, listFavorite));
                    place.setValue(restaurant);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlaceDetailPOJO> call, @NotNull Throwable t) {
                place.setValue(null);
            }
        });
        return place;
    }

    // --- UTILS ---

    public double getRating(Double note) {
        return (note * 4) / 5;
    }

    public String getDistance(android.location.Location myLocation, Location locationRestaurant) {
        android.location.Location restaurantLocation = new android.location.Location("A");
        restaurantLocation.setLatitude(locationRestaurant.getLat());
        restaurantLocation.setLongitude(locationRestaurant.getLng());
        return (int) restaurantLocation.distanceTo(myLocation) + "m";
    }

    private String getPhoto(String photoReference) {
        return "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + photoReference + "&sensor=false&maxheight=400&maxwidth=400&key=" + PlacesRepository.apiKey;
    }

    // ------------------
    // CHECK IS FAVORITE
    // ------------------

    public boolean checkIsFavorite(String idPlace, List<String> listFavorite) {
        boolean check = false;
        int size = listFavorite.size();
        for (int i = 0; i < size; i++) {
            if (listFavorite.get(i).matches(idPlace)) {
                check = true;
            }
        }
        return check;
    }

    // ------------------
    // CHECK LIST WORKMATE
    // ------------------

    public int checkHowManyWorkmate(String idPlace, List<Workmate> workmates) {
        int result = 0;
        int size = workmates.size();
        for (int i = 0; i < size; i++) {
            if (workmates.get(i).getChooseRestaurant() != null) {
                if (workmates.get(i).getChooseRestaurant().matches(idPlace)) {
                    result++;
                }
            }
        }
        return result;
    }
}


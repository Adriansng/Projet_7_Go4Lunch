package com.adriansng.projet_7_go4lunch.service;

import com.adriansng.projet_7_go4lunch.model.POJO.DetailPlace.PlaceDetailPOJO;
import com.adriansng.projet_7_go4lunch.model.POJO.NearbyPlace.PlacePOJO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {

    @GET("nearbysearch/json?")
    Call<PlacePOJO> getNearbyRestaurants(
            @Query(value = "type", encoded = true) String type,
            @Query(value = "location", encoded = true) String location,
            @Query(value = "radius", encoded = true) String radius,
            @Query(value = "key", encoded = true) String key);

    //place_id,name,photo,vicinity,international_phone_number,website,rating,geometry,opening_hours,icon
    @GET("details/json?")
    Call<PlaceDetailPOJO> getDetailRestaurants(@Query("place_id") String placeId,
                                               @Query("key") String key);
}

package com.adriansng.projet_7_go4lunch.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    // --- CREATE INSTANCE RETROFIT ---

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static GoogleApiService getInterface() {
        return retrofit.create(GoogleApiService.class);
    }
}

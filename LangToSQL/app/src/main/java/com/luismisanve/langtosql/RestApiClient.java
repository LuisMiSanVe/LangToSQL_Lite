package com.luismisanve.langtosql;

import retrofit2.*;
import retrofit2.converter.gson.*;

public class RestApiClient {

    private static String BASE_URL = "";
    private static Retrofit retrofit;

    public RestApiClient(String url){
        BASE_URL = url;
    }

    public Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
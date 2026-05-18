package com.luismisanve.langtosql;

import retrofit2.Call;
import retrofit2.http.*;

public interface RestApiCall {
    @FormUrlEncoded
    @POST("AI/AIDatabaseSQL")
    Call<String> generateSQL(
            @Field("Request") String request
    );
}

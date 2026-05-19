package com.luismisanve.langtosql;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface RestApiCall {
    @GET("AI/GenRunQuery")
    Call<ResponseBody> generateSQL(
            @Query("Request") String request
    );
}

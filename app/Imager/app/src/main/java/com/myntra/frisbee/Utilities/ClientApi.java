package com.myntra.frisbee.Utilities;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClientApi {

    @GET("predict/")
    Call<RecommendationPojo> getApiData(@Query("url") String imageUrl);

}
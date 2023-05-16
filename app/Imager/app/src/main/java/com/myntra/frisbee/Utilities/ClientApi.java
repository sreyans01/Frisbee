package com.myntra.frisbee.Utilities;

import com.google.gson.JsonObject;
import com.myntra.frisbee.model.PredictList;
import com.myntra.frisbee.model.UrlPojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClientApi {

    @POST("predict/")
    Call<RecommendationPojo> getRecommendationFromSingleImage(@Body UrlPojo url);

    @POST("predictlist/")
    Call<RecommendationPojo> getRecommendationFromImageList(@Body PredictList predictList);

    @POST("predictyoutube/")
    Call<RecommendationPojo> getPredictionFromYoutube(@Body UrlPojo url);

}
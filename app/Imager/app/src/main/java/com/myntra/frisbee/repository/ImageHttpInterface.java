package com.myntra.frisbee.repository;

import androidx.lifecycle.LiveData;

import com.bumptech.glide.load.engine.Resource;
import com.myntra.frisbee.model.ImageDetails;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ImageHttpInterface {


    @POST
    Single<Resource<List<ImageDetails>>> getImageListFromModel(@Url String url, @Query("url") String imageUrl);

    void OnGettingImageRecommendationList(List<String> predictedList);

    @GET("employees")
    Call<Response> getApiData();

}

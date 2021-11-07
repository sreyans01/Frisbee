package com.myntra.frisbee.external;

import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiCallInterface {



    @GET("employees")
    Observable<Response> login();

    @GET("employees")
    Call<Response> getApiData();

}
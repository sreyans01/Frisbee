package com.myntra.frisbee.Utilities.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.myntra.frisbee.Utilities.ClientApi;
import com.myntra.frisbee.Utilities.Firebase.FirebaseRemoteConfigHelper;
import com.myntra.frisbee.Utilities.RecommendationPojo;
import com.myntra.frisbee.Utilities.Utils;
import com.myntra.frisbee.model.PredictList;
import com.myntra.frisbee.model.UrlPojo;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {

    @Inject
    FirebaseRemoteConfigHelper firebaseRemoteConfigHelper;
    ClientApi clientApi;
    private static final AppRepository ourInstance = new AppRepository();
    public MutableLiveData<RecommendationPojo> recommendationPojoMutableLiveData = new MutableLiveData<>();

    public static AppRepository getInstance() {
        return ourInstance;
    }
    private AppRepository() {
        firebaseRemoteConfigHelper = new FirebaseRemoteConfigHelper();
        firebaseRemoteConfigHelper.fetchRemoteConfigValues();
        Log.i("KLKLKL",firebaseRemoteConfigHelper.getLocalHostAddress());
        clientApi = Utils.getClientAPI(firebaseRemoteConfigHelper);
    }

    public void getRecommendations(String imageUrl){

        UrlPojo urlPojo = new UrlPojo(imageUrl);
        clientApi.getRecommendationFromSingleImage(urlPojo).enqueue(new Callback<RecommendationPojo>() {
            @Override
            public void onResponse(Call<RecommendationPojo> call, Response<RecommendationPojo> response) {
                if(response.isSuccessful()){
                    Log.i("KLKLKL","Success");
                    recommendationPojoMutableLiveData.setValue(response.body());
                }else{
                    Log.i("KLKLKL","Error "+response.message());
                    recommendationPojoMutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RecommendationPojo> call, Throwable t) {

                recommendationPojoMutableLiveData.setValue(null);
            }
        });
    }

    public void getRecommendationFromYoutubeUrl(String imageUrl){

        UrlPojo urlPojo = new UrlPojo(imageUrl);
        clientApi.getPredictionFromYoutube(urlPojo).enqueue(new Callback<RecommendationPojo>() {
            @Override
            public void onResponse(Call<RecommendationPojo> call, Response<RecommendationPojo> response) {
                if(response.isSuccessful()){
                    Log.i("KLKLKL","Success");
                    recommendationPojoMutableLiveData.setValue(response.body());
                }else{
                    Log.i("KLKLKL","Error "+response.message());
                    recommendationPojoMutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RecommendationPojo> call, Throwable t) {

                recommendationPojoMutableLiveData.setValue(null);
            }
        });
    }

    public void getRecommendations(PredictList predictList){

        clientApi.getRecommendationFromImageList(predictList).enqueue(new Callback<RecommendationPojo>() {
            @Override
            public void onResponse(Call<RecommendationPojo> call, Response<RecommendationPojo> response) {
                if(response.isSuccessful()){
                    Log.i("OPOPOP","Success");
                    recommendationPojoMutableLiveData.setValue(response.body());
                }else{
                    Log.i("OPOPOP","Error "+response.message());
                    recommendationPojoMutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RecommendationPojo> call, Throwable t) {

                Log.i("OPOPOP","Error ");
                recommendationPojoMutableLiveData.setValue(null);
            }
        });
    }

}

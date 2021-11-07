package com.myntra.frisbee.Utilities.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.myntra.frisbee.Utilities.ClientApi;
import com.myntra.frisbee.Utilities.RecommendationPojo;
import com.myntra.frisbee.Utilities.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {

    ClientApi clientApi = Utils.getClientAPI();
    private static final AppRepository ourInstance = new AppRepository();
    public MutableLiveData<RecommendationPojo> recommendationPojoMutableLiveData = new MutableLiveData<>();

    public static AppRepository getInstance() {
        return ourInstance;
    }
    private AppRepository() {
        clientApi = Utils.getClientAPI();
    }

    public void getRecommendations(String imageUrl){

        clientApi.getApiData(imageUrl).enqueue(new Callback<RecommendationPojo>() {
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
}

package com.myntra.frisbee.viewmodel;


import androidx.lifecycle.LiveData;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.load.engine.Resource;
import com.myntra.frisbee.Utilities.RecommendationPojo;
import com.myntra.frisbee.Utilities.repository.AppRepository;
import com.myntra.frisbee.external.ApiResponse;
import com.myntra.frisbee.model.ImageDetails;
import com.myntra.frisbee.model.PredictList;
import com.myntra.frisbee.repository.ImagesRepository;
import com.myntra.frisbee.repository.ImagesRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ImageViewModel extends ViewModel {

    ImagesRepository imagesRepository;
    ImagesRepositoryImpl imagesRepositoryImpl;
    AppRepository appRepository = AppRepository.getInstance();

    public ArrayList<String> videoSnapshots;
    public ArrayList<String> videoSnapsFileUri;
    public long totalSnapshots = 10;


    private ImagesRepositoryImpl repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<ApiResponse> responseLiveData = new MutableLiveData<>();


    public ImageViewModel(ImagesRepositoryImpl repository) {
        super();
        this.repository = repository;


    }

    public MutableLiveData<ApiResponse> loginResponse() {
        return responseLiveData;
    }

    /*
     * method to call normal login api with $(mobileNumber + password)
     * */
    public void hitLoginApi(String url) {

        disposables.add(repository.executeLogin(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((d) -> responseLiveData.setValue(ApiResponse.loading()))
                .subscribe(
                        result -> {
                            responseLiveData.setValue(ApiResponse.success(result));
                        },
                        throwable -> {
                            Log.i("KLKLKL",ApiResponse.error(throwable).error.toString());
                            responseLiveData.setValue(ApiResponse.error(throwable));
                        }
                ));

    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public void init() {
        imagesRepositoryImpl = ImagesRepositoryImpl.getInstance();
        imagesRepositoryImpl.setupFirebase();
    }

    public LiveData<Resource<List<ImageDetails>>> getImageListFromModel(String url, String imageUrl){
        return imagesRepositoryImpl.getImageListFromModel(url,imageUrl);
    }

    public Single<String> uploadImage(Uri selectedImageUri) {
        return imagesRepositoryImpl.insertImage(selectedImageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> uploadImage(Uri selectedImageUri, ImageDetails imageDetails) {
        return imagesRepositoryImpl.insertImage(selectedImageUri,imageDetails)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> getTempImageUrl(Uri selectedImageUri) {
        return imagesRepositoryImpl.getTempImageUrl(selectedImageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> getTempVideoSnapshotsUrl(Uri selectedImageUri) {
        return imagesRepositoryImpl.getTempVideoSnapshotsUrl(selectedImageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<Object> getImagesList() {
        return imagesRepositoryImpl.getImagesList();
    }

    public LiveData<RecommendationPojo> getRecommendations(String imageUrl){
        appRepository.getRecommendations(imageUrl);
        return appRepository.recommendationPojoMutableLiveData;
    }

    public LiveData<RecommendationPojo> getRecommendations(ArrayList<String> imageUrls){
        PredictList predictList = new PredictList();
        predictList.setUrlList(imageUrls);
        appRepository.getRecommendations(predictList);
        return appRepository.recommendationPojoMutableLiveData;
    }

    public LiveData<RecommendationPojo> getRecommendationsFromYoutubeUrl(String imageUrl){
        appRepository.getRecommendationFromYoutubeUrl(imageUrl);
        return appRepository.recommendationPojoMutableLiveData;
    }

}

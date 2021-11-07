package com.myntra.frisbee.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.bumptech.glide.load.engine.Resource;
import com.myntra.frisbee.model.ImageDetails;

import java.util.List;

import io.reactivex.Single;

public interface ImagesRepository {

    LiveData<Resource<List<ImageDetails>>> getImageListFromModel(String url, String imageUrl);

    void setupFirebase();

    LiveData<Object> getImagesList();

    Single<String> insertImage(Uri selectedImageUri);

    Single<String> getTempImageUrl(Uri selectedImageUri);

    Single<String> insertImage(Uri selectedImageUri, ImageDetails imageDetails);
}

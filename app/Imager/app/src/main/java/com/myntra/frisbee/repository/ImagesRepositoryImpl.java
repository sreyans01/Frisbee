package com.myntra.frisbee.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myntra.frisbee.external.ApiCallInterface;
import com.myntra.frisbee.model.ImageDetails;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ImagesRepositoryImpl implements ImagesRepository{

    ImageHttpInterface imageHttpInterface;

    private static ImagesRepositoryImpl instance;
    private StorageReference storageReference;
    private StorageReference storageReference_videosnapshots;
    private DatabaseReference databaseReference;
    private List<ImageDetails> imageDetailsList;

    private ApiCallInterface apiCallInterface;

    public ImagesRepositoryImpl(ApiCallInterface apiCallInterface) {
        this.apiCallInterface = apiCallInterface;
    }
    public ImagesRepositoryImpl() {
    }

    /*
     * method to call login api
     * */
    public Observable<Response> executeLogin(String url) {
        return apiCallInterface.login();
    }

    @Override
    public LiveData<Resource<List<ImageDetails>>> getImageListFromModel(String url, String imageUrl) {
        MutableLiveData<Resource<List<ImageDetails>>> data = null;
        //imageHttpInterface.getImageListFromModel(url,imageUrl).
        /*
        data = new MutableLiveData<>(Resource.Companion.loading(null));
        imageHttpInterface.getImageListFromModel(url, imageUrl).enqueue(new Callback<List<ImageDetails>>() {
            @Override
            public void onResponse(Call<List<ImageDetails>> call, Response<List<ImageDetails>> response) {
                if (response.isSuccessful()) {
                    data.postValue(Resource.Companion.success(response.body()));
                } else {
                    data.postValue(Resource.Companion.error("", null));
                }
            }

            @Override
            public void onFailure(Call<List<ImageDetails>> call, Throwable t) {
                Log.d("zzz", "error");
                t.printStackTrace();
                data.postValue(Resource.Companion.error("", null));
            }
        });

         */
        return data;
    }


    public static ImagesRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ImagesRepositoryImpl();
        }
        return instance;
    }



    public void setupFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference().child("uploads");
        storageReference_videosnapshots = FirebaseStorage.getInstance().getReference().child("videosnapshots");
        databaseReference = FirebaseDatabase.getInstance("https://frisbee-ed43b-default-rtdb.firebaseio.com/").getReference();
    }

    public LiveData<Object> getImagesList() {
        imageDetailsList = new ArrayList<>();
        return LiveDataReactiveStreams.fromPublisher(
                Flowable.create(emitter -> {
                    databaseReference.child("uploads").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            imageDetailsList.clear();
                            Log.e("Snap", "onDataChange: " + dataSnapshot);
                            for (DataSnapshot imagesSnapshot : dataSnapshot.getChildren()) {
                                try{
                                    ImageDetails imageDetails = imagesSnapshot.getValue(ImageDetails.class);
                                    //Add Only Men
//                                    if(imageDetails.getSuitableFor().length()>0)
//                                        imageDetailsList.add(imageDetails);
//                                    //Add Only Women
//                                    if(imageDetails.getSuitableFor().length()==0)
//                                        imageDetailsList.add(imageDetails);
//                                    //Add All
                                    imageDetailsList.add(imageDetails);
                                    Log.e("Repository", imageDetails.getImageUrl());
                                }catch (Exception e){}
                            }
                            emitter.onNext(imageDetailsList);
                            emitter.onComplete();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            emitter.isCancelled();
                        }
                    });
                }, BackpressureStrategy.BUFFER)
                        .subscribeOn(Schedulers.io())
        );
    }

    public Single<String> insertImage(Uri selectedImageUri) {
        return Single.create(emitter -> {
            if (selectedImageUri != null) {
                String imageName = System.currentTimeMillis() + "uploadedImage";
                StorageReference fileRef = storageReference.child(imageName);
                fileRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ImageDetails imageDetails = new ImageDetails(imageName, uri.toString());
                                String uploadId = databaseReference.child("uploads").push().getKey();
                                databaseReference.child("uploads").child(uploadId).setValue(imageDetails);
                                emitter.onSuccess("Image Uploaded");
                            }
                        });
                    }
                })
                        .addOnFailureListener(e -> {
                            emitter.onError(new Throwable("Error uploading image "+e.getCause()));
                        });
            } else {
                emitter.onError(new Throwable("Error uploading image "));
            }
        });
    }

    public Single<String> insertImage(Uri selectedImageUri, ImageDetails imageDetails) {
        return Single.create(emitter -> {
            if (selectedImageUri != null) {
                String imageName = System.currentTimeMillis() + "uploadedImage";
                StorageReference fileRef = storageReference.child(imageName);
                fileRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageDetails.setImageUrl(uri.toString());
                                String uploadId = databaseReference.child("uploads").push().getKey();
                                databaseReference.child("uploads").child(uploadId).setValue(imageDetails);
                                emitter.onSuccess("Image Uploaded");
                            }
                        });
                    }
                })
                        .addOnFailureListener(e -> {
                            emitter.onError(new Throwable("Error uploading image "+e.getCause()));
                        });
            } else {
                emitter.onError(new Throwable("Error uploading image "));
            }
        });
    }

    public Single<String> getTempImageUrl(Uri selectedImageUri) {
        return Single.create(emitter -> {
            if (selectedImageUri != null) {
                String imageName =  "tempImage"+System.currentTimeMillis();
                StorageReference fileRef = storageReference.child(imageName);
                fileRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                emitter.onSuccess(uri.toString());
                            }
                        });
                    }
                })
                        .addOnFailureListener(e -> {
                            emitter.onError(new Throwable("Error uploading image "+e.getCause()));
                        });
            } else {
                emitter.onError(new Throwable("Error uploading image "));
            }
        });
    }

    public Single<String> getTempVideoSnapshotsUrl(Uri selectedImageUri) {
        return Single.create(emitter -> {
            if (selectedImageUri != null) {
                String imageName =  "tempImage"+System.currentTimeMillis();
                StorageReference fileRef = storageReference_videosnapshots.child(imageName);
                fileRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                emitter.onSuccess(uri.toString());
                            }
                        });
                    }
                })
                        .addOnFailureListener(e -> {
                            emitter.onError(new Throwable("Error uploading image "+e.getCause()));
                        });
            } else {
                emitter.onError(new Throwable("Error uploading image "));
            }
        });
    }


}

package com.myntra.frisbee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.google.gson.JsonElement;
import com.myntra.frisbee.Utilities.RecommendationPojo;
import com.myntra.frisbee.adapter.ItemReyclerAdapter;
import com.myntra.frisbee.databinding.ActivityMainBinding;
import com.myntra.frisbee.external.ApiResponse;
import com.myntra.frisbee.external.AppComponent;
import com.myntra.frisbee.external.AppModule;
import com.myntra.frisbee.external.DaggerAppComponent;
import com.myntra.frisbee.external.GetRecommendations;
import com.myntra.frisbee.external.GetRecommendationsCallback;
import com.myntra.frisbee.external.MyApplication;
import com.myntra.frisbee.external.UtilsModule;
import com.myntra.frisbee.external.ViewModelFactory;
import com.myntra.frisbee.model.ImageDetails;
import com.myntra.frisbee.repository.ImageHttpInterface;
import com.myntra.frisbee.viewmodel.ImageViewModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityMainBinding binding;
    private Context context = MainActivity.this;
    List<ImageDetails> imageDetailsList = new ArrayList<>();
    private ImageViewModel viewModel;
    private ItemReyclerAdapter adapter;

    public static final int PICK_IMAGE_REQUEST = 120;

    private ProgressDialog progressDialog;

    @Inject
    ViewModelFactory viewModelFactory;

    private AppComponent appComponent;

    private CompositeDisposable firebaseUploadDisposable = new CompositeDisposable();
    private Uri selectedImageUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).utilsModule(new UtilsModule()).build();

        appComponent.doInjection(this);

        progressDialog = new ProgressDialog(context);
        initViewModel();
        handleListeners();
        getImagesList();
        initRecyclerView();

        viewModel.loginResponse().observe(this, this::consumeResponse);


    }

    private void consumeResponse(ApiResponse apiResponse) {

        switch (apiResponse.status) {

            case LOADING:
                progressDialog.show();
                break;

            case SUCCESS:
                progressDialog.dismiss();
                renderSuccessResponse(apiResponse.data);
                break;

            case ERROR:
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,getResources().getString(R.string.errorString), Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    /*
     * method to handle success response
     * */
    private void renderSuccessResponse(Response response) {
        if (response.isSuccessful()) {
            Log.i("KLKLKL", response.toString());
        } else {
            Toast.makeText(MainActivity.this,getResources().getString(R.string.errorString), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageSearch:
                chooseImage();
                break;
            case R.id.reset:
                binding.recommendationsTV.setVisibility(View.GONE);
                Collections.shuffle(imageDetailsList);
                adapter.setList(imageDetailsList);
                adapter.notifyDataSetChanged();
        }
    }
    private void handleListeners(){

        binding.imageSearch.setOnClickListener(this);
        binding.reset.setOnClickListener(this);
    }

    private void initViewModel(){
        viewModel = ViewModelProviders.of(MainActivity.this,viewModelFactory).get(ImageViewModel.class);
        viewModel.init();
    }

    public void getImagesList() {
        try {
            Observer<Object> mObserver = new Observer<Object>() {
                @Override
                public void onChanged(Object o) {
                    imageDetailsList.clear();
                    for (ImageDetails id : (ArrayList<ImageDetails>) o) {
                        imageDetailsList.add(id);
                    }
                    adapter.notifyDataSetChanged();
                }
            };
            viewModel.getImagesList().observe((LifecycleOwner) context, mObserver);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to fetch Images, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initRecyclerView() {
        try {

            adapter = new ItemReyclerAdapter(context, imageDetailsList);
            binding.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            binding.recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to initiate grid, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityIfNeeded(Intent.createChooser(i, "Select picture or video "), PICK_IMAGE_REQUEST);
//
    }

    public void requestPermission(int request_code) {

        if (request_code == PICK_IMAGE_REQUEST)
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            selectedImageUri = data.getData();
            uploadImageForRecommendation();
        }
    }
    private void requestRecommendations( String imageUrl){

        imageUrl = imageUrl.replace("%","()");
        viewModel.getRecommendations(imageUrl).observe((LifecycleOwner) context, new Observer<RecommendationPojo>() {
            @Override
            public void onChanged(RecommendationPojo recommendationPojo) {
                progressDialog.dismiss();
                try {
                    Log.i("KLKLKL", recommendationPojo.getRecommendation().get(0));
                    List<ImageDetails> newImageDetailsList = new ArrayList<>();
                    for(String s : recommendationPojo.getRecommendation()){
                        for(ImageDetails imageDetails : imageDetailsList){
                            if(imageDetails.getImageName().compareTo(s)==0){
                                newImageDetailsList.add(imageDetails);
                            }
                        }
                    }
                    binding.recommendationsTV.setVisibility(View.VISIBLE);
                    adapter.setList(newImageDetailsList);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }
        });

    }

    public void uploadImageForRecommendation() {
        progressDialog.show();
        progressDialog.setMessage("Importing image...");
        Single<String> firebaseUploadSingleObserver = viewModel.getTempImageUrl(selectedImageUri);
        firebaseUploadSingleObserver.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                firebaseUploadDisposable.add(d);
            }

            @Override
            public void onSuccess(String s) {
                progressDialog.setMessage("Processing...");
                requestRecommendations(s);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        firebaseUploadDisposable.clear();
        super.onDestroy();
    }
}
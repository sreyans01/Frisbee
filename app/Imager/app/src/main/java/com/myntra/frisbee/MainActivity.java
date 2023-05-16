package com.myntra.frisbee;

import androidx.annotation.NonNull;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonElement;
import com.myntra.frisbee.Utilities.Constants;
import com.myntra.frisbee.Utilities.RecommendationPojo;
import com.myntra.frisbee.adapter.ItemReyclerAdapter;
import com.myntra.frisbee.databinding.ActivityMainBinding;
import com.myntra.frisbee.external.ApiResponse;
import com.myntra.frisbee.external.AppComponent;
import com.myntra.frisbee.external.AppModule;
import com.myntra.frisbee.external.Constant;
import com.myntra.frisbee.external.DaggerAppComponent;
import com.myntra.frisbee.external.GetRecommendations;
import com.myntra.frisbee.external.GetRecommendationsCallback;
import com.myntra.frisbee.external.MyApplication;
import com.myntra.frisbee.external.UtilsModule;
import com.myntra.frisbee.external.ViewModelFactory;
import com.myntra.frisbee.model.ImageDetails;
import com.myntra.frisbee.repository.ImageHttpInterface;
import com.myntra.frisbee.viewmodel.ImageViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityMainBinding binding;
    private Context context = MainActivity.this;
    List<ImageDetails> imageDetailsList = new ArrayList<>();
    private ImageViewModel viewModel;
    private ItemReyclerAdapter adapter;

    public static final int PICK_IMAGE_REQUEST = 120;
    public static final int VIDEO_PICK_IMAGE_REQUEST = 121;

    private ProgressDialog progressDialog;

    @Inject
    ViewModelFactory viewModelFactory;

    private AppComponent appComponent;

    private CompositeDisposable firebaseUploadDisposable = new CompositeDisposable();
    private Uri selectedMediaUri;

    private BottomSheetDialog addLinkBottomSheet;




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
                break;
            case R.id.addLink:
                addLinkBottomSheet = openAddLinkBottomSheet(context);
                break;
        }
    }
    private void handleListeners(){

        binding.imageSearch.setOnClickListener(this);
        binding.reset.setOnClickListener(this);
        binding.addLink.setOnClickListener(this);
    }

    public BottomSheetDialog openAddLinkBottomSheet(Context context){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_addlink);
        EditText editText = bottomSheetDialog.findViewById(R.id.editText);
        Button submitBtn = bottomSheetDialog.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText!=null){
                    if(TextUtils.isEmpty(editText.getText())){
                        Toast.makeText(context,"Please enter a url",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(isValidYoutubeUrl(editText.getText().toString())){
                        Log.i("UIUIUI","valid url");
                        requestRecommendationsFromYoutubeUrl(editText.getText().toString());
                        bottomSheetDialog.dismiss();
                    }else {
                        Log.i("UIUIUI","invalid url");
                        Toast.makeText(context,"Please enter a valid youtube url",Toast.LENGTH_LONG).show();
                    }



                    return;
                }
            }
        });
        bottomSheetDialog.show();
        return bottomSheetDialog;
    }

    private boolean isValidYoutubeUrl(String url){
        if(url.startsWith("https://youtu.be") || url.startsWith("https://www.youtube"))
            return true;
        return false;
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
        i.setType("image/*,video/*");
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
            String checkMedia = data.getDataString();
            selectedMediaUri = data.getData();
            if(checkMedia.contains("image")){
                // It is image
                uploadImageForRecommendation();
            } else {
                progressDialog.show();
                progressDialog.setMessage("Please wait while we get the things ready for you...");
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        getVideoSnapshotsUrls(selectedMediaUri, new AllSnapshotsUploadedCallback() {
                            @Override
                            public void OnUploadingFinished() {
                                progressDialog.dismiss();
                                Intent i = new Intent(context, VideoSnapshotsActivity.class);
                                i.putStringArrayListExtra(Constants.ImagesList,viewModel.videoSnapshots);
                                i.putStringArrayListExtra(Constants.FileUriList,viewModel.videoSnapsFileUri);
                                startActivityIfNeeded(i,VIDEO_PICK_IMAGE_REQUEST);
                            }
                        });
                    }
                });


            }
        } else if(requestCode == VIDEO_PICK_IMAGE_REQUEST && resultCode == RESULT_OK){

            progressDialog.show();
            progressDialog.setMessage("Processing...");
            Log.i("YPYPYP","Yoyo");
            ArrayList<String> selectedImagesList = data.getStringArrayListExtra(Constants.SelectedImagesList);
            requestRecommendations(selectedImagesList);


        }
    }
    private void requestRecommendations(String imageUrl){

        //imageUrl = imageUrl.replace("%","()");
        viewModel.getRecommendations(imageUrl).observe((LifecycleOwner) context, new Observer<RecommendationPojo>() {
            @Override
            public void onChanged(RecommendationPojo recommendationPojo) {
                progressDialog.dismiss();
                try {
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
                    resetVariables();
                }catch (Exception e){}
            }
        });

    }

    private void requestRecommendationsFromYoutubeUrl(String imageUrl){

        //imageUrl = imageUrl.replace("%","()");
        progressDialog.show();
        progressDialog.setMessage("Processing...");
        Log.i("UIUIUI","Request youtube recomm");
        viewModel.getRecommendationsFromYoutubeUrl(imageUrl).observe((LifecycleOwner) context, new Observer<RecommendationPojo>() {
            @Override
            public void onChanged(RecommendationPojo recommendationPojo) {
                progressDialog.dismiss();
                try {
                    Log.i("UIUIUI","Request success");
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


    private void resetVariables(){
        selectedMediaUri = null;

    }

    private void requestRecommendations(ArrayList<String> imageUrls){

        StringBuilder s = new StringBuilder("");
        for(String url : imageUrls){
            s.append("\""+url+"\""+",");

        }
        Log.i("OPOPOP",s.toString());

        viewModel.getRecommendations(imageUrls).observe((LifecycleOwner) context, new Observer<RecommendationPojo>() {
            @Override
            public void onChanged(RecommendationPojo recommendationPojo) {
                progressDialog.dismiss();
                try {
                    List<ImageDetails> newImageDetailsList = new ArrayList<>();
                    for(String s : recommendationPojo.getRecommendation()){
                        for(ImageDetails imageDetails : imageDetailsList){
                            if(imageDetails.getImageName().compareTo(s)==0){
                                newImageDetailsList.add(imageDetails);
                            }
                        }
                    }
                    Log.i("YPYPYP","Yoyo 22");
                    binding.recommendationsTV.setVisibility(View.VISIBLE);
                    adapter.setList(newImageDetailsList);
                    adapter.notifyDataSetChanged();
                } catch (Exception e){}
            }
        });

    }


    public void uploadImageForRecommendation() {
        progressDialog.show();
        progressDialog.setMessage("Importing image...");
        Single<String> firebaseUploadSingleObserver = viewModel.getTempImageUrl(selectedMediaUri);
        firebaseUploadSingleObserver.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                firebaseUploadDisposable.add(d);
            }

            @Override
            public void onSuccess(String s) {
                progressDialog.show();
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

    public interface AllSnapshotsUploadedCallback {
        void OnUploadingFinished();
    }
    private void getVideoSnapshotsUrls(Uri videoUri, AllSnapshotsUploadedCallback callback) {
        viewModel.videoSnapshots = new ArrayList<>();
        viewModel.videoSnapsFileUri = new ArrayList<>();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context,videoUri);
        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));


        int stepCount = 2000000; // in microseconds
        viewModel.totalSnapshots = (duration*1000-stepCount)/stepCount;

        for(long i=stepCount;i<duration*1000-stepCount;i+=stepCount){

                Bitmap bitmap = retriever.getFrameAtTime(i,MediaMetadataRetriever.OPTION_CLOSEST);
            try{
                uploadVideoSnapshots(getImageUri(context,bitmap), callback);
            }catch (Exception e){
                viewModel.totalSnapshots--;
            }
        }
        retriever.release();
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        long time = System.currentTimeMillis();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "imager_"+String.valueOf(time), null);
        return Uri.parse(path);
    }

    private void uploadVideoSnapshots(Uri uri, AllSnapshotsUploadedCallback callback){
        viewModel.videoSnapsFileUri.add(uri.toString());
        Single<String> firebaseUploadSingleObserver = viewModel.getTempVideoSnapshotsUrl(uri);
        firebaseUploadSingleObserver.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                firebaseUploadDisposable.add(d);
            }

            @Override
            public void onSuccess(String s) {
                viewModel.videoSnapshots.add(s);
                if(viewModel.totalSnapshots == viewModel.videoSnapshots.size()){
                    callback.OnUploadingFinished();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getFilePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }

    private void deleteFile(Uri imageUri){
        File fdelete = new File(getFilePath(imageUri));

        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" );
            } else {
                System.out.println("file not Deleted :");
            }
        }
    }
}
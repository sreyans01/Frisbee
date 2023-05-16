package com.myntra.frisbee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.util.CollectionUtils;
import com.myntra.frisbee.Utilities.Constants;
import com.myntra.frisbee.adapter.VideoSnapshotsRecyclerAdapter;
import com.myntra.frisbee.databinding.ActivityVideoSnapshotsBinding;
import com.myntra.frisbee.event.SelectSnapshotEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoSnapshotsActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityVideoSnapshotsBinding binding;
    private List<String> imageUrls = new ArrayList<>();
    private List<String> fileUris = new ArrayList<>();
    private Context context = VideoSnapshotsActivity.this;
    private VideoSnapshotsRecyclerAdapter adapter;

    private ArrayList<String> selectedImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_snapshots);

        EventBus.getDefault().register(this);

       adapter = new VideoSnapshotsRecyclerAdapter(context);
       imageUrls = getIntent().getStringArrayListExtra(Constants.ImagesList);
       fileUris = getIntent().getStringArrayListExtra(Constants.FileUriList);

        if(imageUrls!=null){
            if(!CollectionUtils.isEmpty(imageUrls)){
                adapter.setList(imageUrls);
                adapter.notifyDataSetChanged();

            }

        }
        binding.recyclerView.setAdapter(adapter);

        handleListeners();


    }

    private void handleListeners(){

        binding.backButton.setOnClickListener(this);
        
        binding.nextButton.setOnClickListener(this);

        binding.selectAll.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
            case R.id.next_button:
                Intent i = new Intent();
                i.putStringArrayListExtra(Constants.SelectedImagesList,selectedImageUrls);
                setResult(RESULT_OK, i);
                finish();
                break;
            case R.id.selectAll:
                adapter.selectAllImages(true);

                break;
        }
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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectSnapshotEvent(SelectSnapshotEvent selectedSnapshot) {
        if (selectedSnapshot != null) {
            if(selectedSnapshot.isChecked()){
                selectedImageUrls.add(imageUrls.get(selectedSnapshot.getPosition()));
            } else {
                selectedImageUrls.remove(imageUrls.get(selectedSnapshot.getPosition()));
            }
        }
    }
}
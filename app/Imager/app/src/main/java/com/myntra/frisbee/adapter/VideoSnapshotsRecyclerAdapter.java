package com.myntra.frisbee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.myntra.frisbee.R;
import com.myntra.frisbee.databinding.ItemProductOuterBinding;
import com.myntra.frisbee.databinding.ItemVideoSnapshotBinding;
import com.myntra.frisbee.model.ImageDetails;
import com.myntra.frisbee.viewholder.ItemViewHolder;
import com.myntra.frisbee.viewholder.VideoSnapshotsViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VideoSnapshotsRecyclerAdapter extends RecyclerView.Adapter<VideoSnapshotsViewHolder> {

    private List<String> imageUrls = new ArrayList<>();
    private Context context;

    private boolean isSelectAll = false;

    public VideoSnapshotsRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public VideoSnapshotsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemVideoSnapshotBinding itemVideoSnapshotBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_video_snapshot, parent, false);
        return new VideoSnapshotsViewHolder(itemVideoSnapshotBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoSnapshotsViewHolder holder, int position) {

        ((VideoSnapshotsViewHolder) holder).onBind(context, imageUrls.get(position), isSelectAll);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void setList(List<String> imageUrls){
        this.imageUrls = new ArrayList<>(imageUrls);
    }

    public void selectAllImages(boolean isSelectAll){
        this.isSelectAll = isSelectAll;
        notifyDataSetChanged();
        this.isSelectAll = false;
    }
}


package com.myntra.frisbee.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.myntra.frisbee.R;
import com.myntra.frisbee.databinding.ItemProductOuterBinding;
import com.myntra.frisbee.databinding.ItemVideoSnapshotBinding;
import com.myntra.frisbee.event.SelectSnapshotEvent;
import com.myntra.frisbee.model.ImageDetails;

import org.greenrobot.eventbus.EventBus;

public class VideoSnapshotsViewHolder extends RecyclerView.ViewHolder {

    ItemVideoSnapshotBinding binding;
    private Context context;

    public VideoSnapshotsViewHolder(@NonNull ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (ItemVideoSnapshotBinding) binding;
        binding.setLifecycleOwner((LifecycleOwner) context);

        ((ItemVideoSnapshotBinding) binding).parent.setOnClickListener(v -> {
            boolean isChecked = ((ItemVideoSnapshotBinding) binding).isChecked.getVisibility()==View.VISIBLE ? true : false;
            ((ItemVideoSnapshotBinding) binding).isChecked.setVisibility(isChecked?View.GONE:View.VISIBLE);
            ((ItemVideoSnapshotBinding) binding).image.setAlpha(isChecked?1f:0.5f);

            EventBus.getDefault().post(new SelectSnapshotEvent(getAdapterPosition(),!isChecked));
        });

    }

    public void onBind(Context context, String imageUrl, boolean isSelectAll) {

        Glide.with(context).load(imageUrl).into(binding.image);

        if(isSelectAll){
            binding.isChecked.setVisibility(View.VISIBLE);
            binding.image.setAlpha(0.5f);

            //EventBus.getDefault().post(new SelectSnapshotEvent(getAdapterPosition(),true));
        }

    }

}

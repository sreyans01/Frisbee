package com.myntra.frisbee.viewholder;

import android.content.Context;
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
import com.myntra.frisbee.model.ImageDetails;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    ItemProductOuterBinding binding;
    private Context context;

    public ItemViewHolder(@NonNull ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (ItemProductOuterBinding) binding;
        binding.setLifecycleOwner((LifecycleOwner) context);


    }

    public void onBind(Context context, ImageDetails imageDetails) {
        try {
            // Circular Progress Drawable to show while Glide loads image
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(10f);
            circularProgressDrawable.setCenterRadius(48f);
            circularProgressDrawable.setColorSchemeColors(Color.BLACK);
            circularProgressDrawable.start();

            // Set the image using Glide library
            Glide.with(context)
                    .load(imageDetails.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(circularProgressDrawable))
                    .apply(new RequestOptions()
                            .fitCenter())
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_broken_image))
                    .into(binding.imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to load Images, please try again later.", Toast.LENGTH_SHORT).show();
        }

        binding.mrp.setText("₹"+imageDetails.getMrp());
        binding.discountedPrice.setText("₹"+imageDetails.getDiscountedPrice());
        if(imageDetails.getSuitableFor().length()==0){
            binding.idealFor.setText("W");
        } else {
            binding.idealFor.setText("M");
        }
        binding.rating.setText(imageDetails.getRating());
        binding.title.setText(imageDetails.getDisplayName());
        
        int mrp = Integer.parseInt(imageDetails.getMrp());
        int discountedPrice = Integer.parseInt(imageDetails.getDiscountedPrice());

        int discountPercent = ((mrp - discountedPrice)*100)/mrp;

        if(discountPercent<0)
            discountPercent *=-1;

        if(discountPercent<3){
            binding.discountPercentage.setVisibility(View.GONE);
        }else{
            binding.discountPercentage.setText(String.valueOf(discountPercent)+"% OFF");
        }


    }
}

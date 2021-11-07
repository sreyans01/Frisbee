package com.myntra.frisbee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.myntra.frisbee.R;
import com.myntra.frisbee.databinding.ItemProductOuterBinding;
import com.myntra.frisbee.model.ImageDetails;
import com.myntra.frisbee.viewholder.ItemViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemReyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<ImageDetails> imageDetailsList = new ArrayList<>();
    private Context context;

    public ItemReyclerAdapter(Context context, List<ImageDetails> imageDetailsList) {
        this.imageDetailsList = imageDetailsList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductOuterBinding itemCommentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_product_outer, parent, false);
        return new ItemViewHolder(itemCommentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemViewHolder holder, int position) {

        ((ItemViewHolder) holder).onBind(context, imageDetailsList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageDetailsList.size();
    }

    public void setList(List<ImageDetails> imageDetailsList){
        this.imageDetailsList = new ArrayList<>(imageDetailsList);
    }
}

package com.myntra.frisbee.external;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.myntra.frisbee.repository.ImagesRepository;
import com.myntra.frisbee.repository.ImagesRepositoryImpl;
import com.myntra.frisbee.viewmodel.ImageViewModel;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private ImagesRepositoryImpl repository;

    @Inject
    public ViewModelFactory(ImagesRepositoryImpl repository) {
        this.repository = repository;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ImageViewModel.class)) {
            return (T) new ImageViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}
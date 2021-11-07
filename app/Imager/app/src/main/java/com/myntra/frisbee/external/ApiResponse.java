package com.myntra.frisbee.external;

import com.google.gson.JsonElement;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import retrofit2.Response;

import static com.myntra.frisbee.external.Status.ERROR;
import static com.myntra.frisbee.external.Status.LOADING;
import static com.myntra.frisbee.external.Status.SUCCESS;

public class ApiResponse {

    public final Status status;

    @Nullable
    public final Response data;

    @Nullable
    public final Throwable error;

    private ApiResponse(Status status, @Nullable Response data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static ApiResponse loading() {
        return new ApiResponse(LOADING, null, null);
    }

    public static ApiResponse success(@NonNull Response data) {
        return new ApiResponse(SUCCESS, data, null);
    }

    public static ApiResponse error(@NonNull Throwable error) {
        return new ApiResponse(ERROR, null, error);
    }

}
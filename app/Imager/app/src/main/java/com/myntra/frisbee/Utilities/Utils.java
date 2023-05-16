package com.myntra.frisbee.Utilities;

import com.myntra.frisbee.Utilities.Firebase.FirebaseRemoteConfigHelper;

import javax.inject.Inject;

public class Utils
{
    private Utils(){}

    public static String BaseUrl="http://192.168.29.81:8001/";

    public static ClientApi getClientAPI(FirebaseRemoteConfigHelper firebaseRemoteConfigHelper)
    {
        return RetrofitClient.getClient(firebaseRemoteConfigHelper.getLocalHostAddress()).create(ClientApi.class);
    }
}
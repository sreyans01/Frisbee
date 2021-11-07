package com.myntra.frisbee.Utilities;

public class Utils
{
    private Utils(){}

    public static String BaseUrl="http://192.168.29.81:8001/";

    public static ClientApi getClientAPI()
    {
        return RetrofitClient.getClient(BaseUrl).create(ClientApi.class);
    }
}
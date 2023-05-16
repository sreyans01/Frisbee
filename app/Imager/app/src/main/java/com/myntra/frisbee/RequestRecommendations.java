package com.myntra.frisbee;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.myntra.frisbee.repository.ImageHttpInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestRecommendations extends AsyncTask<String,Void,String> {
    public JSONArray jsonArray;
    public ImageHttpInterface imageHttpInterface;
    private List<String> predictedList;

    public RequestRecommendations(Context context, ImageHttpInterface imageHttpInterface) {
        this.imageHttpInterface = imageHttpInterface;
    }


    @Override
    protected String doInBackground(String... urls) {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            String dataToBePushed = urls[1];
            String newdata = dataToBePushed.replace("%","()");
            url = new URL(urls[0]+"?url="+newdata);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");

            //urlConnection.setRequestProperty("Content-Type","application/json");
//            urlConnection.setRequestProperty("Authorization","Bearer "+urls[1]);
//            urlConnection.setRequestProperty("ML-Instance-ID","e8b2e1c3-9068-458f-92ff-7b1b413da3af");
            urlConnection.setDefaultUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);





            OutputStream os = urlConnection.getOutputStream();
            os.write(newdata.getBytes());
            os.flush();
            os.close();

            InputStream in = (urlConnection.getInputStream());
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();
            while(data != -1){
                char current = (char)data;
                result += current;
                data = reader.read();

            }
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result==null) {

            return;

        }
        try {
            JSONObject jsonObject = new JSONObject(result);

            String weatherInfo = jsonObject.getString("recommendation");


            predictedList.add(weatherInfo);
            imageHttpInterface.OnGettingImageRecommendationList(predictedList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

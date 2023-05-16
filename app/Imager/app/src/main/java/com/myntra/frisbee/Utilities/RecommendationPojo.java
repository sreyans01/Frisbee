package com.myntra.frisbee.Utilities;


import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecommendationPojo {


    String message = "";
    private List<String> recommendation;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getRecommendation() {
        return recommendation;
    }


    public void setRecommendation(List<String> recommendation) {
        this.recommendation = recommendation;
    }

}

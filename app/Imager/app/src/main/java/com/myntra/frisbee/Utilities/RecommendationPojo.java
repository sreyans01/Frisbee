package com.myntra.frisbee.Utilities;


import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecommendationPojo {


    private List<String> recommendation;


    public List<String> getRecommendation() {
        return recommendation;
    }


    public void setRecommendation(List<String> recommendation) {
        this.recommendation = recommendation;
    }

}

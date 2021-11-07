package com.myntra.frisbee.external;

import org.json.JSONArray;

public interface GetRecommendationsCallback {
    void OnGettingRecommendationData(JSONArray jsonArray);
}

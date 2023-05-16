package com.myntra.frisbee.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PredictList implements Serializable {

    ArrayList<String> url_list = new ArrayList<>();


    public PredictList(){

    }

    public ArrayList<String> getUrlList() {
        return url_list;
    }

    public void setUrlList(ArrayList<String> url_list) {
        this.url_list = url_list;
    }
}

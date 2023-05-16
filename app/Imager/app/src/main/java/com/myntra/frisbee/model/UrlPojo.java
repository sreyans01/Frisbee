package com.myntra.frisbee.model;

import java.io.Serializable;

public class UrlPojo implements Serializable {

    private String url;

    public UrlPojo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

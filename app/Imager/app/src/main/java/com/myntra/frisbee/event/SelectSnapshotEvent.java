package com.myntra.frisbee.event;

public class SelectSnapshotEvent {

    private int position;
    private String url = "";

    private boolean isChecked;
    public SelectSnapshotEvent(int position, boolean isChecked){
        this.position = position;
        this.isChecked = isChecked;
    }
    public SelectSnapshotEvent(String url, boolean isChecked){
        this.url = url;
        this.isChecked = isChecked;
    }
    public SelectSnapshotEvent(int position, String url, boolean isChecked){
        this.position = position;
        this.url = url;
        this.isChecked = isChecked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

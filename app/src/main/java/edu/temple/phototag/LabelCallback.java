package edu.temple.phototag;

public interface LabelCallback {
    void onCallback(String value);
}
//needed for catching asynchronous returns from firebase image labeling
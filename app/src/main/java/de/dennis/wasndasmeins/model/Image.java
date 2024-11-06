package de.dennis.wasndasmeins.model;

import android.os.Parcelable;

public class Image {
    private String imageUrl;
    private boolean done;

    public String getFilename() {
        return imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}

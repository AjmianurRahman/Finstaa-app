package com.example.myapplication.model;

import android.net.Uri;

public class GalleryImages {
    public Uri imageUri;


    public GalleryImages() {
    }

    public GalleryImages(Uri imageUri) {
        this.imageUri = imageUri;

    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }


}

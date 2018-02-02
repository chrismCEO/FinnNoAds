package com.chrismsolutions.finnnoads;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Christian Myrvold on 02.02.2018.
 */

public class FinnAd
    implements Serializable
{
    private String title, location, id;
    private int price;
    private String imageURL;
    private boolean favorite;
    transient private Bitmap image;

    public FinnAd(String mTitle, String mLocation, int mPrice, String mImageUrl, String mId)
    {
        title = mTitle;
        location = mLocation;
        price = mPrice;
        imageURL = mImageUrl;
        id = mId;
        favorite = false;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setImageURL(String imageURL)
    {
        this.imageURL = imageURL;
    }

    public void setFavorite(boolean favorite)
    {
        this.favorite = favorite;
    }

    public void setImage(Bitmap image)
    {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getId()
    {
        return id;
    }

    public String getImageURL()
    {
        return imageURL;
    }

    public boolean isFavorite()
    {
        return favorite;
    }

    public Bitmap getImage()
    {
        return image;
    }
}

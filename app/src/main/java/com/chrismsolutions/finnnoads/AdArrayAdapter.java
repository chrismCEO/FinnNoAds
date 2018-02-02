package com.chrismsolutions.finnnoads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrismsolutions.finnnoads.data.AdUtils;
import com.chrismsolutions.finnnoads.data.DownloadImageTask;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Christian Myrvold on 02.02.2018.
 */

public class AdArrayAdapter extends ArrayAdapter<FinnAd>
{

    private String price = "%s,-";

    public AdArrayAdapter(@NonNull Context context, ArrayList<FinnAd> ads)
    {
        super(context, 0, ads);
    }

    /**
     * Recycle list items with the FinnAd object in @position, keeping the memory usage down.
     * This is also where we download and show the image. If we're not connected to the internet,
     * only show images connected with favorited ads, as these are cached.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View adView = convertView;

        if (adView == null)
        {
            adView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_ad, parent, false);
        }

        final FinnAd finnAd = getItem(position);

        if (finnAd != null)
        {
            //set the ad title
            TextView titleView = adView.findViewById(R.id.ad_title_list_item);
            titleView.setText(finnAd.getTitle());

            //set the ad location
            TextView locationView = adView.findViewById(R.id.ad_location_list_item);
            locationView.setText(finnAd.getLocation());

            //set the ad item price
            TextView priceView = adView.findViewById(R.id.ad_price_list_item);
            priceView.setText(String.format(price, NumberFormat.getInstance().format(finnAd.getPrice())));

            //let the background task bind the image to the imageView
            final ImageView imageView = adView.findViewById(R.id.adImage_list_item);

            if (finnAd.getImage() == null)
            {
                //no cached image, download the image
                DownloadImageTask downloadImageTask = new DownloadImageTask(imageView);
                downloadImageTask.execute(finnAd.getImageURL());
            }
            else
            {
                //use the cached image
                imageView.setImageBitmap(finnAd.getImage());
            }

            //set onClicked logic for favorite image, for memory purposes
            final ImageView favoriteView = adView.findViewById(R.id.ad_favorite);
            favoriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = finnAd.getId();
                    ImageView favorite = (ImageView)view;
                    MainActivity mainActivity = (MainActivity)getContext();

                    if (!finnAd.isFavorite())
                    {
                        //favorite this ad
                        setFavorite(favorite);
                        mainActivity.addToFavorites(finnAd);
                    }
                    else
                    {
                        //unfavorite this ad
                        clearFavorite(favorite);
                        mainActivity.removeFromFavorites(finnAd);
                    }
                    finnAd.setFavorite(!finnAd.isFavorite());

                    if (finnAd.isFavorite())
                    {
                        //store the image for this favorited ad
                        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        finnAd.setImage(image);
                    }
                    else
                    {
                        finnAd.setImage(null);
                    }
                }
            });

            if (finnAd.isFavorite())
            {
                //favorite this ad by coloring the heart red
                setFavorite(favoriteView);
            }
            else
            {
                clearFavorite(favoriteView);
            }
        }
        return adView;
    }

    private void setFavorite(ImageView imageView)
    {
        imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
    }

    private void clearFavorite(ImageView imageView)
    {
        imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));
    }

}

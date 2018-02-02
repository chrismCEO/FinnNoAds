package com.chrismsolutions.finnnoads.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Christian Myrvold on 02.02.2018.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
    private ImageView imageView;

    public DownloadImageTask(ImageView mImageView)
    {
        imageView = mImageView;
    }

    /**
     * Download the image from the URL in the background
     * @param urls
     * @return
     */
    @Override
    protected Bitmap doInBackground(String... urls)
    {
        String url = urls[0];
        Bitmap image = AdUtils.fetchImage(url);

        return image;
    }

    /**
     * When the image is downloaded we bind the ImageView to the image
     * @param bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        imageView.setImageBitmap(bitmap);
    }
}

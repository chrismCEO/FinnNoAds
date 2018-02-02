package com.chrismsolutions.finnnoads.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.chrismsolutions.finnnoads.FinnAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Christian Myrvold on 02.02.2018.
 */

public class AdUtils
{
    private static final String JSON_FINN_QUERY = "https://gist.githubusercontent.com/3lvis/3799feea005ed49942dcb56386ecec2b/raw/63249144485884d279d55f4f3907e37098f55c74/discover.json";
    private static final String JSON_FINN_IMAGE_URL = "https://images.finncdn.no/dynamic/480x360c/";
    private static String LOG_TAG = AdUtils.class.getName();
    private static final int HTTP_READ_TIMEOUT = 10000;
    private static final int HTTP_CONNECT_TIMEOUT = 15000;
    private static int JSON_FINN_RESPONSE_CODE_OK = 200;
    private static final String CHARSET_UTF_8 = "UTF-8";
    private static String JSON_ITEMS = "items";
    private static String JSON_IMAGE = "image";
    private static String JSON_IMAGE_URL = "url";
    private static String JSON_PRICE = "price";
    private static String JSON_PRICE_VALUE = "value";
    private static String JSON_TITLE = "description";
    private static String JSON_LOCATION = "location";
    private static String JSON_ID = "id";

    /**
     * Connect to the URL and parse the JSON response
     * @return
     */
    static ArrayList<FinnAd> fetchJSONData()
    {
        ArrayList<FinnAd> finnAds = new ArrayList<>();
        URL url = createURL();
        String jsonResponse;

        try
        {
            jsonResponse = makeHttpRequest(url);
            finnAds.addAll(extractFromJSON(jsonResponse));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return finnAds;
    }

    /**
     * Parse the JSON response and save the values we need in a FinnAd object
     * and put that object in a list.
     * @param jsonResponse
     * @return
     */
    private static ArrayList<FinnAd> extractFromJSON(String jsonResponse)
    {
        ArrayList<FinnAd> finnAds = new ArrayList<>();

        if (!TextUtils.isEmpty(jsonResponse))
        {
            try
            {
                JSONObject baseJSONResponse = new JSONObject(jsonResponse);

                JSONArray items = baseJSONResponse.getJSONArray(JSON_ITEMS);

                for (int i = 0; i < items.length(); i++)
                {
                    JSONObject adItem = items.getJSONObject(i);

                    String imageURL = null;
                    if (adItem.has(JSON_IMAGE))
                    {
                        //Get the image URL, we'll download it later
                        JSONObject imageItem = adItem.getJSONObject(JSON_IMAGE);
                        imageURL = imageItem.getString(JSON_IMAGE_URL);
                    }

                    //Extract other information from item object
                    int price = 0;
                    if (adItem.has(JSON_PRICE))
                    {
                        JSONObject priceItem = adItem.getJSONObject(JSON_PRICE);
                        price = priceItem.getInt(JSON_PRICE_VALUE);
                    }

                    String title = "";
                    if (adItem.has(JSON_TITLE))
                    {
                        title = adItem.getString(JSON_TITLE);
                    }

                    String location = "";
                    if (adItem.has(JSON_LOCATION))
                    {
                        location = adItem.getString(JSON_LOCATION);
                    }

                    //ID is always present
                    String id = adItem.getString(JSON_ID);

                    FinnAd finnAd = new FinnAd(title, location, price, imageURL, id);

                    finnAds.add(finnAd);
                }
            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG, "Error while reading JSON response", e);
            }
        }

        return finnAds;
    }

    /**
     * Fetch the actual image when we want to show it
     * @param imageURL
     * @return
     */
    public static Bitmap fetchImage(String imageURL)
    {
        InputStream inputStream = null;
        Bitmap image = null;

        try
        {
            URL url = new URL(JSON_FINN_IMAGE_URL + imageURL);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
            image = BitmapFactory.decodeStream(inputStream);
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG, "Malformed URL", e);
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Problem retrieving Finn JSON results", e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, "Error closing image input stream", e);
                }
            }
        }
        return image;
    }


    private static String makeHttpRequest(URL url) throws IOException
    {
        String jsonResponse = "";

        if (url != null)
        {
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try
            {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(HTTP_READ_TIMEOUT);
                connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
                connection.connect();

                if (connection.getResponseCode() == JSON_FINN_RESPONSE_CODE_OK)
                {
                    //We are connected and everything is OK
                    inputStream = connection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else
                {
                    Log.e(LOG_TAG, "Error response codee: " + connection.getResponseCode());
                }
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "Problem retrieving Finn JSON results", e);
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }

                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
        }
        return jsonResponse;
    }

    @NonNull
    private static String readFromStream(InputStream inputStream) throws IOException
    {
        StringBuilder builder = new StringBuilder();

        if (inputStream != null)
        {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(CHARSET_UTF_8));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();

            while (line != null)
            {
                builder.append(line);
                line = reader.readLine();
            }
        }
        return builder.toString();
    }

    private static URL createURL() {
        URL url = null;

        try
        {
            url = new URL(JSON_FINN_QUERY);
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG, "Malformed URL",e);
        }

        return url;
    }
}

package com.chrismsolutions.finnnoads.data;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.chrismsolutions.finnnoads.FinnAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christian Myrvold on 02.02.2018.
 */

public class AdLoader extends AsyncTaskLoader<List<FinnAd>>
{
    private ArrayList<FinnAd> favoriteAds;
    private List<FinnAd> loadedAds;
    private boolean showFavorites, showLoadedAds;

    public AdLoader(Context context,
                    ArrayList<FinnAd> mFavoriteAds,
                    boolean mShowFavorites,
                    List<FinnAd> mLoadedAds,
                    boolean mShowLoadedAds)
    {
        super(context);
        favoriteAds = mFavoriteAds;
        showFavorites = mShowFavorites;
        loadedAds = mLoadedAds;
        showLoadedAds = mShowLoadedAds;
    }

    /**
     * Resend the results if we have them, otherwise force a new load.
     * This is to prevent user data loss when reorientating the phone.
     */
    @Override
    protected void onStartLoading()
    {
        if (showFavorites && favoriteAds != null)
        {
            deliverResult(favoriteAds);
        }
        else if (showLoadedAds && loadedAds != null)
        {
            deliverResult(loadedAds);
        }
        else
        {
            forceLoad();
        }
    }

    /**
     * Fetch the JSON data if we have no cached results. Show the favorite ads
     * if the user has toggled this on. Otherwise just show the cached results from before.
     * @return
     */
    @Override
    public List<FinnAd> loadInBackground()
    {
        List<FinnAd> finnAds = null;

        if (showFavorites  && (favoriteAds == null || favoriteAds.isEmpty()))
        {
            finnAds = null;
        }
        else if (!showFavorites)
        {
            if (showLoadedAds && loadedAds != null && !loadedAds.isEmpty())
            {
                //no need to load all the ads again, just return the cached result
                finnAds = loadedAds;
            }
            else
            {
                finnAds = AdUtils.fetchJSONData();
            }
        }
        else
        {
            finnAds = favoriteAds;
        }

        return finnAds;
    }
}

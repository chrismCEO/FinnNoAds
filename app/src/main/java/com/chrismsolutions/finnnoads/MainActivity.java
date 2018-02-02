package com.chrismsolutions.finnnoads;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chrismsolutions.finnnoads.data.AdLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<List<FinnAd>>
{

    private ArrayList<FinnAd> favoriteAds;
    private AdArrayAdapter adapter;
    private int LOADER_ID = 1;
    private LoaderManager loaderManager;
    private ListView adListView;
    private TextView emptyView;
    private ProgressBar progressBar;
    private boolean showFavorites, showLoadedAds;
    private ImageView showFavoritesImageView;
    private ArrayList<FinnAd> loadedAds;

    private static final String LOADED_ADS = "LOADED_ADS";
    private static final String SHOW_LOADED_ADS = "SHOW_LOADED_ADS";
    private static final String FAVORITE_ADS = "FAVORITE_ADS";
    private static final String SHOW_FAVORITE_ADS = "SHOW_FAVORITE_ADS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.mipmap.finn_logo);
            actionBar.setTitle(" " + getString(R.string.app_name_no_finn));
        }

        favoriteAds = new ArrayList<>();
        showFavorites = false;
        loadedAds = new ArrayList<>();
        showLoadedAds = false;


        adListView = findViewById(R.id.list);
        emptyView = findViewById(R.id.no_data_view);
        adListView.setEmptyView(emptyView);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        showFavoritesImageView = findViewById(R.id.toggle_favorites);
        clearFavoriteToggle();

        adapter = new AdArrayAdapter(MainActivity.this, new ArrayList<FinnAd>());
        adListView.setAdapter(adapter);

        loadAds();
    }

    /**
     * Check for internet connection or if we wish to show the cached ads.
     * If there is no internet connection, show a message to the user pointing this out.
     */
    private void loadAds()
    {
        TextView internetView = findViewById(R.id.no_internet_view);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        internetView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (connectivityManager != null)
        {
            networkInfo = connectivityManager.getActiveNetworkInfo();

            if ((networkInfo != null && networkInfo.isConnected()) ||
                showFavorites)
            {
                if (loaderManager == null)
                {
                    loaderManager = getLoaderManager();
                }
                else
                {
                    loaderManager.destroyLoader(LOADER_ID);
                    loaderManager = getLoaderManager();
                }
                loaderManager.initLoader(LOADER_ID, null, this);
                showLoadedAds = true;
            }
            else
            {
                adListView.setVisibility(View.GONE);
                internetView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public Loader<List<FinnAd>> onCreateLoader(int i, Bundle bundle)
    {
        return new AdLoader(MainActivity.this, favoriteAds, showFavorites, loadedAds, showLoadedAds);
    }

    /**
     * The background task is done loading the data. Add the data to the adapter and store
     * the loaded ads for caching.
     * @param loader
     * @param finnAds
     */
    @Override
    public void onLoadFinished(Loader<List<FinnAd>> loader, List<FinnAd> finnAds)
    {
        if (adapter != null)
        {
            adapter.clear();
        }

        if (finnAds != null && !finnAds.isEmpty())
        {
            adapter.addAll(finnAds);
            if (loadedAds.isEmpty())
            {
                loadedAds = (ArrayList<FinnAd>) finnAds;
            }
        }
        else
        {
            emptyView.setVisibility(View.VISIBLE);
        }
        adListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<FinnAd>> loader)
    {
        adapter.clear();
    }

    @Override
    protected void onRestart()
    {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        super.onRestart();
    }

    /**
     * Show the user that the display now shows the favorited ads
     */
    private void setFavoriteToggle()
    {
        showFavoritesImageView.setColorFilter(ContextCompat.getColor(this, R.color.red));
    }

    /**
     * Show the user that the display now shows all ads
     */
    private void clearFavoriteToggle()
    {
        showFavoritesImageView.setColorFilter(ContextCompat.getColor(this, R.color.grey));
    }

    /**
     * Called from the toggleFavorites view when clicked on
     * @param view
     */
    public void toggleFavorites(View view)
    {
        if (!showFavorites)
        {
            setFavoriteToggle();
        }
        else
        {
            clearFavoriteToggle();
        }
        showFavorites = !showFavorites;
        showLoadedAds = true;

        loadAds();
    }

    public void addToFavorites(FinnAd finnAd)
    {
        if (!favoriteAds.contains(finnAd))
        {
            favoriteAds.add(finnAd);
        }
    }

    public void removeFromFavorites(FinnAd finnAd)
    {
        if (favoriteAds.contains(finnAd))
        {
            favoriteAds.remove(finnAd);
        }
    }

    /**
     * If the phone orientation has changed, get the saved results and show toggles, so we
     * don't lose the user input and show the same result as before
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        loadedAds = (ArrayList<FinnAd>)savedInstanceState.getSerializable(LOADED_ADS);
        showLoadedAds = true;//savedInstanceState.getBoolean(SHOW_LOADED_ADS);

        favoriteAds = (ArrayList<FinnAd>)savedInstanceState.getSerializable(FAVORITE_ADS);
        showFavorites = savedInstanceState.getBoolean(SHOW_FAVORITE_ADS);

        if(showFavorites)
        {
            setFavoriteToggle();
        }
    }

    /**
     * Store the cached results and show toggles if the orientation of the phone changes
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putSerializable(LOADED_ADS, loadedAds);
        //outState.putBoolean(SHOW_LOADED_ADS, showLoadedAds);
        outState.putSerializable(FAVORITE_ADS, favoriteAds);
        outState.putBoolean(SHOW_FAVORITE_ADS, showFavorites);
        super.onSaveInstanceState(outState);
    }
}

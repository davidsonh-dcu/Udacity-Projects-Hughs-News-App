
/*
 * Copyright (C) 2021 Hugh Davidson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.hughsnewsapp;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = NewsActivity.class.getName();

    /** URL for Brexit news data from the Guardian news site  dataset */
    private static final String GUARDIAN_REQUEST_URL ="https://content.guardianapis.com/search?";

    /** Constant value for the earthquake loader ID. */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of news articles */
    private NewsAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.news_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        // View when no data is available
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news articles as input
        mAdapter = new NewsAdapter(this, new ArrayList<>());

        // Set the adapter on the {@link ListView} so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news article.
        newsListView.setOnItemClickListener((adapterView, view, position, l) -> {
            // Find the current news article that was clicked on
            News currentNews = mAdapter.getItem(position);

            // Convert the String URL into a URI object (to pass into the Intent constructor)
            Uri newsUri = Uri.parse(currentNews.getUrl());

            // Create a new intent to view the news URI
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

            // Send the intent to launch a new activity
            startActivity(websiteIntent);
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    // onCreateLoader instantiates and returns a new Loader for the given ID
    // Uri builder to construct the URL string to pass into the Loader constructor
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle){
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that was just parsed so that the add query param can added to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
//https://content.guardianapis.com/search?section=politics&order-by=relevance&show-tags=contributor&page-size=10&q=brexit&api-key=test
        // Append query parameter and its value. E.g. the 'order-by=relevance'
        //uriBuilder.appendQueryParameter("section", "politics");
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", "10");
        uriBuilder.appendQueryParameter("q", "brexit");
        uriBuilder.appendQueryParameter("api-key", "test");

        // Return the completed uri 'https://content.guardianapis.com/search?order-by=newest&q=brexit%2520AND%2520debate&api-key=test'
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsArticlesList) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news articles found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
//        mAdapter.clear();

        // If there is a valid list of {@link News} articles, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsArticlesList != null && !newsArticlesList.isEmpty()) {
            mAdapter.addAll(newsArticlesList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
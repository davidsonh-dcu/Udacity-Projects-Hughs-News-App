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

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** Create a private constructor because no one should ever create a {@link QueryUtils} object.*/
    private QueryUtils() {
    }

    /** Query the Guardian news dataset and return a list of {@link News} objects.*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News} articles

        // Return the list of {@link News} articles
        return extractFeatureFromJson(jsonResponse);
    }

    /** Returns new URL object from the given string URL. */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /** Make an HTTP request to the given URL and return a String as the response.*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), then read the input stream
            // and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the
     * server.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    /**
     * Return a list of {@link News} objects that has been built up from parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news articles to
        List<News> newsArticlesList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject articles = baseJsonResponse.getJSONObject("response");

            JSONArray resultsArray = articles.getJSONArray("results");
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of response (or news articles).
            // JSONArray newsArray = baseJsonResponse.getJSONArray("response");

            // For each news article in the newsArticleArray, create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single new article at position i within the list of news articles
                JSONObject currentNewsArticle = resultsArray.getJSONObject(i);

                // For a given news article, extract the JSONObject associated with the
                // key called "results", which represents a list of all properties
                // for that earthquake.
                //JSONObject results = currentNewsArticle.getJSONObject("results");

                // For a given news article, extract the JSONObject associated with the
                // key called "currentNewsArticle"
                // Extract the value for the key called "webTitle"
                String title  = currentNewsArticle.getString("webTitle");

                // Extract the value for the key called "webPublicationDate"

                String publicationDate = currentNewsArticle.getString("webPublicationDate");

                // Extract the value for the key called "webUrl"
                String url = currentNewsArticle.getString("webUrl");


                // Create a new {@link News} object with the Title, time and URL from the JSON response.
                News news = new News(title, publicationDate, url);

                // Add the new {@link News} to the list of News Articles.
                newsArticlesList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of new articles
        return newsArticlesList;
    }
}

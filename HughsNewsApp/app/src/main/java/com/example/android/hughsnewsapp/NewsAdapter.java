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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * An {@link NewsAdapter} knows how to create a list item layout for each earthquake
 * in the data source (a list of {@link News} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsAdapter extends ArrayAdapter<News> {
    /**
     * Constructs a new {@link NewsAdapter}.
     * @param context of the app
     * @param newsArticlesList is the list of news articles, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<News> newsArticlesList) {
        super(context, 0, newsArticlesList);
    }
    /**
     * Returns a list item view that displays information about the news at the given position
     * in the list of news articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Check if there is an existing list item view (called convertView) that could be reused,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        // Find the news at the given position in the list of news articles
        News currentNews = getItem(position);

        // Find the TextView with view ID title
        TextView titleOfArticleTextView = listItemView.findViewById(R.id.news_article_title_textView);
        // get the title of the news article from the current location news object and set in title view
        titleOfArticleTextView.setText(currentNews.getTitle());

        // Create a new Date object from the time in milliseconds for the news article
        Date dateObject = new Date(currentNews.getTimeInMilliseconds());
        // Format the date string (i.e. "15 March 2021")
        String formattedDate = formatDate(dateObject);

        // Find the TextView with view ID publication date
        TextView publicationDateTextView = listItemView.findViewById(R.id.article_publication_date_textView);
        // get the publication date  of the news article from the current location news object and set in date view
        publicationDateTextView.setText(formattedDate);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /** Return the formatted date string (i.e. "15 March 2021") from a Date object. */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy ");
        return dateFormat.format(dateObject);
    }
}

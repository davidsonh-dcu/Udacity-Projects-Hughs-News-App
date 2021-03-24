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

/**
 * An {@link News} object contains information related toa single news article
 */
public class News {
    /** news article title */
    private final String mTitle;

    /** article date */
    private final long mTimeInMilliseconds;

    /** URL of new article  **/
    private final String mUrl;

    /**
     * Constructor
     * @param title title of news article
     * @param timeInMilliseconds the date the article was published
     *  @param url is the website URL to find more details about the new article
     */

    public News(String title, long timeInMilliseconds, String url){
        mTitle = title;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
    }

    /** Return the title of the news article*/
    public String getTitle(){ return mTitle; }

    /** Return the publication date */
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    /** Return the website URL for the full details of the article*/
    public String getUrl(){
        return mUrl;
    }
}


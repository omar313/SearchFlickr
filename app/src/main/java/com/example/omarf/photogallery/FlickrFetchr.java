package com.example.omarf.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by omarf on 11/3/2016.
 */

public class FlickrFetchr {
    private static final String TAG = "FLICKFETCHR";
    private static final String API_KEY = "39f2b1fb1d68accb5e693487addb6443";
    private static final String FETCH_RECENTS_METHOD="flickr.photos.getRecent";
    private static final String SEARCH_METHOD="flickr.photos.search";
    private static final Uri ENDPOINT=Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage());
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

//    public List<GalleryItem> fetchItems(int pageNumber) {
//        List<GalleryItem> items = new ArrayList<>();
//        try {
//            String url = Uri.parse("https://api.flickr.com/services/rest/").buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
//                    .appendQueryParameter("format", "json")
//                    .appendQueryParameter("nojsoncallback", "1")
//                    .appendQueryParameter("extras", "url_s")
//                    .appendQueryParameter("page",pageNumber+"")
//                    .build()
//                    .toString();
//
//            String jsonString = getUrlString(url);
//            Log.i(TAG, jsonString);
//            JSONObject jsonBody = new JSONObject(jsonString);
//           items= parseItemsUsingGson( jsonBody);
//        } catch (IOException e) {
//            Log.e(TAG, "Failed to fetch url", e);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return items;
//
//    }

    private List<GalleryItem> downloadGalleryItem(String url){
        List<GalleryItem> items=new ArrayList<>();

        try {
            String jsonString =getUrlString(url);
            Log.i(TAG,"Received json"+jsonString);
            JSONObject jsonBody=new JSONObject(jsonString);
            parseItems(items,jsonBody);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;

    }

    private String buildUrl(String buildMethod,String query){
        Uri.Builder builder= ENDPOINT.buildUpon().appendQueryParameter("method",buildMethod);

        if(buildMethod.equals(SEARCH_METHOD)){
           return builder.appendQueryParameter("text",query).build().toString();
        }
        return  builder.build().toString();
    }

    public List<GalleryItem> fetchRecentPhotos(){
        String url=buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItem(url);
    }

    public List<GalleryItem> searchPhotos(String query){
        String url= buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItem(url);
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        JSONObject jsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoArray = jsonObject.getJSONArray("photo");

        for (int i = 0; i < photoArray.length(); i++) {
            JSONObject photoObject = photoArray.getJSONObject(i);
            if (!photoObject.has("url_s")) {
                continue;
            }
            GalleryItem item = new GalleryItem();
            item.setmId(photoObject.getString("id"));
            item.setmCaption(photoObject.getString("title"));
            item.setmUrl(photoObject.getString("url_s"));
            items.add(item);
        }
    }

    private List<GalleryItem> parseItemsUsingGson(JSONObject jsonBody) throws JSONException {
        Gson gson = new GsonBuilder().create();
        JSONObject jsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoArray = jsonObject.getJSONArray("photo");
        GalleryItem[] galleryItemsArray = gson.fromJson(photoArray.toString(), GalleryItem[].class);
        List<GalleryItem> items = new ArrayList<>(Arrays.asList(galleryItemsArray));


        for (int i = 0; i < items.size(); i++) {
            GalleryItem item = items.get(i);
            if (item.getmUrl() == null) {
                items.remove(i);
            }
        }
     return items;

    }


}

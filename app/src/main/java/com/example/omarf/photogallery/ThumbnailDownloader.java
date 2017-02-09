package com.example.omarf.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static android.R.attr.bitmap;
import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.fromYScale;

/**
 * Created by omarf on 11/23/2016.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloaderTag";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ConcurrentHashMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
    private LruCache<String, Bitmap> mBitmapCache;
    private int mTotalCache = 0;
    private int mTotalBitmapCreated = 0;


    public ThumbnailDownloader(Handler handler) {
        super(TAG);
        mResponseHandler = handler;

    }

/*
    public void urlArrayToCache(final String[] urlArray, T target) {

        Log.i(TAG, "Inside urlArrayToCache");
        for (int i = 0; i < urlArray.length - 1; i++) {
            String url = urlArray[i];

            if (getBitmapFromCache(url) == null) {
                Bitmap bitmap = urlToBitmap(url);
                addBitmapToCache(url, bitmap);
            }
        }
        queueThumbnail(target, urlArray[0], urlArray[urlArray.length - 1]);


    }*/

    public void queueThumbnail(T target, String targetUrl, String cacheUrl) {
        Log.i(TAG, "Got a Url " + targetUrl);
        if (targetUrl == null) {
            Log.i(TAG, "target url null");
            mRequestMap.remove(target);
        } else {
            //mRequestMap.put(target, targetUrl);
            Log.i(TAG, "item added to requestMap");
            Bitmap tempBitmap;
            while (true) {
                tempBitmap = getBitmapFromCache(targetUrl);
                if(tempBitmap!=null)
                {
                    mThumbnailDownloadListener.onThumbnailDownloaded(tempBitmap, target);
                    break;
                }

            }
            MyMessage myMessage = new MyMessage(cacheUrl);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, myMessage).sendToTarget();
        }
    }


    public void queueThumbnail(String[] urlArray) {
        if (urlArray != null) {
            Log.i(TAG, "url array received " + urlArray.length);
            // mRequestMap.put(target, urlArray[0]);
            MyMessage myMessage = new MyMessage(urlArray);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, myMessage).sendToTarget();

        }
    }


    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
//        final int maxSize = (int) Runtime.getRuntime().maxMemory() / 1024;
//        final int cacheSize = maxSize / 8;
        mBitmapCache = new LruCache<String, Bitmap>(36); /*{
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };*/
        Log.i(TAG, "Lru cache created");

        mRequestHandler = new Handler() {
            int totalRequest = 0;

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    MyMessage myMessage = (MyMessage) msg.obj;
                    //   Log.i(TAG, "get a url request " + mRequestMap.get(myMessage.target));
                    Log.i(TAG, "Total url request " + totalRequest++);

                    handleRequest(myMessage);

                }
            }
        };

    }

   /* public void handleRequest(final T target) {
        final String url = mRequestMap.get(target);

        if (url == null)
            return;
        try {
            final Bitmap bitmap;
            Bitmap tempBitmap;
            if ((tempBitmap = getBitmapFromCache(url)) != null) {
                bitmap = tempBitmap;
                Log.i(TAG, "fetch from cache "+ mTotalCache++);
            } else {
                byte[] bytesArray = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.length);
                addBitmapToCache(url, bitmap);
                Log.i(TAG, "create a bitmap "+mTotalBitmapCreated++);

            }
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (url != mRequestMap.get(target)) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(bitmap, target);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/

    public void handleRequest(final MyMessage myMessage) {
        Bitmap targetBitmap = null;
        boolean isBeginState = false;
        String cacheUrl = null;


        String[] urlArray = myMessage.mUrlArray;
        final T target = myMessage.target;

        if (urlArray != null) {
            isBeginState = true;
            for (int i = 9; i >= 0; i--) {
                Bitmap tempBitmap = urlToBitmap(urlArray[i]);
                addBitmapToCache(urlArray[i], tempBitmap);

            }

        } else {
            // caching for normal case

            cacheUrl = myMessage.mCacheUrl;
            Bitmap cacheBitmap = urlToBitmap(cacheUrl);
            if (cacheBitmap != null)
                addBitmapToCache(cacheUrl, cacheBitmap);

            Log.i(TAG, "total evict " + mBitmapCache.evictionCount());
            Log.i(TAG, "total put " + mBitmapCache.putCount());
            Log.i(TAG, "total items " + mBitmapCache.size());

        }


    }

    public void removeBitmapFromCache(String url) {
        if (getBitmapFromCache(url) != null) {
            mBitmapCache.remove(url);
            Log.i(TAG, "bitmap remove from cache");
        }
    }


    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(Bitmap bitmap, T target);
    }

    public void setmThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public void clearMessageQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        Log.i(TAG, "Queued clear");
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            mBitmapCache.put(key, bitmap);
            Log.i(TAG, "Bitmap added to Cache");
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return mBitmapCache.get(key);
    }

    public Bitmap urlToBitmap(String url) {
        Bitmap bitmap = null;
        try {
            byte[] data = new FlickrFetchr().getUrlBytes(url);
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    class MyMessage {
        public String mCacheUrl;
        public T target;
        public String[] mUrlArray;

        public MyMessage(String mCacheUrl) {
            this.mCacheUrl = mCacheUrl;
            this.target = target;
        }

        public MyMessage(String[] urlArray) {
            this.mUrlArray = urlArray;
        }
    }

}

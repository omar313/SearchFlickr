package com.example.omarf.photogallery;


import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends VisibleFragment {

    private int mPageNumber = 1;
    private static final String TAG = "PhotoGalleryFragmentTag";
    private RecyclerView mPhotoRecyclerView;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private List<GalleryItem> mItems = new ArrayList<>();
//    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
//    private boolean isBeginState = true;
//    private boolean isBackScrolling = false;

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        updateItems();
        setHasOptionsMenu(true);
        /*Intent intent=PollService.newIntent(getActivity());
        getActivity().startService(intent);*/

        //  PollService.setServiceAlarm(getActivity(), true);

       /* Handler handler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(handler);
        mThumbnailDownloader.setmThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(Bitmap bitmap, PhotoHolder target) {
                if (isAdded()) {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    target.mImageView.setImageDrawable(drawable);
                }


            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread Start");*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_photo_gallery_progress_bar);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recyler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (dy < 0) {
//                    Log.i(TAG, " dy value: " + dy);
//                    isBackScrolling = true;
//
//                } else {
//                    Log.i(TAG, " dy value: " + dy);
//                    isBackScrolling = false;
//                }
//
//                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//                int lastItemIndex = layoutManager.getItemCount() - 1;
//                if (layoutManager.findLastCompletelyVisibleItemPosition() == lastItemIndex) {
//
//                    new FetchItemTask().execute(mPageNumber++);
//                    Log.i(TAG, "atLast " + mPageNumber);
//
//                } else {
//                    Log.i(TAG, "Fetch Item Task Failed");
//                }
//
//           //     Log.i(TAG,"find first completely visible item position "+layoutManager.findFirstCompletelyVisibleItemPosition());
////                Log.i(TAG,"find first visible item position "+layoutManager.findFirstVisibleItemPosition());
//                Log.i(TAG, "total item display " +
//                        (layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition()));
//                Log.i(TAG,"find last visible position "+layoutManager.findLastVisibleItemPosition());
//
//
//                Log.i(TAG, "current page number " + mPageNumber);
//            }
//
//        });
        Log.i(TAG, "onCreateView Called ");

        setupAdapter();
        return v;
    }


    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_imageView);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        List<GalleryItem> mItems;
        int totalViewHolder = 0;

        public PhotoAdapter(List<GalleryItem> mItems) {
            this.mItems = mItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.gallery_item, parent, false);
            Log.i(TAG, "Total ViewHolder created " + totalViewHolder++);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mItems.get(position);
            /*int cacheUrlPosition = 0;

            if (!isBackScrolling) {
                cacheUrlPosition = position + 10;
            } else if (isBackScrolling && (position - 10) >= 0) {
                cacheUrlPosition = position - 10;
              //  mThumbnailDownloader.removeBitmapFromCache(mItems.get(position + 10).getmUrl());
            }

            Log.i(TAG, "cache url position " + cacheUrlPosition + " current url position " + position);

            mThumbnailDownloader.queueThumbnail(holder,
                    galleryItem.getmUrl(),
                    mItems.get(cacheUrlPosition).getmUrl());*/

            Picasso.with(getActivity())
                    .load(galleryItem.getmUrl())
                    .into(holder.mImageView);


        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

//        @Override
//        public void onViewRecycled(PhotoHolder holder) {
//            super.onViewRecycled(holder);
//            holder.mImageView.setImageBitmap(null);
//        }
    }

    private class FetchItemTask extends AsyncTask<Integer, Void, List<GalleryItem>> {
        private int mPagenumber;
        private String mQuery;


        public FetchItemTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Integer... pageNumber) {

            mPagenumber = pageNumber[0];

            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            }
            return new FlickrFetchr().searchPhotos(mQuery);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);
            mProgressBar.setVisibility(View.GONE);
            mPhotoRecyclerView.setVisibility(View.VISIBLE);
            if (mPagenumber >= 1) {
                mItems.addAll(items);
                mPhotoRecyclerView.getAdapter().notifyDataSetChanged();
            } else {
                mItems = items;
                setupAdapter();
            }
            Log.i(TAG, "Total items " + mItems.size());

          /*  if (isBeginState) {
                String urlArray[] = new String[11];
                for (int i = 0; i < urlArray.length; i++) {
                    urlArray[i] = mItems.get(i).getmUrl();
                    Log.i(TAG, "url added to urlArray");
                }
                mThumbnailDownloader.queueThumbnail(urlArray);
                isBeginState = false;
//                 mThumbnailDownloader.urlArrayToCache(urlArray,holder);
            }*/
        }
    }


    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mThumbnailDownloader.quit();
//        Log.i(TAG, "Background Thread Destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        mSearchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "QueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "QueryTextChange " + newText);
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                mSearchView.setQuery(query, false);
            }
        });
        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PollJobService.isJobSchedule(getActivity())) {
                toggleItem.setTitle(R.string.stop_polling);
            } else {
                toggleItem.setTitle(R.string.start_polling);
            }
        } else {
            if (PollService.isServiceAlarmOn(getActivity()))
                toggleItem.setTitle(R.string.stop_polling);
            else {
                toggleItem.setTitle(R.string.start_polling);
            }
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    boolean toggling = PollService.isServiceAlarmOn(getActivity());

                    PollService.setServiceAlarm(getActivity(), !toggling);
                } else {
                    boolean jobToggling = PollJobService.isJobSchedule(getActivity());
                    PollJobService.setServiceAlarm(getActivity(), !jobToggling);
                }


                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateItems() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mPhotoRecyclerView.setVisibility(View.GONE);
            mSearchView.setIconified(true);
            mSearchView.setIconified(true);
        }

        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemTask(query).execute(0);

    }
}

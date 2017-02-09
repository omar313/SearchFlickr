package com.example.omarf.photogallery;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by omarf on 1/6/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {
    private static final String TAG = "PollJobServiceTag";

    private static final int JOB_ID = 1;
    private JobHandle mJobHandle;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mJobHandle = new JobHandle();
        mJobHandle.execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "OnStopJob called");
        if (mJobHandle != null)
            mJobHandle.cancel(true);
        return false;
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        return cm.getActiveNetworkInfo().isConnected() && isNetworkAvailable;
    }

    private class JobHandle extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... jobParameters) {
            JobParameters parameter = jobParameters[0];


            if (!isNetworkAvailableAndConnected())
                return null;
            Context context = PollJobService.this;
            ArrayList<GalleryItem> items;
            String lastResultId = QueryPreferences.getLastResultId(context);
            String query = QueryPreferences.getStoredQuery(context);
            if (query == null)
                items = (ArrayList<GalleryItem>) new FlickrFetchr().fetchRecentPhotos();
            else
                items = (ArrayList<GalleryItem>) new FlickrFetchr().searchPhotos(query);

            String recentResultId = items.get(items.size() - 1).getmId();

          /*  if (lastResultId == null)
                lastResultId = recentResultId;*/

            if (lastResultId != null && lastResultId.equals(recentResultId)) {
                Log.i(TAG, "new photo isn't available yet");
            } else {
                Log.i(TAG, "new photo is available " + recentResultId);
                QueryPreferences.setLastResultId(context, recentResultId);
                Intent intent = PhotoGalleryActivity.newIntent(context);
                PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
                Resources resources = getResources();

                Notification notification = new NotificationCompat.Builder(context)
                        .setContentIntent(pi)
                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                        .setContentText(resources.getString(R.string.new_pictures_text))
                        .setAutoCancel(true)
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .build();
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                managerCompat.notify(0, notification);
                jobFinished(parameter, false);
            }


            return null;
        }
    }

    public static void setServiceAlarm(Context context, boolean isOn) {


        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        if (!isOn) {
            jobScheduler.cancel(JOB_ID);
            return;
        }

        boolean isJobScheduled = isJobSchedule(context);
        for (JobInfo jobinfo : jobScheduler.getAllPendingJobs()
                ) {
            if (jobinfo.getId() == JOB_ID) {

                isJobScheduled = true;
                break;
            }
        }


        if (!isJobScheduled) {
            Log.i(TAG, "new JobInfo Create ");
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context, PollJobService.class))
                    .setPersisted(true)
                    .setPeriodic(1000 * 60)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        }


    }

    public static boolean isJobSchedule(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        boolean isScheduled = false;
        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()
                ) {
            if (jobInfo.getId() == JOB_ID) {

                isScheduled = true;
                break;
            }

        }

        return isScheduled;
    }


}

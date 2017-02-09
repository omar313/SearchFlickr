package com.example.omarf.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by omarf on 1/14/2017.
 */

public abstract class VisibleFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intent=new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification,intent);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    private BroadcastReceiver mOnShowNotification=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(getActivity(), "Got a BroadCast "+intent.getAction(), Toast.LENGTH_LONG).show();
        }
    };
}

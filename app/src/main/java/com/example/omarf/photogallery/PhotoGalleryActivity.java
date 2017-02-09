package com.example.omarf.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PhotoGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragment_container);

        if(fragment==null)
        {
            fragment=new PhotoGalleryFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }

    }

    public  static Intent newIntent(Context context){
        return  new Intent(context,PhotoGalleryActivity.class);
    }
}

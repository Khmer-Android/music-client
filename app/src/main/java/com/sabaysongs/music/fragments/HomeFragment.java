package com.sabaysongs.music.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sabaysongs.music.R;


public class HomeFragment extends BaseFragment implements

        View.OnClickListener,
        LocationListener {
    private Context context;

    ProgressDialog dialog;
    private static boolean isGPSEnabled, isNetworkEnabled;



    private final int MY_PERMISSIONS_REQUEST_LOCATION = 5;
    private final int MY_PERMISSIONS_REQUEST_STOP = 6;
    private static LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, null);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        context = getActivity();



        return root;
    }


    @Override
    protected void backPressed() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {

    }
}
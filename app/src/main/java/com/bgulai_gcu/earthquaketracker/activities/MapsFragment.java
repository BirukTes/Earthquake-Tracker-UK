package com.bgulai_gcu.earthquaketracker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bgulai_gcu.earthquaketracker.R;
import com.bgulai_gcu.earthquaketracker.generalClasses.LocationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Objects;

public class MapsFragment extends Fragment {
    private static GoogleMap googleMap;
    private final MapsFragment mapsFragment = this;
    private static ClusterManager<LocationModel> clusterManager;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mapsFragment.googleMap = googleMap;
            LatLng ukCoordinates = new LatLng(55.3781, 3.4360);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ukCoordinates, 4));
        }


    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public static void runClusterManager(Context context, ArrayList<LocationModel> locationModelList) {
        if (clusterManager != null){
            clusterManager.clearItems();
            googleMap.clear();
        }
        clusterManager = new ClusterManager<>(context, googleMap);
        clusterManager.setAnimation(true);
        if (locationModelList != null) {
            googleMap.setOnCameraIdleListener(clusterManager);

            clusterManager.addItems(locationModelList);
            clusterManager.cluster();
        }
    }
}
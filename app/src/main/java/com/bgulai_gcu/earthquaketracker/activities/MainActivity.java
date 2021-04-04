package com.bgulai_gcu.earthquaketracker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bgulai_gcu.earthquaketracker.generalClasses.LoadXmlFromNetwork;
import com.bgulai_gcu.earthquaketracker.generalClasses.LocationModel;
import com.bgulai_gcu.earthquaketracker.generalClasses.LocationCardAdapter;
import com.bgulai_gcu.earthquaketracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    private RecyclerView recyclerView;
    private LocationCardAdapter locationCardAdapter;
    private ArrayList<LocationModel> locationModelList;

    private CardView filterSortCardView;
    private ConstraintLayout sortConstraintLayout;
    private ConstraintLayout filterConstraintLayout;
    private FloatingActionButton sortFloatingActionButton;
    private FloatingActionButton filterFloatingActionButton;
    private Button filterApplyButton;
    private RadioGroup sortRG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String quakesBgsUrlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

        mTextView = findViewById(R.id.text);

        new GetLocationListAndSetData().execute(quakesBgsUrlSource);

        filterSortCardView = findViewById(R.id.filterSortCardView);
        sortConstraintLayout = findViewById(R.id.sortConstraintLayout);
        filterConstraintLayout = findViewById(R.id.filterConstraintLayout);

        sortFloatingActionButton = findViewById(R.id.sortFloatingActionButton);
        sortFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterSortCardView.setVisibility(View.VISIBLE);
                sortConstraintLayout.setVisibility(View.VISIBLE);
                sortConstraintLayout.animate().translationY(0);
            }
        });
        filterFloatingActionButton = findViewById(R.id.filterFloatingActionButton);
        filterFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterSortCardView.setVisibility(View.VISIBLE);
                filterConstraintLayout.setVisibility(View.VISIBLE);
                filterConstraintLayout.animate().translationY(0);
            }
        });

        //sortContainerLL.setTranslationY(-sortContainerLL.getHeight());
        filterSortCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortConstraintLayout.animate()
                        .translationY(-sortConstraintLayout.getHeight())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                sortConstraintLayout.animate().setListener(null);
                                filterSortCardView.setVisibility(View.GONE);

                                if (sortConstraintLayout.getVisibility() == View.VISIBLE) {
                                    sortConstraintLayout.setVisibility(View.GONE);
                                } else if (filterConstraintLayout.getVisibility() == View.VISIBLE) {
                                    filterConstraintLayout.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });


        sortRG = findViewById(R.id.sortRG);
        sortRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.sortByMagnitude:
                        break;
                    case R.id.sortByDepth:
                        break;
                }
                sortConstraintLayout.animate()
                        .translationY(-sortConstraintLayout.getHeight())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                sortConstraintLayout.animate().setListener(null);
                                filterSortCardView.setVisibility(View.GONE);
                                sortConstraintLayout.setVisibility(View.GONE);
                            }
                        });
            }
        });

        filterApplyButton = findViewById(R.id.filterApplyButton);
        filterApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterConstraintLayout.animate()
                        .translationY(-filterConstraintLayout.getHeight())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                filterConstraintLayout.animate().setListener(null);
                                filterSortCardView.setVisibility(View.GONE);
                                filterConstraintLayout.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    private void initialiseRecyclerViewValues(ArrayList<LocationModel> locationModelList) {
        if (locationModelList != null) {
            recyclerView = findViewById(R.id.locationRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            locationCardAdapter = new LocationCardAdapter(this, locationModelList);
            recyclerView.setAdapter(locationCardAdapter);
        } else {
            Log.e("Locations Null", "Null");
        }
    }

    // Implementation of AsyncTask used to download XML feed from url
    private class GetLocationListAndSetData extends AsyncTask<String, Void, ArrayList<LocationModel>> {
        @Override
        protected ArrayList<LocationModel> doInBackground(String... urls) {
            try {
                return new LoadXmlFromNetwork().loadXml(urls[0]);
            } catch (IOException | XmlPullParserException e) {
//                return getResources().getString(R.string.connection_error);
                return null;
            } //                return getResources().getString(R.string.xml_error);

        }

        @Override
        protected void onPostExecute(ArrayList<LocationModel> result) {
            initialiseRecyclerViewValues(result);
        }
    }
}


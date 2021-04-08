package com.bgulai_gcu.earthquaketracker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bgulai_gcu.earthquaketracker.generalClasses.Filter;
import com.bgulai_gcu.earthquaketracker.generalClasses.LoadXmlFromNetwork;
import com.bgulai_gcu.earthquaketracker.generalClasses.LocationModel;
import com.bgulai_gcu.earthquaketracker.generalClasses.LocationCardAdapter;
import com.bgulai_gcu.earthquaketracker.R;
import com.bgulai_gcu.earthquaketracker.generalClasses.Preferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    private RecyclerView recyclerView;
    private LocationCardAdapter locationCardAdapter;
    private ArrayList<LocationModel> locationModelList;
    private ArrayList<LocationModel> locationModelListFiltered;

    private CardView filterSortCardView;

    private ConstraintLayout sortConstraintLayout;
    private RadioGroup sortRG;
    private RadioButton sortByNone;
    private RadioButton sortByMagnitude;
    private RadioButton sortByDepth;

    private ConstraintLayout filterConstraintLayout;
    private Button filterApplyButton;
    private Button filterCancelButton;
    private Button fromDateTextButton;
    private Button toDateTextButton;
    private Spinner magSpinner;
    private Spinner depthSpinner;
    private Spinner locationSpinner;

    private FloatingActionButton sortFloatingActionButton;
    private FloatingActionButton filterFloatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String quakesBgsUrlSource = "https://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

        mTextView = findViewById(R.id.text);

        new GetLocationListAndSetData().execute(quakesBgsUrlSource);

        filterSortCardView = findViewById(R.id.filterSortCardView);

        sortConstraintLayout = findViewById(R.id.sortConstraintLayout);
        sortRG = findViewById(R.id.sortRG);
        sortByNone = findViewById(R.id.sortByNone);
        sortByMagnitude = findViewById(R.id.sortByMagnitude);
        sortByDepth = findViewById(R.id.sortByDepth);

        filterConstraintLayout = findViewById(R.id.filterConstraintLayout);
        filterApplyButton = findViewById(R.id.filterApplyButton);
        filterCancelButton = findViewById(R.id.filterCancelButton);


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


        sortRG.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.sortByNone:
                    break;
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
        });

        filterApplyButton.setOnClickListener(view -> filterConstraintLayout.animate()
                .translationY(-filterConstraintLayout.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        filterConstraintLayout.animate().setListener(null);
                        filterSortCardView.setVisibility(View.GONE);
                        filterConstraintLayout.setVisibility(View.GONE);
                    }
                }));
    }

    private void initialiseRecyclerViewValues(ArrayList<LocationModel> locationModelList) {
        if (locationModelList != null) {
            recyclerView = findViewById(R.id.locationRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            fromDateTextButton = findViewById(R.id.fromDateTextButton);
            toDateTextButton = findViewById(R.id.toDateTextButton);
            magSpinner = findViewById(R.id.magSpinner);
            depthSpinner = findViewById(R.id.depthSpinner);
            locationSpinner = findViewById(R.id.locationSpinner);

            locationModelListFiltered = locationModelList;

            if (!Preferences.filters.isEmpty()) {
                ArrayList<LocationModel> filteredLocationModels = new ArrayList<>();
                List<String> colors = Preferences.filters.get(Filter.INDEX_DATE).getSelected();
                List<String> magnitudes = Preferences.filters.get(Filter.INDEX_MAGNITUDE).getSelected();
                List<String> depth = Preferences.filters.get(Filter.INDEX_DEPTH).getSelected();
                List<String> locations = Preferences.filters.get(Filter.INDEX_LOCATION).getSelected();

                for (LocationModel locationModel : locationModelListFiltered) {
                    boolean colorMatched = true;
                    if (colors.size() > 0 && !colors.contains(locationModel.getColor())) {
                        colorMatched = false;
                    }
                    boolean sizeMatched = true;
                    if (sizes.size() > 0 && !sizes.contains(locationModel.getSize().toString())) {
                        sizeMatched = false;
                    }
                    boolean priceMatched = true;
                    if (prices.size() > 0 && !priceContains(prices, locationModel.getPrice())) {
                        priceMatched = false;
                    }
                    if (colorMatched && sizeMatched && priceMatched) {
                        filteredLocationModels.add(locationModel);
                    }
                }
                locationModelListFiltered = filteredLocationModels;
            }

            locationCardAdapter = new LocationCardAdapter(this, locationModelListFiltered);
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
            locationModelList = result;
            initialiseRecyclerViewValues(result);
        }
    }
}


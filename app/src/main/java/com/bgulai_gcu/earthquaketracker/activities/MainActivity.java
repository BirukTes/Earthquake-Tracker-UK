package com.bgulai_gcu.earthquaketracker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bgulai_gcu.earthquaketracker.generalClasses.Filter;
import com.bgulai_gcu.earthquaketracker.generalClasses.FilterManager;
import com.bgulai_gcu.earthquaketracker.generalClasses.LoadXmlFromNetwork;
import com.bgulai_gcu.earthquaketracker.generalClasses.LocationModel;
import com.bgulai_gcu.earthquaketracker.generalClasses.LocationCardAdapter;
import com.bgulai_gcu.earthquaketracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.ClusterManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context appMainContext = this;
    private ClusterManager<LocationModel> clusterManager;

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
    private String fromDateMinAvailableDate;
    private String toDateMaxAvailableDate;
    private Spinner magSpinner;
    private Spinner depthSpinner;
    private Spinner locationSpinner;
    private Calendar calendar = Calendar.getInstance();
    private Locale id = new Locale("in", "ID");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", id);
    private Date date_minimal;
    private Date date_maximal;

    private FloatingActionButton sortFloatingActionButton;
    private FloatingActionButton filterFloatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String quakesBgsUrlSource = "https://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

        new GetLocationListAndSetData().execute(quakesBgsUrlSource);

        initialiseSortButtons();

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


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new GetLocationListAndSetData().execute(quakesBgsUrlSource);

                handler.postDelayed(this, 5 * 60 * 1000); // every 5 minutes
                /* your code here */
            }
        }, 5 * 60 * 1000); // first run after 5 minutes
    }

    private void initialiseSortButtons() {
        filterSortCardView = findViewById(R.id.filterSortCardView);
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

        sortConstraintLayout = findViewById(R.id.sortConstraintLayout);
        sortRG = findViewById(R.id.sortRG);

        sortRG.setOnCheckedChangeListener((group, checkedId) -> {
            sortRecyclerList(checkedId, locationModelListFiltered, false);

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
    }

    private ArrayList<LocationModel> sortRecyclerList(int checkedId, ArrayList<LocationModel> locationModelListLocal, boolean fromFilter) {
        ArrayList<LocationModel> locationModelArrayList = new ArrayList<>();
        switch (checkedId) {
            case R.id.sortByNone:
                locationModelArrayList = new ArrayList<>(locationModelListLocal); // Take a copy, not reference!!
                break;
            case R.id.sortByMagnitude:
                LocationModel[] locationModels = new LocationModel[locationModelListLocal.size()];
                locationModels = locationModelListLocal.toArray(locationModels);
                Arrays.sort(locationModels, LocationModel.magnitudeComparator);
                locationModelArrayList = new ArrayList<>(Arrays.asList(locationModels));
                break;
            case R.id.sortByDepth:
                LocationModel[] locationModels1 = new LocationModel[locationModelListLocal.size()];
                locationModels1 = locationModelListLocal.toArray(locationModels1);
                Arrays.sort(locationModels1, LocationModel.depthComparator);
                locationModelArrayList = new ArrayList<>(Arrays.asList(locationModels1));
                break;
        }

        if (!fromFilter && locationModelArrayList != null) {
            locationCardAdapter = new LocationCardAdapter(appMainContext, locationModelArrayList);
            recyclerView.setAdapter(locationCardAdapter);
        }
        return locationModelArrayList;
    }

    private void initialiseRecyclerViewValues(ArrayList<LocationModel> locationModelList) {
        if (locationModelList != null) {
            recyclerView = findViewById(R.id.locationRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(appMainContext));
            initialiseFilterButtons();

            runFilterAndSort(locationModelList);
        } else {
            Log.e("Locations Null", "Null");
        }
    }

    private void initialiseFilterButtons() {
        filterApplyButton = findViewById(R.id.filterApplyButton);
        filterCancelButton = findViewById(R.id.filterCancelButton);
        fromDateTextButton = findViewById(R.id.fromDateTextButton);
        toDateTextButton = findViewById(R.id.toDateTextButton);
        magSpinner = findViewById(R.id.magSpinner);
        depthSpinner = findViewById(R.id.depthSpinner);
        locationSpinner = findViewById(R.id.locationSpinner);

        // Spinner click listener
        magSpinner.setOnItemSelectedListener(this);
        depthSpinner.setOnItemSelectedListener(this);
        locationSpinner.setOnItemSelectedListener(this);


        fromDateMinAvailableDate = simpleDateFormat.format(locationModelList.get(locationModelList.size() - 1).getDateTime()); // Last is oldest
        toDateMaxAvailableDate = simpleDateFormat.format(new Date());
        ArrayList<String> arrayListDate = new ArrayList<>();
        arrayListDate.add(fromDateMinAvailableDate);
        arrayListDate.add(toDateMaxAvailableDate);
        fromDateTextButton.setText(fromDateMinAvailableDate);
        toDateTextButton.setText(toDateMaxAvailableDate);

        ArrayList<String> arrayListMag = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.magnitude_array)));
        ;
        ArrayList<String> arrayListDepth = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.depth_array)));
        ;
        ArrayList<String> arrayListLocation = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.location_array)));
        ;
        FilterManager.initialisePrefs(arrayListDate, arrayListMag, arrayListDepth, arrayListLocation);

        filterApplyButton.setOnClickListener(view -> filterConstraintLayout.animate()
                .translationY(-filterConstraintLayout.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        runFilterAndSort(locationModelList);

                        filterConstraintLayout.animate().setListener(null);
                        filterSortCardView.setVisibility(View.GONE);
                        filterConstraintLayout.setVisibility(View.GONE);
                    }
                }));
        filterCancelButton.setOnClickListener(view -> filterConstraintLayout.animate()
                .translationY(-filterConstraintLayout.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        FilterManager.clearAll();
                        fromDateTextButton.setText(fromDateMinAvailableDate);
                        toDateTextButton.setText(toDateMaxAvailableDate);
                        magSpinner.setSelection(0);
                        depthSpinner.setSelection(0);
                        locationSpinner.setSelection(0);

                        runFilterAndSort(locationModelList);

                        filterConstraintLayout.animate().setListener(null);
                        filterSortCardView.setVisibility(View.GONE);
                        filterConstraintLayout.setVisibility(View.GONE);
                    }
                }));

        // Date range
        fromDateTextButton.setOnClickListener(v -> {
            Calendar localCalendar = Calendar.getInstance();
            if (date_minimal != null) {
                localCalendar.setTime(date_minimal);
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(appMainContext, getDialogTheme(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                String date = simpleDateFormat.format(calendar.getTime());
                fromDateTextButton.setText(date);
                date_minimal = calendar.getTime();

                ArrayList<String> dates = new ArrayList<>();
                dates.add(simpleDateFormat.format(date_minimal));
                if (date_maximal == null) {
                    date_maximal = new Date();
                }
                dates.add(simpleDateFormat.format(date_maximal));
                addSelectionToFilterSelected(R.id.fromDateTextButton, dates);
            }, localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar.DAY_OF_MONTH));

            try {
                datePickerDialog.getDatePicker().setMinDate(Objects.requireNonNull(simpleDateFormat.parse(fromDateMinAvailableDate)).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        toDateTextButton.setOnClickListener(v -> {
            Calendar localCalendar = Calendar.getInstance();
            if (date_maximal != null) {
                localCalendar.setTime(date_maximal);
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(appMainContext, getDialogTheme(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                String date = simpleDateFormat.format(calendar.getTime());
                toDateTextButton.setText(date);
                date_maximal = calendar.getTime();

                ArrayList<String> dates = new ArrayList<>();
                if (date_minimal == null) {
                    try {
                        date_minimal = simpleDateFormat.parse(fromDateMinAvailableDate);
                    } catch (ParseException e) {
                        date_minimal = new Date();
                        e.printStackTrace();
                    }
                }
                dates.add(simpleDateFormat.format(date_minimal));
                dates.add(simpleDateFormat.format(date_maximal));
                addSelectionToFilterSelected(R.id.toDateTextButton, dates);
            }, localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar.DAY_OF_MONTH));
            try {
                datePickerDialog.getDatePicker().setMinDate(Objects.requireNonNull(simpleDateFormat.parse(fromDateMinAvailableDate)).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    private int getDialogTheme() {
//        int nightModeFlags = getResources().getConfiguration().uiMode &
//                Configuration.UI_MODE_NIGHT_MASK;
        int dialogTheme = android.R.style.Theme_DeviceDefault_Dialog;
//        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
//            dialogTheme = android.R.style.Theme_DeviceDefault_Light_Dialog;
//        }
        return dialogTheme;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        ArrayList<String> arrayList = new ArrayList<>();
        if (!item.contains("All")) { // By not adding the item, will cause reset
            arrayList.add(item);
        }
        addSelectionToFilterSelected(parent.getId(), arrayList);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void addSelectionToFilterSelected(int objectId, ArrayList<String> selectedValues) {
        switch (objectId) {
            case R.id.fromDateTextButton:
            case R.id.toDateTextButton:
                FilterManager.Preferences.filters.get(Filter.INDEX_DATE).setSelected(selectedValues);
                break;
            case R.id.magSpinner:
                FilterManager.Preferences.filters.get(Filter.INDEX_MAGNITUDE).setSelected(selectedValues);
                break;
            case R.id.depthSpinner:
                FilterManager.Preferences.filters.get(Filter.INDEX_DEPTH).setSelected(selectedValues);
                break;
            case R.id.locationSpinner:
                FilterManager.Preferences.filters.get(Filter.INDEX_LOCATION).setSelected(selectedValues);
                break;
        }
    }


    private void runFilterAndSort(ArrayList<LocationModel> locationModelList) {
        locationModelListFiltered = FilterManager.filterList(new ArrayList<>(locationModelList));
        locationModelListFiltered = sortRecyclerList(sortRG.getCheckedRadioButtonId(), new ArrayList<>(locationModelListFiltered), true);

        locationCardAdapter = new LocationCardAdapter(appMainContext, locationModelListFiltered);
        recyclerView.setAdapter(locationCardAdapter);
        MapsFragment.runClusterManager(appMainContext, locationModelListFiltered);
    }

    // Implementation of AsyncTask used to download XML feed from url
    private class GetLocationListAndSetData extends AsyncTask<String, Void, ArrayList<LocationModel>> {
        @Override
        protected ArrayList<LocationModel> doInBackground(String... urls) {
            try {
                return new LoadXmlFromNetwork().loadXml(urls[0]);
            } catch (IOException | XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<LocationModel> result) {
            locationModelList = result;
            initialiseRecyclerViewValues(result);
            MapsFragment.runClusterManager(appMainContext, result);
        }
    }
}


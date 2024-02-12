package it.uniba.dib.sms232417.fithub.coach.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.Map;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterAthlete;
import it.uniba.dib.sms232417.fithub.entity.Treatment;
import it.uniba.dib.sms232417.fithub.entity.WorkoutPlan;
import it.uniba.dib.sms232417.fithub.interfaces.OnTreatmentsCallback;

public class TreatmentFormNotesFragment extends Fragment {

    private WorkoutPlan workoutPlan;
    private String notesString;
    private String patientUUID;
    private String patientName;
    private String patientAge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.workout_days), getResources().getString(R.string.notes)};

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_treatment_form_notes, container, false);


        StateProgressBar stateProgressBar = (StateProgressBar) view.findViewById(R.id.state_progress_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        patientName = "";
        patientAge = "";
        patientUUID = "";

        workoutPlan = null;
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            workoutPlan = bundle.getParcelable("treatment");
            patientUUID = bundle.getString("patientUUID");
            patientName = bundle.getString("patientName");
            patientAge = bundle.getString("patientAge");
        }

        Button btnContinue = requireView().findViewById(R.id.goNext);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get notes
                TextInputEditText notes = requireView().findViewById(R.id.notesEditText);

                notesString = "";
                if (!notes.getText().toString().isEmpty()) {
                    notesString = notes.getText().toString();
                    workoutPlan.setNotes(notesString);
                }


                View bottomNavView = requireActivity().findViewById(R.id.nav_view);

                // ADD TREATMENT TO DB
                DatabaseAdapterAthlete dbAdapter = new DatabaseAdapterAthlete(getContext());
                /*
                dbAdapter.addTreatment(patientUUID, workoutPlan, new OnTreatmentsCallback() {
                    @Override
                    public void onCallback(Map<String, Treatment> treatments) {
                        View rootView = requireActivity().findViewById(android.R.id.content);
                        Snackbar snackbar = Snackbar.make(rootView, getResources().getString(R.string.treatment_added_successfully), Snackbar.LENGTH_LONG);
                        // Set the anchor view to bottom navigation view to show the snackbar above the bottom navigation view
                        snackbar.setAnchorView(bottomNavView);
                        snackbar.show();
                        Log.d("TreatmentAdded", "Treatment added successfully");
                    }


                    @Override
                    public void onCallbackFailed(Exception e) {

                        View rootView = requireActivity().findViewById(android.R.id.content);
                        Snackbar snackbar = Snackbar.make(rootView, getResources().getString(R.string.error_occured_treatment), Snackbar.LENGTH_LONG);
                        // Set the anchor view to bottom navigation view to show the snackbar above the bottom navigation view
                        snackbar.setAnchorView(bottomNavView);
                        snackbar.show();

                    }
                });
                */

                AthleteFragment athleteFragment = new AthleteFragment();
                // Create a new bundle to pass the selected tab index
                Bundle bundleWithSelectedTab = new Bundle();
                bundleWithSelectedTab.putString("patientUUID", patientUUID);
                bundleWithSelectedTab.putString("patientName", patientName);
                bundleWithSelectedTab.putString("patientAge", patientAge);
                bundleWithSelectedTab.putInt("selectedTab", 1); // 1 is the index of the Treatment tab


                athleteFragment.setArguments(bundleWithSelectedTab);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.nav_host_fragment_activity_main, athleteFragment);

                //transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button btnBack = requireView().findViewById(R.id.goBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStack();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

}
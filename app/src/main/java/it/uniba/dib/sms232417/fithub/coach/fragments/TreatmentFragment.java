package it.uniba.dib.sms232417.fithub.coach.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.MaterialContainerTransform;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterAthlete;
import it.uniba.dib.sms232417.fithub.entity.Exercise;
import it.uniba.dib.sms232417.fithub.entity.Medication;
import it.uniba.dib.sms232417.fithub.entity.Treatment;
import it.uniba.dib.sms232417.fithub.entity.WorkoutDay;
import it.uniba.dib.sms232417.fithub.entity.WorkoutPlan;
import it.uniba.dib.sms232417.fithub.interfaces.OnWorkoutPlanCallback;
import it.uniba.dib.sms232417.fithub.utilities.MappedValues;


public class TreatmentFragment extends Fragment {

    private String patientUUID;
    private String patientName;
    private String patientAge;
    private String user;
    private ExtendedFloatingActionButton fab;
    private FloatingActionButton share;
    private Map<String, Treatment> treatments;
    private Map<String, WorkoutPlan> exercises;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patientUUID = "";
        patientName = "";
        patientAge = "";
        user = ""; // Type of user: "patient" or "doctor"

        fab = view.findViewById(R.id.fab);
        share = view.findViewById(R.id.share);

        share.hide();

        // Create an instance of DatabaseAdapterPatient
        DatabaseAdapterAthlete adapter = new DatabaseAdapterAthlete(requireContext());

        if (this.getArguments() != null) {
            patientUUID = this.getArguments().getString("patientUUID");
            patientName = this.getArguments().getString("patientName");
            patientAge = this.getArguments().getString("patientAge");
            user = this.getArguments().getString("user");
            if (user == null) {
                user = "";
            }
        }

        adapter.getWorkoutPlan(patientUUID, new OnWorkoutPlanCallback() {
            @Override
            public void onCallback(Map<String, WorkoutPlan> workoutPlans) {
                Log.d("WorkoutPlan", "WorkoutPlan: " + workoutPlans.toString());
                TreatmentFragment.this.exercises = workoutPlans;

                if (workoutPlans == null || workoutPlans.isEmpty()) {
                    LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View noTreatmentLayout = inflater.inflate(R.layout.no_treatments_found_layout, null);
                    // Add the inflated layout to the parent layout
                    LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);
                    parentLayout.addView(noTreatmentLayout);
                } else {
                    Iterator<Map.Entry<String, WorkoutPlan>> iterator = workoutPlans.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, WorkoutPlan> entry = iterator.next();
                        String workoutPlanID = entry.getKey();
                        WorkoutPlan workoutPlan = entry.getValue();

                        // Add the treatment to the parent layout
                        // Check if it is the last treatment
                        // if it is last treatment then a bottom margin of 85dp is added to the last treatment layout
                        addTreatmentCardView(workoutPlanID, workoutPlan, !iterator.hasNext());
                    }
                    /*
                    share.show();

                    // Set the OnClickListener for the share button here

                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Check if the application has the WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                // If the application has the permissions, call the createAndSharePdf method
                                createAndSharePdf(treatments);
                            } else {
                                // If the application does not have the permissions, request them from the user
                                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                            }
                        }
                    });ù
                        */
                }

            }

            @Override
            public void onCallbackFailed(Exception e) {

            }
        });


        /*

        adapter.getWorkoutPlan(patientUUID, new OnTreatmentsCallback() {
            @Override
            public void onCallback(Map<String, Treatment> treatments) {
                TreatmentFragment.this.treatments = treatments;

                if (treatments == null || treatments.isEmpty()) {
                    LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View noTreatmentLayout = inflater.inflate(R.layout.no_treatments_found_layout, null);
                    // Add the inflated layout to the parent layout
                    LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);
                    parentLayout.addView(noTreatmentLayout);
                } else {
                    Iterator<Map.Entry<String, Treatment>> iterator = treatments.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Treatment> entry = iterator.next();
                        String treatmentId = entry.getKey();
                        Treatment treatment = entry.getValue();

                        // Add the treatment to the parent layout
                        // Check if it is the last treatment
                        // if it is last treatment then a bottom margin of 85dp is added to the last treatment layout
                        addTreatmentCardView(treatmentId, treatment, !iterator.hasNext());
                    }
                    /*
                    share.show();

                    // Set the OnClickListener for the share button here

                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Check if the application has the WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                // If the application has the permissions, call the createAndSharePdf method
                                createAndSharePdf(treatments);
                            } else {
                                // If the application does not have the permissions, request them from the user
                                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                            }
                        }
                    });ù

                }
            }

            @Override
            public void onCallbackFailed(Exception e) {
                Log.e("Error", "Failed to get treatments", e);
                LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View noTreatmentLayout = inflater.inflate(R.layout.no_treatments_found_layout, null);
                // Add the inflated layout to the parent layout
                LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);
                parentLayout.addView(noTreatmentLayout);
            }
        });

         */


        // register the nestedScrollView from the main layout
        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // handle the nestedScrollView behaviour with OnScrollChangeListener
        // to extend or shrink the Extended Floating Action Button
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // the delay of the extension of the FAB is set for 12 items
                if (scrollY > oldScrollY + 12 && fab.isExtended()) {
                    fab.shrink();
                    share.hide();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !fab.isExtended()) {
                    fab.extend();
                    share.show();
                }

                // if the nestedScrollView is at the first item of the list then the
                // extended floating action should be in extended state
                if (scrollY == 0) {
                    fab.extend();
                    share.show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the transition name for the FAB
                fab.setTransitionName("shared_element_container");

                // Create an instance of MaterialContainerTransform
                MaterialContainerTransform transform = new MaterialContainerTransform();
                transform.setDuration(600);
                transform.setScrimColor(Color.TRANSPARENT);
                transform.setAllContainerColors(requireContext().getResources().getColor(R.color.md_theme_light_surface));

                // Set the shared element enter transition for the fragment
                TreatmentFormGeneralFragment treatmentFormGeneralFragment = new TreatmentFormGeneralFragment();
                treatmentFormGeneralFragment.setSharedElementEnterTransition(transform);

                // Create a bundle and put patientUUID, patientName, and patientAge into it
                Bundle bundle = new Bundle();
                bundle.putString("patientUUID", patientUUID);
                bundle.putString("patientName", patientName);
                bundle.putString("patientAge", patientAge);

                // Set the bundle as arguments to the fragment
                treatmentFormGeneralFragment.setArguments(bundle);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Add the shared element to the transaction
                transaction.addSharedElement(fab, fab.getTransitionName());

                transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormGeneralFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }


    protected void addTreatmentCardView(String treatmentId, WorkoutPlan workoutPlan, boolean isLast) {
        String workoutPlanTarget, notes;
        int i, j;
        ArrayList<Exercise> exercises;
        ArrayList<WorkoutDay> workoutDays;
        Exercise exercise;


        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View treatmentLayout = inflater.inflate(R.layout.treatment_layout, null);

        float density = getResources().getDisplayMetrics().density;

        if (isLast) {
            int bottomMarginDp = 140;
            int bottomMarginPx = (int) (bottomMarginDp * density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, bottomMarginPx);
            treatmentLayout.setLayoutParams(params);
        }

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.linearLayoutCardView);

        // TREATMENT TARGET -> Workout Plan targert
        workoutPlanTarget = workoutPlan.getWorkoutTarget();
        TextView treatmentTargetText = treatmentLayout.findViewById(R.id.treatmentTarget);
        treatmentTargetText.setText(workoutPlanTarget);

        // DATE
        TextView dateText = treatmentLayout.findViewById(R.id.dateText);
        if (workoutPlan.getEndDateString().equals("")) {
            dateText.setText(workoutPlan.getStartDateString() + " - " + getResources().getString(R.string.ongoing));
        } else {
            dateText.setText(workoutPlan.getStartDateString() + " - " + workoutPlan.getEndDateString());
        }

        // MEDICATIONS
        LinearLayout medicationsLayout = treatmentLayout.findViewById(R.id.linearLayoutMedications);
        workoutDays = workoutPlan.getWorkoutDays();

        for (i = 0; i < workoutDays.size(); i++) {
            // Aggiungere la textView per il titolo del workoutDay
            /*
            <TextView
        android:id="@+id/workoutDay"
        style="@style/TitleText3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="2dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="0dp"
        android:text="Day 1" />
             */

            // Create a new TextView
            TextView workoutDayTextView = new TextView(requireContext()); // Use 'getActivity()' instead of 'this' if you're in a fragment

            // Set the TextView properties
            //workoutDayTextView.setId(R.id.workoutDay);
            workoutDayTextView.setText("Day 1");
            workoutDayTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            medicationsLayout.addView(workoutDayTextView);

            exercises = workoutDays.get(i).getExercises(); //getExerciseList();
            for (j = 0; j < exercises.size(); j++) {
                exercise = exercises.get(j);
                medicationsLayout.addView(getMedicationLayout(exercise));
                Log.d("Exercise " + j, "Exercise: " + exercise.toString());
            }
        }


        parentLayout.addView(treatmentLayout);


        // NOTES
        notes = workoutPlan.getNotes();
        if (notes == null || notes.equals("")) {
            LinearLayout linearLayoutNotes = treatmentLayout.findViewById(R.id.linearLayoutNotes);
            linearLayoutNotes.setVisibility(View.GONE);
        } else {
            TextView notesText = treatmentLayout.findViewById(R.id.notes);
            notesText.setText(notes);
        }

        // DELETE BUTTON
        // Find the delete button
        Button deleteButton = treatmentLayout.findViewById(R.id.deleteButton);

        if ("patient".equals(user)) {
            deleteButton.setVisibility(View.GONE);
            // set treatmentTarget textView marginTop to 16
            // Convert dp to pixels
            int startMarginPx = (int) (16 * density);
            int endMarginPx = (int) (4 * density);
            int topMarginPx = (int) (8 * density);
            int bottomMarginPx = (int) (8 * density);

            // Get the current LayoutParams and set the new margins
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) treatmentTargetText.getLayoutParams();
            params.setMargins(startMarginPx, topMarginPx, endMarginPx, bottomMarginPx);

            // Apply the new LayoutParams to the TextView
            treatmentTargetText.setLayoutParams(params);
        } else {

            // Create an instance of DatabaseAdapterPatient
            DatabaseAdapterAthlete adapter = new DatabaseAdapterAthlete(requireContext());
            // Set an OnClickListener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete the treatment from your data source
                    // You might need to create a method in your DatabaseAdapterPatient class to delete a treatment by its id
                    new MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
                            .setTitle(getResources().getString(R.string.delete_treatment))
                            .setMessage(getResources().getString(R.string.delete_treatment_msg))
                            .setNegativeButton(getResources().getString(R.string.keep), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Respond to negative button press
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Respond to positive button press
                                    adapter.deleteTreatment(patientUUID, treatmentId);

                                    // Remove the treatmentLayout from the parentLayout
                                    parentLayout.removeView(treatmentLayout);

                                    // If there are no more treatments, show the noTreatmentLayout
                                    if (parentLayout.getChildCount() == 0) {
                                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View noTreatmentLayout = inflater.inflate(R.layout.no_treatments_found_layout, null);
                                        // Add the inflated layout to the parent layout
                                        parentLayout.addView(noTreatmentLayout);
                                    }

                                }
                            })
                            .create()
                            .show();

                }
            });
        }
    }


    protected View getMedicationLayout(Exercise exercise) {
        MappedValues mappedValues = new MappedValues(requireContext());
        String muscleGroup, nameExercise, numSets, numReps, restTime;
        ArrayList<WeekdaysDataItem> selectedWeekdays;


        muscleGroup = exercise.getMuscleGroup();
        nameExercise = exercise.getName();
        numSets = exercise.getSets();
        numReps = exercise.getReps();
        restTime = exercise.getRest();


        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View medicationLayout = inflater.inflate(R.layout.medication_layout, null);

        // Exercise NAME
        TextView exerciseNameText = medicationLayout.findViewById(R.id.exerciseName);
        exerciseNameText.setText("\u2022 " + nameExercise); //exercises.get(i).getExerciseName());

        // Muscle Group
        TextView muscleGroupText = medicationLayout.findViewById(R.id.muscleGroup); //steps x Reps
        muscleGroupText.setText(muscleGroup);

        // Sets and Reps
        TextView setsAndRepsStr = medicationLayout.findViewById(R.id.setsAndReps);
        setsAndRepsStr.setText(numSets + " " + getResources().getString(R.string.sets) + " x " + numReps + " " + getResources().getString(R.string.reps)); //exercises.get(i).getExerciseName());

        // Rest Time
        TextView medicationNameText = medicationLayout.findViewById(R.id.restTime);
        medicationNameText.setText(restTime);

        TextView restTimeText = medicationLayout.findViewById(R.id.restTime);
        restTimeText.setText(restTime + " min");
        /*
        if (exercise.getHowRegularly() == 0) {
            // Daily
            setsAndRepsStr.setText(numSets);
        } else {
            if (exercise.getHowRegularly() == 1) {
                // Interval
                setsAndRepsStr.setText(mappedValues.getFormattedInterval(exercise.getIntervalSelectedType(), exercise.getIntervalSelectedNumber()));
            } else {
                // Weekdays
                String selectedWeekdaysString;

                selectedWeekdaysString = exercise.getSelectedWeekdaysString();

                setsAndRepsStr.setText(selectedWeekdaysString);
            }
        }

        // INTAKES
        TextView intakesText = medicationLayout.findViewById(R.id.intakes);
        // Quantity How to take at time
        int size = intakeTimes.size();
        int i;
        int quantityNumber;

        String quantity;
        String intakeTime;
        String intakesString;
        intakesString = "";

        for (i = 0; i < size; i++) {
            quantity = quantities.get(i);
            intakeTime = intakeTimes.get(i);

            if (quantity.equals("1/4") || quantity.equals("1/2") || quantity.equals("3/4")) {
                quantityNumber = 1; // Not plural
            } else {
                quantityNumber = 2; // Plural
            }

            intakesString = intakesString + quantity + " " + (mappedValues.getFormattedHowToTake(mappedValues.getHowToTakeKey(nameExercise), quantityNumber)).toLowerCase() + " " + requireContext().getResources().getString(R.string.at_time) + " " + intakeTime;

            if (i != size - 1) {
                intakesString = intakesString + "\n";
            }
        }

        intakesText.setText(intakesString); //rest time
        */
        return medicationLayout;
    }

    public ExtendedFloatingActionButton getFab() {
        return fab;
    }


    @Override
    public void onResume() {
        super.onResume();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private void createAndSharePdf(Map<String, Treatment> treatments) {
        // Create a new PdfDocument
        PdfDocument pdfDocument = new PdfDocument();
        MappedValues mappedValues = new MappedValues(requireContext());

        // Start a page with default page info
        int pageHeight = 842;
        int pageWidth = 595;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Create a Canvas from the started page
        Canvas canvas = page.getCanvas();

        // Draw the text on the Canvas
        Paint paintRegular = new Paint();
        paintRegular.setTextSize(16);

        // Get the font from res/font directory
        Typeface ember_light = ResourcesCompat.getFont(requireContext(), R.font.ember_light);
        // Set the typeface to the Paint object
        paintRegular.setTypeface(ember_light);
        paintRegular.setColor(getResources().getColor(R.color.seed));

        // Draw the text on the Canvas
        Paint paintTitle = new Paint();
        paintTitle.setTextSize(36);

        // Get the font from res/font directory
        Typeface ember_display_light = ResourcesCompat.getFont(requireContext(), R.font.ember_display_light);
        // Set the typeface to the Paint object
        paintTitle.setTypeface(ember_display_light);
        paintTitle.setColor(getResources().getColor(R.color.seed));

        // Draw the text on the Canvas
        Paint paintSubtitle = new Paint();
        paintSubtitle.setTextSize(20);

        // Get the font from res/font directory
        Typeface ember_bold = ResourcesCompat.getFont(requireContext(), R.font.ember_bold);
        // Set the typeface to the Paint object
        paintSubtitle.setTypeface(ember_bold);
        paintSubtitle.setColor(getResources().getColor(R.color.seed));

        // Draw the text on the Canvas
        Paint paintSubtitle2 = new Paint();
        paintSubtitle2.setTextSize(16);
        paintSubtitle2.setColor(getResources().getColor(R.color.seed));

        // Set the typeface to the Paint object
        paintSubtitle2.setTypeface(ember_bold);

        // TITLE
        canvas.drawText(getResources().getString(R.string.treatments), 210, 50, paintTitle);

        // Iterate through the treatments
        int i;
        int x = 40;
        int y = 100;
        // Get the iterator
        Iterator<Map.Entry<String, Treatment>> iterator = treatments.entrySet().iterator();

        // Loop through the treatments
        while (iterator.hasNext()) {
            Map.Entry<String, Treatment> entry = iterator.next();

            // Get the treatment
            Treatment treatment = entry.getValue();

            // Check if y exceeds pageHeight
            if (y > pageHeight - 50) {
                // If y exceeds pageHeight, finish the current page and start a new one
                pdfDocument.finishPage(page);
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();

                // Reset y to the starting y-coordinate for the new page
                y = 50;
            }

            // TREATMENT TARGET
            canvas.drawText(treatment.getTreatmentTarget(), x, y, paintSubtitle);
            y = y + 30;

            // DURATION
            if (treatment.getEndDateString().isEmpty()) {
                // Ongoing treatment
                canvas.drawText(treatment.getStartDateString() + " - " + getResources().getString(R.string.ongoing), x, y, paintRegular);
            } else {
                canvas.drawText(treatment.getStartDateString() + " - " + treatment.getEndDateString(), x, y, paintRegular);
            }

            y = y + 30;

            // MEDICATIONS
            ArrayList<Medication> medications = treatment.getMedications();
            Iterator<Medication> medicationIterator = medications.iterator();
            while (medicationIterator.hasNext()) {
                Medication medication = medicationIterator.next();

                // Check if y exceeds pageHeight
                if (y > pageHeight - 50) {
                    // If y exceeds pageHeight, finish the current page and start a new one
                    pdfDocument.finishPage(page);
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Reset y to the starting y-coordinate for the new page
                    y = 50;
                }
                // MEDICATION NAME
                canvas.drawText("\u2022 " + medication.getMedicationName(), x, y, paintRegular);
                y = y + 20;


                paintRegular.setColor(getResources().getColor(R.color.md_theme_light_tertiary));
                // INTAKES
                // Check if y exceeds pageHeight
                if (y > pageHeight - 50) {
                    // If y exceeds pageHeight, finish the current page and start a new one
                    pdfDocument.finishPage(page);
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Reset y to the starting y-coordinate for the new page
                    y = 50;
                }
                if (medication.getHowRegularly() == 0) {
                    // Daily
                    canvas.drawText(medication.toStringHowRegularly(requireContext()), x + 10, y, paintRegular);
                } else {
                    if (medication.getHowRegularly() == 1) {
                        // Interval
                        canvas.drawText(mappedValues.getFormattedInterval(medication.getIntervalSelectedType(), medication.getIntervalSelectedNumber()), x + 10, y, paintRegular);
                    } else {
                        // Weekdays
                        String selectedWeekdaysString;

                        selectedWeekdaysString = medication.getSelectedWeekdaysString();

                        canvas.drawText(selectedWeekdaysString, x + 10, y, paintRegular);
                    }
                }
                paintRegular.setColor(getResources().getColor(R.color.seed));

                y = y + 20;

                // INTAKES
                // Quantity How to take at time
                int size = medication.getIntakesTime().size();
                int quantityNumber;

                String quantity;
                String intakeTime;
                String intakesString;

                for (i = 0; i < size; i++) {
                    quantity = medication.getQuantities().get(i);
                    intakeTime = medication.getIntakesTime().get(i);

                    if (quantity.equals("1/4") || quantity.equals("1/2") || quantity.equals("3/4")) {
                        quantityNumber = 1; // Not plural
                    } else {
                        quantityNumber = 2; // Plural
                    }

                    intakesString = quantity + " " + (mappedValues.getFormattedHowToTake(mappedValues.getHowToTakeKey(medication.toStringHowToTake(requireContext())), quantityNumber)).toLowerCase() + " " + requireContext().getResources().getString(R.string.at_time) + " " + intakeTime;

                    // Check if y exceeds pageHeight
                    if (y > pageHeight - 50) {
                        // If y exceeds pageHeight, finish the current page and start a new one
                        pdfDocument.finishPage(page);
                        page = pdfDocument.startPage(pageInfo);
                        canvas = page.getCanvas();

                        // Reset y to the starting y-coordinate for the new page
                        y = 50;
                    }
                    canvas.drawText(intakesString, x + 10, y, paintRegular);

                    if (i != size - 1) {
                        y = y + 20;
                    }
                }

                y = y + 25;


            }

            if (!treatment.getNotes().isEmpty()) {
                // Check if y exceeds pageHeight
                if (y > pageHeight - 50) {
                    // If y exceeds pageHeight, finish the current page and start a new one
                    pdfDocument.finishPage(page);
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Reset y to the starting y-coordinate for the new page
                    y = 50;
                }
                canvas.drawText(getResources().getString(R.string.notes) + ": " + treatment.getNotes(), x, y, paintRegular);
                y = y + 40;
            }

        }

        // Finish the page
        pdfDocument.finishPage(page);

        // Write the PdfDocument to a file
        File file = new File(requireContext().getExternalFilesDir(null), getResources().getString(R.string.treatments) + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the PdfDocument
        pdfDocument.close();

        // Share the file
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        // Use FileProvider to get a content URI
        Uri fileUri = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

        // Grant temporary read permission to the content URI
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.setType("application/pdf");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // If the permissions are granted, call the createAndSharePdf method
                createAndSharePdf(treatments);
            } else {
                // If the permissions are denied, show a message to the user explaining why the permissions are needed
                Toast.makeText(requireContext(), "Storage permissions are required to create and share a PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

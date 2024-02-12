package it.uniba.dib.sms232417.fithub.coach.fragments;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.entity.Exercise;
import it.uniba.dib.sms232417.fithub.entity.Treatment;
import it.uniba.dib.sms232417.fithub.entity.WorkoutPlan;
import it.uniba.dib.sms232417.fithub.utilities.MappedValues;

public class ExercisesFragment extends Fragment {


    private static int intakeCount = 1;

    private int selectedReps;
    private int selectedSeconds;
    private ArrayList<WeekdaysDataItem> selectedWeekdays = new ArrayList<>();
    private boolean isMilliliters = false;


    private List<Exercise> exercises;
    private WorkoutPlan workoutPlan;

    private boolean validInput;
    private String patientUUID;
    private String patientName;
    private String patientAge;
    private static int workoutDaysNumber = 1;
    private boolean isSelectedMuscleGroup = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        String[] descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.workout_days), getResources().getString(R.string.notes)};

        StateProgressBar stateProgressBar = view.findViewById(R.id.state_progress_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);

        return view;
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        patientName = "";
        patientAge = "";
        patientUUID = "";

        // Get the bundle
        Bundle bundle = this.getArguments();
        validInput = false;
        workoutPlan = null;
        selectedReps = -1;
        selectedSeconds = -1;

        if (bundle != null) {
            workoutPlan = bundle.getParcelable("workoutPlan");
            Log.d("WorkoutPlan (ExercisesFragment): ", workoutPlan.toString());
            patientName = bundle.getString("patientName");
            patientAge = bundle.getString("patientAge");
            patientUUID = bundle.getString("patientUUID");
        }

        //Initialize the list of execises
        exercises = new ArrayList<>();

        TextView intakeLabel = view.findViewById(R.id.intakeLabel);
        intakeLabel.setText(getResources().getString(R.string.exercise) + " " + intakeCount);

        LinearLayout parentLayout = requireView().findViewById(R.id.linearLayoutIntake);

        TextView titleText = view.findViewById(R.id.titleText);

        titleText.setText(getResources().getQuantityString(R.plurals.days, 1, 1) + " " + workoutDaysNumber);

        AutoCompleteTextView muscleGroup = parentLayout.findViewById(R.id.muscleGroup);
        AutoCompleteTextView setsSelection = parentLayout.findViewById(R.id.setsNumber);
        AutoCompleteTextView repsSelection = parentLayout.findViewById(R.id.repsNumber);
        AutoCompleteTextView intervalSelection = parentLayout.findViewById(R.id.restSelection);

        // Creare un nuovo ArrayAdapter
        String[] muscleGroups = getResources().getStringArray(R.array.musclegroup_array);
        ArrayAdapter<String> adapterMuscleGroup = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, muscleGroups);
        muscleGroup.setAdapter(adapterMuscleGroup);



        Button btnAddIntake = view.findViewById(R.id.btnAddIntake);
        btnAddIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewIntakeLayout();
                updateIntakeLabels();
            }
        });



        AutoCompleteTextView exercise = parentLayout.findViewById(R.id.exerciseString);

        

        muscleGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] exercises;
                if (position == 0) {
                    exercises = getResources().getStringArray(R.array.chest_exercise_array);
                } else if (position == 1) {
                    exercises = getResources().getStringArray(R.array.back_exercise_array);
                } else if (position == 2) {
                    exercises = getResources().getStringArray(R.array.shoulders_exercise_array);
                } else if (position == 3) {
                    exercises = getResources().getStringArray(R.array.biceps_exercise_array);
                } else if (position == 4) {
                    exercises = getResources().getStringArray(R.array.triceps_exercise_array);
                } else if (position == 5) {
                    exercises = getResources().getStringArray(R.array.legs_and_glutes_exercise_array);
                } else {
                    exercises = getResources().getStringArray(R.array.abdominal_exercise_array);

                }
                isSelectedMuscleGroup = true;
                TextInputLayout muscleGroupInputLayout = parentLayout.findViewById(R.id.muscleGroupInputLayout);
                muscleGroupInputLayout.setError(null);
                ArrayAdapter<String> adapterExercises = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, exercises);
                exercise.setAdapter(adapterExercises);
            }
        });

        exercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isSelectedMuscleGroup){
                    TextInputLayout muscleGroupInputLayout = parentLayout.findViewById(R.id.muscleGroupInputLayout);
                    muscleGroupInputLayout.setError(getResources().getString(R.string.muscle_group_first));
                    Toast.makeText(requireActivity(), getResources().getString(R.string.muscle_group_first), Toast.LENGTH_SHORT).show();
                }
            }
        });

        setsSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetsDialog(parentLayout);
            }
        });

        setsSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showSetsDialog(parentLayout);
                }
                return false;
            }
        });

        repsSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showRepsDialog(parentLayout);
                }
                return false;
            }
        });

        intervalSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showRestDialog(parentLayout);
                }
                return false;
            }
        });



        Button btnContinue = requireView().findViewById(R.id.goNext);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    // Get the bundle
                    Bundle bundle = setBundle();

                    bundle.putString("patientName", patientName);
                    bundle.putString("patientAge", patientAge);
                    bundle.putString("patientUUID", patientUUID);

                    TreatmentFormNotesFragment treatmentFormNotesFragment = new TreatmentFormNotesFragment();
                    treatmentFormNotesFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormNotesFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(requireActivity(), getResources().getString(R.string.fill_inputs), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnBack = requireView().findViewById(R.id.goBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        Button btnAddMedication = requireView().findViewById(R.id.addMedication);

        btnAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the current fragment
                if (validateInput()) {
                    // Get the bundle
                    Bundle bundle = setBundle();

                    workoutDaysNumber++;

                    bundle.putString("patientName", patientName);
                    bundle.putString("patientAge", patientAge);
                    bundle.putString("patientUUID", patientUUID);

                    /*
                    TreatmentFormMedicationsFragment treatmentFormMedicationsFragment = new TreatmentFormMedicationsFragment();
                    treatmentFormMedicationsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormMedicationsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                     */
                    ExercisesFragment exercisesFragment = new ExercisesFragment();
                    exercisesFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, exercisesFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(requireActivity(), getResources().getString(R.string.fill_inputs), Toast.LENGTH_SHORT).show();
                }
            }
        });


        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                workoutPlan.removeExerciseAtIndex(workoutPlan.getExercises().size() - 1);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    private void addNewIntakeLayout() {
        // Increment intakeCount
        intakeCount++;
        // Inflate the layout from XML file
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View intakeLayout = inflater.inflate(R.layout.add_intake_layout, null);

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Get the index of the second last view in parentLayout
        int index = parentLayout.getChildCount() - 2;

        AutoCompleteTextView muscleGroup = intakeLayout.findViewById(R.id.muscleGroup);
        AutoCompleteTextView exercise = intakeLayout.findViewById(R.id.exerciseString);

        // Creare un nuovo ArrayAdapter
        String[] muscleGroups = getResources().getStringArray(R.array.musclegroup_array);
        ArrayAdapter<String> adapterMuscleGroup = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, muscleGroups);
        muscleGroup.setAdapter(adapterMuscleGroup);

        muscleGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] exercises;
                if (position == 0) {
                    exercises = getResources().getStringArray(R.array.chest_exercise_array);
                } else if (position == 1) {
                    exercises = getResources().getStringArray(R.array.back_exercise_array);
                } else if (position == 2) {
                    exercises = getResources().getStringArray(R.array.shoulders_exercise_array);
                } else if (position == 3) {
                    exercises = getResources().getStringArray(R.array.biceps_exercise_array);
                } else if (position == 4) {
                    exercises = getResources().getStringArray(R.array.triceps_exercise_array);
                } else if (position == 5) {
                    exercises = getResources().getStringArray(R.array.legs_and_glutes_exercise_array);
                } else {
                    exercises = getResources().getStringArray(R.array.abdominal_exercise_array);

                }
                isSelectedMuscleGroup = true;
                TextInputLayout muscleGroupInputLayout = parentLayout.findViewById(R.id.muscleGroupInputLayout);
                muscleGroupInputLayout.setError(null);
                ArrayAdapter<String> adapterExercises = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, exercises);
                exercise.setAdapter(adapterExercises);
            }
        });

        exercise.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isSelectedMuscleGroup){
                    TextInputLayout muscleGroupInputLayout = parentLayout.findViewById(R.id.muscleGroupInputLayout);
                    muscleGroupInputLayout.setError(getResources().getString(R.string.muscle_group_first));
                    Toast.makeText(requireActivity(), getResources().getString(R.string.muscle_group_first), Toast.LENGTH_SHORT).show();
                }else{
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    Log.d("Selected Exercize: ", selectedItem);
                }
            }
        });


        // Add the new layout to the parent layout at the index of the "Add Intake" button
        parentLayout.addView(intakeLayout, index);
        AutoCompleteTextView setsSelection = intakeLayout.findViewById(R.id.setsNumber);

        setsSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showSetsDialog(intakeLayout);
                }
                return false;
            }
        });

        AutoCompleteTextView repsSelection = intakeLayout.findViewById(R.id.repsNumber);

        repsSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showRepsDialog(intakeLayout);
                }
                return false;
            }
        });

        AutoCompleteTextView intervalSelection = intakeLayout.findViewById(R.id.restSelection);
        intervalSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showRestDialog(intakeLayout);
                }
                return false;
            }
        });

        TextView intakeLabel = intakeLayout.findViewById(R.id.intakeLabel);

        intakeLabel.setText(getResources().getString(R.string.exercise) + " " + intakeCount);
        // Find the close button in the layout
        Button closeButton = intakeLayout.findViewById(R.id.closeButton);

        updateIntakeLabels();
        if (intakeCount == 1) {
            closeButton.setVisibility(View.GONE);
        } else {
            closeButton.setVisibility(View.VISIBLE);
        }


        // Set a click listener on the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the intakeLayout from the parent layout
                parentLayout.removeView(intakeLayout);
                Toast.makeText(requireActivity(), getResources().getString(R.string.exercise_removed), Toast.LENGTH_SHORT).show();
                // Decrement intakeCount
                intakeCount--;
                updateIntakeLabels();
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void updateIntakeLabels() {
        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Initialize intakeCount to 1
        int intakeCount = 1;

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeLabel in the child view
                TextView intakeLabel = childView.findViewById(R.id.intakeLabel);

                // Update the text of the intakeLabel
                intakeLabel.setText(getResources().getString(R.string.exercise) + " " + intakeCount);

                // Increment intakeCount
                intakeCount++;
            }
        }
    }


    private boolean validateInput() {return true;

        setupTextWatchers();


        AutoCompleteTextView muscleGroup = getView().findViewById(R.id.muscleGroup);
        AutoCompleteTextView excerciseString = getView().findViewById(R.id.exerciseString);
        AutoCompleteTextView setsNumber = getView().findViewById(R.id.setsNumber);
        AutoCompleteTextView repsNumber = getView().findViewById(R.id.repsNumber);
        AutoCompleteTextView restTime = getView().findViewById(R.id.restSelection)

        validInput = true;

        if (muscleGroup.getText().toString().isEmpty()) {
            TextInputLayout medicationsListLayout = getView().findViewById(R.id.medicationNameInputLayout);
            medicationsListLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }
        if (excerciseString.getText().toString().isEmpty()) {
            TextInputLayout howToTakeMedicineLayout = getView().findViewById(R.id.how_to_take_medicine_input_layout);
            howToTakeMedicineLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }
        if (setsNumber.getText().toString().isEmpty()) {
            TextInputLayout howRegularlyInputLayout = getView().findViewById(R.id.how_regularly_input_layout);
            howRegularlyInputLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        } else {
            if (setsNumber.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
                if (restTime.getText().toString().isEmpty()) {
                    TextInputLayout intervalSelectionInputLayout = getView().findViewById(R.id.intervalSelectionInputLayout);
                    intervalSelectionInputLayout.setError(getResources().getString(R.string.required_field));
                    validInput = false;
                }
            } else {
                if (setsNumber.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[2])) {
                    if (selectedWeekdays.isEmpty()) {
                        // Assuming you have a TextView to display this error
                        Toast.makeText(requireActivity(), getResources().getString(R.string.select_weekdays), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if (repsNumber.getText().toString().isEmpty()){
            TextInputLayout howRegularlyInputLayout = getView().findViewById(R.id.how_regularly_input_layout);
            howRegularlyInputLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }
        if (restTime.getText().toString().isEmpty()) {
            TextInputLayout intervalSelectionInputLayout = getView().findViewById(R.id.intervalSelectionInputLayout);
            intervalSelectionInputLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeTime and quantity views in the child view
                Button intakeTime = childView.findViewById(R.id.intakeTime);
                TextInputLayout quantityStringInputLayout = childView.findViewById(R.id.quantityStringInputLayout);
                TextInputLayout quantityNumberInputLayout = childView.findViewById(R.id.quantityNumberInputLayout);
                AutoCompleteTextView quantityString = childView.findViewById(R.id.quantityString);
                AutoCompleteTextView quantityNumber = childView.findViewById(R.id.quantityNumber);

                // Check if intakeTime and quantity are filled
                if (intakeTime.getText().toString().isEmpty() || intakeTime.getText().toString().equals(getResources().getString(R.string.select_time))) {
                    intakeTime.setError(getResources().getString(R.string.required_field));
                    validInput = false;
                }

                if (isMilliliters) {
                    if (quantityNumber.getText().toString().isEmpty()) {
                        quantityNumberInputLayout.setError(getResources().getString(R.string.required_field));
                        validInput = false;
                    }
                } else {
                    if (quantityString.getText().toString().isEmpty()) {
                        quantityStringInputLayout.setError(getResources().getString(R.string.required_field));
                        validInput = false;
                    }
                }
            }
        }

        return validInput;
    }

    private void setupTextWatchers() {
        AutoCompleteTextView muscleGroup = getView().findViewById(R.id.muscleGroup);
        AutoCompleteTextView exerciseString = getView().findViewById(R.id.exerciseString);
        AutoCompleteTextView setsNumber = getView().findViewById(R.id.setsNumber);
        AutoCompleteTextView repsNumber = getView().findViewById(R.id.repsNumber);
        AutoCompleteTextView restSelection = getView().findViewById(R.id.restSelection);

        // Create a TextWatcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed here
            }

            @Override
            public void afterTextChanged(Editable s) {
                // After the text has changed, check if the field is now filled and if so, remove the error
                if (!s.toString().isEmpty()) {
                    View view = getActivity().getCurrentFocus();
                    if (view instanceof AutoCompleteTextView) {
                        ((TextInputLayout) view.getParent().getParent()).setError(null);
                    }
                }
            }
        };

        // Add the TextWatcher to your AutoCompleteTextViews
        muscleGroup.addTextChangedListener(textWatcher);
        exerciseString.addTextChangedListener(textWatcher);
        setsNumber.addTextChangedListener(textWatcher);
        restSelection.addTextChangedListener(textWatcher);

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        /*
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeTime and quantity views in the child view
                Button intakeTime = childView.findViewById(R.id.intakeTime);
                AutoCompleteTextView quantityString = childView.findViewById(R.id.quantityString);
                AutoCompleteTextView quantityNumber = childView.findViewById(R.id.quantityNumber);

                intakeTime.addTextChangedListener(textWatcher);

                if (isMilliliters) {
                    quantityNumber.addTextChangedListener(textWatcher);
                } else {
                    quantityString.addTextChangedListener(textWatcher);
                }
            }
        }

         */

    }

    private Bundle setBundle() {
        Bundle bundle = new Bundle();
        MappedValues mappedValues = new MappedValues(requireActivity());

        String name_exercise = "";
        String muscleGroup_exercise ="";
        String reps_exercise="";
        int sets_exercise=0;
        String rest_exercise="";

        AutoCompleteTextView muscleGroup = requireView().findViewById(R.id.muscleGroup);
        AutoCompleteTextView exerciseName = requireView().findViewById(R.id.exerciseString);
        AutoCompleteTextView numberSet= requireView().findViewById(R.id.setsNumber);
        AutoCompleteTextView numberRep = requireView().findViewById(R.id.repsNumber);
        AutoCompleteTextView restTime = requireView().findViewById(R.id.restSelection);

        if (!muscleGroup.getText().toString().isEmpty()) {
            muscleGroup_exercise = muscleGroup.getText().toString();
        }


        if (!exerciseName.getText().toString().isEmpty()) {
            name_exercise = exerciseName.getText().toString();

            /*
            if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
                if (!intervalSelection.getText().toString().isEmpty()) {
                    intervalSelected = intervalSelection.getText().toString();
                }
            }
             */
        }
        if(!numberSet.getText().toString().isEmpty()){
            sets_exercise = Integer.parseInt(numberSet.getText().toString());
        }
        if(!numberRep.getText().toString().isEmpty()){
            reps_exercise = numberRep.getText().toString();
        }
        if(!restTime.getText().toString().isEmpty()){
            rest_exercise = restTime.getText().toString();
        }


        //Medication medication = new Medication(medicationNameString, mappedValues.getHowToTakeKey(howToTakeString), mappedValues.getHowRegularlyKey(howRegularlyString), selectedWeekdays);

        Exercise exercise = new Exercise(name_exercise, muscleGroup_exercise, reps_exercise, sets_exercise, rest_exercise);
        /*
        medication.setSelectedWeekdays(selectedWeekdays);
        medication.setSelectedWeekdaysString(medication.convertWeekdaysToString());
        Log.d("SelectedWeekdaysString (TreatmentFormMedication): ", medication.getSelectedWeekdaysString());

        if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
            if (!intervalSelection.getText().toString().isEmpty()) {
                //medication.setIntervalSelectedType(mappedValues.getIntervalKey(selectedSeconds));
                medication.setIntervalSelectedNumber(selectedReps);
            }
        }



        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeTime and quantity views in the child view
                Button intakeTime = childView.findViewById(R.id.intakeTime);
                AutoCompleteTextView quantityString = childView.findViewById(R.id.quantityString);
                AutoCompleteTextView quantityNumber = childView.findViewById(R.id.quantityNumber);

                // Check if intakeTime and quantity are filled
                if (!intakeTime.getText().toString().isEmpty() && !intakeTime.getText().toString().equals(getResources().getString(R.string.select_time))) {
                    medication.addIntakeTime(intakeTime.getText().toString());
                }

                if (isMilliliters) {
                    if (!quantityNumber.getText().toString().isEmpty()) {
                        // treatment.addQuantity(quantityNumber.getText().toString());
                        medication.addQuantity(quantityNumber.getText().toString());
                    }
                } else {
                    if (!quantityString.getText().toString().isEmpty()) {
                        // treatment.addQuantity(quantityString.getText().toString());
                        medication.addQuantity(quantityString.getText().toString());
                    }
                }

            }
        } */
        workoutPlan.addExercise(exercise);


        bundle.putParcelable("workoutPlan", workoutPlan);
        Log.d("WorkoutPlan (ExercisesFragment): ", workoutPlan.toString());
        return bundle;
    }

    @SuppressLint("SetTextI18n")
    private void showSetsDialog(View intakeLayout) {
        // Create a dialog builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.CustomMaterialDialog);

        // Create a LinearLayout
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL); // Change orientation to VERTICAL
        layout.setGravity(Gravity.CENTER);

        Typeface ember_bold = ResourcesCompat.getFont(requireContext(), R.font.ember_bold);

        // Create a LinearLayout for the labels
        LinearLayout labelLayout = new LinearLayout(getActivity());
        labelLayout.setOrientation(LinearLayout.HORIZONTAL); // Change orientation to HORIZONTAL
        labelLayout.setGravity(Gravity.CENTER);

        // Create a TextView for the first label
        TextView label1 = new TextView(getActivity());
        label1.setText(getResources().getString(R.string.sets)); // Set the text for the label
        label1.setGravity(Gravity.CENTER); // Center the text
        label1.setTextSize(18); // Set the text size
        label1.setTypeface(ember_bold); // Set the text style to bold

        labelLayout.addView(label1); // Add the first label to the layout

        layout.addView(labelLayout); // Add the labelLayout to the main layout

        // Create the NumberPickers
        NumberPicker numberPickerSets = new NumberPicker(getActivity());

        // Set the min and max values for numberPickerSets
        numberPickerSets.setMinValue(1);
        numberPickerSets.setMaxValue(5);


        // Create layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) (12 * getResources().getDisplayMetrics().density), 0); // 8dp to px

        // Create a LinearLayout for the NumberPickers
        LinearLayout numberPickerLayout = new LinearLayout(getActivity());
        numberPickerLayout.setOrientation(LinearLayout.HORIZONTAL);
        numberPickerLayout.setGravity(Gravity.CENTER);

        // Add the NumberPickers to the LinearLayout
        numberPickerLayout.addView(numberPickerSets); // Add layout params to numberPickerSets

        layout.addView(numberPickerLayout); // Add the numberPickerLayout to the main layout

        // Set the LinearLayout as the dialog view
        builder.setView(layout);

        // Set the dialog title and buttons
        builder.setTitle(getResources().getString(R.string.what_interval_sets)).setMessage(getResources().getString(R.string.what_interval_message_sets))
                .setPositiveButton("OK", (dialog, id) -> {

                    // User clicked OK, retrieve the selected values
                    selectedReps = numberPickerSets.getValue();


                    AutoCompleteTextView intervalSelection = intakeLayout.findViewById(R.id.setsNumber);

                    intervalSelection.setText(selectedReps + " " + (getResources().getQuantityString(R.plurals.set, selectedReps, selectedReps)).toLowerCase());
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog, do something if necessary
                });

        // Create and show the dialog
        Dialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showRepsDialog(View intakeLayout) {
        // Create a dialog builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.CustomMaterialDialog);

        // Create a LinearLayout
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL); // Change orientation to VERTICAL
        layout.setGravity(Gravity.CENTER);

        Typeface ember_bold = ResourcesCompat.getFont(requireContext(), R.font.ember_bold);

        // Create a LinearLayout for the labels
        LinearLayout labelLayout = new LinearLayout(getActivity());
        labelLayout.setOrientation(LinearLayout.HORIZONTAL); // Change orientation to HORIZONTAL
        labelLayout.setGravity(Gravity.CENTER);

        // Create a TextView for the first label
        TextView label1 = new TextView(getActivity());
        label1.setText(getResources().getQuantityString(R.plurals.rep, 2, 2)); // Set the text for the label
        label1.setGravity(Gravity.CENTER); // Center the text
        label1.setTextSize(18); // Set the text size
        label1.setTypeface(ember_bold); // Set the text style to bold

        // Add the first label to the layout with layout params
        labelLayout.addView(label1);


        layout.addView(labelLayout); // Add the labelLayout to the main layout

        // Create the NumberPickers
        NumberPicker numberPickerReps = new NumberPicker(getActivity());

        // Set the min and max values for numberPickerMinutes
        numberPickerReps.setMinValue(1);
        numberPickerReps.setMaxValue(15);

        // Create layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) (12 * getResources().getDisplayMetrics().density), 0); // 8dp to px

        // Create a LinearLayout for the NumberPickers
        LinearLayout numberPickerLayout = new LinearLayout(getActivity());
        numberPickerLayout.setOrientation(LinearLayout.HORIZONTAL);
        numberPickerLayout.setGravity(Gravity.CENTER);

        // Add the NumberPickers to the LinearLayout
        numberPickerLayout.addView(numberPickerReps); // Add layout params to numberPickerMinutes

        layout.addView(numberPickerLayout); // Add the numberPickerLayout to the main layout

        // Set the LinearLayout as the dialog view
        builder.setView(layout);

        // Set the dialog title and buttons
        builder.setTitle(getResources().getString(R.string.what_interval_reps)).setMessage(getResources().getString(R.string.what_interval_message_reps))
                .setPositiveButton("OK", (dialog, id) -> {
                    // User clicked OK, retrieve the selected values
                    selectedReps = numberPickerReps.getValue();


                    String formattedSelectedInterval = "";

                    AutoCompleteTextView intervalSelection = intakeLayout.findViewById(R.id.repsNumber);

                    intervalSelection.setText(selectedReps + " " + (getResources().getQuantityString(R.plurals.rep, selectedReps, selectedReps)).toLowerCase());

                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog, do something if necessary
                });

        // Create and show the dialog
        Dialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showRestDialog(View intakeLayout) {
        // Create a dialog builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.CustomMaterialDialog);

        // Create a LinearLayout
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL); // Change orientation to VERTICAL
        layout.setGravity(Gravity.CENTER);

        Typeface ember_bold = ResourcesCompat.getFont(requireContext(), R.font.ember_bold);

        // Create a LinearLayout for the labels
        LinearLayout labelLayout = new LinearLayout(getActivity());
        labelLayout.setOrientation(LinearLayout.HORIZONTAL); // Change orientation to HORIZONTAL
        labelLayout.setGravity(Gravity.CENTER);

        // Create a TextView for the first label
        TextView label1 = new TextView(getActivity());
        label1.setText(getResources().getQuantityString(R.plurals.minute, 2, 2)); // Set the text for the label
        label1.setGravity(Gravity.CENTER); // Center the text
        label1.setTextSize(18); // Set the text size
        label1.setTypeface(ember_bold); // Set the text style to bold

        // Create layout parameters for the first label with margin end
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(0, 0, (int) (12 * getResources().getDisplayMetrics().density), 0); // 4dp to px

        // Add the first label to the layout with layout params
        labelLayout.addView(label1, layoutParams1);

        // Create a TextView for the second label
        TextView label2 = new TextView(getActivity());
        label2.setText(getResources().getQuantityString(R.plurals.second, 2, 2)); // Set the text for the label
        label2.setGravity(Gravity.CENTER); // Center the text
        label2.setTextSize(18); // Set the text size
        label2.setTypeface(ember_bold); // Set the text style to bold

        // Create layout parameters for the second label with margin start
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins((int) (8 * getResources().getDisplayMetrics().density), 0, 0, 0); // 4dp to px

        // Add the second label to the layout with layout params
        labelLayout.addView(label2, layoutParams2);

        layout.addView(labelLayout); // Add the labelLayout to the main layout

        // Create the NumberPickers
        NumberPicker numberPickerMinutes = new NumberPicker(getActivity());
        NumberPicker numberPickerSeconds = new NumberPicker(getActivity());

        // Set the min and max values for numberPickerMinutes
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(5);

        // Set the min and max values for numberPickerSeconds
        numberPickerSeconds.setMinValue(0);
        numberPickerSeconds.setMaxValue(5);

        // Set a formatter
        numberPickerSeconds.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return Integer.toString(value * 10);
            }
        });

        // Create layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) (12 * getResources().getDisplayMetrics().density), 0); // 8dp to px

        // Create a LinearLayout for the NumberPickers
        LinearLayout numberPickerLayout = new LinearLayout(getActivity());
        numberPickerLayout.setOrientation(LinearLayout.HORIZONTAL);
        numberPickerLayout.setGravity(Gravity.CENTER);

        // Add the NumberPickers to the LinearLayout
        numberPickerLayout.addView(numberPickerMinutes, layoutParams); // Add layout params to numberPickerMinutes
        numberPickerLayout.addView(numberPickerSeconds);

        layout.addView(numberPickerLayout); // Add the numberPickerLayout to the main layout

        // Set the LinearLayout as the dialog view
        builder.setView(layout);

        // Set the dialog title and buttons
        builder.setTitle(getResources().getString(R.string.what_interval_rest)).setMessage(getResources().getString(R.string.what_interval_message_rest))
                .setPositiveButton("OK", (dialog, id) -> {
                    // User clicked OK, retrieve the selected values
                    selectedReps = numberPickerMinutes.getValue();
                    // Get the selected value from numberPickerSeconds
                    selectedSeconds = numberPickerSeconds.getValue() * 10;


                    String formattedSelectedInterval = "";

                    AutoCompleteTextView intervalSelection = intakeLayout.findViewById(R.id.restSelection);

                    if (selectedSeconds == 0 && selectedReps == 0) {
                        intervalSelection.setText(getResources().getString(R.string.no_rest));
                    } else {
                        if (selectedReps == 0) {
                            intervalSelection.setText(selectedSeconds + " " + getResources().getQuantityString(R.plurals.second, selectedSeconds, selectedSeconds));
                        } else {
                            if (selectedSeconds == 0) {
                                intervalSelection.setText(selectedReps + " " + getResources().getQuantityString(R.plurals.minute, selectedReps, selectedReps));
                            } else {
                                intervalSelection.setText(selectedReps + " " + getResources().getQuantityString(R.plurals.minute, selectedReps, selectedReps) + " " + selectedSeconds + " " + getResources().getQuantityString(R.plurals.second, selectedSeconds, selectedSeconds));
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog, do something if necessary
                });

        // Create and show the dialog
        Dialog dialog = builder.create();
        dialog.show();
    }
}
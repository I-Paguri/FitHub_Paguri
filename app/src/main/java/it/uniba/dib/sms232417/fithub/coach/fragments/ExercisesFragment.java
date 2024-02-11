package it.uniba.dib.sms232417.fithub.coach.fragments;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;
import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.entity.Medication;
import it.uniba.dib.sms232417.fithub.entity.Treatment;
import it.uniba.dib.sms232417.fithub.utilities.MappedValues;

public class ExercisesFragment extends Fragment {

    private View linearLayoutInterval;
    private TextView subtitleInterval;
    private View linearLayoutWeekdays;
    private TextView subtitleWeekdays;
    private Button btnIntakeTime;
    private static int intakeCount = 1;
    private AutoCompleteTextView intervalSelection;
    private AutoCompleteTextView howRegularly;
    private AutoCompleteTextView howToTakeMedicine;
    private int intervalSelectedNumber;
    private String intervalSelectedString;
    private ArrayAdapter<String> adapterQuantity;
    // Declare an ArrayList to hold the selected weekdays
    private ArrayList<WeekdaysDataItem> selectedWeekdays = new ArrayList<>();
    private String[] quantityValues;
    private List<String> quantityValuesList;
    private boolean isMilliliters = false;

    // Received data from TreatmentFormGeneralFragment
    private String treatmentTarget;
    private long startDate, endDate;
    private boolean endDateSwitch;
    private Treatment treatment;

    private ArrayList<Medication> medications;

    private boolean validInput;
    private String patientUUID;
    private String patientName;
    private String patientAge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        String[] descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.medications), getResources().getString(R.string.notes)};

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
        treatment = null;
        medications = new ArrayList<>();
        intervalSelectedNumber = -1;
        intervalSelectedString = "";

        if (bundle != null) {
            treatment = bundle.getParcelable("treatment");
            patientName = bundle.getString("patientName");
            patientAge = bundle.getString("patientAge");
            patientUUID = bundle.getString("patientUUID");
        }


        TextView intakeLabel = view.findViewById(R.id.intakeLabel);
        intakeLabel.setText(getResources().getString(R.string.exercise) + " " + intakeCount);

        Button btnAddIntake = view.findViewById(R.id.btnAddIntake);
        btnAddIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewIntakeLayout();
                updateIntakeLabels();
            }
        });

        btnIntakeTime = view.findViewById(R.id.intakeTime);



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

                    bundle.putString("patientName", patientName);
                    bundle.putString("patientAge", patientAge);
                    bundle.putString("patientUUID", patientUUID);

                    TreatmentFormMedicationsFragment treatmentFormMedicationsFragment = new TreatmentFormMedicationsFragment();
                    treatmentFormMedicationsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormMedicationsFragment);
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
                treatment.removeMedicationAtIndex(treatment.getMedications().size() - 1);
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


        // Add the new layout to the parent layout at the index of the "Add Intake" button
        parentLayout.addView(intakeLayout, index);

        TextView intakeLabel = intakeLayout.findViewById(R.id.intakeLabel);
        intakeLabel.setText(getResources().getString(R.string.exercise) + " " + intakeCount);
        // Find the close button in the layout
        Button closeButton = intakeLayout.findViewById(R.id.closeButton);

        // Set a click listener on the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the intakeLayout from the parent layout
                parentLayout.removeView(intakeLayout);
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


    private boolean validateInput() {
        setupTextWatchers();

        AutoCompleteTextView medicinesList = getView().findViewById(R.id.medicines_list);
        AutoCompleteTextView howToTakeMedicine = getView().findViewById(R.id.how_to_take_medicine);
        AutoCompleteTextView howRegularly = getView().findViewById(R.id.how_regularly);
        AutoCompleteTextView intervalSelection = getView().findViewById(R.id.intervalSelection);

        validInput = true;

        if (medicinesList.getText().toString().isEmpty()) {
            TextInputLayout medicationsListLayout = getView().findViewById(R.id.medicationNameInputLayout);
            medicationsListLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }
        if (howToTakeMedicine.getText().toString().isEmpty()) {
            TextInputLayout howToTakeMedicineLayout = getView().findViewById(R.id.how_to_take_medicine_input_layout);
            howToTakeMedicineLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }
        if (howRegularly.getText().toString().isEmpty()) {
            TextInputLayout howRegularlyInputLayout = getView().findViewById(R.id.how_regularly_input_layout);
            howRegularlyInputLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        } else {
            if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
                if (intervalSelection.getText().toString().isEmpty()) {
                    TextInputLayout intervalSelectionInputLayout = getView().findViewById(R.id.intervalSelectionInputLayout);
                    intervalSelectionInputLayout.setError(getResources().getString(R.string.required_field));
                    validInput = false;
                }
            } else {
                if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[2])) {
                    if (selectedWeekdays.isEmpty()) {
                        // Assuming you have a TextView to display this error
                        Toast.makeText(requireActivity(), getResources().getString(R.string.select_weekdays), Toast.LENGTH_SHORT).show();
                    }
                }
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
        AutoCompleteTextView medicinesList = getView().findViewById(R.id.medicines_list);
        AutoCompleteTextView howToTakeMedicine = getView().findViewById(R.id.how_to_take_medicine);
        AutoCompleteTextView howRegularly = getView().findViewById(R.id.how_regularly);
        AutoCompleteTextView intervalSelection = getView().findViewById(R.id.intervalSelection);

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
        medicinesList.addTextChangedListener(textWatcher);
        howToTakeMedicine.addTextChangedListener(textWatcher);
        howRegularly.addTextChangedListener(textWatcher);
        intervalSelection.addTextChangedListener(textWatcher);

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

                intakeTime.addTextChangedListener(textWatcher);

                if (isMilliliters) {
                    quantityNumber.addTextChangedListener(textWatcher);
                } else {
                    quantityString.addTextChangedListener(textWatcher);
                }
            }
        }

    }

    private Bundle setBundle() {
        Bundle bundle = new Bundle();
        MappedValues mappedValues = new MappedValues(requireActivity());

        String medicationNameString, howToTakeString, howRegularlyString, intervalSelected;
        medicationNameString = "";
        howToTakeString = "";
        howRegularlyString = "";
        intervalSelected = "";

        AutoCompleteTextView medicinesList = requireView().findViewById(R.id.medicines_list);
        AutoCompleteTextView howToTakeMedicine = requireView().findViewById(R.id.how_to_take_medicine);
        AutoCompleteTextView howRegularly = requireView().findViewById(R.id.how_regularly);
        AutoCompleteTextView intervalSelection = requireView().findViewById(R.id.intervalSelection);

        if (!medicinesList.getText().toString().isEmpty()) {
            medicationNameString = medicinesList.getText().toString();
        }

        if (!howToTakeMedicine.getText().toString().isEmpty()) {
            howToTakeString = howToTakeMedicine.getText().toString();

        }

        if (!howRegularly.getText().toString().isEmpty()) {
            howRegularlyString = howRegularly.getText().toString();

            /*
            if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
                if (!intervalSelection.getText().toString().isEmpty()) {
                    intervalSelected = intervalSelection.getText().toString();
                }
            }
             */
        }


        Medication medication = new Medication(medicationNameString, mappedValues.getHowToTakeKey(howToTakeString), mappedValues.getHowRegularlyKey(howRegularlyString), selectedWeekdays);

        medication.setSelectedWeekdays(selectedWeekdays);
        medication.setSelectedWeekdaysString(medication.convertWeekdaysToString());
        Log.d("SelectedWeekdaysString (TreatmentFormMedication): ", medication.getSelectedWeekdaysString());

        if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
            if (!intervalSelection.getText().toString().isEmpty()) {
                medication.setIntervalSelectedType(mappedValues.getIntervalKey(intervalSelectedString));
                medication.setIntervalSelectedNumber(intervalSelectedNumber);
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
        }

        treatment.addMedication(medication);

        bundle.putParcelable("treatment", treatment);

        return bundle;
    }
}
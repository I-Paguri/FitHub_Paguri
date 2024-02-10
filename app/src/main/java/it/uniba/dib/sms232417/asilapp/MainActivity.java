package it.uniba.dib.sms232417.asilapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.doctor.fragments.HomeFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.HealthcareFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MeasureFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MyPatientsFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MyAccountFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.PatientFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.TreatmentFormGeneralFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.TreatmentFormMedicationsFragment;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.patientsFragments.ExpensesFragment;
import it.uniba.dib.sms232417.asilapp.patientsFragments.MapsFragment;
import it.uniba.dib.sms232417.asilapp.patientsFragments.PatientViewFragment;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;


public class MainActivity extends AppCompatActivity {

    Fragment selectedFragment = null;

    private boolean doubleBackToExitPressedOnce = false;
    private TreatmentFormMedicationsFragment treatmentFormMedicationsFragment;

    public static Context getContext() {
        return getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        treatmentFormMedicationsFragment = new TreatmentFormMedicationsFragment();
        Intent intent = getIntent();
        Patient loggedPatient = (Patient) intent.getParcelableExtra("loggedPatient");
        Doctor loggedDoctor = (Doctor) intent.getParcelableExtra("loggedDoctor");

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.getMenu().clear();

        if (loggedPatient != null) {
            try {
                FileOutputStream fos = openFileOutput(StringUtils.PATIENT_LOGGED, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(loggedPatient);
                Log.d("Patient", "Patient saved");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_patient);

            bottomNavigationView.setOnItemSelectedListener(item -> {


                treatmentFormMedicationsFragment.setIntakeCount(1);
                int itemId = item.getItemId();
                /*
                 * If the selected item is HomeFragment, HealthcareFragment, MyPatientsFragment, or MyAccountFragment,
                 * replace the current fragment with the selected one.
                 * If the selected item is MeasureFragment, check the permission to use the camera.
                 */
                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else {
                    if (itemId == R.id.navigation_payments) {
                        selectedFragment = new ExpensesFragment();
                    } else {
                        if (itemId == R.id.navigation_diary) {
                            selectedFragment = new PatientFragment();
                            Bundle bundle = new Bundle();

                            bundle.putString("patientUUID", loggedPatient.getUUID());
                            bundle.putString("patientName", loggedPatient.getNome());
                            bundle.putString("patientAge", loggedPatient.getAge() + " " + getResources().getQuantityString(R.plurals.age,
                                    loggedPatient.getAge(), loggedPatient.getAge()));
                            bundle.putString("user", "patient");

                            selectedFragment.setArguments(bundle);
                        } else {
                            if (itemId == R.id.navigation_my_account) {
                                selectedFragment = new MyAccountFragment();
                            } else {
                                if (itemId == R.id.navigation_measure) {
                                    checkPermission();
                                }
                            }
                        }

                    }
                }

                if (selectedFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

                return true;
            });

            // Set default selection
            bottomNavigationView.setSelectedItemId(R.id.navigation_home); // replace with your actual menu item id

        }
        if (loggedDoctor != null) {
            try {
                FileOutputStream fos = openFileOutput(StringUtils.DOCTOR_LOGGED, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(loggedDoctor);
                Log.d("Doctor", "Doctor saved");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_doctor);
            bottomNavigationView.setOnItemSelectedListener(item -> {


                treatmentFormMedicationsFragment.setIntakeCount(1);
                int itemId = item.getItemId();
                /*
                 * If the selected item is HomeFragment, HealthcareFragment, MyPatientsFragment, or MyAccountFragment,
                 * replace the current fragment with the selected one.
                 * If the selected item is MeasureFragment, check the permission to use the camera.
                 */
                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else {
                    if (itemId == R.id.navigation_my_patients) {
                        selectedFragment = new MyPatientsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("doctor", loggedDoctor);
                        selectedFragment.setArguments(bundle);
                    } else {
                        if (itemId == R.id.navigation_my_account) {
                            selectedFragment = new MyAccountFragment();
                        }
                    }

                }


                if (selectedFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

                return true;
            });

            // Set default selection
            bottomNavigationView.setSelectedItemId(R.id.navigation_home); // replace with your actual menu item id

        }

    }

    public void checkPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != getPackageManager().PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.CAMERA)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.attention);
                    builder.setMessage(R.string.explain_permission_camera);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, 101);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.attention);
                    builder.setMessage(R.string.explain_permission_camera);
                    builder.setPositiveButton("OK", null);
                    builder.show();

                }
            } else {
                selectedFragment = new MeasureFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
            selectedFragment = new MeasureFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     * This method is called when the back button is pressed.
     * If the current fragment is HealthcareFragment, MyPatientsFragment, or MyAccountFragment, navigate to HomeFragment.
     * If the current fragment is HomeFragment, exit the app.
     */
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        if (currentFragment instanceof HealthcareFragment || currentFragment instanceof MyPatientsFragment
                || currentFragment instanceof MyAccountFragment || currentFragment instanceof MeasureFragment) {
            // If the current fragment is HealthcareFragment, MyPatientsFragment, or MyAccountFragment, navigate to HomeFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else {
            if (currentFragment instanceof TreatmentFormGeneralFragment) {
                // If the current fragment is TreatmentFormGeneralFragment, show a dialog
                new MaterialAlertDialogBuilder(this, R.style.CustomMaterialDialog)
                        .setTitle(getResources().getString(R.string.going_back))
                        .setMessage(getResources().getString(R.string.unsaved_changes))
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to negative button press
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.go_back), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to positive button press
                                // Navigate back
                                getSupportFragmentManager().popBackStack();
                            }
                        })
                        .create()
                        .show();
            } else {
                if (currentFragment instanceof HomeFragment) {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                        return;
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getResources().getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                } else {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        super.onBackPressed();
                    }
                }
            }
        }
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
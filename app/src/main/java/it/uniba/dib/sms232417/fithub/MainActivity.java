package it.uniba.dib.sms232417.fithub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import it.uniba.dib.sms232417.fithub.coach.fragments.HomeFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.HealthcareFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.MeasureFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.MyAthletesFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.MyAccountFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.AthleteFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.TreatmentFormGeneralFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.TreatmentFormMedicationsFragment;
import it.uniba.dib.sms232417.fithub.entity.Coach;
import it.uniba.dib.sms232417.fithub.entity.Athlete;
import it.uniba.dib.sms232417.fithub.athleteFragments.ExpensesFragment;
import it.uniba.dib.sms232417.fithub.utilities.StringUtils;


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
        Athlete loggedAthlete = (Athlete) intent.getParcelableExtra("loggedPatient");
        Coach loggedCoach = (Coach) intent.getParcelableExtra("loggedDoctor");

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.getMenu().clear();

        if (loggedAthlete != null) {
            try {
                FileOutputStream fos = openFileOutput(StringUtils.PATIENT_LOGGED, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(loggedAthlete);
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
                            selectedFragment = new AthleteFragment();
                            Bundle bundle = new Bundle();

                            bundle.putString("patientUUID", loggedAthlete.getUUID());
                            bundle.putString("patientName", loggedAthlete.getNome());
                            bundle.putString("patientAge", loggedAthlete.getAge() + " " + getResources().getQuantityString(R.plurals.age,
                                    loggedAthlete.getAge(), loggedAthlete.getAge()));
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
        if (loggedCoach != null) {
            try {
                FileOutputStream fos = openFileOutput(StringUtils.DOCTOR_LOGGED, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(loggedCoach);
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
                        selectedFragment = new MyAthletesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("doctor", loggedCoach);
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

        if (currentFragment instanceof HealthcareFragment || currentFragment instanceof MyAthletesFragment
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
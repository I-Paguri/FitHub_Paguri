package it.uniba.dib.sms232417.fithub.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import it.uniba.dib.sms232417.fithub.MainActivity;
import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterCoach;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterAthlete;
import it.uniba.dib.sms232417.fithub.auth.coach.LoginCoachCredentialFragment;
import it.uniba.dib.sms232417.fithub.auth.athlete.LoginFragment;
import it.uniba.dib.sms232417.fithub.auth.athlete.RegisterFragment;
import it.uniba.dib.sms232417.fithub.entity.Coach;
import it.uniba.dib.sms232417.fithub.entity.Athlete;
import it.uniba.dib.sms232417.fithub.interfaces.OnCoachDataCallback;
import it.uniba.dib.sms232417.fithub.interfaces.OnAthleteDataCallback;
import it.uniba.dib.sms232417.fithub.utilities.StringUtils;

import javax.crypto.SecretKey;

public class EntryActivity extends AppCompatActivity {

    DatabaseAdapterAthlete dbAdapterAthlete;
    DatabaseAdapterCoach dbAdapterCoach;
    private boolean doubleBackToExitPressedOnce = false;

    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_layout);

        checkAutomaticLogin();
        replaceFragment(new LoginDecisionFragment());


    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_container_login,fragment);
        FrameLayout frameLayout = findViewById(R.id.fragment_container_login);

        if(frameLayout.getVisibility() == FrameLayout.VISIBLE)
            frameLayout.setVisibility(FrameLayout.VISIBLE);


        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void checkAutomaticLogin() {

        final Athlete[] loggedAthlete = {null};

        RelativeLayout loading = findViewById(R.id.loading);
        loading.setVisibility(RelativeLayout.VISIBLE);

        FrameLayout frameLayout = findViewById(R.id.fragment_container_login);
        frameLayout.setVisibility(FrameLayout.GONE);

        if(frameLayout.getVisibility() == FrameLayout.GONE)
            Log.d("Visibility_auth", "GONE");

        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);
            String email = sharedPreferences.getString("email", null);
            String password = sharedPreferences.getString("password", null);
            String iv = sharedPreferences.getString("iv", null);
            boolean isDoctor = sharedPreferences.getBoolean("isDoctor", false);

            if(!isDoctor) {
                if (email != null) {
                    if (password != null) {

                        byte[] encryptPassword = Base64.decode(password, Base64.DEFAULT);
                        byte[] ivByte = Base64.decode(iv, Base64.DEFAULT);
                        try {
                            SecretKey secretKey = CryptoUtil.loadSecretKey(email);
                            byte[] decryptData = CryptoUtil.decryptWithKey(secretKey, encryptPassword, ivByte);
                            password = new String(decryptData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (password != null) {
                            dbAdapterAthlete = new DatabaseAdapterAthlete(context);
                            dbAdapterAthlete.onLogin(email, password, new OnAthleteDataCallback() {
                                @Override
                                public void onCallback(Athlete athlete) {
                                    RelativeLayout relativeLayout = findViewById(R.id.loading);
                                    relativeLayout.setVisibility(RelativeLayout.GONE);
                                    Intent intent = new Intent(EntryActivity.this, MainActivity.class);
                                    intent.putExtra("loggedAthlete", (Parcelable) athlete);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCallbackError(Exception e, String Message) {
                                    e.printStackTrace();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.error).setMessage(Message);
                                    builder.setPositiveButton(R.string.yes, null);
                                    builder.show();

                                }
                            });
                        }
                    }
                } else {
                    loading.setVisibility(RelativeLayout.GONE);
                    frameLayout.setVisibility(FrameLayout.VISIBLE);
                }
            }else
                checkAutomaticLoginDoctor();
    }


    private void checkAutomaticLoginDoctor() {
        final Coach[] loggedCoach = {null};
        RelativeLayout loading = findViewById(R.id.loading);
        loading.setVisibility(RelativeLayout.VISIBLE);
        FrameLayout frameLayout = findViewById(R.id.fragment_container_login);
        frameLayout.setVisibility(FrameLayout.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);


            String email = sharedPreferences.getString("email", null);
            String password = sharedPreferences.getString("password", null);
            String iv = sharedPreferences.getString("iv", null);
            boolean isDoctor = sharedPreferences.getBoolean("isDoctor", false);
            if (isDoctor) {
                if (email != null) {
                    if (password != null) {

                        byte[] encryptPassword = Base64.decode(password, Base64.DEFAULT);
                        byte[] ivByte = Base64.decode(iv, Base64.DEFAULT);
                        try {
                            SecretKey secretKey = CryptoUtil.loadSecretKey(email);
                            byte[] decryptData = CryptoUtil.decryptWithKey(secretKey, encryptPassword, ivByte);
                            password = new String(decryptData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (password != null) {

                            dbAdapterCoach = new DatabaseAdapterCoach(context);
                            dbAdapterCoach.onLogin(email, password, new OnCoachDataCallback() {

                                @Override
                                public void onCallback(Coach coach) {

                                    RelativeLayout relativeLayout = findViewById(R.id.loading);
                                    relativeLayout.setVisibility(RelativeLayout.GONE);
                                    Intent intent = new Intent(EntryActivity.this, MainActivity.class);
                                    intent.putExtra("loggedCoach", (Parcelable) coach);
                                    startActivity(intent);
                                    finish();

                                }

                                @Override
                                public void onCallbackError(Exception e, String Message) {
                                    e.printStackTrace();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.error).setMessage(Message);
                                    builder.setPositiveButton(R.string.yes, null);
                                    builder.show();

                                }
                            });

                        }
                    }
                } else {
                    loading.setVisibility(RelativeLayout.GONE);
                    frameLayout.setVisibility(FrameLayout.VISIBLE);
                }
        }
    }
    /*
    public void checkPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(EntryActivity.this, android.Manifest.permission.CAMERA) != getPackageManager().PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(EntryActivity.this, android.Manifest.permission.CAMERA)) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    builder.setTitle(R.string.attention);
                    builder.setMessage(R.string.explain_permission_camera);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(EntryActivity.this, new String[]{android.Manifest.permission.CAMERA}, 101);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } else {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    builder.setTitle(R.string.attention);
                    builder.setMessage(R.string.explain_permission_camera);
                    builder.setPositiveButton("OK", null);
                    builder.show();

                }
            } else {
                replaceFragment(new LoginCoachQrCodeFragment());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     */

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_login);

        if (currentFragment instanceof LoginFragment) {
            replaceFragment(new LoginDecisionFragment());
        }else if(currentFragment instanceof RegisterFragment) {
            replaceFragment(new LoginFragment());
        }else if(currentFragment instanceof LoginCoachCredentialFragment){
            replaceFragment(new LoginDecisionFragment());
        }else if (currentFragment instanceof LoginDecisionFragment) {
            if (doubleBackToExitPressedOnce) {
                finish();

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

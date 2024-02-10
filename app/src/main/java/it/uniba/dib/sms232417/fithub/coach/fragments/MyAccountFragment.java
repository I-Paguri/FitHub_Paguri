package it.uniba.dib.sms232417.fithub.coach.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterCoach;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterAthlete;
import it.uniba.dib.sms232417.fithub.auth.CryptoUtil;
import it.uniba.dib.sms232417.fithub.auth.EntryActivity;
import it.uniba.dib.sms232417.fithub.entity.Athlete;
import it.uniba.dib.sms232417.fithub.entity.Coach;
import it.uniba.dib.sms232417.fithub.utilities.StringUtils;

public class MyAccountFragment extends Fragment {
    Toolbar toolbar;
    Athlete loggedAthlete;
    Coach loggedCoach;
    final String NAME_FILE = "automaticLogin";
    DatabaseAdapterAthlete dbAdapter;
    DatabaseAdapterCoach dbAdapterCoach;
    BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loggedAthlete = checkPatientLogged();
        loggedCoach = checkDoctorLogged();

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        Button btnLogout = getView().findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loggedCoach != null)
                    try {
                        onLogout(v, loggedCoach.getEmail());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                else if(loggedAthlete != null){
                    try {
                        onLogout(v, loggedAthlete.getEmail());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        toolbar = requireActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.home, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_account));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Navigate to HomeFragment
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        if (loggedAthlete != null) {
            TextView txtName = getView().findViewById(R.id.txt_name);
            TextView txtSurname = getView().findViewById(R.id.txt_surname);
            TextView txtRegion = getView().findViewById(R.id.txt_region);
            TextView txtage = getView().findViewById(R.id.txt_age);
            txtName.setText(loggedAthlete.getNome());
            txtSurname.setText(loggedAthlete.getCognome());
            String dataNascita = loggedAthlete.getDataNascita();

        }else if(loggedCoach != null){
            TextView txtName = getView().findViewById(R.id.txt_name);
            TextView txtSurname = getView().findViewById(R.id.txt_surname);

            txtName.setText(loggedCoach.getNome());
            txtSurname.setText(loggedCoach.getCognome());

        }else {
            RelativeLayout relativeLayout = getView().findViewById(R.id.not_logged_user);
            relativeLayout.setVisibility(View.VISIBLE);
        }



    }

    public void onLogout(View v, String email) throws Exception {
            dbAdapterCoach = new DatabaseAdapterCoach(getContext());
            dbAdapter = new DatabaseAdapterAthlete(getContext());


            Toast.makeText(getContext(),
                    "Logout effettuato",
                    Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(NAME_FILE, requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            CryptoUtil.deleteKey(email);

            File loggedAthleteFile = new File(StringUtils.FILE_PATH_ATHLETE_LOGGED);
            File loggedCoachFile = new File(StringUtils.FILE_PATH_COACH_LOGGED);
            if(loggedAthleteFile.exists()){
                loggedAthleteFile.delete();
                dbAdapter.onLogout();
            }
            if(loggedCoachFile.exists()){
                loggedCoachFile.delete();
                dbAdapterCoach.onLogout();
            }

            Intent esci = new Intent(getContext(), EntryActivity.class);
            startActivity(esci);
            requireActivity().finish();
    }




    public Athlete checkPatientLogged(){
        Athlete loggedAthlete;
        File loggedPatientFile = new File(StringUtils.FILE_PATH_ATHLETE_LOGGED);
        if(loggedPatientFile.exists()){
            try {
                FileInputStream fis = requireActivity().openFileInput(StringUtils.ATHLETE_LOGGED);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loggedAthlete = (Athlete) ois.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return loggedAthlete;
        }else
            return null;

    }

    public Coach checkDoctorLogged(){
        Coach loggedCoach;
        File loggedDoctorFile = new File(StringUtils.FILE_PATH_COACH_LOGGED);
        if(loggedDoctorFile.exists()){
            try {
                FileInputStream fis = requireActivity().openFileInput(StringUtils.COACH_LOGGED);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loggedCoach = (Coach) ois.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return loggedCoach;
        }else
            return null;
    }


}
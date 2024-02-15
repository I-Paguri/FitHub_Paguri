package it.uniba.dib.sms232417.fithub.coach.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.LinkedList;
import java.util.List;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterCoach;
import it.uniba.dib.sms232417.fithub.adapters.RecyclerListViewAdapter;
import it.uniba.dib.sms232417.fithub.entity.Athlete;
import it.uniba.dib.sms232417.fithub.entity.Coach;
import it.uniba.dib.sms232417.fithub.interfaces.OnAthleteListDataCallback;
import it.uniba.dib.sms232417.fithub.utilities.listItem;

public class MyAthletesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerListViewAdapter adapter;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    Coach coach;
    List<Athlete> myPatientsList;
    DatabaseAdapterCoach dbAdapterDoctor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.in_progress_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
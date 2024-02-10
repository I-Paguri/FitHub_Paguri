package it.uniba.dib.sms232417.fithub.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.auth.coach.LoginCoachCredentialFragment;
import it.uniba.dib.sms232417.fithub.auth.athlete.LoginFragment;

public class LoginDecisionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.login_decision_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnPatient = getView().findViewById(R.id.btnLoginPatient);
        MaterialButton btnDoctor = getView().findViewById(R.id.btnLoginDoctor);

        btnPatient.setOnClickListener(v -> {
            ((EntryActivity) getActivity()).replaceFragment(new LoginFragment());
        });

        btnDoctor.setOnClickListener(v -> {
            ((EntryActivity) getActivity()).replaceFragment(new LoginCoachCredentialFragment());
        });
    }


}

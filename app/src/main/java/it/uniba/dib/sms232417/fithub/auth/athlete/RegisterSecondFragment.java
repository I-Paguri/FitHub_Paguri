package it.uniba.dib.sms232417.fithub.auth.athlete;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import adil.dev.lib.materialnumberpicker.dialog.GenderPickerDialog;
import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.auth.EntryActivity;

public class RegisterSecondFragment extends Fragment {


    String nome;
    String cognome;
    String dataNascita;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.register_second_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle != null) {
            nome = bundle.getString("nome");
            cognome = bundle.getString("cognome");
            dataNascita = bundle.getString("dataNascita");
        }

        ImageView imageView = (ImageView) getView().findViewById(R.id.arrow_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        TextInputEditText gender = (TextInputEditText) getView().findViewById(R.id.txtGender);
        gender.setKeyListener(null);
        gender.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                GenderPickerDialog dialog=new GenderPickerDialog(getContext());
                dialog.setOnSelectingGender(new GenderPickerDialog.OnGenderSelectListener() {
                    @Override
                    public void onSelectingGender(String value) {
                        gender.setText(value);
                    }
                });
                dialog.show();

            }
            return false;
        });
        Button next = (Button) getView().findViewById(R.id.btn_continue);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("nome", nome);
                bundle.putString("cognome", cognome);
                bundle.putString("dataNascita", dataNascita);
                ((EntryActivity) getActivity()).replaceFragmentForRegistration(new RegisterThirdFragment(), bundle);
            }
        });
    }
}
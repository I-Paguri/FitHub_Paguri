package it.uniba.dib.sms232417.fithub.auth.athlete;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import javax.crypto.SecretKey;

import it.uniba.dib.sms232417.fithub.MainActivity;
import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterAthlete;
import it.uniba.dib.sms232417.fithub.auth.CryptoUtil;
import it.uniba.dib.sms232417.fithub.entity.Athlete;
import it.uniba.dib.sms232417.fithub.interfaces.OnAthleteDataCallback;
import it.uniba.dib.sms232417.fithub.utilities.StringUtils;

public class RegisterThirdFragment extends Fragment {
    String nome;
    String cognome;
    String dataNascita;
    DatabaseAdapterAthlete dbAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.register_third_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            nome = bundle.getString("nome");
            cognome = bundle.getString("cognome");
            dataNascita = bundle.getString("dataNascita");
            Log.d("NOME", nome);
            Log.d("COGNOME", cognome);
            Log.d("DATA", dataNascita);
        }
        ImageView imageView = (ImageView) getView().findViewById(R.id.arrow_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Button next = (Button) getView().findViewById(R.id.btn_register);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputEditText email = (TextInputEditText) getView().findViewById(R.id.txtEmail);
                TextInputEditText password = (TextInputEditText) getView().findViewById(R.id.txtPassword);
                TextInputEditText confirmPassword = (TextInputEditText) getView().findViewById(R.id.txtConfirm);

                if(email.getText().toString().isEmpty()){
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setTitle(R.string.error).setMessage(R.string.empty_fields_email);
                    builder.setPositiveButton(R.string.yes, null);
                    builder.show();
                } else if (password.getText().toString().isEmpty()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setTitle(R.string.error).setMessage(R.string.empty_fields_password);
                    builder.setPositiveButton(R.string.yes, null);
                    builder.show();
                } else if (confirmPassword.getText().toString().isEmpty()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setTitle(R.string.error).setMessage(R.string.empty_fields_password);
                    builder.setPositiveButton(R.string.yes, null);
                    builder.show();
                } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setTitle(R.string.error).setMessage(R.string.password_not_match);
                    builder.setPositiveButton(R.string.yes, null);
                    builder.show();
                } else {
                    onRegisterUsers(v, email.getText().toString(), password.getText().toString(), nome, cognome, dataNascita);

                }
            }
        });
    }

    public void onRegisterUsers(View v, String email, String password, String nome, String cognome,String dataNascita) {


        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        dbAdapter = new DatabaseAdapterAthlete(getContext());
        dbAdapter.onRegister(nome, cognome, email, dataNascita, password, new OnAthleteDataCallback() {
            @Override
            public void onCallback(Athlete athlete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.save_password).setMessage(R.string.save_password_explain);
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {

                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, requireActivity().MODE_PRIVATE).edit();
                    editor.putString("email", athlete.getEmail());
                    editor.putBoolean("isCoach",false);
                    //Encrypt password con chiave simmetrica e salva su file
                    byte[] encryptedPassword = new byte[0];
                    byte[] iv = new byte[0];
                    try {
                        CryptoUtil.generateandSaveSecretKey(athlete.getEmail());
                        SecretKey secretKey = CryptoUtil.loadSecretKey(athlete.getEmail());
                        Pair<byte[], byte[]> encryptionResult = CryptoUtil.encryptWithKey(secretKey, password.getBytes());
                        encryptedPassword = encryptionResult.first;
                        iv = encryptionResult.second;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    editor.putString("password", Base64.encodeToString(encryptedPassword, Base64.DEFAULT));
                    editor.putString("iv", Base64.encodeToString(iv, Base64.DEFAULT));
                    editor.commit();

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("loggedAthlete", (Parcelable) athlete);
                    startActivity(intent);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    requireActivity().finish();
                });

                builder.setNegativeButton(R.string.no, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("loggedAthlete", (Parcelable) athlete);
                    startActivity(intent);
                    requireActivity().finish();
                });
                builder.show();
            }


            @Override
            public void onCallbackError(Exception e, String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.error).setMessage(message);
                builder.setPositiveButton(R.string.yes, null);
                builder.show();
                progressBar.setVisibility(ProgressBar.INVISIBLE);

            }
        });
    }
}

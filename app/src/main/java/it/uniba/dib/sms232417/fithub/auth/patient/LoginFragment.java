package it.uniba.dib.sms232417.fithub.auth.patient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import javax.crypto.SecretKey;

import it.uniba.dib.sms232417.fithub.MainActivity;
import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.fithub.auth.CryptoUtil;
import it.uniba.dib.sms232417.fithub.auth.EntryActivity;
import it.uniba.dib.sms232417.fithub.interfaces.OnPatientDataCallback;
import it.uniba.dib.sms232417.fithub.entity.Patient;
import it.uniba.dib.sms232417.fithub.utilities.StringUtils;

public class LoginFragment extends Fragment {

    DatabaseAdapterPatient dbAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.login_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView forgetPass = (TextView) getView().findViewById(R.id.txtForgetPass);
        TextView register = (TextView) getView().findViewById(R.id.txtRegister);
        Button login = (Button) getView().findViewById(R.id.btnLogin);
        ImageView imageView = (ImageView) getView().findViewById(R.id.backArrow);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EntryActivity.class);
                startActivity(intent);
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgettPass(v);
            }

        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister(v);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((TextInputEditText) getView().findViewById(R.id.txtEmail)).getText().toString();
                String password = ((TextInputEditText) getView().findViewById(R.id.txtPassword)).getText().toString();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
                if (email.toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_email)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else if (password.toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_password)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else {
                    ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    dbAdapter = new DatabaseAdapterPatient(getContext());
                    dbAdapter.onLogin(email, password, new OnPatientDataCallback() {
                        @Override
                        public void onCallback(Patient patient) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(R.string.save_password)
                                    .setMessage(R.string.save_password_explain)
                                    .create();
                            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                                //Save password
                                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, requireActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", patient.getEmail());
                                editor.putBoolean("isDoctor",false);
                                //Encrypt password con chiave simmetrica e salva su file
                                byte[] encryptedPassword = new byte[0];
                                byte[] iv = new byte[0];
                                try {
                                    CryptoUtil.generateandSaveSecretKey(patient.getEmail());
                                    SecretKey secretKey = CryptoUtil.loadSecretKey(patient.getEmail());
                                    Pair<byte[], byte[]> encryptionResult = CryptoUtil.encryptWithKey(secretKey, password.getBytes());
                                    encryptedPassword = encryptionResult.first;
                                    iv = encryptionResult.second;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                editor.putString("password", Base64.encodeToString(encryptedPassword, Base64.DEFAULT));
                                editor.putString("iv", Base64.encodeToString(iv, Base64.DEFAULT));
                                editor.apply();

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("loggedPatient", (Parcelable) patient);
                                startActivity(intent);
                                progressBar.setVisibility(ProgressBar.GONE);
                                requireActivity().finish();
                            });
                            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("loggedPatient", (Parcelable) patient);
                                startActivity(intent);
                                progressBar.setVisibility(ProgressBar.GONE);
                                requireActivity().finish();
                            });
                            builder.show();
                        }
                        @Override
                        public void onCallbackError(Exception exception, String message) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(R.string.error).setMessage(message);
                            builder.setPositiveButton(R.string.yes, null);
                            builder.show();

                            progressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                }
            }
        });
    }

    private void onForgettPass(View V) {
        Toast.makeText(getContext(),
                "Hai Dimenticato la Password",
                Toast.LENGTH_SHORT).show();
    }

    private void onRegister(View V) {
        ((EntryActivity) getActivity()).replaceFragment(new RegisterFragment());
    }
}
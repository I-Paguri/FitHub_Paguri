package it.uniba.dib.sms232417.fithub.auth.coach;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import it.uniba.dib.sms232417.fithub.MainActivity;
import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.adapters.DatabaseAdapterCoach;
import it.uniba.dib.sms232417.fithub.auth.CryptoUtil;
import it.uniba.dib.sms232417.fithub.auth.EntryActivity;
import it.uniba.dib.sms232417.fithub.entity.Coach;
import it.uniba.dib.sms232417.fithub.interfaces.OnCoachDataCallback;
import it.uniba.dib.sms232417.fithub.utilities.StringUtils;

public class RegistrationCoachFragment extends Fragment {
    DatabaseAdapterCoach dbAdapter;

    String strDataNascita;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.coach_registration_layout, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        TextView login = (TextView) getView().findViewById(R.id.txtLogin);
        Button register = (Button) getView().findViewById(R.id.btnRegister);
        MaterialButton btnDataNascita = (MaterialButton) getView().findViewById(R.id.date_of_birth);
        strDataNascita = "";
        btnDataNascita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select a Date");

                Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendarStart.set(1940, 0, 1);
                long minDate = calendarStart.getTimeInMillis();
                Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendarEnd.set(2006, 0, 1);
                long maxDate = calendarEnd.getTimeInMillis();
                builder.setCalendarConstraints(new CalendarConstraints.Builder().setStart(minDate).setEnd(maxDate).build());

                MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.show(getFragmentManager(), "DATE_PICKER");
                materialDatePicker.addOnPositiveButtonClickListener(selection ->  {
                    Date selectedDate = new Date(selection);
                    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String date = format.format(selectedDate);
                    btnDataNascita.setText(date);
                    strDataNascita = date;

                });
                materialDatePicker.addOnNegativeButtonClickListener(
                        dialog -> {
                            btnDataNascita.setText(R.string.birth_date);
                            strDataNascita = "";
                        });
            }
        }
        );


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin(v);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();

                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }

                String email = ((TextInputEditText) getView().findViewById(R.id.txtEmail)).getText().toString();
                String password = ((TextInputEditText) getView().findViewById(R.id.txtPassword)).getText().toString();
                String confermaPassword = ((TextInputEditText) getView().findViewById(R.id.txtPasswordConf)).getText().toString();
                String nome = ((TextInputEditText) getView().findViewById(R.id.txtName)).getText().toString();
                String cognome = ((TextInputEditText) getView().findViewById(R.id.txtSurname)).getText().toString();


                if(email.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_email)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                }else if(password.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_password)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                }else if(nome.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_nome)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                }else if(cognome.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_cognome)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                }else if(strDataNascita.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_data)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                }else if(!password.equals(confermaPassword)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.password_not_match)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                }else
                    onRegisterCoach(v,email,password,nome,cognome, strDataNascita);
            }
        });



    }

    public void onLogin(View v) {
        ((EntryActivity) getActivity()).replaceFragment(new LoginCoachCredentialFragment());
    }

    public void onRegisterCoach(View v, String email, String password, String nome, String cognome, String dataNascita) {


        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        dbAdapter = new DatabaseAdapterCoach(getContext());
        dbAdapter.onRegister(nome, cognome, email, dataNascita, password, new OnCoachDataCallback() {
            @Override
            public void onCallback(Coach coach) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.save_password).setMessage(R.string.save_password_explain);
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {

                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, requireActivity().MODE_PRIVATE).edit();
                    editor.putString("email", coach.getEmail());
                    editor.putBoolean("isDoctor",false);
                    //Encrypt password con chiave simmetrica e salva su file
                    byte[] encryptedPassword = new byte[0];
                    byte[] iv = new byte[0];
                    try {
                        CryptoUtil.generateandSaveSecretKey(coach.getEmail());
                        SecretKey secretKey = CryptoUtil.loadSecretKey(coach.getEmail());
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
                    intent.putExtra("loggedCoach", (Parcelable) coach);
                    startActivity(intent);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    requireActivity().finish();
                });

                builder.setNegativeButton(R.string.no, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("loggedCoach", (Parcelable) coach);
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



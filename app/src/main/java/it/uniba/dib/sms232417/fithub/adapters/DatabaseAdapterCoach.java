package it.uniba.dib.sms232417.fithub.adapters;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;
import java.util.List;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.entity.Coach;
import it.uniba.dib.sms232417.fithub.entity.Athlete;
import it.uniba.dib.sms232417.fithub.entity.Treatment;
import it.uniba.dib.sms232417.fithub.interfaces.OnCoachDataCallback;
import it.uniba.dib.sms232417.fithub.interfaces.OnAthleteListDataCallback;

public class DatabaseAdapterCoach {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Coach coach;
    Context context;
    public DatabaseAdapterCoach(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }
    public void onRegister(String nome, String cognome, String email, String dataNascita, String password, OnCoachDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser utente = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("REGISTER", "Registrazione effettuata con successo");

                        coach = new Coach(utente.getUid(), nome, cognome, email, dataNascita);
                        Log.d("REGISTER", "Utente: " + coach.toString());
                        db.collection("coach")
                                .document(utente.getUid())
                                .set(coach)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onCallback(coach);
                                    Log.d("Firestore", "Utente scritto con successo");
                                })
                                .addOnFailureListener(task1 -> {
                                    Log.d("Error", task1.toString());
                                    callback.onCallbackError(new Exception(), task1.toString());
                                });
                    }
                })
                .addOnFailureListener(task -> {
                    callback.onCallbackError(new Exception(), task.toString());
                });
    }
    public void onLogin(String email, String password, OnCoachDataCallback callback){
        Log.d("LOGIN", "inizioMetodo");
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser dottore = mAuth.getCurrentUser();
                    db = FirebaseFirestore.getInstance();

                    db.collection("coach")
                            .document(dottore.getUid())
                            .get()
                            .addOnSuccessListener(datiUtente-> {
                                if (datiUtente.exists()) {
                                    Log.d("Login", "Login in corso");
                                    coach = new Coach(datiUtente.getString("UUID"),
                                            datiUtente.getString("nome"),
                                            datiUtente.getString("cognome"),
                                            datiUtente.getString("email"),
                                            datiUtente.getString("dataNascita")
                                            );
                                            /*
                                            List<String> myPatients = (List<String>) datiUtente.get("myPatients");
                                            coach.setMyPatientsUUID(myPatients);

                                            if(myPatients == null || myPatients.isEmpty())

                                                Log.d("MyPatients", "Non ce");
                                            if (myPatients != null && !myPatients.isEmpty())
                                                Log.d("MyPatients", myPatients.toString());

                                             */
                                    callback.onCallback(coach);
                                }else{
                                    callback.onCallbackError(new Exception(), context.getString(R.string.error_login_section_patient));
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(new Exception(), e.toString());
                });
    }
    public void onLogout(){
        mAuth.signOut();
    }
    /*
    public void getDoctorPatients(List<String> patientUUID, OnAthleteListDataCallback callback) {
    db = FirebaseFirestore.getInstance();

    for (String uuid : patientUUID){
        db.collection("patient")
                .whereIn("uuid", Collections.singletonList(uuid))
                .orderBy("nome", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Athlete> athletes = queryDocumentSnapshots.toObjects(Athlete.class);

                        callback.onCallback(athletes);
                    } else {
                        Log.d("PATIENT", "Error");
                        callback.onCallbackError(new Exception(), "Errore caricamento pazienti");
                    }
                });
    }
}
    public void onLoginQrCode(String token, OnCoachDataCallback callback){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCustomToken(token)
                .addOnSuccessListener(authResult -> {
                    Log.d("Login", "Login in corso");
                    FirebaseUser dottore = mAuth.getCurrentUser();
                    db = FirebaseFirestore.getInstance();

                    db.collection("doctor")
                            .document(dottore.getUid())
                            .get()
                            .addOnSuccessListener(datiUtente-> {
                                if (datiUtente.exists()) {
                                    Log.d("Login", "Login in corso");
                                    coach = new Coach(datiUtente.getString("nome"),
                                            datiUtente.getString("cognome"),
                                            datiUtente.getString("email"),
                                            datiUtente.getString("dataNascita"),
                                            datiUtente.getString("regione"),
                                            datiUtente.getString("specializzazione"),
                                            datiUtente.getString("numeroDiRegistrazioneMedica"));

                                    List<String> myPatients = (List<String>) datiUtente.get("myPatients");
                                    coach.setMyPatientsUUID(myPatients);
                                    if(myPatients == null || myPatients.isEmpty())
                                        Log.d("MyPatients", "Non ce");
                                    if (myPatients != null && !myPatients.isEmpty())
                                        Log.d("MyPatients", myPatients.toString());
                                    callback.onCallback(coach);
                                }else{
                                    callback.onCallbackError(new Exception(), "QR Code non valido");
                                    Log.d("Login", "Login non effettuato");
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("Login", "Login non effettuato");
                    callback.onCallbackError(new Exception(), e.toString());
                });
    }

    public void addTreatment(Treatment treatment) {

    }

     */
}

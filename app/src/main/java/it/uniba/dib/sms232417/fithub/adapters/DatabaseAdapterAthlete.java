package it.uniba.dib.sms232417.fithub.adapters;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms232417.fithub.R;
import it.uniba.dib.sms232417.fithub.entity.Athlete;
import it.uniba.dib.sms232417.fithub.entity.Treatment;
import it.uniba.dib.sms232417.fithub.interfaces.OnAthleteDataCallback;
import it.uniba.dib.sms232417.fithub.interfaces.OnCountCallback;
import it.uniba.dib.sms232417.fithub.interfaces.OnTreatmentsCallback;

public class DatabaseAdapterAthlete {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Athlete athlete;
    Athlete resultAthlete;
    Context context;

    public DatabaseAdapterAthlete(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void onLogin(String emailIns, String password, OnAthleteDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(emailIns, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser utente = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("LOGIN", "Login effettuato con successo");

                        db.collection("athlete")
                                .document(utente.getUid())
                                .get()
                                .addOnSuccessListener(datiUtente -> {
                                    if (datiUtente.exists()) {
                                        resultAthlete = new Athlete(utente.getUid(), datiUtente.getString("nome"),
                                                datiUtente.getString("cognome"),
                                                datiUtente.getString("email"),
                                                datiUtente.getString("dataNascita"));
                                        callback.onCallback(resultAthlete);
                                    } else {
                                        callback.onCallbackError(new Exception(), context.getString(R.string.error_login_section_coach));
                                    }
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

    public void onRegister(String nome, String cognome, String email, String dataNascita, String password, OnAthleteDataCallback callback) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser utente = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();
                        Log.d("REGISTER", "Registrazione effettuata con successo");

                        athlete = new Athlete(utente.getUid(), nome, cognome, email, dataNascita);
                        Log.d("REGISTER", "Utente: " + athlete.toString());
                        db.collection("athlete")
                                .document(utente.getUid())
                                .set(athlete)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onCallback(athlete);
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

    public void onLogout() {
        mAuth.signOut();
    }

    public void addTreatment(String athleteUUID, Treatment treatment, OnTreatmentsCallback onTreatmentsCallback) {
        Log.d("AddedNewTreatment", treatment.toString());

        getTreatmentCount(athleteUUID, new OnCountCallback() {
            @Override
            public void onCallback(int count) {
                String treatmentId = "treatment" + (count + 1);
                db.collection("athlete")
                        .document(athleteUUID)
                        .collection("treatments")
                        .document(treatmentId)
                        .set(treatment)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Treatment successfully written!");
                            onTreatmentsCallback.onCallback(null);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Error writing treatment", e);
                            onTreatmentsCallback.onCallbackFailed(e);
                        });
            }

            @Override
            public void onCallbackFailed(Exception e) {
                Log.w("Firestore", "Error getting treatment count", e);
                onTreatmentsCallback.onCallbackFailed(e);
            }
        });
    }

    private void getTreatmentCount(String athleteUUID, OnCountCallback callback) {
        try {
            db.collection("athlete")
                    .document(athleteUUID)
                    .collection("treatments")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        callback.onCallback(queryDocumentSnapshots.size());
                    })
                    .addOnFailureListener(e -> {
                        callback.onCallbackFailed(e);
                    });
        } catch (Exception e) {
            callback.onCallbackFailed(e);
        }
    }


    public void getTreatments(String athleteUUID, OnTreatmentsCallback callback) {

        db.collection("athlete")
                .document(athleteUUID)
                .collection("treatments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Treatment> treatments = new HashMap<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Treatment treatment = doc.toObject(Treatment.class);
                        String treatmentId = doc.getId(); // Get the treatmentId

                        treatments.put(treatmentId, treatment); // Add the treatmentId and Treatment object to the map
                    }
                    callback.onCallback(treatments); // Pass the map to the callback
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackFailed(e);
                });
    }


    public void deleteTreatment(String athleteUUID, String treatmentId) {
        db.collection("athlete")
                .document(athleteUUID)
                .collection("treatments")
                .document(treatmentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Treatment successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting treatment", e);
                });
    }
}

package it.uniba.dib.sms232417.fithub.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coach implements Parcelable, Serializable {
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private String regione;
    private String specializzazione;
    private String numeroDiRegistrazioneMedica;
    private List<String> myPatientsUUID;

    public Coach() {
    }

    public Coach(String nome, String cognome, String email, String dataNascita, String regione, String specializzazione, String numeroDiRegistrazioneMedica) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;
        this.regione = regione;
        this.specializzazione = specializzazione;

        this.numeroDiRegistrazioneMedica = numeroDiRegistrazioneMedica;

        this.myPatientsUUID = new ArrayList<>();
    }

    protected Coach(Parcel in) {
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        dataNascita = in.readString();
        regione = in.readString();
        specializzazione = in.readString();
        numeroDiRegistrazioneMedica = in.readString();
        myPatientsUUID = in.createStringArrayList();
    }

    public static final Creator<Coach> CREATOR = new Creator<Coach>() {
        @Override
        public Coach createFromParcel(Parcel in) {
            return new Coach(in);
        }

        @Override
        public Coach[] newArray(int size) {
            return new Coach[size];
        }
    };

    public List<String> getMyPatientsUUID() {
        return myPatientsUUID;
    }

    public void setMyPatientsUUID(List<String> myPatients) {
        this.myPatientsUUID = myPatients;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getRegione() {
        return regione;
    }

    public String getSpecializzazione() {
        return specializzazione;
    }


    public String getNumeroDiRegistrazioneMedica() {
        return numeroDiRegistrazioneMedica;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(email);
        dest.writeString(dataNascita);
        dest.writeString(regione);
        dest.writeString(specializzazione);
        dest.writeString(numeroDiRegistrazioneMedica);
        dest.writeStringList(myPatientsUUID);
    }
    /*

    public void addTreatment(Treatment treatment, Patient patient) {
        String UUID = patient.getUUID();

    }
    */
}

package it.uniba.dib.sms232417.fithub.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coach implements Parcelable, Serializable {
    private String UUID;
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;

    private String regione;
    private String specializzazione;
    private String numeroDiRegistrazioneMedica;
    private List<String> myAthletesUUID;

    public Coach() {
    }

    public Coach(String uuid,String nome, String cognome, String email, String dataNascita) {
        this.UUID = uuid;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;

    }


    @Override
    public String toString() {
        return "Coach{" +
                "UUID='" + UUID + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascita='" + dataNascita + '\'' +
                '}';
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
        this.myAthletesUUID = new ArrayList<>();

    }

    protected Coach(Parcel in) {
        UUID = in.readString();
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        dataNascita = in.readString();

        regione = in.readString();
        specializzazione = in.readString();
        numeroDiRegistrazioneMedica = in.readString();
        myAthletesUUID = in.createStringArrayList();

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


    public List<String> getMyAthletesUUID() {
        return myAthletesUUID;
    }

    public void setMyAthletesUUID(List<String> myAthletes) {
        this.myAthletesUUID = myAthletes;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(UUID);
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(email);
        dest.writeString(dataNascita);
        dest.writeString(regione);
        dest.writeString(specializzazione);
        dest.writeString(numeroDiRegistrazioneMedica);
        dest.writeStringList(myAthletesUUID);
    }
    /*

    public void addTreatment(Treatment treatment, Patient patient) {
        String UUID = patient.getUUID();

    }
    */
}

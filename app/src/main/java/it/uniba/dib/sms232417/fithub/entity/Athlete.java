package it.uniba.dib.sms232417.fithub.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Athlete implements Parcelable, Serializable {
    private String UUID;
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private List<Treatment> treatments;
    private Coach coach;

    public Athlete() {
    }

    public Athlete(String UUID, String nome, String cognome, String email, String dataNascita){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;

        this.UUID = UUID;

    }

    protected Athlete(Parcel in) {
        UUID = in.readString();
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        dataNascita = in.readString();
    }

    public static final Creator<Athlete> CREATOR = new Creator<Athlete>() {
        @Override
        public Athlete createFromParcel(Parcel in) {
            return new Athlete(in);
        }

        @Override
        public Athlete[] newArray(int size) {
            return new Athlete[size];
        }
    };

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUUID() {
        return UUID;
    }

    public String getCognome() {
        return cognome;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public int getAge() {
        String birthdayString = this.getDataNascita();
        try {
            // Parse the birthday string into a Date object
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

            Date birthDate = sdf.parse(birthdayString);

            // Get the current date
            Calendar today = Calendar.getInstance();

            // Convert the birth date into a Calendar object
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTimeInMillis(birthDate.getTime());

            // Calculate the age
            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // If the birthday has not occurred this year, subtract one from the age
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }



    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "UUID='" + UUID + '\'' +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascita='" + dataNascita + '\'' +
                '}';
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
    }
}

package it.uniba.dib.sms232417.fithub.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Exercise implements Parcelable {

    private String name;
    private String muscleGroup;
    private String reps;
    private int sets;
    private String rest;

    public Exercise() {
    }
    public Exercise(String name, String muscleGroup, String reps, int sets, String rest) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.reps = reps;
        this.sets = sets;
        this.rest = rest;
    }

    protected Exercise(Parcel in) {
        name = in.readString();
        muscleGroup = in.readString();
        reps = in.readString();
        sets = in.readInt();
        rest = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }

    @NonNull
    @Override
    public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ", muscleGroup='" + muscleGroup + '\'' +
                ", reps='" + reps + '\'' +
                ", sets=" + sets +
                ", rest=" + rest +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(muscleGroup);
        dest.writeString(reps);
        dest.writeInt(sets);
        dest.writeString(rest);
    }
}

package it.uniba.dib.sms232417.fithub.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class WorkoutDay implements Parcelable {
    private int dayNumber;
    private ArrayList<Exercise> exercises;


    public WorkoutDay() {

    }

    public WorkoutDay(int dayNumber) {
        this.dayNumber = dayNumber;
        this.exercises = new ArrayList<>();
    }

    protected WorkoutDay(Parcel in) {
        dayNumber = in.readInt();
        exercises = in.createTypedArrayList(Exercise.CREATOR);
    }

    public static final Parcelable.Creator<WorkoutDay> CREATOR = new Parcelable.Creator<WorkoutDay>() {
        @Override
        public WorkoutDay createFromParcel(Parcel in) {
            return new WorkoutDay(in);
        }

        @Override
        public WorkoutDay[] newArray(int size) {
            return new WorkoutDay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(dayNumber);
        dest.writeTypedList(exercises);
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        this.exercises.remove(exercise);
    }

    public void removeExercise(int index) {
        this.exercises.remove(index);
    }

    public void clearExercises() {
        this.exercises.clear();
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkoutDay{" +
                "dayNumber=" + dayNumber +
                ", exercises=" + exercises +
                '}';
    }


}

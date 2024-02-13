package it.uniba.dib.sms232417.fithub.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WorkoutPlan implements Parcelable{
    private String workoutTarget;
    private Date startDate, endDate;

    private ArrayList<WorkoutDay> workoutDays;
    private String notes;

    public WorkoutPlan() {

    }

    public WorkoutPlan(String workoutTarget, Date startDate, Date endDate) {
        this.workoutTarget = workoutTarget;
        this.startDate = startDate;
        this.endDate = endDate;

        // Default values
        //this.exercises = new ArrayList<>();
        this.workoutDays = new ArrayList<>();
        this.notes = "";
    }

    protected WorkoutPlan(Parcel in) {
        workoutTarget = in.readString();
    }

    public static final Parcelable.Creator<WorkoutPlan> CREATOR = new Parcelable.Creator<WorkoutPlan>() {
        @Override
        public WorkoutPlan createFromParcel(Parcel in) {
            return new WorkoutPlan(in);
        }

        @Override
        public WorkoutPlan[] newArray(int size) {
            return new WorkoutPlan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(workoutTarget);
        dest.writeString(getStartDateString());
        dest.writeString(getEndDateString());
    }

    public String getWorkoutTarget() {
        return workoutTarget;
    }

    public void setWorkoutTarget(String workoutTarget) {
        this.workoutTarget = workoutTarget;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return dateFormat.format(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());
        String endDateString;
        endDateString = "";

        if (endDate != null) {
            endDateString = dateFormat.format(endDate);
        }

        return endDateString;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public ArrayList<WorkoutDay> getWorkoutDays() {
        return workoutDays;
    }

    public void setWorkoutDays(ArrayList<WorkoutDay> workoutDays) {
        this.workoutDays = workoutDays;
    }

    public void addWorkoutDay(WorkoutDay workoutDay) {
        this.workoutDays.add(workoutDay);
    }

    public void removeWorkoutDay(int index) {
        this.workoutDays.remove(index);
    }

    public void removeWorkoutDay(WorkoutDay workoutDay) {
        this.workoutDays.remove(workoutDay);
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        String treatmentString;

        treatmentString = "Target: " + getWorkoutTarget() + "\n";
        treatmentString = treatmentString + "Start date: " + getStartDateString() + "\n";
        if (!getEndDateString().isEmpty()) {
            treatmentString = treatmentString + "End date: " + getEndDateString() + "\n";
        }

        treatmentString = treatmentString + "Workout days: " + getWorkoutDays().toString() + "\n";


        if (!getNotes().isEmpty()) {
            treatmentString = treatmentString + "Notes: " + getNotes() + "\n";
        }

        return treatmentString;
    }

}

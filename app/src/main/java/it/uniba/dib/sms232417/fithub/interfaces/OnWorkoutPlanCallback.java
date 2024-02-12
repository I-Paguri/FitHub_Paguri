package it.uniba.dib.sms232417.fithub.interfaces;

import java.util.Map;

import it.uniba.dib.sms232417.fithub.entity.Treatment;
import it.uniba.dib.sms232417.fithub.entity.WorkoutPlan;

public interface OnWorkoutPlanCallback {
    void onCallback(Map<String, WorkoutPlan> workoutPlans);
    void onCallbackFailed(Exception e);
}

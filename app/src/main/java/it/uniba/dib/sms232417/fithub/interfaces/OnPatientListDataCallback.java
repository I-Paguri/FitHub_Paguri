package it.uniba.dib.sms232417.fithub.interfaces;

import java.util.List;

import it.uniba.dib.sms232417.fithub.entity.Athlete;

public interface OnPatientListDataCallback {
    void onCallback(List<Athlete> athleteList);
    void onCallbackError(Exception exception, String message);
}

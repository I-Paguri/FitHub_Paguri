package it.uniba.dib.sms232417.fithub.interfaces;

import it.uniba.dib.sms232417.fithub.entity.Athlete;

public interface OnPatientDataCallback {
    void onCallback(Athlete athlete);
    void onCallbackError(Exception exception, String message);
}

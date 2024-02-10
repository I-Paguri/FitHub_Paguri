package it.uniba.dib.sms232417.fithub.interfaces;

import it.uniba.dib.sms232417.fithub.entity.Patient;

public interface OnPatientDataCallback {
    void onCallback(Patient patient);
    void onCallbackError(Exception exception, String message);
}

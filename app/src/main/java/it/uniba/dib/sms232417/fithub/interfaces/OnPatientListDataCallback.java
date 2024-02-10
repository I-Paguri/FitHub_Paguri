package it.uniba.dib.sms232417.fithub.interfaces;

import java.util.List;

import it.uniba.dib.sms232417.fithub.entity.Patient;

public interface OnPatientListDataCallback {
    void onCallback(List<Patient> patientList);
    void onCallbackError(Exception exception, String message);
}

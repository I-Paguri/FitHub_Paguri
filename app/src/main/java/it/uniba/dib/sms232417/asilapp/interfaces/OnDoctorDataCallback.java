package it.uniba.dib.sms232417.asilapp.interfaces;

import it.uniba.dib.sms232417.asilapp.entity.Doctor;

public interface OnDoctorDataCallback {
    void onCallback(Doctor doctor);


    void onCallbackError(Exception exception,String message);
}

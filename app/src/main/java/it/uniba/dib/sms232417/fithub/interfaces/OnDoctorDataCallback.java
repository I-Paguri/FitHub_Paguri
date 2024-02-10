package it.uniba.dib.sms232417.fithub.interfaces;

import it.uniba.dib.sms232417.fithub.entity.Doctor;

public interface OnDoctorDataCallback {
    void onCallback(Doctor doctor);


    void onCallbackError(Exception exception,String message);
}

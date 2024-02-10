package it.uniba.dib.sms232417.fithub.interfaces;

import it.uniba.dib.sms232417.fithub.entity.Coach;

public interface OnCoachDataCallback {
    void onCallback(Coach coach);


    void onCallbackError(Exception exception,String message);
}

package it.uniba.dib.sms232417.fithub.interfaces;

public interface OnCountCallback {
    void onCallback(int count);
    void onCallbackFailed(Exception e);
}
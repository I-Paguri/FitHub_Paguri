package it.uniba.dib.sms232417.fithub.interfaces;

import java.util.Map;

import it.uniba.dib.sms232417.fithub.entity.Treatment;

public interface OnTreatmentsCallback {
    void onCallback(Map<String, Treatment> treatments);
    void onCallbackFailed(Exception e);
}
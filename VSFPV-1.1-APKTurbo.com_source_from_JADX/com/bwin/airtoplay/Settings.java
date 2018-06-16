package com.bwin.airtoplay;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class Settings {
    private static final String PARAMETER_SETTING_ALTITUDE_HOLD = "com.bwin.airtoplay.altitude_hold";
    private static final String PARAMETER_SETTING_PARAMETERS_AUTO_SAVE = "com.bwin.airtoplay.parameters_auto_save";
    private static final String PARAMETER_SETTING_RIGHT_HAND_MODE = "com.bwin.airtoplay.right_hand_mode";
    private static final String PARAMETER_SETTING_SPEED_LIMIT = "com.bwin.airtoplay.speed_limit";
    private static final String PARAMETER_SETTING_TRIM_AIL = "com.bwin.airtoplay.trim_ail";
    private static final String PARAMETER_SETTING_TRIM_ELE = "com.bwin.airtoplay.trim_ele";
    private static final String PARAMETER_SETTING_TRIM_RUDD = "com.bwin.airtoplay.trim_rudd";
    private static Settings instance = null;
    private static Context mContext;

    private Settings(Context context) {
        mContext = context;
    }

    public static Settings getInstance(Context context) {
        synchronized (Settings.class) {
            if (instance == null) {
                instance = new Settings(context);
            }
        }
        return instance;
    }

    public void resetSettings() {
        Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME, 0).edit();
        editor.putInt(PARAMETER_SETTING_TRIM_RUDD, 0);
        editor.putInt(PARAMETER_SETTING_TRIM_ELE, 0);
        editor.putInt(PARAMETER_SETTING_TRIM_AIL, 0);
        editor.putBoolean(PARAMETER_SETTING_ALTITUDE_HOLD, false);
        editor.putInt(PARAMETER_SETTING_SPEED_LIMIT, 0);
        editor.commit();
    }

    private void putBoolean(String s, boolean b) {
        Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME, 0).edit();
        editor.putBoolean(s, b);
        editor.commit();
    }

    private void putInt(String s, int i) {
        Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME, 0).edit();
        editor.putInt(s, i);
        editor.commit();
    }

    public void saveParameterForAutosave(boolean autosave) {
        putBoolean(PARAMETER_SETTING_PARAMETERS_AUTO_SAVE, autosave);
    }

    public void saveParameterForRightHandMode(boolean rightHandMode) {
        putBoolean(PARAMETER_SETTING_RIGHT_HAND_MODE, rightHandMode);
    }

    public void saveParameterForTrimRUDD(int trimValue) {
        putInt(PARAMETER_SETTING_TRIM_RUDD, trimValue);
    }

    public void saveParameterForTrimELE(int trimValue) {
        putInt(PARAMETER_SETTING_TRIM_ELE, trimValue);
    }

    public void saveParameterForTrimAIL(int trimValue) {
        putInt(PARAMETER_SETTING_TRIM_AIL, trimValue);
    }

    public void saveParameterForAltitudeHold(boolean altitudeHold) {
        putBoolean(PARAMETER_SETTING_ALTITUDE_HOLD, altitudeHold);
    }

    public void saveParameterForSpeedLimit(int speedLimit) {
        putInt(PARAMETER_SETTING_SPEED_LIMIT, speedLimit);
    }

    private int getInt(String s) {
        return mContext.getSharedPreferences(Constants.PREFS_NAME, 0).getInt(s, 0);
    }

    public boolean getParameterForAutosave() {
        return mContext.getSharedPreferences(Constants.PREFS_NAME, 0).getBoolean(PARAMETER_SETTING_PARAMETERS_AUTO_SAVE, true);
    }

    public boolean getParameterForRightHandMode() {
        return mContext.getSharedPreferences(Constants.PREFS_NAME, 0).getBoolean(PARAMETER_SETTING_RIGHT_HAND_MODE, false);
    }

    public int getParameterForTrimRUDD() {
        return getInt(PARAMETER_SETTING_TRIM_RUDD);
    }

    public int getParameterForTrimELE() {
        return getInt(PARAMETER_SETTING_TRIM_ELE);
    }

    public int getParameterForTrimAIL() {
        return getInt(PARAMETER_SETTING_TRIM_AIL);
    }

    public boolean getParameterForAltitudeHold() {
        return mContext.getSharedPreferences(Constants.PREFS_NAME, 0).getBoolean(PARAMETER_SETTING_ALTITUDE_HOLD, false);
    }

    public int getParameterForSpeedLimit() {
        return getInt(PARAMETER_SETTING_SPEED_LIMIT);
    }
}

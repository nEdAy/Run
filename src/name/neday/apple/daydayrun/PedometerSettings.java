/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;

import android.content.SharedPreferences;

/**
 * 包装 {@link SharedPreferences}, 处理用户偏好任务。
 * @author nEdAy
 */
public class PedometerSettings {

    SharedPreferences mSettings;
    
    public static int M_NONE = 1;
    public static int M_PACE = 2;
    public static int M_SPEED = 3;
    
    public PedometerSettings(SharedPreferences settings) {
        mSettings = settings;
    }
    
    public float getStepLength() {
        try {
            return Float.valueOf(mSettings.getString("step_length", "75").trim());
        }
        catch (NumberFormatException e) {
            // TODO: 重置数值，并在某种程度上通知用户
            return 0f;
        }
    }
    
    public float getBodyWeight() {
        try {
            return Float.valueOf(mSettings.getString("body_weight", "50").trim());
        }
        catch (NumberFormatException e) {
            // TODO: 重置数值，并在某种程度上通知用户
            return 0f;
        }
    }

    public boolean isRunning() {
        return mSettings.getString("exercise_type", "running").equals("running");
    }

    
    //-------------------------------------------------------------------
    // 语音:
    
    public boolean shouldSpeak() {
        return mSettings.getBoolean("speak", false);
    }
    public float getSpeakingInterval() {
        try {
            return Float.valueOf(mSettings.getString("speaking_interval", "1"));
        }
        catch (NumberFormatException e) {
            // 这不能发生当值从列表中选择
            return 1;
        }
    }
    public boolean shouldTellSteps() {
        return mSettings.getBoolean("speak", false) 
        && mSettings.getBoolean("tell_steps", false);
    }
    public boolean shouldTellPace() {
        return mSettings.getBoolean("speak", false) 
        && mSettings.getBoolean("tell_pace", false);
    }
    public boolean shouldTellDistance() {
        return mSettings.getBoolean("speak", false) 
        && mSettings.getBoolean("tell_distance", false);
    }
    public boolean shouldTellSpeed() {
        return mSettings.getBoolean("speak", false) 
        && mSettings.getBoolean("tell_speed", false);
    }
    public boolean shouldTellCalories() {
        return mSettings.getBoolean("speak", false) 
        && mSettings.getBoolean("tell_calories", false);
    }
  
    public boolean wakeAggressively() {
        return mSettings.getString("operation_level", "run_in_background").equals("wake_up");
    }
    public boolean keepScreenOn() {
        return mSettings.getString("operation_level", "run_in_background").equals("keep_screen_on");
    }
    
    // 核心
    public void saveServiceRunningWithNullTimestamp(boolean running) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", running);
        editor.commit();
    }
    public void clearServiceRunning() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", false);
        editor.commit();
    }
    public boolean isServiceRunning() {
        return mSettings.getBoolean("service_running", false);
    }
}

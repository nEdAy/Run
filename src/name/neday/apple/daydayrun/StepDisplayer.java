/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;

import java.util.ArrayList;

/**
 * 利用计步传感器及通过的电流统计步数 
 * 统计步数的活动.
 */
public class StepDisplayer implements StepListener, SpeakingTimer.Listener {

    private int mCount = 0;
    PedometerSettings mSettings;
    Utils mUtils;

    public StepDisplayer(PedometerSettings settings, Utils utils) {
        mUtils = utils;
        mSettings = settings;
        notifyListener();
    }
    public void setUtils(Utils utils) {
        mUtils = utils;
    }

    public void setSteps(int steps) {
        mCount = steps;
        notifyListener();
    }
    @Override
	public void onStep() {
        mCount ++;
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }
    @Override
	public void passValue() {
    }
    
    

    //-----------------------------------------------------
    // 监听器
    
    public interface Listener {
        public void stepsChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged(mCount);
        }
    }
    
    //-----------------------------------------------------
    // 语音
    
    @Override
	public void speak() {
        if (mSettings.shouldTellSteps()) { 
            if (mCount > 0) {
                mUtils.say("" + mCount + " steps");
            }
        }
    }
    
    
}

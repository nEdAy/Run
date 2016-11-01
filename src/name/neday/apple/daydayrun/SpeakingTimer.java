/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;

import java.util.ArrayList;

/**
 * 反腐调用所有监听对象. 
 * 间隔被定义在用户设置中.
 * @author nEdAy
 */
public class SpeakingTimer implements StepListener {

    PedometerSettings mSettings;
    Utils mUtils;
    boolean mShouldSpeak;
    float mInterval;
    long mLastSpeakTime;
    
    public SpeakingTimer(PedometerSettings settings, Utils utils) {
        mLastSpeakTime = System.currentTimeMillis();
        mSettings = settings;
        mUtils = utils;
        reloadSettings();
    }
    public void reloadSettings() {
        mShouldSpeak = mSettings.shouldSpeak();
        mInterval = mSettings.getSpeakingInterval();
    }
    
    @Override
	public void onStep() {
        long now = System.currentTimeMillis();
        long delta = now - mLastSpeakTime;
        
        if (delta / 60000.0 >= mInterval) {
            mLastSpeakTime = now;
            notifyListeners();
        }
    }
    
    @Override
	public void passValue() {
        // 没用
    }

    
    //-----------------------------------------------------
    // 监听器
    
    public interface Listener {
        public void speak();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListeners() {
        mUtils.ding();
        for (Listener listener : mListeners) {
            listener.speak();
        }
    }

    //-----------------------------------------------------
    // 语言
    
    public boolean isSpeaking() {
        return mUtils.isSpeakingNow();
    }
}


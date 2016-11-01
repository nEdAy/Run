/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;

import java.util.ArrayList;

/**
 * 计算并显示步幅 (步 / 分钟), 处理所需的步幅,
 //* 通知用户他/她走的快还是慢.  
 * @author nEdAy
 */
public class PaceNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void paceChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();
    
    int mCounter = 0;
    
    private long mLastStepTime = 0;
    private long[] mLastStepDeltas = {-1, -1, -1, -1};
    private int mLastStepDeltasIndex = 0;
    private long mPace = 0;
    
    PedometerSettings mSettings;
    Utils mUtils;

    public PaceNotifier(PedometerSettings settings, Utils utils) {
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }
    public void setPace(int pace) {
        mPace = pace;
        int avg = (int)(60*1000.0 / mPace);
        for (int i = 0; i < mLastStepDeltas.length; i++) {
            mLastStepDeltas[i] = avg;
        }
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }
    
    public void addListener(Listener l) {
        mListeners.add(l);
    }

    @Override
	public void onStep() {
        long thisStepTime = System.currentTimeMillis();
        mCounter ++;
        
        // 计算步幅基于最后的x步
        if (mLastStepTime > 0) {
            long delta = thisStepTime - mLastStepTime;
            
            mLastStepDeltas[mLastStepDeltasIndex] = delta;
            mLastStepDeltasIndex = (mLastStepDeltasIndex + 1) % mLastStepDeltas.length;
            
            long sum = 0;
            boolean isMeaningfull = true;
            for (int i = 0; i < mLastStepDeltas.length; i++) {
                if (mLastStepDeltas[i] < 0) {
                    isMeaningfull = false;
                    break;
                }
                sum += mLastStepDeltas[i];
            }
            if (isMeaningfull && sum > 0) {
                long avg = sum / mLastStepDeltas.length;
                mPace = 60*1000 / avg;
            }
            else {
                mPace = -1;
            }
        }
        mLastStepTime = thisStepTime;
        notifyListener();
    }
    
    private void notifyListener() {
        for (Listener listener : mListeners) {
            listener.paceChanged((int)mPace);
        }
    }
    
    @Override
	public void passValue() {
        // 没有用处
    }

    //-----------------------------------------------------
    //语音
    
    @Override
	public void speak() {
        if (mSettings.shouldTellPace()) {
            if (mPace > 0) {
                mUtils.say(mPace + " steps per minute");
            }
        }
    }
    

}


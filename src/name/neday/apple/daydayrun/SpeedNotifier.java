/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */
package name.neday.apple.daydayrun;


/**
 * 计算并显示步幅 (步 /分钟), 处理需要步幅的输入,
 //* 通知用户他/她走的快还是慢.
 * 
 * 通过调用 {@link PaceNotifier}, 用步幅和步长计算速度。
 * 
 * @author nEdAy
 */
public class SpeedNotifier implements PaceNotifier.Listener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    
    int mCounter = 0;
    float mSpeed = 0;
    
    float mStepLength;

    PedometerSettings mSettings;
    Utils mUtils;
    boolean mShouldTellSpeed;

    
    public SpeedNotifier(Listener listener, PedometerSettings settings, Utils utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }
    public void setSpeed(float speed) {
        mSpeed = speed;
        notifyListener();
    }
    public void reloadSettings() {
        mStepLength = mSettings.getStepLength();
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged(mSpeed);
    }
    
    @Override
	public void paceChanged(int value) {
            mSpeed = // 千米/时
                value * mStepLength // 厘米/分钟
                / 100000f * 60f; // 厘米/公里

        notifyListener();
    }
    
    @Override
	public void passValue() {
        // 没用
    }

    @Override
	public void speak() {
        if (mSettings.shouldTellSpeed()) {
            if (mSpeed >= .01f) {
                  mUtils.say(("" + (mSpeed + 0.000001f)).substring(0, 4) + ("kilometers per hour"));
            }
        }
        
    }

}


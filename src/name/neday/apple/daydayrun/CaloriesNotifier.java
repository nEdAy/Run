/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;


/**
 * 计算 并显示卡路里的大致消耗.  
 * @author nEdAy
 */
public class CaloriesNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    
    private static double METRIC_RUNNING_FACTOR = 1.02784823;

    private static double METRIC_WALKING_FACTOR = 0.708;

    private float mCalories = 0;
    
    PedometerSettings mSettings;
    Utils mUtils;
    
    boolean mIsRunning;
    float mStepLength;
    float mBodyWeight;

    public CaloriesNotifier(Listener listener, PedometerSettings settings, Utils utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }
    public void setCalories(float calories) {
        mCalories = calories;
        notifyListener();
    }
    public void reloadSettings() {
        mIsRunning = mSettings.isRunning();
        mStepLength = mSettings.getStepLength();
        mBodyWeight = mSettings.getBodyWeight();
        notifyListener();
    }
    public void resetValues() {
        mCalories = 0;
    }
    
    public void setStepLength(float stepLength) {
        mStepLength = stepLength;
    }
    
    @Override
	public void onStep() {
            mCalories += (float)
                ((mBodyWeight * (mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                // Distance:
                * mStepLength // centimeters
                / 100000f); // centimeters/kilometer
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged(mCalories);
    }
    
    @Override
	public void passValue() {
    }
    
    @Override
	public void speak() {
        if (mSettings.shouldTellCalories()) {
            if (mCalories > 0) {
                mUtils.say("" + (int)mCalories + " calories burned");
            }
        }
        
    }
}


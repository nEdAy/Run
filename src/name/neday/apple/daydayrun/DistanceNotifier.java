/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;


/**
 * 计算并显示行走的路程距离.  
 * @author nEdAy
 */
public class DistanceNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    
    float mDistance = 0;
    
    PedometerSettings mSettings;
    Utils mUtils;
    
    float mStepLength;

    public DistanceNotifier(Listener listener, PedometerSettings settings, Utils utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }
    public void setDistance(float distance) {
        mDistance = distance;
        notifyListener();
    }
    
    public void reloadSettings() {
        mStepLength = mSettings.getStepLength();
        notifyListener();
    }
    
    @Override
	public void onStep() {
            mDistance += mStepLength // centimeters
			/ 100000f; // centimeters/kilometer
        notifyListener();
        }
    
    private void notifyListener() {
        mListener.valueChanged(mDistance);
    }
    
    @Override
	public void passValue() {
        // Callback of StepListener - Not implemented
    }

    @Override
	public void speak() {
        if (mSettings.shouldTellDistance()) {
            if (mDistance >= .001f) {
            	  mUtils.say(("" + (mDistance + 0.000001f)).substring(0, 4) + (" kilometers"));
                // TODO: format numbers (no "." at the end)
            }
        }
    }
    

}


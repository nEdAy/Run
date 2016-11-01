/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 这是实现本地运行在同一进程中的应用程序服务的一个例子。
 * {@link StepServiceController}和{@link StepServiceBinding} 类 展示了如何 实现服务的互动.
 * <p>注意使用 {@link NotificationManager} 当服务中发生有趣的事情。
 * 这通常是 后台服务应该和用户互动,而不是做一些破坏性的事情如，调用 startActivity().
 */

public class StepService extends Service {
	private static final String TAG = "name.neday.apple.burningapple.StepService";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private Utils mUtils;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    private StepDisplayer mStepDisplayer;
    private PaceNotifier mPaceNotifier;
    private DistanceNotifier mDistanceNotifier;
    private SpeedNotifier mSpeedNotifier;
    private CaloriesNotifier mCaloriesNotifier;
    private SpeakingTimer mSpeakingTimer;
    
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNM;

    private int mSteps;
    private int mPace;
    private float mDistance;
    private float mSpeed;
    private float mCalories;
    
    /**
     * 为了客户端访问的类.  
     * 因为我们知道这个服务作为其客户一直运行在同一进程中，我们不需要处理的进程间的通信。
     */
    public class StepBinder extends Binder {
        StepService getService() {
            return StepService.this;
        }
    }
    
    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();
        
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        // 载入设置
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mState = getSharedPreferences("state", 0);

        mUtils = Utils.getInstance();
        mUtils.setService(this);
        mUtils.initTTS();

        acquireWakeLock();
        
        // 开始检测
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        //注册我们的屏幕待机活动的接收器。
        //这将使手机进入待机模式时，我们的接收器的代码会被调用。
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        mStepDisplayer = new StepDisplayer(mPedometerSettings, mUtils);
        mStepDisplayer.setSteps(mSteps = mState.getInt("steps", 0));
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);

        mPaceNotifier = new PaceNotifier(mPedometerSettings, mUtils);
        mPaceNotifier.setPace(mPace = mState.getInt("pace", 0));
        mPaceNotifier.addListener(mPaceListener);
        mStepDetector.addStepListener(mPaceNotifier);

        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings, mUtils);
        mDistanceNotifier.setDistance(mDistance = mState.getFloat("distance", 0));
        mStepDetector.addStepListener(mDistanceNotifier);
        
        mSpeedNotifier = new SpeedNotifier(mSpeedListener,mPedometerSettings, mUtils);
        mSpeedNotifier.setSpeed(mSpeed = mState.getFloat("speed", 0));
        mPaceNotifier.addListener(mSpeedNotifier);
        
        mCaloriesNotifier = new CaloriesNotifier(mCaloriesListener, mPedometerSettings, mUtils);
        mCaloriesNotifier.setCalories(mCalories = mState.getFloat("calories", 0));
        mStepDetector.addStepListener(mCaloriesNotifier);
        
        mSpeakingTimer = new SpeakingTimer(mPedometerSettings, mUtils);
        mSpeakingTimer.addListener(mStepDisplayer);
        mSpeakingTimer.addListener(mPaceNotifier);
        mSpeakingTimer.addListener(mDistanceNotifier);
        mSpeakingTimer.addListener(mSpeedNotifier);
        mSpeakingTimer.addListener(mCaloriesNotifier);
        mStepDetector.addStepListener(mSpeakingTimer);

        // 开启语音
        reloadSettings();

        // 通知用户程式已启动.
        Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");
        mUtils.shutdownTTS();

        // 注销我们的接收器.
        unregisterReceiver(mReceiver);
        unregisterDetector();
        
        mStateEditor = mState.edit();
        mStateEditor.putInt("steps", mSteps);
        mStateEditor.putInt("pace", mPace);
        mStateEditor.putFloat("distance", mDistance);
        mStateEditor.putFloat("speed", mSpeed);
        mStateEditor.putFloat("calories", mCalories);
        mStateEditor.commit();
        
        mNM.cancel(R.string.app_name);

        wakeLock.release();
        
        super.onDestroy();
        
        // 停止检测
        mSensorManager.unregisterListener(mStepDetector);

        // 通知用户程式已停止.
        Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT).show();
    }

    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER /*| 
            Sensor.TYPE_MAGNETIC_FIELD | 
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
            mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    /**
     * 接受来自活动的消息
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value);
        public void paceChanged(int value);
        public void distanceChanged(float value);
        public void speedChanged(float value);
        public void caloriesChanged(float value);
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }
    
    public void reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (mStepDetector != null) { 
            mStepDetector.setSensitivity(
                    Float.valueOf(mSettings.getString("sensitivity", "10"))
            );
        }
        
        if (mStepDisplayer    != null) mStepDisplayer.reloadSettings();
        if (mPaceNotifier     != null) mPaceNotifier.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
        if (mSpeedNotifier    != null) mSpeedNotifier.reloadSettings();
        if (mCaloriesNotifier != null) mCaloriesNotifier.reloadSettings();
        if (mSpeakingTimer    != null) mSpeakingTimer.reloadSettings();
    }
    
    public void resetValues() {
        mStepDisplayer.setSteps(0);
        mPaceNotifier.setPace(0);
        mDistanceNotifier.setDistance(0);
        mSpeedNotifier.setSpeed(0);
        mCaloriesNotifier.setCalories(0);
    }
    
    /**
     * 发送步数的数值 从步数通知器到活动中. 
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        @Override
		public void stepsChanged(int value) {
            mSteps = value;
            passValue();
        }
        @Override
		public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
            }
        }
    };
    /**
     * 发送步幅的数值 步幅通知器到活动中. 
     */
    private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
        @Override
		public void paceChanged(int value) {
            mPace = value;
            passValue();
        }
        @Override
		public void passValue() {
            if (mCallback != null) {
                mCallback.paceChanged(mPace);
            }
        }
    };
    /**
     * 发送距离的数值 从距离通知器到活动中. 
     */
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        @Override
		public void valueChanged(float value) {
            mDistance = value;
            passValue();
        }
        @Override
		public void passValue() {
            if (mCallback != null) {
                mCallback.distanceChanged(mDistance);
            }
        }
    };
    /**
     * 发送速度的数值 从速度通知器到活动中. 
     */
    private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        @Override
		public void valueChanged(float value) {
            mSpeed = value;
            passValue();
        }
        @Override
		public void passValue() {
            if (mCallback != null) {
                mCallback.speedChanged(mSpeed);
            }
        }
    };
    /**
     * 发送燃烧卡路里的数值 从卡路里通知器到活动中. 
     */
    private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        @Override
		public void valueChanged(float value) {
            mCalories = value;
            passValue();
        }
        @Override
		public void passValue() {
            if (mCallback != null) {
                mCallback.caloriesChanged(mCalories);
            }
        }
    };
    
    /**
     * 当服务正在运行时，显示一个通知。
     */
    @SuppressWarnings("deprecation")
	private void showNotification() {
        CharSequence text = getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_notification, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        Intent pedometerIntent = new Intent();
        pedometerIntent.setComponent(new ComponentName(this, Pedometer.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0);
        notification.setLatestEventInfo(this, text,
                getText(R.string.notification_subtitle), contentIntent);

        mNM.notify(R.string.app_name, notification);
    }


    // 处理屏幕休眠的广播接收器.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 检查活动，以防万一
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // 再次注销监听器和寄存器
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
                if (mPedometerSettings.wakeAggressively()) {
                    wakeLock.release();
                    acquireWakeLock();
                }
            }
        }
    };

    @SuppressWarnings("deprecation")
	private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        if (mPedometerSettings.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        }
        else if (mPedometerSettings.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }
        else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }

}


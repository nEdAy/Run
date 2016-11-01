package name.neday.apple.daydayrun;

import java.util.Locale;

import android.app.Service;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class Utils implements TextToSpeech.OnInitListener {
    private static final String TAG = "Utils";
    private Service mService;

    private static Utils instance = null;

    private Utils() {
    }
     
    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }
    
    public void setService(Service service) {
        mService = service;
    }
    
    /********** 语音 **********/
    
    private TextToSpeech mTts;
    private boolean mSpeak = false;
    private boolean mSpeakingEngineAvailable = false;

    public void initTTS() {
        // 初始化文本到语音。这是一个异步操作。
        // OnInitListener (第二个参数) 在初始化完成后被调用.
        Log.i(TAG, "Initializing TextToSpeech...");
        mTts = new TextToSpeech(mService,
            this  // TextToSpeech.OnInitListener
            );
    }
    public void shutdownTTS() {
        Log.i(TAG, "Shutting Down TextToSpeech...");

        mSpeakingEngineAvailable = false;
        mTts.shutdown();
        Log.i(TAG, "TextToSpeech Shut Down.");

    }
    public void say(String text) {
        if (mSpeak && mSpeakingEngineAvailable) {
            mTts.speak(text,
                    TextToSpeech.QUEUE_ADD,  // 删除所有悬而未决的条目中的播放队列。
                    null);
        }
    }

    // 实现  TextToSpeech.OnInitListener.
    @Override
	public void onInit(int status) {
        // 状态可以是  TextToSpeech.SUCCESS 或  TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               //语言数据丢失或不支持该语言。
                Log.e(TAG, "Language is not available.");
            } else {
                Log.i(TAG, "TextToSpeech Initialized.");
                mSpeakingEngineAvailable = true;
            }
        } else {
            // 初始化失败
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    public void setSpeak(boolean speak) {
        mSpeak = speak;
    }

    public boolean isSpeakingEnabled() {
        return mSpeak;
    }

    public boolean isSpeakingNow() {
        return mTts.isSpeaking();
    }

    public void ding() {
    }
}

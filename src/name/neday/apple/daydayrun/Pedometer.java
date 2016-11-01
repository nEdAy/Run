/*
 *  天理跑团 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;

import java.util.Date;
import java.util.List;

import com.lenovo.lps.sus.SUS;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.message.PushAgent;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import name.neday.apple.daydayrun.R;
import name.neday.apple.daydayrun.bean.User;
import name.neday.apple.daydayrun.location.LocationSourceActivity;
import name.neday.apple.daydayrun.semicircularmenu.SemiCircularRadialMenu;
import name.neday.apple.daydayrun.semicircularmenu.SemiCircularRadialMenuItem;
import name.neday.apple.daydayrun.semicircularmenu.SemiCircularRadialMenuItem.OnSemiCircularRadialMenuPressed;
import name.neday.apple.daydayrun.ui.AboutActivity;
import name.neday.apple.daydayrun.ui.AddFriendActivity;
import name.neday.apple.daydayrun.ui.CloudActivity;
import name.neday.apple.daydayrun.ui.FlashlightActivity;
import name.neday.apple.daydayrun.ui.LockActivity;
import name.neday.apple.daydayrun.ui.LoginActivity;
import name.neday.apple.daydayrun.ui.MainActivity;
import name.neday.apple.daydayrun.ui.NearPeopleActivity;
import name.neday.apple.daydayrun.ui.SetMyInfoActivity;
import name.neday.apple.daydayrun.util.ImageLoadOptions;
import name.neday.apple.daydayrun.view.DragLayoutView;
import name.neday.apple.daydayrun.view.DragLayoutView.DragListener;

public class Pedometer extends Activity implements OnClickListener, Runnable {
	protected static final int CAMERA = 0;
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private Utils mUtils;
	User user;
	private DragLayoutView dl;
	private TextView mStepValueView, mPaceValueView, mDistanceValueView,
			mSpeedValueView, mCaloriesValueView;
	TextView mDesiredPaceView;
	private int mStepValue, mPaceValue;
	private float mDistanceValue, mSpeedValue, mCaloriesValue;
	// 侧边栏
	TextView CheckForUpdates, btn_logout, about;
	ImageView iv_set_avator;
	TextView UserName, NearPeople, AddPeople, MyLocationNow, daydaylist;
	// 定义标题栏上左侧的按钮
	private ImageView menuImg;
	// 定义标题栏上右侧的按钮
	private ImageView titleBtn;
	// True, 当服务正在运行时.
	private boolean mIsRunning;
	// 当前所经过的时间
	private long time = 0;
	// 开始时间
	private long startTime;
	private Handler handler;
	// 用于显示时间
	private TextView timeView;
	// 开始按钮 //暂停按钮 //重置按钮 //Chat按钮
	private TextView startButton, pauseButton, resetButton, chatButton,
			markButton;
	// 秒表的当前状态，分为正在运行、暂停、停止三种状态
	private int state = 0;
	private static int STATE_RUNNING = 1;
	private static int STATE_STOP = 0;
	private static int STATE_PAUSE = 2;
	private static int STATE_LOCK = 3;
	String Avatar;

	private SoundPool sp;// 声明一个SoundPool
	private int music;// 定义一个整型用load（）；来设置suondID

	public String username;

	private SemiCircularRadialMenu mMenu;
	private SemiCircularRadialMenuItem mCamera, mDislike, mInfo, mRefresh,
			mSearch;

	public static final int RINGER_MODE_SILENT = 0;
	public static final int RINGER_MODE_VIBRATE = 1;
	public static final int RINGER_MODE_NORMAL = 2;
	Boolean flag = true;

	/** 当活动被创建时调用. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStepValue = 0;
		mPaceValue = 0;
		setContentView(R.layout.main);
		mUtils = Utils.getInstance();

		// 开启IM定时检测服务
		BmobChat.getInstance(this).startPollService(30);

		// 友盟推送注册
		PushAgent.getInstance(getBaseContext()).onAppStart();
		PushAgent mPushAgent = PushAgent.getInstance(getBaseContext());
		mPushAgent.enable();

		// 用户名
		UserName = (TextView) findViewById(R.id.UserName);
		menuImg = (ImageView) findViewById(R.id.title_bar_menu_btn);
		titleBtn = (ImageView) findViewById(R.id.title_btn);
		// 侧边栏
		AddPeople = (TextView) findViewById(R.id.AddPeople);
		NearPeople = (TextView) findViewById(R.id.NearPeople);
		MyLocationNow = (TextView) findViewById(R.id.MyLocationNow);
		CheckForUpdates = (TextView) findViewById(R.id.CheckForUpdates);
		btn_logout = (TextView) findViewById(R.id.btn_logout);
		about = (TextView) findViewById(R.id.about);
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		daydaylist = (TextView) findViewById(R.id.daydaylist);

		menuImg.setOnClickListener(this);
		titleBtn.setOnClickListener(this);
		NearPeople.setOnClickListener(this);
		AddPeople.setOnClickListener(this);
		MyLocationNow.setOnClickListener(this);
		CheckForUpdates.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		iv_set_avator.setOnClickListener(this);
		about.setOnClickListener(this);
		daydaylist.setOnClickListener(this);

		initDragLayout();
		query();

		// 计时器读取环境信息（偏好 ）
		readEnvironment();

		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
		music = sp.load(this, R.raw.notify, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级

		// 开始按钮
		startButton = (TextView) findViewById(R.id.start);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onStartClick();
			}
		});

		// 暂停按钮
		pauseButton = (TextView) findViewById(R.id.pause);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onPauseClick();
			}
		});
		// 重置按钮
		resetButton = (TextView) findViewById(R.id.reset);
		resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onResetClick();
			}
		});
		// 分记按钮
		markButton = (TextView) findViewById(R.id.mark);
		markButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				vibrator();
				sp.play(music, 1, 1, 0, 0, 1);
			}
		});
		// Chat按钮
		chatButton = (TextView) findViewById(R.id.chat);
		chatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(Pedometer.this, MainActivity.class));
			}
		});

		timeView = (TextView) findViewById(R.id.timeView);
		// 创建 handler
		handler = new Handler();

		// 设置各按钮
		setButtons();

		// 设置时间显示
		if (state == STATE_STOP) {
			timeView.setText("Let's Run!");
		} else if (state == STATE_PAUSE) {
			timeView.setText(getFormatTime(time));
		} else if (state == STATE_LOCK) {
			// timeView.setText("锁屏中");
		}

		mCamera = new SemiCircularRadialMenuItem("camera", getResources()
				.getDrawable(R.drawable.ic_action_camera), "Camera");
		mDislike = new SemiCircularRadialMenuItem("dislike", getResources()
				.getDrawable(R.drawable.ic_action_dislike), "Dislike");
		mInfo = new SemiCircularRadialMenuItem("info", getResources()
				.getDrawable(R.drawable.ic_action_info), "Info");
		mRefresh = new SemiCircularRadialMenuItem("refresh", getResources()
				.getDrawable(R.drawable.ic_action_refresh), "Refresh");
		mSearch = new SemiCircularRadialMenuItem("search", getResources()
				.getDrawable(R.drawable.ic_action_search), "Search");

		mMenu = (SemiCircularRadialMenu) findViewById(R.id.radial_menu);
		mMenu.addMenuItem(mCamera.getMenuID(), mCamera);
		mMenu.addMenuItem(mDislike.getMenuID(), mDislike);
		mMenu.addMenuItem(mInfo.getMenuID(), mInfo);
		mMenu.addMenuItem(mRefresh.getMenuID(), mRefresh);
		mMenu.addMenuItem(mSearch.getMenuID(), mSearch);

		mCamera.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onMenuItemPressed() {
				try {
					Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(camera, CAMERA);
				} catch (Exception e) {
					ShowToast("权限被拦截！");
				}
				mMenu.dismissMenu();
			}
		});

		mDislike.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				try {
					startActivity(new Intent(Pedometer.this,
							FlashlightActivity.class));
				} catch (Exception e) {
					ShowToast("权限被拦截！");
				}
				mMenu.dismissMenu();
			}
		});

		mInfo.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				startActivity(new Intent(Pedometer.this, LockActivity.class));
				state = STATE_LOCK;
				mMenu.dismissMenu();
			}
		});

		mRefresh.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@Override
			public void onMenuItemPressed() {
				if (mStepValue <= 100) {
					ShowToast("失败，100步再来签到吧！");
				} else {
					ShowToast("今日签到成功！");
				}
				mMenu.dismissMenu();
			}
		});

		mSearch.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onMenuItemPressed() {
				vibrator();
				AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				if (flag == true) {
					audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0); // mute
																			// music
																			// stream
					audio.setStreamVolume(AudioManager.STREAM_RING, 0, 0); // mute
																			// ring
																			// stream
					if (Build.VERSION.SDK_INT >= 8) {
						audio.requestAudioFocus(null,
								AudioManager.STREAM_MUSIC,
								AudioManager.AUDIOFOCUS_GAIN);
						audio.requestAudioFocus(null, AudioManager.STREAM_RING,
								AudioManager.AUDIOFOCUS_GAIN);
					}
					flag = false;
					ShowToast("进入静音状态");
				} else {
					audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
							AudioManager.VIBRATE_SETTING_ON);
					audio.setVibrateSetting(
							AudioManager.VIBRATE_TYPE_NOTIFICATION,
							AudioManager.VIBRATE_SETTING_ON);
					flag = true;
					ShowToast("退出静音状态");
				}

				mMenu.dismissMenu();
			}
		});
	}

	private void initDragLayout() {
		dl = (DragLayoutView) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {

			}

			@Override
			public void onClose() {
				shake();
			}

			@Override
			public void onDrag(float percent) {
				ViewHelper.setAlpha(menuImg, 1 - percent);
			}
		});
	}

	private void shake() {
		menuImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
	}

	/**
	 * 根据状态设置按钮显示于不显示
	 */
	private void setButtons() {
		switch (state) {
		case 1:// 如果正在运行
			startButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.VISIBLE);
			chatButton.setVisibility(View.VISIBLE);
			markButton.setVisibility(View.VISIBLE);
			resetButton.setVisibility(View.GONE);
			resetButton.setEnabled(false);
			break;
		case 2:// 如果暂停中
			startButton.setVisibility(View.VISIBLE);
			startButton.setText(R.string.resume);
			pauseButton.setVisibility(View.GONE);
			chatButton.setVisibility(View.VISIBLE);
			markButton.setVisibility(View.GONE);
			resetButton.setVisibility(View.VISIBLE);
			resetButton.setEnabled(true);
			break;
		case 0:// 如果停止中
			startButton.setVisibility(View.VISIBLE);
			pauseButton.setVisibility(View.GONE);
			markButton.setVisibility(View.GONE);
			chatButton.setVisibility(View.VISIBLE);
			resetButton.setVisibility(View.VISIBLE);
			resetButton.setEnabled(false);
			break;
		default:
			break;
		}
	}

	/**
	 * 读取环境
	 */
	private void readEnvironment() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"environment", MODE_PRIVATE);
		state = sharedPreferences.getInt("state", STATE_STOP);
		startTime = sharedPreferences.getLong("startTime", 0);
		time = sharedPreferences.getLong("time", 0);
	}

	/**
	 * 保存环境
	 */
	private void saveEnvironment() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"environment", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("state", state);
		editor.putLong("time", time);
		editor.putLong("startTime", startTime);
		editor.commit();
	}

	private static long firstTime;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_bar_menu_btn:
			dl.open();
			break;
		case R.id.title_btn:
			startActivity(new Intent(Pedometer.this, DialogActivity.class));
			break;
		case R.id.iv_set_avator:
			query();
			Intent intent = new Intent(Pedometer.this, SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startActivity(intent);
			break;
		case R.id.NearPeople:
			startActivity(new Intent(Pedometer.this, NearPeopleActivity.class));
			break;
		case R.id.AddPeople:
			startActivity(new Intent(Pedometer.this, AddFriendActivity.class));
			break;
		case R.id.MyLocationNow:
			startActivity(new Intent(Pedometer.this,
					LocationSourceActivity.class));
			break;
		case R.id.CheckForUpdates:
			if (!SUS.isVersionUpdateStarted()) {
				SUS.AsyncStartVersionUpdate(this);
				ShowToast("更新到最新版本");
			}
			break;
		case R.id.about:
			startActivity(new Intent(Pedometer.this, AboutActivity.class));
			break;
		case R.id.daydaylist:
			Intent intentb = new Intent();
			intentb.setClass(Pedometer.this, CloudActivity.class);
			intentb.putExtra("name", BmobUserManager.getInstance(null)
					.getCurrentUserName());
			startActivity(intentb);
			break;
		case R.id.btn_logout:
			if (firstTime + 2000 > System.currentTimeMillis()) {
				CustomApplcation.getInstance().logout();
				// 重置计步器
				resetValues(true);
				if (mIsRunning) {
					stopStepService();
					unbindStepService();
				}
				// 不再刷新
				if (state == STATE_RUNNING) {// 如果正在运行
					handler.removeCallbacks(this);
				}
				// 设置状态为暂停
				state = STATE_PAUSE;
				// 设置各按钮
				setButtons();
				// 设置时间显示
				time = 0;
				timeView.setText(getFormatTime(time));
				saveEnvironment();
				// 设置各按钮
				setButtons();
				finish();
				startActivity(new Intent(Pedometer.this, LoginActivity.class));
				break;
			} else {
				ShowToast("再按一次,注销帐号");
			}
			firstTime = System.currentTimeMillis();
		}
	}

	/**
	 * 连续按两次返回键就后台运行程序
	 */
	@Override
	@SuppressLint("NewApi")
	public void onBackPressed() {
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("再按一次,后台运行程序");
		}
		firstTime = System.currentTimeMillis();
	}

	Toast mToast;

	@SuppressLint("NewApi")
	private void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {
				@Override
				@SuppressLint("NewApi")
				public void run() {
					// TODO Auto-generated method stub
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);
		mUtils.setSpeak(mSettings.getBoolean("speak", false));
		// 阅读偏好信息， 如果服务在上一次暂停后正在运行
		mIsRunning = mPedometerSettings.isServiceRunning();
		mPedometerSettings.clearServiceRunning();
		mStepValueView = (TextView) findViewById(R.id.step_value);
		mPaceValueView = (TextView) findViewById(R.id.pace_value);
		mDistanceValueView = (TextView) findViewById(R.id.distance_value);
		mSpeedValueView = (TextView) findViewById(R.id.speed_value);
		mCaloriesValueView = (TextView) findViewById(R.id.calories_value);

		((TextView) findViewById(R.id.distance_units))
				.setText(getString(R.string.kilometers));
		((TextView) findViewById(R.id.speed_units))
				.setText(getString(R.string.kilometers_per_hour));
		// 计时器
		if (state == STATE_RUNNING) {// 如果正在运行
			handler.post(this);
			bindStepService();
		}
	}

	@Override
	protected void onPause() {
		if (mIsRunning) {
			unbindStepService();
		}
		mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
		super.onPause();
		// 计时器
		if (state == STATE_RUNNING) {// 如果正在运行
			handler.removeCallbacks(this);
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// 计时器，保存环境
		saveEnvironment();
		super.onDestroy();
		// 取消IM检测服务
		BmobChat.getInstance(this).stopPollService();
	}

	@Override
	protected void onRestart() {
		super.onDestroy();
	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();
			mService.registerCallback(mCallback);
			mService.reloadSettings();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			mIsRunning = true;
			startService(new Intent(Pedometer.this, StepService.class));
		}
	}

	private void bindStepService() {
		bindService(new Intent(Pedometer.this, StepService.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() {
		unbindService(mConnection);
	}

	private void stopStepService() {
		if (mService != null) {
			stopService(new Intent(Pedometer.this, StepService.class));
		}
		mIsRunning = false;
	}

	private void resetValues(boolean updateDisplay) {
		if (mService != null && mIsRunning) {
			mService.resetValues();
		} else {
			mStepValueView.setText("0");
			mPaceValueView.setText("0");
			mDistanceValueView.setText("0");
			mSpeedValueView.setText("0");
			mCaloriesValueView.setText("0");
			SharedPreferences state = getSharedPreferences("state", 0);
			SharedPreferences.Editor stateEditor = state.edit();
			if (updateDisplay) {
				stateEditor.putInt("steps", 0);
				stateEditor.putInt("pace", 0);
				stateEditor.putFloat("distance", 0);
				stateEditor.putFloat("speed", 0);
				stateEditor.putFloat("calories", 0);
				stateEditor.commit();
			}
		}
	}

	private static final int MENU_SETTINGS = 8;
	private static final int MENU_QUIT = 9;

	private static final int MENU_PAUSE = 1;
	private static final int MENU_RESUME = 2;
	private static final int MENU_RESET = 3;

	/* 创建菜单项 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		if (mIsRunning) {
			menu.add(0, MENU_PAUSE, 0, R.string.pause)
					.setIcon(android.R.drawable.ic_media_pause)
					.setShortcut('1', 'p');
		} else {
			menu.add(0, MENU_RESUME, 0, R.string.resume)
					.setIcon(android.R.drawable.ic_media_play)
					.setShortcut('1', 'p');
		}
		menu.add(0, MENU_RESET, 0, R.string.reset)
				.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
				.setShortcut('2', 'r');
		menu.add(0, MENU_SETTINGS, 0, R.string.settings)
				.setIcon(android.R.drawable.ic_menu_preferences)
				.setShortcut('8', 's')
				.setIntent(new Intent(this, Settings.class));
		menu.add(0, MENU_QUIT, 0, R.string.quit)
				.setIcon(android.R.drawable.ic_lock_power_off)
				.setShortcut('9', 'q');
		return true;
	}

	public void quit() {
		if (mIsRunning) {
			unbindStepService();
			stopStepService();
		}
		resetValues(false);
		// 不再刷新
		if (state == STATE_RUNNING) {// 如果正在运行
			handler.removeCallbacks(this);
		}
		// 设置状态为停止
		state = STATE_STOP;
		// 设置时间显示
		time = 0;
		timeView.setText(getFormatTime(time));
		saveEnvironment();
		// 设置各按钮
		setButtons();
		finish();
	}

	/* 处理项目的选择 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PAUSE:
			onPauseClick();
			return true;
		case MENU_RESUME:
			onStartClick();
			return true;
		case MENU_RESET:
			onResetClick();
			return true;
		case MENU_QUIT:
			quit();
			return true;
		}
		return false;
	}

	// TODO: 合并所有到一个消息类型
	private StepService.ICallback mCallback = new StepService.ICallback() {
		@Override
		public void stepsChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
		}

		@Override
		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}

		@Override
		public void distanceChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
					(int) (value * 1000), 0));
		}

		@Override
		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
					(int) (value * 1000), 0));
		}

		@Override
		public void caloriesChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
					(int) (value * 1000), 0));
		}
	};

	private static final int STEPS_MSG = 1;
	private static final int PACE_MSG = 2;
	private static final int DISTANCE_MSG = 3;
	private static final int SPEED_MSG = 4;
	private static final int CALORIES_MSG = 5;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEPS_MSG:
				mStepValue = msg.arg1;
				mStepValueView.setText("" + mStepValue);
				break;
			case PACE_MSG:
				mPaceValue = msg.arg1;
				if (mPaceValue <= 0) {
					mPaceValueView.setText("0");
				} else {
					mPaceValueView.setText("" + mPaceValue);
				}
				break;
			case DISTANCE_MSG:
				mDistanceValue = (msg.arg1) / 1000f;
				if (mDistanceValue <= 0) {
					mDistanceValueView.setText("0");
				} else {
					mDistanceValueView
							.setText(("" + (mDistanceValue + 0.000001f))
									.substring(0, 5));
				}
				break;
			case SPEED_MSG:
				mSpeedValue = (msg.arg1) / 1000f;
				if (mSpeedValue <= 0) {
					mSpeedValueView.setText("0");
				} else {
					mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f))
							.substring(0, 4));
				}
				break;
			case CALORIES_MSG:
				mCaloriesValue = (msg.arg1) / 1000f;
				if (mCaloriesValue <= 0) {
					mCaloriesValueView.setText("0");
				} else {
					mCaloriesValueView.setText("" + mCaloriesValue);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

	/**
	 * 按钮震动
	 */
	@SuppressLint("DefaultLocale")
	protected void vibrator() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[] { 0, 150 }, -1);
	}

	/**
	 * 点击重置
	 */
	@SuppressLint("DefaultLocale")
	protected void onResetClick() {
		if (mIsRunning) {
			stopStepService();
			unbindStepService();
		}
		// 不再刷新
		if (state == STATE_RUNNING) {// 如果正在运行
			handler.removeCallbacks(this);
		}
		// 设置状态为暂停
		state = STATE_STOP;
		setButtons();
		SharedPreferences total = getSharedPreferences("total", 0);
		Long total_time = total.getLong("Time", 0);
		int Steps = total.getInt("Steps", 0);
		float Distance = total.getFloat("Distance", 0);
		float Calories = total.getFloat("Calories", 0);
		total_time = total_time + time;
		Steps = Steps + mStepValue;
		Distance = Distance + mDistanceValue;
		Calories = Calories + mCaloriesValue;
		SharedPreferences.Editor editor = total.edit();
		editor.putLong("Time", total_time);
		editor.putInt("Steps", Steps);
		editor.putFloat("Distance", Distance);
		editor.putFloat("Calories", Calories);
		editor.commit();
		resetValues(true);
		// 设置时间显示
		time = 0;
		timeView.setText(getFormatTime(time));
		vibrator();
		sp.play(music, 1, 1, 0, 0, 1);
	}

	/**
	 * 点击暂停
	 */
	protected void onPauseClick() {
		if (mIsRunning) {
			stopStepService();
			unbindStepService();
		}
		// 不再刷新
		if (state == STATE_RUNNING) {// 如果正在运行
			handler.removeCallbacks(this);
		}
		// 设置状态为暂停
		state = STATE_PAUSE;
		// 设置各按钮
		setButtons();
		vibrator();
		sp.play(music, 1, 1, 0, 0, 1);
	}

	/**
	 * 点击开始
	 */
	protected void onStartClick() {
		if (!mIsRunning) {
			startStepService();
			bindStepService();
		}
		startTime = new Date().getTime() - time;
		handler.post(this);
		// 设置状态为正在运行
		state = STATE_RUNNING;
		// 设置各按钮
		setButtons();
		vibrator();
		sp.play(music, 1, 1, 0, 0, 1);
	}

	@Override
	public void run() {
		handler.postDelayed(this, 50);
		time = new Date().getTime() - startTime;
		timeView.setText(getFormatTime(time));
	}

	private void query() {
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", BmobUserManager.getInstance(null)
				.getCurrentUserName());
		query.findObjects(this, new FindListener<User>() {
			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				user = arg0.get(0);
				Avatar = user.getAvatar();
				UserName.setText(user.getNick());
				if (Avatar != null && !Avatar.equals("")) {
					ImageLoader.getInstance().displayImage(Avatar,
							iv_set_avator, ImageLoadOptions.getOptions());
				} else {
					iv_set_avator.setImageResource(R.drawable.default_head);
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * 得到一个格式化的时间
	 * 
	 * @return 分：秒：毫秒
	 */
	private String getFormatTime(long time) {
		// long millisecond = time % 1000;
		long second = (time / 1000) % 60;
		long minute = (time / 1000 / 60) % 60;
		long hour = time / 1000 / 60 / 60;
		// 秒以下的只显示一位
		// String strMillisecond = "" + (millisecond / 100);
		// 秒显示两位
		String strSecond = ("00" + second)
				.substring(("00" + second).length() - 2);
		// 分显示两位
		String strMinute = ("00" + minute)
				.substring(("00" + minute).length() - 2);
		// 时显示两位
		String strHour = ("00" + hour).substring(("00" + hour).length() - 2);
		// return strHour + ":" + strMinute + ":" + strSecond + ":" +
		// strMillisecond;
		return strHour + ":" + strMinute + ":" + strSecond;
	}
}
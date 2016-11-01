package name.neday.apple.daydayrun.ui;

import name.neday.apple.daydayrun.CustomApplcation;
import name.neday.apple.daydayrun.Pedometer;
import name.neday.apple.daydayrun.R;
import name.neday.apple.daydayrun.config.Config;
import name.neday.apple.daydayrun.firstuse.WelcomeA;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.bmob.im.BmobChat;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;

/**
 * 引导页
 */
public class SplashActivity extends BaseActivity {

	private static final int GO_HOME = 100;
	private static final int GO_NOUSER = 200;

	// 定位获取当前用户的地理位置
	private LocationClient mLocationClient;

	private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//BmobIM SDK初始化--只需要这一段代码即可完成初始化
		//请到Bmob官网(http://www.bmob.cn/)申请ApplicationId,具体地址:http://docs.bmob.cn/android/faststart/index.html?menukey=fast_start&key=start_android
		BmobChat.getInstance(this).init(Config.applicationId);
		// 开启定位
		initLocClient();
		// 注册地图 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);

		if (userManager.getCurrentUser() != null) {
			// 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
			updateUserInfos();
			mHandler.sendEmptyMessageDelayed(GO_HOME, 800);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_NOUSER, 800);
		}
		
	}

	/**
	 * 开启定位，更新当前用户的经纬度坐标
	 * @Title: initLocClient
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initLocClient() {
		mLocationClient = CustomApplcation.getInstance().mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
		option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
		option.setScanSpan(3600000);//设置发起定位请求的间隔时间为半小时 //低于1000为手动定位一次，大于或等于1000则为定时定位
		option.setIsNeedAddress(true);// 需要包含地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
//		Hight_Accuracy;高精度定位模式下，会同时使用GPS、Wifi和基站定位，返回的是当前条件下精度最好的定位结果
//		Battery_Saving;低功耗定位模式下，仅使用网络定位即Wifi和基站定位，返回的是当前条件下精度最好的网络定位结果
//		Device_Sensors;仅用设备定位模式下，只使用用户的GPS进行定位。这个模式下，由于GPS芯片锁定需要时间，首次定位速度会需要一定的时间
//		gcj02";国测局加密经纬度坐标//"bd09ll";百度加密经纬度坐标//"bd09";百度加密墨卡托坐标
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				startAnimActivity(Pedometer.class);
				finish();
				break;
			case GO_NOUSER:
				startAnimActivity(WelcomeA.class);
				finish();
				break;
			}
		}
	};

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ShowToast("baidu mapsdk key 验证出错!");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ShowToast("当前网络连接不稳定，请检查您的网络设置!");
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

}

package name.neday.apple.daydayrun;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import name.neday.apple.daydayrun.bean.UserItem;
import name.neday.apple.daydayrun.ui.ActivityBase;
import name.neday.apple.daydayrun.util.ScreenListener;
import name.neday.apple.daydayrun.util.ScreenListener.ScreenStateListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 功能描述：弹出Activity界面
 */
public class DialogActivity extends ActivityBase implements OnClickListener {
	private TextView cloudup, clouddown;
	private TextView totaltimes, totalsteps, totaldistance, totalcalories;
	private long Times;
	private int Steps;
	private float Distance;
	private float Calories;
	String _Times,_Steps,_Distance,_Calories;
	UserItem UserItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		cloudup = (TextView) findViewById(R.id.cloudup);
		cloudup.setOnClickListener(this);
		clouddown = (TextView) findViewById(R.id.clouddown);
		clouddown.setOnClickListener(this);

		totaltimes = (TextView) findViewById(R.id.totaltimes);
		totalsteps = (TextView) findViewById(R.id.totalsteps);
		totaldistance = (TextView) findViewById(R.id.totaldistance);
		totalcalories = (TextView) findViewById(R.id.totalcalories);

		SharedPreferences total = getSharedPreferences("total", 0);
		Times = total.getLong("Time", 0);
		Steps = total.getInt("Steps", 0);
		Distance = total.getFloat("Distance", 0);
		Calories = total.getFloat("Calories", 0);

		_Times = String.valueOf(Times);
		_Steps = String.valueOf(Steps);
		_Distance = String.valueOf(Distance);
		_Calories = String.valueOf(Calories);
		totaltimes.setText("时间累计" + _Times + "秒");
		totalsteps.setText("计步累计" + _Steps + "步");
		totaldistance.setText("路程累计" + _Distance + "千米");
		totalcalories.setText("热量累计" + _Calories + "卡路里");
		if (Steps != 0) {
			cloudup();// 弹出即上传数据
		}

		ScreenListener l = new ScreenListener(this);
		l.begin(new ScreenStateListener() {

			@Override
			public void onUserPresent() {
			}

			@Override
			public void onScreenOn() {
			}

			@Override
			public void onScreenOff() {
				ScreenListener.unregisterListener();
				finish();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void cloudup() {
		BmobQuery<UserItem> query = new BmobQuery<UserItem>();
		query.addWhereEqualTo("username", BmobUserManager.getInstance(null)
				.getCurrentUserName());
		query.findObjects(this, new FindListener<UserItem>() {
			@Override
			public void onSuccess(List<UserItem> object) {
				// TODO Auto-generated method stub
				if(object.size()==0){
					cloudup0();
				}else{
					cloudup1();
				}
				
			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("查询失败：" + msg);
			}
		});
	}
	public void cloudup0() {
		UserItem UserItem = new UserItem();
		UserItem.setUsername(BmobUserManager.getInstance(null)
				.getCurrentUserName());
		UserItem.setTimes(_Times);
		UserItem.setSteps(_Steps);
		UserItem.setDistance(_Distance);
		UserItem.setCalories(_Calories);
		UserItem.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("上传用户累计数据信息成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("由于网路问题，上传用户累计数据信息失败");
			}
		});
	}
	public void cloudup1() {
		JSONObject jso = new JSONObject();
		try {
			jso.put("username",BmobUserManager.getInstance(null)
					.getCurrentUserName());
			jso.put("Times", _Times);
			jso.put("Steps",_Steps);
			jso.put("Distance",_Distance);
			jso.put("Calories", _Calories);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
		//第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
		ace.callEndpoint(getBaseContext(),"UpdateItem",jso, 
		    new CloudCodeListener() {
		            @Override
		            public void onSuccess(Object object) {
		                // TODO Auto-generated method stub
						ShowToast("上传用户累计数据信息成功");
		            }
		            @Override
		            public void onFailure(int code, String msg) {
		                // TODO Auto-generated method stub
						ShowToast("由于网路问题，上传用户累计数据信息失败");
		            }
		        });
	}

	public void clouddown() {
		BmobQuery<UserItem> query = new BmobQuery<UserItem>();
		query.addWhereEqualTo("username", BmobUserManager.getInstance(null)
				.getCurrentUserName());
		query.findObjects(this, new FindListener<UserItem>() {
			@Override
			public void onSuccess(List<UserItem> arg0) {
				// TODO Auto-generated method stub
				ShowToast("下载用户累计数据信息成功");
				UserItem = arg0.get(0);
				_Times = UserItem.getTimes();
				_Steps = UserItem.getSteps();
				_Distance = UserItem.getDistance();
				_Calories = UserItem.getCalories();
				Times=Long.parseLong(_Times);
				Steps=Integer.parseInt(_Steps);
				Distance=Float.parseFloat(_Distance);
				Calories=Float.parseFloat(_Calories);
				SharedPreferences total = getSharedPreferences("total", 0);
				SharedPreferences.Editor editor = total.edit();
				editor.putLong("Time", Times);
				editor.putInt("Steps", Steps);
				editor.putFloat("Distance", Distance);
				editor.putFloat("Calories", Calories);
				editor.commit();
				totaltimes.setText("时间累计" + _Times + "秒");
				totalsteps.setText("计步累计" + _Steps + "步");
				totaldistance.setText("路程累计" + _Distance + "千米");
				totalcalories.setText("热量累计" + _Calories + "卡路里");

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("由于网路问题，下载用户累计数据信息失败");
				// ShowToast("下载用户累计数据信息失败："+arg1);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cloudup:
			if (Steps != 0) {
				cloudup();// 上传数据
			}
			break;
		case R.id.clouddown:
			clouddown();
			break;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onDestroy();
	}
}

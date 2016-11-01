package name.neday.apple.daydayrun.ui;

import name.neday.apple.daydayrun.Pedometer;
import name.neday.apple.daydayrun.R;
import name.neday.apple.daydayrun.config.BmobConstants;
import name.neday.apple.daydayrun.util.CommonUtils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity implements OnClickListener {

	EditText et_username, et_password;
	Button btn_login;
	TextView btn_register,btn_resetpassword;
	BmobChatUser currentUser;

	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		//注册退出广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);

	}
	
	private void init() {
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (TextView) findViewById(R.id.btn_register);
		btn_resetpassword = (TextView) findViewById(R.id.btn_resetpassword);
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		btn_resetpassword.setOnClickListener(this);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && BmobConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
				finish();
			}
		}

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register:
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_resetpassword:
			inputTitleDialog();
			break;
		case R.id.btn_login:
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if(!isNetConnected){
				ShowToast(R.string.network_tips);
				return;
			}
			login();
			break;
		}

	}
	 private void inputTitleDialog() {
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.inputserverview, null);
		    builder.setView(layout);
		    final EditText inputServer = (EditText)layout.findViewById(R.id.inputServer);
	        inputServer.setFocusable(true);

	        builder.setTitle(getString(R.string.resetpassword))
	        .setPositiveButton(getString(R.string.resetpassword_ok),
	                new DialogInterface.OnClickListener() {
	                    @Override
						public void onClick(DialogInterface dialog, int which) {
	                        final String resetemail = inputServer.getText().toString();
	                        BmobUser.resetPassword(mApplication, resetemail, new ResetPasswordListener() {
	                            @Override
	                            public void onSuccess() {
	                                // TODO Auto-generated method stub
	                                ShowToast("重置密码请求成功，请到" + resetemail + "邮箱进行密码重置操作");
	                            }
	                            @Override
	                            public void onFailure(int code, String e) {
	                                // TODO Auto-generated method stub
	                                ShowToast("重置密码失败:" + e);
	                            }
	                        });
	                    }
	                });
	        builder.show();
	    }
	private void login(){
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();

		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(
				LoginActivity.this);
		progress.setMessage("正在登陆...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.login(name, password, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.setMessage("正在获取跑友列表...");
					}
				});
				//更新用户的地理位置以及好友的资料
				updateUserInfos();
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,Pedometer.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				// TODO Auto-generated method stub
				progress.dismiss();
				BmobLog.i(arg0);
				ShowToast(arg0);
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
}

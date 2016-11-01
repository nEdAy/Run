package name.neday.apple.daydayrun.ui;

import java.util.List;

import name.neday.apple.daydayrun.R;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FlashlightActivity extends Activity implements OnClickListener {

	public ImageView btnimageView = null;
	public RelativeLayout bgmageView = null;
	private Camera camera;
	private boolean isOpen = true;
	public final static int OPEN_CAMERA = 1011;
	public final static int OPEN_LIGHT = 1012;
	public final static int CLOSE_LIGHT = 1013;
	private Vibrator vibrator;
	long[] pattern = { 100, 200 };
	PowerManager.WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash);
		handler.sendEmptyMessage(OPEN_CAMERA);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		btnimageView = (ImageView) findViewById(R.id.btn_image);
		bgmageView = (RelativeLayout) findViewById(R.id.bg_image);
		bgmageView.setBackgroundResource(R.drawable.bg_flashlight_off);
		btnimageView.setImageResource(R.drawable.btn_flash_light_off);
		btnimageView.setOnClickListener(this);
		bgmageView.setBackgroundResource(R.drawable.bg_flashlight_on);
		btnimageView.setImageResource(R.drawable.btn_flash_light_on);
		handler.sendEmptyMessage(OPEN_LIGHT);
		vibrator.vibrate(pattern, -1);

	}

	Handler handler = new Handler(Looper.getMainLooper()) {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPEN_CAMERA:
				camera = Camera.open();
				break;
			case OPEN_LIGHT:
				Parameters params = camera.getParameters();
				List<String> list = params.getSupportedFlashModes();
				if (list.contains(Parameters.FLASH_MODE_TORCH)) {
					params.setFlashMode(Parameters.FLASH_MODE_TORCH);
				} else {
					Toast.makeText(getApplicationContext(), "此设备不支持闪光灯模式",
							Toast.LENGTH_SHORT).show();
				}
				camera.setParameters(params);
				camera.startPreview();
				isOpen = true;
				break;
			case CLOSE_LIGHT:
				if (isOpen) {
					Parameters closepParameters = camera.getParameters();
					closepParameters
							.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					camera.setParameters(closepParameters);
					camera.stopPreview();
					// camera.release();
					isOpen = false;
					// sendEmptyMessage(OPEN_CAMERA);
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != camera) {
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		vibrator.cancel();
		if (null != camera) {
			camera.release();
			camera = null;
		}

		FlashlightActivity.this.finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, "test");
		wakeLock.acquire();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (wakeLock != null)
			wakeLock.release();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_image:
			if (isOpen) {
				bgmageView.setBackgroundResource(R.drawable.bg_flashlight_off);
				btnimageView.setImageResource(R.drawable.btn_flash_light_off);
				handler.sendEmptyMessage(CLOSE_LIGHT);
				vibrator.vibrate(pattern, -1);
			} else {
				bgmageView.setBackgroundResource(R.drawable.bg_flashlight_on);
				btnimageView.setImageResource(R.drawable.btn_flash_light_on);
				handler.sendEmptyMessage(OPEN_LIGHT);
				vibrator.vibrate(pattern, -1);
			}
			break;
		default:
			break;
		}

	}

}

package name.neday.apple.daydayrun.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import name.neday.apple.daydayrun.R;

public class LockActivity extends BaseActivity{
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	 this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
	requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
	setContentView(R.layout.activity_lock);
	}

	private static long firstTime;
	/**
	 * 连续按两次返回键就退出锁屏
	 */
	@Override
	public void onBackPressed() {
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("再按一次,退出锁屏");
		}
		firstTime = System.currentTimeMillis();
	}
	
	@SuppressWarnings("static-access")
	@Override
    public boolean onKeyDown( int keyCode, KeyEvent event) {
           // TODO Auto-generated method stub
           if (keyCode == event. KEYCODE_HOME) {
                 return true;
          }
           return super.onKeyDown(keyCode, event);
    }
}

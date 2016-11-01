package name.neday.apple.daydayrun.ui;

import name.neday.apple.daydayrun.R;
import name.neday.apple.daydayrun.electric.ElectricActivity;
import name.neday.apple.daydayrun.ui.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AboutActivity extends BaseActivity implements OnClickListener {
	ImageView app_logo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initTopBarForLeft("关于");
		app_logo = (ImageView)findViewById(R.id.app_logo);
		app_logo.setOnClickListener(this);
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this,ElectricActivity.class));
	}

}

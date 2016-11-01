/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun.preferences;


import name.neday.apple.daydayrun.R;
import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * {@link EditTextPreference} 只允许浮点数数值.
 * @author nEdAy
 */
public class BodyWeightPreference extends EditMeasurementPreference {

	public BodyWeightPreference(Context context) {
		super(context);
	}
	public BodyWeightPreference(Context context, AttributeSet attr) {
		super(context, attr);
	}
	public BodyWeightPreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
	}

	@Override
	protected void initPreferenceDetails() {
		mTitleResource = R.string.body_weight_setting_title;
		mMetricUnitsResource = R.string.kilograms;
		mImperialUnitsResource = R.string.pounds;
	}
}


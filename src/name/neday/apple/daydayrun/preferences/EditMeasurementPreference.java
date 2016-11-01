package name.neday.apple.daydayrun.preferences;

import android.content.Context;
//import android.os.Bundle;
import android.preference.EditTextPreference;
//import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * {@link EditTextPreference} 选择适合的数值测量单位.
 * 这个处理公制/英制设置。
 * @author nEdAy
 */
abstract public class EditMeasurementPreference extends EditTextPreference {
	boolean mIsMetric;
	
	protected int mTitleResource;
	protected int mMetricUnitsResource;
	protected int mImperialUnitsResource;
	
	public EditMeasurementPreference(Context context) {
		super(context);
		initPreferenceDetails();
	}
	public EditMeasurementPreference(Context context, AttributeSet attr) {
		super(context, attr);
		initPreferenceDetails();
	}
	public EditMeasurementPreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		initPreferenceDetails();
	}
	
	abstract protected void initPreferenceDetails();
	
	@Override
	protected void onAddEditTextToDialogView (View dialogView, EditText editText) {
		editText.setRawInputType(
				InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		super.onAddEditTextToDialogView(dialogView, editText);
	}
	@Override
	public void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			try {
				Float.valueOf(((CharSequence)(getEditText().getText())).toString());
			}
			catch (NumberFormatException e) {
				this.showDialog(null);
				return;
			}
		}
		super.onDialogClosed(positiveResult);
	}
}

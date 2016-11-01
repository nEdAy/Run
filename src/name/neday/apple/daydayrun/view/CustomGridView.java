package name.neday.apple.daydayrun.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/** 自定义GridView
  */
public class CustomGridView extends GridView {

	public CustomGridView(Context context) {
		super(context);
	}
	
	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}


}

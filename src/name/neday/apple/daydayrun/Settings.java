/*
 *  计步器 - 安卓应用
 *  Copyright (C) 2014 nEdAy
 */

package name.neday.apple.daydayrun;



import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 计步器设置活动
 * 当用户点击主菜单中设置选项时启动。
 * @author nEdAy
 */
public class Settings extends PreferenceActivity {
    /** 当活动首次创建时调用. */
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
    }
}

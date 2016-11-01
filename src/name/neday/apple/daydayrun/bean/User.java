package name.neday.apple.daydayrun.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/** 重载BmobChatUser对象：若还有其他需要增加的属性可在此添加
  */
public class User extends BmobChatUser {
	private static final long serialVersionUID = 1L;
	//显示数据拼音的首字母
	private String sortLetters;
	//性别-true-男
	private boolean sex;
	//地理坐标
	private BmobGeoPoint location;
	//本地号码
	public String localnumber;

	public BmobGeoPoint getLocation() {
		return location;
	}
	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}
	public boolean getSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getLocalNumber() {
		return localnumber;
	}
	public void setLocalNumber(String localnumber) {
		this.localnumber = localnumber;
	}

}

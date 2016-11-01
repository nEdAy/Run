package name.neday.apple.daydayrun.bean;

import cn.bmob.v3.BmobObject;


/** 重载BmobChatUser对象：若还有其他需要增加的属性可在此添加
  */
public class UserItem extends BmobObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String username,Times,Steps,Distance,Calories;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getTimes() {
		return Times;
	}
	public void setTimes(String Times) {
		this.Times = Times;
	}
	
	public String getSteps() {
		return Steps;
	}
	public void setSteps(String Steps) {
		this.Steps = Steps;
	}
	
	public String getCalories() {
		return Calories;
	}
	public void setCalories(String calories) {
		this.Calories = calories;
	}
	
	public String getDistance() {
		return Distance;
	}
	public void setDistance(String Distance) {
		this.Distance = Distance;
	}
}

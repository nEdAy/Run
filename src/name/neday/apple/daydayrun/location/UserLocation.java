package name.neday.apple.daydayrun.location;

import cn.bmob.v3.BmobObject;

public class UserLocation extends BmobObject{

	private static final long serialVersionUID = 1L;
    private String username;
    private String location;
    private String coordinate;
    private float radius;
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getCoordinate() {
        return coordinate;
    }
    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }
    public float getRadius() {
        return radius;
    }
    public void setRadius(float f) {
        this.radius = f;
    }

}
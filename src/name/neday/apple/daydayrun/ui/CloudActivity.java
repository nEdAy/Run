package name.neday.apple.daydayrun.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;
import name.neday.apple.daydayrun.R;
import name.neday.apple.daydayrun.ui.BaseActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CloudActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud);
		final TextView[] show = new TextView[11];
		show[0] = (TextView) findViewById(R.id.textView2);
		show[1] = (TextView) findViewById(R.id.textView3);
		show[2] = (TextView) findViewById(R.id.textView4);
		show[3] = (TextView) findViewById(R.id.textView5);
		show[4] = (TextView) findViewById(R.id.textView6);
		show[5] = (TextView) findViewById(R.id.textView7);
		show[6] = (TextView) findViewById(R.id.textView8);
		show[7] = (TextView) findViewById(R.id.textView9);
		show[8] = (TextView) findViewById(R.id.textView10);
		show[9] = (TextView) findViewById(R.id.textView11);
		show[10] = (TextView) findViewById(R.id.textView12);		
		initTopBarForLeft("每日排行");
		Intent intent = getIntent();
		String str = intent.getStringExtra("name");
		
		JSONObject jso = new JSONObject();

		try {
			jso.put("myname", str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			ShowToast("参数传输失败");
			e.printStackTrace();
		}

	AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
	//第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
	ace.callEndpoint(CloudActivity.this, "orderlist",jso, 
	    new CloudCodeListener() {
	            @SuppressLint("NewApi")
				@Override
	            public void onSuccess(Object object) {
	                // TODO Auto-generated method stub
//					show.setText("云端usertest方法返回:\n" + object.toString());
					try {
					    show[10].setText("您尚未进入排行榜前十！");	
						JSONTokener jsonParser = new JSONTokener(object.toString());  
						JSONObject person = (JSONObject) jsonParser.nextValue();  
					    person.getInt("count");  
					    int have = person.getInt("have");  
					    JSONArray ArrayData = person.getJSONArray("records");  
						String[] array=new String[ArrayData.length()]; 
						for (int i = 0; i < ArrayData.length(); i++) {  
						    JSONObject temp = (JSONObject) ArrayData.get(i);  
						    String name = temp.getString("name");  
						    int cur = temp.getInt("cur");  
						    double distance = temp.getDouble("distance");  
						    double steps = temp.getDouble("steps");  
						    double calories = temp.getDouble("calories");  
						    if(have==1){
							    if (cur==1){
								    array[i] = i+1 +"  "+ name +"  "+ steps +"  "+ distance +"  "+ calories +"★";
								    show[i].setText(array[i]);	
								    show[10].setText("您已经进入排行榜前十！");	
							    }
							    else
							    {
							    	array[i] = i+1 +"  "+ name +"  "+ steps +"  "+ distance +"  "+ calories;
								    show[i].setText(array[i]);	
							    }
						    }
						  
						}  
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						show[10].setText("err"+e.toString());
					}
	            }
	            @Override
	            public void onFailure(int code, String msg) {
	                // TODO Auto-generated method stub
	            	show[10].setText("网路问题,访问云端方法失败:");	
	            }
	        });
	}
	
	
    @Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	

}

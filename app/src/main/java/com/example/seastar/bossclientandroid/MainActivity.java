package com.example.seastar.bossclientandroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;

import org.json.JSONObject;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);

        Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(this);

        Button button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(this);

        Button button5 = (Button)findViewById(R.id.button5);
        button5.setOnClickListener(this);

        Button button6 = (Button)findViewById(R.id.button6);
        button6.setOnClickListener(this);

        Button button7 = (Button)findViewById(R.id.button7);
        button7.setOnClickListener(this);

        Button button8 = (Button)findViewById(R.id.button8);
        button8.setOnClickListener(this);

        Button button9 = (Button)findViewById(R.id.button9);
        button9.setOnClickListener(this);

        Button button10 = (Button)findViewById(R.id.button10);
        button10.setOnClickListener(this);
    }


    public void onClick(View view){

        if(view.getId() == R.id.button1){
            startTracking(this,appId,appKey,"");
        }
        if(view.getId() == R.id.button2){
            trackRegistration(this,"2222");
        }
        if(view.getId() == R.id.button3){
            trackLogin(this,"2222");
        }
        if(view.getId() == R.id.button4){
            trackPurchase(this,"2222","11");
        }
        if(view.getId() == R.id.button5){
            trackonLine(this,"2222");
        }
        if(view.getId() == R.id.button6){
            trackAppsFlyerActice();
        }
        if(view.getId() == R.id.button7){
            trackAppsFlyerRegistration();
        }
        if(view.getId() == R.id.button8){
            trackAppsFlyerLogin();
        }
        if(view.getId() == R.id.button9){
            trackAppsFlyerLevelAchieved(10,10);
        }
        if(view.getId() == R.id.button10){
            trackAppsFlyerPurchase(10,"11","11","11");
        }
    }

    private String appId = "123";
    private String appKey = "456";
    private String channelId = "_default_";

    private MessageSender messageSender = new MessageSender();

    public void startTracking(Context context, String appId, String key, String channelId) {
        messageSender.startup();

        // 需要判断是打开还是安装后首次打开
        if (!Utility.isInstalled(context)) {
            Utility.installed(context);

            try {
                JSONObject msg = new JSONObject();
                  msg.put("api","device/install");
                  msg.put("appId",appId);
                msg.put("deviceId",getDeviceInfo(context));
                msg.put("deviceType","Android");
                msg.put("country","China");
                msg.put("clientTime","");
                messageSender.send(msg.toString(),"http://10.10.10.248:8080/device/install");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONObject msg = new JSONObject();
            msg.put("api","device/install");
            msg.put("appId",appId);
            msg.put("deviceId",getDeviceInfo(context));
            msg.put("deviceType","Android");
            msg.put("country","China");
            msg.put("clientTime","");

            messageSender.send(msg.toString(),"http://10.10.10.248:8080/device/install");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackRegistration(Context context, String userId) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("api","user/register");
            msg.put("userId",userId);
            msg.put("appId",appId);
            msg.put("deviceId",getDeviceInfo(context));
            msg.put("deviceType","Android");
            msg.put("country","China");
            msg.put("clientTime","");

            messageSender.send(msg.toString(),"http://10.10.10.248:8080/user/register");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackLogin(Context context, String userId) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("api","user/login");
            msg.put("appId",appId);
            msg.put("userId",userId);
            msg.put("clientTime","");

            messageSender.send(msg.toString(),"http://10.10.10.248:8080/user/login");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackPurchase(Context context, String userId, String payMoney) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("api","user/pay");
            msg.put("appId",appId);
            msg.put("userId",userId);
            msg.put("payMoney",payMoney);
            msg.put("clientTime","");

            messageSender.send(msg.toString(),"http://10.10.10.248:8080/user/pay");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackonLine(Context context, String userId) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("api","user/online");
            msg.put("appId",appId);
            msg.put("userId",userId);
            msg.put("clientTime","");

            messageSender.send(msg.toString(),"http://10.10.10.248:8080/user/online");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getDeviceInfo(Context context) {
        JSONObject content = new JSONObject();
        try {
            content.put("deviceid", Utility.getDeviceId(context));
            content.put("imei", Utility.getImei(context));
            content.put("imsi", Utility.getImsi(context));
            content.put("mac", Utility.getMacAddress(context));
            content.put("androidid", Utility.getAndroidId(context));
            content.put("board", Utility.getBoard());
            content.put("device", Utility.getDevice());
            content.put("brand", Utility.getBrand());
            content.put("manufacturer", Utility.getManufacturer());
            content.put("model", Utility.getModel());
            content.put("product", Utility.getProduct());
            content.put("systemversion", Utility.getSystemVersion());
            content.put("network", Utility.getNetworkType(context));
            content.put("width", Utility.getDisplayWidth(context));
            content.put("height", Utility.getDisplayHeight(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return content;
    }



    public void trackAppsFlyerActice(){

        AppsFlyerLib.getInstance().startTracking(this.getApplication(),"");
    }
    public void trackAppsFlyerLevelAchieved(int level, int score){
        Map<String,Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.LEVEL,level);
        eventValue.put(AFInAppEventParameterName.SCORE,score);
        AppsFlyerLib.getInstance().trackEvent(this,AFInAppEventType.LEVEL_ACHIEVED,eventValue);

    }

    public void trackAppsFlyerPurchase(int price, String skuType, String sku, String currency){
        Map<String,Object> eventValue = new HashMap<String ,Object>();
        eventValue.put(AFInAppEventParameterName.REVENUE,price);
        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,skuType);
        eventValue.put(AFInAppEventParameterName.CONTENT_ID,sku);
        eventValue.put(AFInAppEventParameterName.CURRENCY,currency);
        AppsFlyerLib.getInstance().trackEvent(this,AFInAppEventType.PURCHASE,eventValue);
    }

    public void trackAppsFlyerRegistration(){
        Map<String,Object> eventValue = new HashMap<String,Object>();
        AppsFlyerLib.getInstance().trackEvent(this,AFInAppEventType.COMPLETE_REGISTRATION,eventValue);

    }

    public void trackAppsFlyerLogin(){
        Map<String,Object> eventValue = new HashMap<String,Object>();
        AppsFlyerLib.getInstance().trackEvent(this,AFInAppEventType.LOGIN,eventValue);
    }





}

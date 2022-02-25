package com.kaka.bluetoothble_client;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class SpeechApplication extends Application {

    @Override
    public void onCreate() {

        StringBuffer param = new StringBuffer();

        param.append("appid="+"75d416b1");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(SpeechApplication.this, param.toString());
        super.onCreate();
    }
}




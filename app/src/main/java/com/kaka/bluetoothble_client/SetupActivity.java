package com.kaka.bluetoothble_client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import static com.kaka.bluetoothble_client.VideoChatViewActivity.mChatId;
import static com.kaka.bluetoothble_client.VideoChatViewActivity.mLogView;

public class SetupActivity extends AppCompatActivity {

    private TextView MIDtext;
    private TextView ACDtext;
    private Button MID;
    private Button ACD;
    private Button back;
    private Button close;
    private Button seave;//保存
    private Button reset;//恢复默认设置
    //private String mChatId;
    //private LoggerRecyclerView mLogView;
    private String UL="off";
    private String INF="off";
    private String PIR="off";
    private String FIRE="off";
    private String Far_near="near";
    private String roulette_button="roulette";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        back=findViewById(R.id.breakk);
        close=findViewById(R.id.Close);
        MIDtext=findViewById(R.id.textView4);
        ACDtext=findViewById(R.id.textView5);
        MID =findViewById(R.id.button4);
        ACD =findViewById(R.id.button5);
        seave=findViewById(R.id.Baocun);
        reset=findViewById(R.id.Quxiao);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchComponent = (Switch) findViewById(R.id.switch1);
        switchComponent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //sendData("ULO");
                    Log.d("Bt_log","UL开");
                    UL="on";
                }else{
                    //sendData("ULF");
                    Log.d("Bt_log","UL关");
                    UL="off";
                }
            }
        });
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchComponent3 = (Switch) findViewById(R.id.switch3);
        switchComponent3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // sendData("PIRO");
                    Log.d("Bt_log","PIR开");
                    PIR="on";
                }else{
                    // sendData("PIRF");
                    Log.d("Bt_log","PIR关");
                    PIR="off";
                }
            }
        });
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchComponent4 = (Switch) findViewById(R.id.switch4);
        switchComponent4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // sendData("FIREO");
                    Log.d("Bt_log","FIRE开");
                    FIRE="on";
                }else{
                    //sendData("FIREF");
                    Log.d("Bt_log","FIRE关");
                    FIRE="off";
                }
            }
        });
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchComponent6 = (Switch) findViewById(R.id.control_Mode);
        switchComponent6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    Log.d("Bt_log","轮盘控制");
                    roulette_button="roulette";
                }else{
                    Log.d("Bt_log","按键控制");
                    roulette_button="button";
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SetupActivity.this, VideoChatViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        //保存设置
        seave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                }
        });
        //取消保存
    }
    public void MID_onClick(View v) {
        //执行写入操作
      String  COMMID="MID000";
      if (MIDtext.getText()!=null){
          COMMID= MIDtext.getText().toString().trim();;
          //writeData(COMID);
          sendData(COMMID);
      }

    }
    public void ADC_onClick(View v) {
        //执行写入操作
        String COMADC="ADC00";
        if (ACDtext.getText()!=null){
            COMADC= ACDtext.getText().toString().trim();;
            //writeData(COMADC);
            sendData(COMADC);
        }
    }
    public void sendData(String COM){
        // String content = mInputEdit.getText().toString().trim();
        if (!TextUtils.isEmpty(COM)) {
            // mInputEdit.setText("");
            // 创建一条新消息，第一个参数为消息内容，第二个为接受者username
            EMMessage message = EMMessage.createTxtSendMessage(COM, mChatId);
            // 将新的消息内容和时间加入到下边
            mLogView.logI("发送："
                    + COM
                    + " - time: "
                    + message.getMsgTime());
            // 调用发送消息的方法
            EMClient.getInstance().chatManager().sendMessage(message);
            // 为消息设置回调
            message.setMessageStatusCallback(new EMCallBack() {
                @Override public void onSuccess() {
                    // 消息发送成功，打印下日志，正常操作应该去刷新ui
                    Log.i("lzan13", "send message on success");
                }

                @Override public void onError(int i, String s) {
                    // 消息发送失败，打印下失败的信息，正常操作应该去刷新ui
                    Log.i("lzan13", "send message on error " + i + " - " + s);
                }

                @Override public void onProgress(int i, String s) {
                    // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt不回调
                }
            });
        }

    }


}

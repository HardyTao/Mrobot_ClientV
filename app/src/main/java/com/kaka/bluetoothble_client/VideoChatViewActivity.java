package com.kaka.bluetoothble_client;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.util.FileUtil;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.kaka.rockerlibrary.view.RockerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.media.RtcTokenBuilder;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static android.app.PendingIntent.getActivity;
import static com.kaka.rockerlibrary.view.RockerView.Direction.DIRECTION_CENTER;
import static com.kaka.rockerlibrary.view.RockerView.Direction.DIRECTION_DOWN;
import static com.kaka.rockerlibrary.view.RockerView.Direction.DIRECTION_DOWN_LEFT;
import static com.kaka.rockerlibrary.view.RockerView.Direction.DIRECTION_DOWN_RIGHT;
import static com.kaka.rockerlibrary.view.RockerView.Direction.DIRECTION_UP;
import static com.kaka.rockerlibrary.view.RockerView.Direction.DIRECTION_UP_LEFT;
import static com.kaka.rockerlibrary.view.RockerView.Direction.DIRECTION_UP_RIGHT;
import static com.kaka.rockerlibrary.view.RockerView.DirectionUD.DIRECTIONUD_DOWN;
import static com.kaka.rockerlibrary.view.RockerView.DirectionUD.DIRECTIONUD_UP;


public class VideoChatViewActivity extends AppCompatActivity implements EMMessageListener {
    private static final String TAG = VideoChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;


    private Button close;//关闭
    private Button brleak;//返回
    ImageView Go_on;//向前
    ImageView Back_off;//向后
    ImageView Turn_left;//向左
    ImageView Turn_right;//向右
    ImageView Go_straight;//直行
    private RelativeLayout control;
    private RelativeLayout control_panel;
    private RelativeLayout ble_parameter;
    private RelativeLayout control_rocker;
    private RelativeLayout controlResponse;
    private RelativeLayout  controlResponse1;
    private TextView MIDtext;
    private TextView ACDtext;
    private Button MID;
    private Button ACD;
    private ImageView Setup;
    private ImageView Speedup;
    private ImageView Stop;
    private ImageView Slowdown;
    private Button seave;//保存
    private Button reset;//恢复默认设置

    private TextView rl_Shudu;
    private TextView rl_dianliang;
    private  String Speed="";
    private String electric="";
    private  double Electric=0.00;
    private  String PIR_FIRE="1111";
    private int rl_left_times=0;
    private int rl_right_times=0;
    private  int shudu=40;
    private int shudu1=35;
    // 聊天信息输入框
    private EditText mInputEdit;
    // 发送按钮
    private Button mSendBtn;

    // 显示内容的 TextView
    private TextView mContentText;

    // 消息监听器
    private EMMessageListener mMessageListener;
    // 当前聊天的 ID
    static String mChatId;
    // 当前会话对象
    private EMConversation mConversation;
    private String COM="S085";//直行
    private String COMab="S085";//前进 后退
    private String COMud="U050";//加速 减速
    private String COMLED="SET(000,000,0,0011,99,99)";//led全开
    private String Ledcoms="0011";
    // Customized logger view
    static LoggerRecyclerView mLogView;

    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogView.logI("Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                   // WriteLogDate("Join channel success, uid: " + (uid & 0xFFFFFFFFL)+"--time ："+getDate());
                }
            });
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogView.logI("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    WriteLogDate( filetxt, "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                 //   WriteLogDate("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL)+"--time ："+getDate());
                    setupRemoteVideo(uid);
                }
            });
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogView.logI("User offline, uid: " + (uid & 0xFFFFFFFFL));
                    WriteLogDate( filetxt, "User offline, uid: " + (uid & 0xFFFFFFFFL));
                  //  WriteLogDate("User offline, uid: " + (uid & 0xFFFFFFFFL)+"--time ："+getDate());
                    onRemoteUserLeft(uid);
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        ViewGroup parent = mRemoteContainer;
        if (parent.indexOfChild(mLocalVideo.view) > -1) {
            parent = mLocalContainer;
        }

        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        if (mRemoteVideo != null) {
            return;
        }
        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(mRemoteVideo);
    }

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
        }
    }
    String file_path ="";
    String StingfileName ="";
    String DebugLogfileName ="";
    File fileini = null;
    File filetxt =null;
    int day=1;
    int  month =1;
    int year=2021;
    private Handler handler_wifi;

    private static final int NETWORKTYPE_WIFI = 0;
    private static final int NETWORKTYPE_4G = 1;
    private static final int NETWORKTYPE_2G = 2;
    private static final int NETWORKTYPE_NONE = 3;
    public TextView mTextView;
    public  ImageView iv_ser_signal;
    public TelephonyManager mTelephonyManager;
    public PhoneStatListener mListener;
    public int mGsmSignalStrength;
    private NetWorkBroadCastReciver mNetWorkBroadCastReciver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);
        // 保存在DebugLog文件夹下
        Calendar ca = Calendar.getInstance();
        year = ca.get(Calendar.YEAR);           // 获取年份
        month = ca.get(Calendar.MONTH);         // 获取月份
        day = ca.get(Calendar.DATE);            // 获取日
        file_path = this.getExternalCacheDir() + File.separator ;
        new File(file_path).mkdirs();//会自动判断路径是否存在如果不存在，创建路径
        StingfileName = file_path +"Sting.ini";
        DebugLogfileName=file_path + "DebugLog_"+year+"_"+(month+1)+"_"+day+""+".txt";
        Log.i("StingfileName:", StingfileName);
        Log.i("DebugLogfileName:", DebugLogfileName);
        fileini= new File(StingfileName);
        filetxt = new File(DebugLogfileName);
        if(!fileini.exists()){
            try{
                fileini.createNewFile();
            }catch (IOException e){
                Log.e("ALeeObj", e.toString());
                Log.i("err:", "creat F");
            }
        }
        if(!filetxt.exists()){
            try{
                filetxt.createNewFile();
            }catch (IOException e){
                Log.e("ALeeObj", e.toString());
                Log.i("err:", "creat F");
            }
        }
      //  WriteLogDate("Start Time>>>>"+getDate());
        // 获取当前会话的username(如果是群聊就是群id)
        mChatId = getIntent().getStringExtra("ec_chat_id");
        channelName=getIntent().getStringExtra("Vedio_channelName");
        mMessageListener = this;
        initView();
        initUI();
        initConversation();
        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
/*WIFI 信号监听*/
        // 使用定时器,每隔5秒获得一次信号强度值
    /*    Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isWifiConnect()) {
                    WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
                    int wifi = mWifiInfo.getRssi();//获取wifi信号强度
                    if (wifi > -50 && wifi < 0) {//最强
                        Message msg = new Message();
                        msg.what = 1;
                        handler_wifi.sendMessage(msg);
                    } else if (wifi > -70 && wifi < -50) {//较强
                        Message msg = new Message();
                        msg.what = 2;
                        handler_wifi.sendMessage(msg);
                    } else if (wifi > -80 && wifi < -70) {//较弱
                        Message msg = new Message();
                        msg.what = 3;
                        handler_wifi.sendMessage(msg);
                    } else if (wifi > -100 && wifi < -80) {//微弱
                        Message msg = new Message();
                        msg.what = 4;
                        handler_wifi.sendMessage(msg);
                    }
                }else {//WIFI无连接
                    Message msg = new Message();
                    msg.what = 5;
                    handler_wifi.sendMessage(msg);
                }
            }
        },1000,5000);
        // 使用Handler实现UI线程与Timer线程之间的信息传递,每5秒告诉UI线程获得wifiInto
        handler_wifi = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    // 如果收到正确的消息就获取WifiInfo，改变图片并显示信号强度
                    case 1:
                        WriteLogDate( filetxt, "信号强度：" +  " 信号最好");
                        // Toast.makeText(MainActivity.this, "信号强度：" +  " 信号最好", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        WriteLogDate( filetxt,  "信号强度：" +  " 信号较好");
                        // Toast.makeText(MainActivity.this, "信号强度：" +  " 信号较好", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        WriteLogDate( filetxt,  "信号强度：" +  " 信号一般");
                        // Toast.makeText(MainActivity.this, "信号强度：" +  " 信号一般", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        WriteLogDate( filetxt,  "信号强度：" +   " 信号较差");
                        //Toast.makeText(MainActivity.this, "信号强度：" +  " 信号较差", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        WriteLogDate( filetxt,  "信号强度：" +   " 无信号");
                        Toast.makeText(VideoChatViewActivity.this,
                                "WIFI信号强度：" +" 无信号", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        //以防万一
                        WriteLogDate( filetxt,  " WIFI无信号");
                        Toast.makeText(VideoChatViewActivity.this, "WIFI无信号",
                                Toast.LENGTH_SHORT).show();
                }
            }
        };
    */
        iv_ser_signal=findViewById(R.id.iv_ser_signal);

        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //开始监听
        mListener = new PhoneStatListener();
        /**由于信号值变化不大时，监听反应不灵敏，所以通过广播的方式同时监听wifi和信号改变更灵敏*/
        mNetWorkBroadCastReciver = new NetWorkBroadCastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(mNetWorkBroadCastReciver, intentFilter);

    }
    private boolean pull_view=true;
    private RelativeLayout control_reveal;
    private RelativeLayout RL;
    private RelativeLayout pullView;
    private ImageView pull_right;
    /**
     * 初始化界面
     */
    private void initView() {
    /*    mInputEdit = (EditText) findViewById(R.id.ec_edit_message_input);
        mSendBtn = (Button) findViewById(R.id.ec_btn_send);
        mContentText = (TextView) findViewById(R.id.ec_text_content);
        // 设置textview可滚动，需配合xml布局设置
        mContentText.setMovementMethod(new ScrollingMovementMethod());
*/
        // 设置发送按钮的点击事件
       /* mSendBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override public void onClick(View v) {
                String content = mInputEdit.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    mInputEdit.setText("");
                    // 创建一条新消息，第一个参数为消息内容，第二个为接受者username
                    EMMessage message = EMMessage.createTxtSendMessage(content, mChatId);
                    // 将新的消息内容和时间加入到下边
                    mContentText.setText(mContentText.getText()
                            + "\n发送："
                            + content
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
        });
        */
        Go_on=findViewById(R.id.control_Go_on);;//向前
        Back_off=findViewById(R.id.control_Back_off);//向后
        Turn_left=findViewById(R.id.control_left);;//向左
        Turn_right=findViewById(R.id.control_right);;//向右
        Go_straight=findViewById(R.id.control_Go_straight);;//直行
        Setup=findViewById(R.id.control_Setup);//设置
        Speedup=findViewById(R.id.control_Speedup);//加速
        Stop=findViewById(R.id.control_Stop);//停止
        Slowdown=findViewById(R.id.control_Slowdown);//减速
        rl_Shudu=findViewById(R.id.rl_speed);
        rl_dianliang=findViewById(R.id.rl_electric);
        control_reveal=findViewById(R.id.control_reveal);
        RL=findViewById(R.id.rl);


        pull_right=findViewById(R.id.pull_right);
        pull_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pull_view){
                    pull_view=false;
                    pull_right.setImageResource(R.drawable.pull_right);
                    RL.setVisibility(View.VISIBLE);
                }else {
                    pull_view=true;
                   // control_reveal.setBackgroundDrawable(getResources().getDrawable(R.drawable.listview_left));
                    pull_right.setImageResource(R.drawable.pull_left);
                    RL.setVisibility(View.GONE);
                }
            }
        });
    }
    private String UL="off";
    private String INF="off";
    private String PIR="off";
    private String FIRE="off";
    private String Far_near="near";
    private String roulette_button="roulette";

    private void initUI() {

        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);
        control=findViewById(R.id.control);
        control_panel=findViewById(R.id.control_panel);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);
        mLogView = findViewById(R.id.log_recycler_view);

        ble_parameter=findViewById(R.id.ble_parameter);
        control_rocker=findViewById(R.id.control_rockerView);//小车轮盘控制view
        controlResponse=findViewById(R.id.control_response);//小车按键控制view
        close=findViewById(R.id.Close);
        brleak=findViewById(R.id.breakk);
        seave=findViewById(R.id.Baocun);
        reset=findViewById(R.id.Quxiao);
        MIDtext=findViewById(R.id.textView4);
        ACDtext=findViewById(R.id.textView5);
        MID =findViewById(R.id.button4);
        ACD =findViewById(R.id.button5);


        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchComponent = (Switch) findViewById(R.id.switch1);
        switchComponent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //sendData("ULO");
                  //  WriteLogDate("SetUpBtLog->->->UL开"+"--time ："+getDate());
                    Log.d("Bt_log","UL开");
                    UL="on";
                }else{
                    //sendData("ULF");
                 //   WriteLogDate("SetUpBtLog->->->UL关"+"--time ："+getDate());
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
                //    WriteLogDate("SetUpBtLog->->->PIR开"+"--time ："+getDate());
                    Log.d("Bt_log","PIR开");
                    PIR="on";
                }else{
                    // sendData("PIRF");
                  //  WriteLogDate("SetUpBtLog->->->PIR关"+"--time ："+getDate());
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
                 //   WriteLogDate("SetUpBtLog->->->FIRE开"+"--time ："+getDate());
                    Log.d("Bt_log","FIRE开");
                    FIRE="on";
                }else{
                    //sendData("FIREF");
                //    WriteLogDate("SetUpBtLog->->->FIRE关"+"--time ："+getDate());
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
                  //  WriteLogDate("SetUpBtLog->->-轮盘控制"+"--time ："+getDate());
                    Log.d("Bt_log","轮盘控制");
                    roulette_button="roulette";
                }else{
                  //  WriteLogDate("SetUpBtLog->->-按键控制"+"--time ："+getDate());
                    Log.d("Bt_log","按键控制");
                    roulette_button="button";
                }

            }
        });

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch led1 = (Switch) findViewById(R.id.led1);
        led1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    WriteLogDate( filetxt,"打开电源指示灯");
                    // COMLED="SET(000,000,0,0011,99,99)";//led全开
                    Log.d(TAG,"COMLED="+COMLED);
                    String []coms=COMLED.split(",");
                    Ledcoms=coms[3];
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    char[]ledcoms=Ledcoms.toCharArray();
                    Ledcoms="";
                    for (int i=0;i<ledcoms.length;i++){
                        if (i==3){
                            ledcoms[i]='1';
                        }
                        Ledcoms+=ledcoms[i];
                    }
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    COMLED="";
                    for (int j=0;j<coms.length;j++){
                        if (j==3){
                            coms[j]=Ledcoms;
                        }
                        COMLED= COMLED+coms[j]+",";

                    }
                    COMLED= COMLED.substring(0,COMLED.length()-1);
                    Log.d(TAG,"COMLED="+COMLED);
                    sendData(COMLED);
                }else{
                    WriteLogDate( filetxt,"关闭电源指示灯");
                    Log.d(TAG,"COMLED="+COMLED);
                    String []coms=COMLED.split(",");
                    String Ledcoms=coms[3];
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    char[]ledcoms=Ledcoms.toCharArray();
                    Ledcoms="";
                    for (int i=0;i<ledcoms.length;i++){
                        if (i==3){
                            ledcoms[i]='0';
                        }
                        Ledcoms+=ledcoms[i];
                    }
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    COMLED="";
                    for (int j=0;j<coms.length;j++){
                        if (j==3){
                            coms[j]=Ledcoms;
                        }
                        COMLED= COMLED+coms[j]+",";
                    }
                    COMLED= COMLED.substring(0,COMLED.length()-1);
                    Log.d(TAG,"COMLED="+COMLED);
                    sendData(COMLED);
                }

            }
        });
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch led2 = (Switch) findViewById(R.id.led2);
        led2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    WriteLogDate( filetxt,"打开蓝牙指示灯");
                    // COMLED="SET(000,000,0,0011,99,99)";//led全开
                    Log.d(TAG,"COMLED="+COMLED);
                    String []coms=COMLED.split(",");
                    String Ledcoms=coms[3];
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    char[]ledcoms=Ledcoms.toCharArray();
                    Ledcoms="";
                    for (int i=0;i<ledcoms.length;i++){
                        if (i==2){
                            ledcoms[i]='1';
                        }
                        Ledcoms+=ledcoms[i];
                    }
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    COMLED="";
                    for (int j=0;j<coms.length;j++){
                        if (j==3){
                            coms[j]=Ledcoms;
                        }
                        COMLED= COMLED+coms[j]+",";

                    }
                    COMLED= COMLED.substring(0,COMLED.length()-1);
                    Log.d(TAG,"COMLED="+COMLED);
                    sendData(COMLED);
                }else{
                    WriteLogDate( filetxt,"关闭蓝牙指示灯");
                    Log.d(TAG,"COMLED="+COMLED);
                    String []coms=COMLED.split(",");
                    String Ledcoms=coms[3];
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    char[]ledcoms=Ledcoms.toCharArray();
                    Ledcoms="";
                    for (int i=0;i<ledcoms.length;i++){
                        if (i==2){
                            ledcoms[i]='0';
                        }
                        Ledcoms+=ledcoms[i];
                    }
                    Log.d(TAG,"Ledcoms="+Ledcoms);
                    COMLED="";
                    for (int j=0;j<coms.length;j++){
                        if (j==3){
                            coms[j]=Ledcoms;
                        }
                        COMLED= COMLED+coms[j]+",";
                    }
                    COMLED= COMLED.substring(0,COMLED.length()-1);
                    Log.d(TAG,"COMLED="+COMLED);
                    sendData(COMLED);
                }
            }
        });
        //保存设置
        seave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Far_near=="near"){
                    if (roulette_button=="button"){
                        ble_parameter.setVisibility(View.GONE);
                        control.setVisibility(View.VISIBLE);
                        control_panel.setVisibility(View.VISIBLE);
                        control_rocker.setVisibility(View.GONE);
                        controlResponse.setVisibility(View.VISIBLE);
                    }else if (roulette_button=="roulette"){
                        ble_parameter.setVisibility(View.GONE);
                        control.setVisibility(View.VISIBLE);
                        control_panel.setVisibility(View.VISIBLE);
                        control_rocker.setVisibility(View.VISIBLE);
                        controlResponse.setVisibility(View.GONE);
                    }
                }
                if (isReceive){
                    if(UL=="off"){
                        WriteLogDate( filetxt,  "UL关");
                        String []coms=COMLED.split(",");
                        String COMULF=COMLED.split(",")[2];
                        COMULF="0";
                        COMLED="";
                        for (int j=0;j<coms.length;j++){
                            if (j==2){
                                coms[j]=COMULF;
                            }
                            COMLED= COMLED+coms[j]+",";
                        }
                        COMLED= COMLED.substring(0,COMLED.length()-1);
                        sendData(COMLED);
                        changeSeting( fileini,"UL","off");

                    }else if(UL=="on"){
                        WriteLogDate( filetxt,  "UL开");
                        String []coms=COMLED.split(",");
                        String COMULF=COMLED.split(",")[2];
                        COMULF="1";
                        COMLED="";
                        for (int j=0;j<coms.length;j++){
                            if (j==2){
                                coms[j]=COMULF;
                            }
                            COMLED= COMLED+coms[j]+",";
                        }
                        COMLED= COMLED.substring(0,COMLED.length()-1);
                        sendData(COMLED);
                        changeSeting( fileini,"UL","on");
                    }
                    if(PIR=="off"){
                        WriteLogDate( filetxt,  "PIR关");
                        sendData("PIRF");
                        changeSeting( fileini,"PIR","off");
                    }else if(PIR=="on"){
                        WriteLogDate( filetxt,  "PIR开");
                        sendData("PIRO");
                        changeSeting( fileini,"PIR","on");
                    }
                    if(FIRE=="off"){
                        WriteLogDate( filetxt,  "FIRE关");
                        sendData("FIREF");
                        changeSeting( fileini,"FIRE","off");
                    }else if(FIRE=="on"){
                        WriteLogDate( filetxt,  "FIRE开");
                         sendData("FIREO");
                        changeSeting( fileini,"FIRE","off");
                    }
                }
                // 设置回调模式
                RockerView rockerViewLeft = (RockerView) findViewById(R.id.rockerView);
                if (rockerViewLeft != null) {
                    rockerViewLeft.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
                    rockerViewLeft.setOnShakeListener(RockerView.DirectionMode.DIRECTION_6_ROTATE, new RockerView.OnShakeListener() {
                        @Override
                        public void onStart() {
                            mLogView.logI(null);
                        }
                        @Override
                        public void direction(RockerView.Direction direction) {
                            mLogView.logI("摇动方向 : " + getDirection(direction));

                            //交互
                        }
                        public void directionUD(RockerView.DirectionUD direction) {
                            mLogView.logI("摇动方向 : " + getDirectionUD(direction));
                            //交互
                        }
                        @Override
                        public void onFinish() {
                            mLogView.logI(null);
                        }
                    });
                }
            }
        });
        //取消保存
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UL="off";
                INF="off";
                PIR="off";
                FIRE="off";
                Far_near="near";
                roulette_button="roulette";
                if (roulette_button=="button"){
                    ble_parameter.setVisibility(View.GONE);
                    control.setVisibility(View.VISIBLE);
                    control_panel.setVisibility(View.VISIBLE);
                    control_rocker.setVisibility(View.GONE);
                    controlResponse.setVisibility(View.VISIBLE);
                }else if (roulette_button=="roulette"){
                    ble_parameter.setVisibility(View.GONE);
                    control.setVisibility(View.VISIBLE);
                    control_panel.setVisibility(View.VISIBLE);
                    control_rocker.setVisibility(View.VISIBLE);
                    controlResponse.setVisibility(View.GONE);
                }
                if (isReceive){
                    if(UL=="off"){
                        WriteLogDate( filetxt,  "UL关");
                        String []coms=COMLED.split(",");
                        String COMULF=COMLED.split(",")[2];
                        COMULF="0";
                        COMLED="";
                        for (int j=0;j<coms.length;j++){
                            if (j==2){
                                coms[j]=COMULF;
                            }
                            COMLED= COMLED+coms[j]+",";
                        }
                        COMLED= COMLED.substring(0,COMLED.length()-1);
                        sendData(COMLED);
                        changeSeting( fileini,"UL","off");

                    }else if(UL=="on"){
                        WriteLogDate( filetxt,  "UL开");
                        String []coms=COMLED.split(",");
                        String COMULF=COMLED.split(",")[2];
                        COMULF="1";
                        COMLED="";
                        for (int j=0;j<coms.length;j++){
                            if (j==2){
                                coms[j]=COMULF;
                            }
                            COMLED= COMLED+coms[j]+",";
                        }
                        COMLED= COMLED.substring(0,COMLED.length()-1);
                        sendData(COMLED);
                        changeSeting( fileini,"UL","on");
                    }
                    if(PIR=="off"){
                        WriteLogDate( filetxt,  "PIR关");
                         sendData("PIRF");
                        changeSeting( fileini,"PIR","off");
                    }else if(PIR=="on"){
                        WriteLogDate( filetxt,  "PIR开");
                        sendData("PIRO");
                        changeSeting( fileini,"PIR","on");
                    }
                    if(FIRE=="off"){
                        WriteLogDate( filetxt,  "FIRE关");
                        sendData("FIREF");
                        changeSeting( fileini,"FIRE","off");
                    }else if(FIRE=="on"){
                        WriteLogDate( filetxt,  "FIRE开");
                        sendData("FIREO");
                        changeSeting( fileini,"FIRE","off");
                    }
                }
                // 设置回调模式
                RockerView rockerViewLeft = (RockerView) findViewById(R.id.rockerView);
                if (rockerViewLeft != null) {
                    rockerViewLeft.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
                    rockerViewLeft.setOnShakeListener(RockerView.DirectionMode.DIRECTION_6_ROTATE, new RockerView.OnShakeListener() {
                        @Override
                        public void onStart() {
                            mLogView.logI(null);
                        }
                        @Override
                        public void direction(RockerView.Direction direction) {
                            mLogView.logI("摇动方向 : " + getDirection(direction));
                            //交互
                        }
                        @Override
                        public void directionUD(RockerView.DirectionUD direction) {
                            mLogView.logI("摇动方向 : " + getDirectionUD(direction));
                            //交互
                        }
                        @Override
                        public void onFinish() {
                            mLogView.logI(null);
                        }
                    });
                }
            }
        });
        // Sample logs are optional.
        showSampleLogs();
    }
    public void MID_onClick(View v) {
        //执行写入操作

        if (MIDtext.getText()!=null){
            COM=MIDtext.getText().toString().trim();;
            sendData(COM);
        }

    }
    public void ADC_onClick(View v) {
        //执行写入操作
        String COMADC="ADC00";
        if (ACDtext.getText()!=null){
            COMADC="ADC"+ ACDtext.getText().toString().trim();;
            //writeData(COMADC);
            sendData(COMADC);
        }
    }
       //向前   -
        public void onGo_onClick(View v) {
            //执行写入操作
            if (Electric>10.4){
                COMab="MOT(A,0"+shudu+")";
                sendData(COMab);
            }

        }
    //向后
        public void onBack_offClick(View v) {
            //执行写入操作
            if (Electric>10.4){
                COMab="MOT(B,0"+shudu+")";
                sendData(COMab);
            }

        }
    //向左
        public void onTurn_leftClick(View v) {
            //执行写入操作
            rl_left_times++;
            if (rl_left_times>3){
                rl_left_times=3;
            }
            if (Electric>10.4){
                if (COMab.contains("(A,")){
                    COMab="MOT(A,0"+shudu1+")";
                }
                if (COMab.contains("(B,")){
                    COMab="MOT(B,0"+shudu1+")";
                }
                COM=COMab+"SEV(L,"+ rl_left_times*10+")";
                sendData(COM);
            }
        }
    //向右
        public void onTurn_rightClick(View v) {
            //执行写入操作
            rl_left_times++;
            if (rl_left_times>3){
                rl_left_times=3;
            }
            if (Electric>10.4){
                if (COMab.contains("(A,")){
                    COMab="MOT(A,0"+shudu1+")";
                }
                if (COMab.contains("(B,")){
                    COMab="MOT(B,0"+shudu1+")";
                }
                COM=COMab+"SEV(R,"+ rl_left_times*10+")";
                sendData(COM);
            }

        }
    //直行
        public void onGo_straightClick(View v) {
            //执行写入操作
            rl_right_times=0;
            rl_left_times=0;
            if (Electric>10.4){
                COM="SEV(M,00)";
                // writeData(COM);
                sendData(COM);
            }

        }
    //设置
        public void onSetupClick(View v) {
            ble_parameter.setVisibility(View.VISIBLE);
            control.setVisibility(View.GONE);
            control_panel.setVisibility(View.GONE);
        }
    //加速
        public void onSpeedupClick(View v) {
            //执行写入操作
            if (Electric>10.4){
                COM="MOT(U,020)";
                sendData(COM);
            }
        }
    //停止
        public void onStopClick(View v) {
            //执行写入操作
            if (Electric>10.4){
                COMab="MOT(S,000)";
                //writeData(COMab);
                sendData(COMab);
                rl_Shudu.setText("0cm/s");
            }

        }
    //减速
        public void onSlowdownClick(View v) {
            //执行写入操作
            if (Electric>10.4){
                COM="MOT(D,020)";
                //writeData(COMab);
                sendData(COM);
            }

        }

    private void showSampleLogs() {
        mLogView.logI("Welcome to Agora 1v1 video call");
        mLogView.logW("You will see custom logs here");
        mLogView.logE("You can also use this to show errors");
        WriteLogDate( filetxt,  "Welcome to Agora 1v1 video call");
        WriteLogDate( filetxt,  "You will see custom logs here");
        WriteLogDate( filetxt,  "You can also use this to show errors");
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_ID) {
           if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));

        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
        mRtcEngine.startPreview();
    }


    static String appId = "d17187e70cb448529c2c19504e67b956";
    static String appCertificate = "addea61d51a8403db139bd5b22b4665e";
    static String channelName = "demoChannel1";
    static String userAccount = "2082341273";
    static int uid = 0;
    static int expirationTimeInSeconds = 3600;
    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result =token.buildTokenWithUid(appId, appCertificate,
                channelName, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        //  String result = token.buildTokenWithUserAccount(appId, appCertificate,
        //        channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        String Token = result;
        if (TextUtils.isEmpty(Token) || TextUtils.equals(Token, "#YOUR ACCESS TOKEN#")) {
            Token = null; // default, no token
        }
        mRtcEngine.joinChannel(Token, channelName, "", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendData("SLEEP");
        if (!mCallEnd) {
            leaveChannel();

        }
        unregisterReceiver(mNetWorkBroadCastReciver);
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
        }

        showButtons(!mCallEnd);
    }

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            mLocalContainer.addView(canvas.view);
        }
    }

    public void onLocalContainerClick(View view) {
        switchView(mLocalVideo);
        switchView(mRemoteVideo);
    }

    private Boolean matching=true;//是否与服务器端匹配

    /**
     * 初始化会话对象，并且根据需要加载更多消息
     */
    @SuppressLint("SetTextI18n")
    private void initConversation() {
        /**
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(mChatId, null, true);
        // 设置当前会话未读数为 0
        mConversation.markAllMessagesAsRead();
        int count = mConversation.getAllMessages().size();
        if (count < mConversation.getAllMsgCount() && count < 20) {
            // 获取已经在列表中的最上边的一条消息id
            String msgId = mConversation.getAllMessages().get(0).getMsgId();
            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
            mConversation.loadMoreMsgFromDB(msgId, 20 - count);
        }
        //打开聊天界面获取最后一条消息内容并显示
        if (mConversation.getAllMessages().size() > 0) {
            EMMessage messge = mConversation.getLastMessage();
            EMTextMessageBody body = (EMTextMessageBody) messge.getBody();
            // 将消息内容和时间显示出来
            mLogView.logI("聊天记录：" + body.getMessage() + " - time: " + mConversation.getLastMessage()
                    .getMsgTime());
            WriteLogDate( filetxt,  "聊天记录：" + body.getMessage() + " - time: " + mConversation.getLastMessage()
                    .getMsgTime());
        }else {
            matching=false;
           // Toast.makeText(VideoChatViewActivity.this, "", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 自定义实现Handler，主要用于刷新UI操作
     */
    String OutputBuff="";
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    EMMessage message = (EMMessage) msg.obj;
                    // 这里只是简单的demo，也只是测试文字消息的收发，所以直接将body转为EMTextMessageBody去获取内容
                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    // 将新的消息内容和时间加入到下边
                    mLogView.logI("接收："
                            + body.getMessage()
                            + " - time: "
                            + getDate());
                    WriteLogDate( filetxt,  "接收到信息："
                            + body.getMessage());
                    OutputBuff=body.getMessage();
                    if (!TextUtils.isEmpty(OutputBuff) && OutputBuff.contains("IN:(")){
                       // PIR_FIRE= OutputBuff.substring(OutputBuff.length()-4,OutputBuff.length());//后4位
                        PIR_FIRE= GetSubstringFromString(OutputBuff, "IN:(", ")SPEED:", false);
                        Log.d(TAG,"PIR_FIRE_COM="+PIR_FIRE);
                        char[]PIR_FIRE_COMs=PIR_FIRE.toCharArray();
                        if(PIR_FIRE_COMs[2]=='0'||PIR_FIRE_COMs[3]=='0'){
                            WriteLogDate( filetxt,  "接收到信息："
                                    + body.getMessage()+";"+"\n"+"手机机器人感应到周围有火焰，注意明火，小心引起火灾！");
                            Toast.makeText(VideoChatViewActivity.this, "手机机器人感应到周围有火焰，注意明火，小心引起火灾！", Toast.LENGTH_SHORT).show();
                        }
                        if(PIR_FIRE_COMs[0]=='1'){
                            WriteLogDate( filetxt,  "接收到信息："
                                    + body.getMessage()+";"+"\n"+"手机机器人感应到周围有人靠近，请即时确认是否为熟人，谨防小偷入室偷窃！");
                            Toast.makeText(VideoChatViewActivity.this, "手机机器人感应到周围有人靠近，请即时确认是否为熟人，谨防小偷入室偷窃！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(OutputBuff.contains("OK")||OutputBuff.contains("->SPEED:")){
                        isReceive=true;
                    }

                    if (OutputBuff.contains("SPEED:")) {
                        if (COMab.contains("(A,")) {
                            Speed = GetSubstringFromString(OutputBuff, "SPEED:(", ")DIR", false);
                            rl_Shudu.setText(Speed + "cm/s");
                        }
                        if (COMab.contains("(B,")) {
                            Speed = GetSubstringFromString(OutputBuff, "SPEED:(", ")DIR", false);
                            rl_Shudu.setText(Speed + "cm/s");
                        }
                        electric=GetSubstringFromString(OutputBuff,"BATERY:(",")UL",false);
                        Electric=Double.parseDouble(electric.substring(0,4));
                        rl_dianliang.setText(Electric+"");
                        Log.d(TAG, "Electric:"+Electric);
                        if (Electric<10.4||Electric==10.4){
                            COMab="MOT(A,040)";
                            sendData("SLEEP");
                            sendData("SET(000,000,0,0000,99,99)");
                            Toast.makeText(VideoChatViewActivity.this, "手机机器人电量过低，请及时充电！", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    };

    @Override protected void onResume() {
        super.onResume();
        acquireWakeLock();//获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override protected void onStop() {
        super.onStop();
        releaseWakeLock();// 释放设备电源锁
        // 移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);

    }
    /**
     * --------------------------------- Message Listener -------------------------------------
     * 环信消息监听主要方法
     */
    /**
     * 收到新消息
     *
     * @param list 收到的新消息集合
     */
private  boolean isReceive=false;
    public void onMessageReceived(List<EMMessage> list) {
        // 循环遍历当前收到的消息
        for (EMMessage message : list) {
            Log.i("lzan13", "收到新消息:" + message);

            if (message.getFrom().equals(mChatId)) {
                // 设置消息为已读
                mConversation.markMessageAsRead(message.getMsgId());

                // 因为消息监听回调这里是非ui线程，所以要用handler去更新ui
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = message;
                mHandler.sendMessage(msg);
            } else {
                // TODO 如果消息不是当前会话的消息发送通知栏通知
            }
        }
    }

    /**
     * 收到新的 CMD 消息
     */
     public void onCmdMessageReceived(List<EMMessage> list) {
        for (int i = 0; i < list.size(); i++) {
            // 透传消息
            EMMessage cmdMessage = list.get(i);
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
            Log.i("lzan13", "收到 CMD 透传消息" + body.action());
        }
    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到消息已读回执
     */
     public void onMessageRead(List<EMMessage> list) {}

    /**
     * 收到新的发送回执
     * TODO 无效 暂时有bug
     *
     * @param list 收到发送回执的消息集合
     */
    public void onMessageDelivered(List<EMMessage> list) {}

    /**
     * 消息撤回回调
     *
     * @param list 撤回的消息列表
     */
    public void onMessageRecalled(List<EMMessage> list) {}

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param object 包含改变的消息
     */
     public void onMessageChanged(EMMessage message, Object object) {}

    private PowerManager.WakeLock wakeLock = null;

    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     */
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                //  Log.i(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }
    // 释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            // Log.i(TAG, "call releaseWakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }
    private boolean isSend=false;
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
                    + getDate());
            WriteLogDate( filetxt,  "向服务端或第二个用户端发送指令："
                    + COM);
         //   WriteLogDate("发送：" + COM + " - time: " + getDate());
            // 调用发送消息的方法
            EMClient.getInstance().chatManager().sendMessage(message);
            // 为消息设置回调
            message.setMessageStatusCallback(new EMCallBack() {
                @Override public void onSuccess() {
                    // 消息发送成功，打印下日志，正常操作应该去刷新ui
                    isSend=true;
                  //  WriteLogDate("send message on success" + " - time: " + getDate());
                    Log.i("lzan13", "send message on success");
                    WriteLogDate( filetxt,  "send message on success");
                }

                @Override public void onError(int i, String s) {
                    // 消息发送失败，打印下失败的信息，正常操作应该去刷新ui
                    isSend=false;
                //    WriteLogDate( "send message on error " + i + " - " + s + " - time: " + getDate());
                    Log.i("lzan13", "send message on error " + i + " - " + s);
                    WriteLogDate( filetxt,  "send message on error " + i + " - " + s);
                }

                @Override public void onProgress(int i, String s) {
                    // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt不回调
                }
            });
        }
    }
    public String GetSubstringFromString(String deststr, String from, String to, boolean trim )
    {
//->SPEED:0backBATERY:12.30528P
        int start = deststr.indexOf(from) + from.length();
        int end = deststr.indexOf(to);
        Log.e(TAG,"from:"+from+"to:"+to);
        Log.e(TAG,"start:"+ start+"end:"+end);
        if (end > 0)
        {
            if (trim)
            { //  Log.e(TAG,deststr.substring(start, end).trim());
                return deststr.substring(start, end).trim();}
            else
            {    Log.e(TAG,deststr.substring(start, end+1));
                return deststr.substring(start, end);
            }
        }
        else
        {
            return "";
        }
    }
    private String getDirection (RockerView.Direction direction){
        String message = null;
        switch (direction) {
            case DIRECTION_LEFT:
                message = "左";
                if (Electric>10.4){
                    COM="SEV(L,30)";
                    sendData(COM);
                }
                break;
            case DIRECTION_RIGHT:
                message = "右";
                if (Electric>10.4){
                    COM="SEV(R,30)";
                    sendData(COM);
                }
                break;
            case DIRECTION_Go_straight:
                message = "直行";
                rl_right_times=0;
                rl_left_times=0;
                if (Electric>10.4){
                    COM="SEV(M,00)";
                    sendData(COM);
                }
                break;

            default:
                break;
        }
        return message;
    }

    private String getDirectionUD(RockerView.DirectionUD direction){
        String message = null;
        if(direction== DIRECTIONUD_UP){
            message = "前进";
            if (Electric>10.4){
                COMab="MOT(A,0"+shudu+")";
                sendData(COMab);
            }

        }else if(direction== DIRECTIONUD_DOWN){
            message = "后退";
            if (Electric>10.4){
                COMab="MOT(B,0"+shudu+")";
                sendData(COMab);
            }

        }

        return message;
    }
    /**
     * 获取系统时间
     *
     * @return
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH);         // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR);           // 小时
        int second = ca.get(Calendar.SECOND);       // 秒
        String date = "" + year +"."+ (month + 1)+"." + day +"."+ hour +"."+ minute+":" + second;
        Log.d(TAG, "date:" + date);
        return date;
    }


    /**
     * 获取系统时间
     *
     * @return
     */

    public  void WriteLogDate(File file, String sLog) {
        FileOutputStream fos = null;
        sLog=  getDate()+">>>>"+sLog;
        sLog+="\n";
        try {
            fos = new FileOutputStream(file.getPath(),true);
            Log.i(" DebugLogFile",  file+"DebugLog.txt");
            byte[] bytes = new byte[0];  // 将字符串按指定编码集编码--》将信息转成二进制数
            bytes = sLog.getBytes("UTF-8");
            fos.write(bytes);  // 这样写入的数据，会将文件中的原数据覆盖
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public  String  readStingINI(File file,String sItem)  {
        String result="";

        if(!file.exists()){
            return null;
        }
        try {
            FileInputStream inputStream = new FileInputStream(file.getPath());
            int length = inputStream.available();
            byte bytes[] = new byte[length];
            inputStream.read(bytes);
            inputStream.close();
            String str =new String(bytes, StandardCharsets.UTF_8);
            String Strs[]=new  String[length];
            Strs=str.split("\n");
            for (int i=0;i<Strs.length;i++){
                if (Strs[i].contains(sItem)){
                    result=Strs[i].split("=")[1].split("//")[0].trim();
                    Log.i(" readStingINI:", result);
                    return  result;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(" readStingINI err:",  e.toString());
        }
        return  result;
    }
    public File getAlbumStorageDir(String albumName) {
// Get the directory for the user's public pictures directory.

        File file =  new File(Environment.getDataDirectory().getAbsolutePath(),
                albumName);
        if(!file.exists()){
            file.mkdirs();
        }
        Log.i(" DebugLogFile",file.getPath() );
        Log.i( "codecraeer" , "getFilesDir = " + getFilesDir());
        Log.i( "codecraeer" , "getExternalFilesDir = " + getExternalFilesDir( "exter_test" ).getAbsolutePath());
        Log.i( "codecraeer" , "getDownloadCacheDirectory = " + Environment.getDownloadCacheDirectory().getAbsolutePath());
        Log.i( "codecraeer" , "getDataDirectory = " + Environment.getDataDirectory().getAbsolutePath());
        Log.i( "codecraeer" , "getExternalStorageDirectory = " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i( "codecraeer" , "getExternalStoragePublicDirectory = " + Environment.getExternalStoragePublicDirectory( "pub_test" ));
        return file;
    }
    private void delay(Integer time){
        //  Thread.currentThread();
        // Thread.sleep(ms);
        //  Integer time = 2000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },time);
    }
    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }
    public  void changeSeting(File file,String steingItem,String value) {

        FileInputStream inputStream =null;
        try {
            inputStream = new FileInputStream(file.getPath());
            int length = inputStream.available();
            byte bytes[] = new byte[length];
            inputStream.read(bytes);
            inputStream.close();
            String str =new String(bytes, StandardCharsets.UTF_8);
            String Strs[]=new  String[length];
            Strs=str.split("\n");
            if (file.exists()) // 判断文件是否存在
            {
                file.delete(); // 存在则先删除
            }
            file.createNewFile(); // 再创建
            for (int i=0;i<Strs.length;i++){
                if (Strs[i].contains(steingItem)){
                    String  stxt=Strs[i].split("//")[1];
                    Strs[i]=steingItem+"="+value+"//"+stxt;
                }

                FileOutputStream  fos = new FileOutputStream(file.getPath(),true);
                byte[] bytevs = new byte[0];  // 将字符串按指定编码集编码--》将信息转成二进制数
                bytevs = (Strs[i]+"\n").getBytes("UTF-8");
                fos.write(bytevs);  // 这样写入的数据，会将文件中的原数据覆盖
                fos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class PhoneStatListener extends PhoneStateListener {
        //获取信号强度

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //获取网络信号强度
            //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
//            int level = signalStrength.getLevel();
            mGsmSignalStrength = signalStrength.getGsmSignalStrength();
            //网络信号改变时，获取网络信息
            getNetWorkInfo();
         /*   int gsmSignalStrength = signalStrength.getGsmSignalStrength();
            int netWorkType = getNetWorkType(VideoChatViewActivity.this);
            switch (netWorkType) {
                case NETWORKTYPE_WIFI:
                    mTextView.setText("当前网络为wifi,信号强度为：" + gsmSignalStrength);
                    break;
                case NETWORKTYPE_2G:
                    mTextView.setText("当前网络为2G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NETWORKTYPE_4G:
                    mTextView.setText("当前网络为4G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NETWORKTYPE_NONE:
                    mTextView.setText("当前没有网络,信号强度为：" + gsmSignalStrength);
                    break;
                case -1:
                    mTextView.setText("当前网络错误,信号强度为：" + gsmSignalStrength);
                    break;
            }*/
        }
    }

    /**暂时不用这个方法*/
    public int getNetWorkType(Context context) {
        int mNetWorkType = -1;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return isFastMobileNetwork() ? NETWORKTYPE_4G : NETWORKTYPE_2G;
            }
        } else {
            mNetWorkType = NETWORKTYPE_NONE;//没有网络
        }
        return mNetWorkType;
    }

    /**
     * 判断网络速度
     */
    private boolean isFastMobileNetwork() {
        if (mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
            //这里只简单区分两种类型网络，认为4G网络为快速，但最终还需要参考信号值
            return true;
        }
        return false;
    }

    //接收网络状态改变的广播
    class NetWorkBroadCastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getNetWorkInfo();
        }
    }

    /**
     * 获取网络的信息
     */
    private void getNetWorkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    //wifi
                    iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.library_template_null));
                    WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo connectionInfo = manager.getConnectionInfo();
                    int wifi = connectionInfo.getRssi();
                  //  mTextView.setText("当前为wifi网络，信号强度=" + rssi);
                   // Toast.makeText(VideoChatViewActivity.this, "当前为wifi网络，信号强度=" + wifi, Toast.LENGTH_SHORT).show();

                    if (wifi > -50 && wifi < 0) {//最强
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.library_template));
                    } else if (wifi > -70 && wifi < -50) {//较强
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.library_template_2));
                    } else if (wifi > -80 && wifi < -70) {//较弱
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.library_template_1));
                    } else if (wifi > -100 && wifi < -80) {//微弱
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.library_template_0));
                    }
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    //移动网络,可以通过TelephonyManager来获取具体细化的网络类型
               int NetworkType=getNetWorkType(this);
                    String netWorkStatus = isFastMobileNetwork() ? "4G网络" : "2G网络";
                   // mTextView.setText("当前为" + netWorkStatus + "，信号强度=" + mGsmSignalStrength);
                  //Toast.makeText(VideoChatViewActivity.this, "当前为" + netWorkStatus + "，信号强度="+mGsmSignalStrength, Toast.LENGTH_SHORT).show();
                    iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.sagin0));
                    if (mGsmSignalStrength > 80 && mGsmSignalStrength < 100) {//最强
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.sagin4));
                    } else if (mGsmSignalStrength > 70 && mGsmSignalStrength < 80) {//较强
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.sagin3));
                    } else if (mGsmSignalStrength > 50 && mGsmSignalStrength < 70) {//较弱
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.sagin2));
                    } else if ( mGsmSignalStrength < 50) {//微弱
                        iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.sagin1));
                    }
                    break;
            }
        } else {
            //mTextView.setText("没有可用网络");
            Toast.makeText(VideoChatViewActivity.this, "没有可用网络", Toast.LENGTH_SHORT).show();
            iv_ser_signal.setImageDrawable(getResources().getDrawable(R.drawable.sagin0));
        }
    }



}

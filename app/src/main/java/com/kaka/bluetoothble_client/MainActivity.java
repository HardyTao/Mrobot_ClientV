package com.kaka.bluetoothble_client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.iflytek.cloud.util.ResourceUtil;
import com.kaka.bluetoothble_client.adapter.BleAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


import org.json.JSONException;
import org.json.JSONObject;


import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.stream.RCRTCVideoStreamConfig;
import cn.rongcloud.rtc.base.RCRTCParamsType;
import io.rong.calllib.RongCallClient;
import io.rong.imlib.RongIMClient;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="ble_tag" ;
    ProgressBar pbSearchBle;
    ImageView ivSerBleStatus;
    TextView tvSerBleStatus;
    TextView tvSerBindStatus;
    ListView bleListView;

    private LinearLayout operaView;
    private LinearLayout  control_reveal;
    private RelativeLayout  controlResponse;
    private RelativeLayout  controlResponse1;
    private RelativeLayout  rl_view1;
    private RelativeLayout  camera_view1;

    private Button btnWrite;
    private Button btnRead;
    private Button close_all;//关闭
    private Button back;//返回

     ImageView Go_on;//向前
     ImageView Back_off;//向后
     ImageView Turn_left;//向左
    ImageView Turn_right;//向右
     ImageView Go_straight;//直行
    private Button Setup;
    private Button Speedup;
    private Button Stop;
    private Button Slowdown;

    private Button Kongzhi;
    private Button shiping;
    private Button video;
   // private VideoView videoView;

    private EditText etWriteContent;
    private TextView tvResponse;
    private List<BluetoothDevice> mDatas;
    private List<Integer> mRssis;
    private BleAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private boolean isScaning=false;
    private boolean isConnecting=false;
    private BluetoothGatt mBluetoothGatt;
    private TextView rl_speed;//速度
    private TextView rl_electric;//电量
    //服务和特征值
    private UUID write_UUID_service;
    private UUID write_UUID_chara;
    private UUID read_UUID_service;
    private UUID read_UUID_chara;
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;
    private UUID indicate_UUID_service;
    private UUID indicate_UUID_chara;
    private String hex="7B46363941373237323532443741397D";
    private String COM="S085";//直行
    private String COMab="S085";//前进 后退
    private String COMud="U050";//加速 减速
    private int view=0;
    private int speed_up=0;
    private int speed=30;
    private String Output_buffer="";


    private CameraPreview mPreview;
    SurfaceView sView;
    SurfaceHolder surfaceHolder;
    int screenWidth, screenHeight;
    Button takePhoto;
    Camera camera;  // 定义系统所用的照相机
    boolean isPreview = false;//是否在浏览中
    int cameraId=1;
    private MediaRecorder mediaRecorder;
    private boolean mStartedFlg = false;//是否正在录像
    private boolean mIsPlay = false;//是否正在播放录像
    private MediaRecorder mRecorder;
    private String path;
    //语音识别
    private SpeechRecognizer mIat;// 语音听写对象
    //private SpeechUtility   mIat;
    private RecognizerDialog mIatDialog;// 语音听写UI

    // 用HashMap存储听写结果
    private final HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private SharedPreferences mSharedPreferences;//缓存

  //  private String mEngineType = SpeechConstant.TYPE_CLOUD;// 引擎类型在线语音听写
    private String mEngineType = SpeechConstant.TYPE_LOCAL;// 引擎类型 离线语音听写
    private String language = "zh_cn";//识别语言

    private TextView tvResult;//识别结果
    private Button btnStart;//开始识别
    private String resultType = "json";//结果内容数据格式
    private android.os.Handler handler = new android.os.Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(this,1000);
        }
    };

    public static int permission;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE1= {"android.permission.CAMERA"};
    //android.permission.RECORD_AUDIO
    private static String[] PERMISSIONS_STORAGE2 = {"android.permission.RECORD_AUDIO"};
    //位置权限
    private static String[] PERMISSIONS_STORAGE3= {"android.permission.ACCESS_COARSE_LOCATION"};
    private static String[] PERMISSIONS_STORAGE4= {"android.permission.ACCESS_FINE_LOCATION"};
    //然后通过一个函数来申请
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.CAMERA");
            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE1,REQUEST_EXTERNAL_STORAGE);
            }
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.RECORD_AUDIO");
            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE2,REQUEST_EXTERNAL_STORAGE);
            }
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.ACCESS_COARSE_LOCATION");
            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE3,REQUEST_EXTERNAL_STORAGE);
            }
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.ACCESS_FINE_LOCATION");
            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE4,REQUEST_EXTERNAL_STORAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final int CONNECT_SUCCESS = 0x01;
    private static final int CONNECT_FAILURE = 0x02;
    private static final int DISCONNECT_SUCCESS = 0x03;
    private static final int SEND_SUCCESS = 0x04;
    private static final int SEND_FAILURE= 0x05;
    private static final int RECEIVE_SUCCESS= 0x06;
    private static final int RECEIVE_FAILURE =0x07;
    private static final int START_DISCOVERY = 0x08;
    private static final int STOP_DISCOVERY = 0x09;
    private static final int DISCOVERY_DEVICE = 0x0A;
    private static final int DEVICE_BOND_NONE= 0x0B;
    private static final int DEVICE_BONDING = 0x0C;
    private static final int DEVICE_BONDED = 0x0D;
    private ListView lvDevices;
    private LVDevicesAdapter lvDevicesAdapter;
    private BtBroadcastReceiver btBroadcastReceiver;
    //连接设备的UUID
    public static final String MY_BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";  //蓝牙通讯
    //当前要连接的设备
    private BluetoothDevice curBluetoothDevice;
    //发起连接的线程
   private ConnectThread connectThread;
    //管理连接的线程
   private ConnectedThread connectedThread;
    //当前设备连接状态
    private boolean curConnState = false;
    //当前设备与系统配对状态
    private boolean curBondState = false;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case START_DISCOVERY:
                    Log.d(TAG, "开始搜索设备...");
                    tvSerBindStatus.setText("搜索设备");
                    break;

                case STOP_DISCOVERY:
                    Log.d(TAG, "停止搜索设备...");
                    tvSerBindStatus.setText("停止搜索设备");
                    break;

                case DISCOVERY_DEVICE:  //扫描到设备
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) msg.obj;
                    lvDevicesAdapter.addDevice(bluetoothDevice);

                    break;

                case CONNECT_FAILURE: //连接失败
                    Log.d(TAG, "连接失败");
                    tvSerBindStatus.setText("连接失败");
                    curConnState = false;
                    break;

                case CONNECT_SUCCESS:  //连接成功
                    Log.d(TAG, "连接成功");
                    tvSerBindStatus.setText("连接成功");
                    curConnState = true;
                    if (view==0){
                        Kongzhi.setVisibility(View.VISIBLE);
                        shiping.setVisibility(View.VISIBLE);
                        view=1;
                    }
                    ShowView(1);
                    tvSerBindStatus.setText("已连接");
                    break;

                case DISCONNECT_SUCCESS:
                    tvSerBindStatus.setText("断开成功");
                    curConnState = false;

                    break;

                case SEND_FAILURE: //发送失败
                    Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    break;

                case SEND_SUCCESS:  //发送成功
                    String sendResult = (String) msg.obj;
                    tvResponse.setText("发送成功:"+sendResult);
                    break;

                case RECEIVE_FAILURE: //接收失败
                    String receiveError = (String) msg.obj;
                    tvResponse.setText(receiveError);
                    break;

                case RECEIVE_SUCCESS:  //接收成功
                    String receiveResult = (String) msg.obj;
                    tvResponse.setText("设备返回值:"+receiveResult);
                    break;

                case DEVICE_BOND_NONE:  //已解除配对
                    tvSerBindStatus.setText("解除配对成功");
                    curBondState = false;
                    break;

                case DEVICE_BONDING:   //正在配对
                    tvSerBindStatus.setText("正在配对...");
                    break;

                case DEVICE_BONDED:   //已配对
                    tvSerBindStatus.setText("配对成功");
                    curBondState = true;
                    break;
            }
        }
    };
    // 上下文菜单
    private Context mContext;
    // 发起聊天 username 输入框
    private EditText mChatIdEdit;
    private EditText channelName;
    // 发起聊天
    private Button mStartChatBtn;
    // 退出登录
    private Button mSignOutBtn;
    // 记录是否已经初始化
    private boolean isInit = false;

    String file_path="";
    String StingfileName ="";
    String DebugLogfileName ="";
    File fileini = null;
    File filetxt =null;
    private int useTimes=0;
    int day=1;
    int  month =1;
    int year=2021;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//始终竖屏
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
        File  filetxtLest= new File(file_path + "DebugLog_"+year+"_"+(month+1)+"_"+(day-1)+""+".txt");
        Log.i("filetxtLest:", filetxtLest.getPath());
        if(filetxtLest.exists()){
            filetxtLest.deleteOnExit();
        }
        if(!filetxt.exists()){
            try{
                filetxt.createNewFile();
            }catch (IOException e){
                Log.e("ALeeObj", e.toString());
                Log.i("err:", "creat F");
            }
        }
        if(!fileini.exists()){
            Log.i("Stingfileiscreate:","createNewSetingFile" );
            try{
                fileini.createNewFile();
            }catch (IOException e){
                Log.e("ALeeObj", e.toString());
                Log.i("err:", "creat F");
            }
            WriteLogDate( fileini,"<<<<...AppSeting...>>>>");
            WriteLogDate( fileini,getString(R.string.UserTimes));
            WriteLogDate( fileini,getString(R.string.UserName));
            WriteLogDate( fileini,getString(R.string.UserPW));
            WriteLogDate( fileini,getString(R.string.chat_id));
            WriteLogDate( fileini,getString(R.string.channelName));
            WriteLogDate( fileini,"<<<<...BleSeting...>>>>");
            WriteLogDate(fileini,getString(R.string.BleName));
            WriteLogDate(fileini,getString(R.string.BlePW));
            WriteLogDate( fileini,getString(R.string.BondState));
            WriteLogDate( fileini,getString(R.string.ConnState));
            WriteLogDate( fileini,"<<<<...CatSeting...>>>>");
            WriteLogDate( fileini,getString(R.string.UL));
            WriteLogDate( fileini,getString(R.string.INF));
            WriteLogDate( fileini,getString(R.string.PIR));
            WriteLogDate( fileini,getString(R.string.FIRE));
            WriteLogDate( fileini,getString(R.string.Far_near));
            WriteLogDate( fileini,getString(R.string.roulette_button));
        }

        useTimes+=1;
        changeSeting( fileini,"UserTimes",useTimes+"");

        // 使用定时器,每隔5秒获得一次信号强度值
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

            }

        },1000,5000);

  /*     initPermission();//权限请求
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
       mIat = SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
       mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
        mSharedPreferences = getSharedPreferences("ASR",
                Activity.MODE_PRIVATE);
        setContentView(R.layout.activity_search_device);
      //添加权限
        verifyStoragePermissions(this);
        initView();
        initData();
        mBluetoothManager= (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter=mBluetoothManager.getAdapter();
        if (mBluetoothAdapter==null||!mBluetoothAdapter.isEnabled()){
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,0);
        }
       //初始化蓝牙广播
        initBtBroadcast();
       // RongIMClient.init(this,"appKey",false);//初始化 融云SDK
        //相机视频预览
        //设置全屏
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //       WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置屏幕方向
      /*  this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
      //视频预览
        WindowManager wm = (WindowManager) getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 获取屏幕的宽和高
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        // 获取界面中SurfaceView组件
        sView = (SurfaceView) findViewById(R.id.sView);
        //设置监听器，触摸屏幕即拍照
       /* sView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if (camera != null)
                    {
                        // 6.调用Camera的takePicture()方法进行拍照.
                        camera.takePicture(null, null , myjpegCallback);
                        return true;
                    }
                }
                return false;
            }
        });*/
        // 获得SurfaceView的SurfaceHolder
  /*   surfaceHolder = sView.getHolder();
        //保持屏幕常亮
        surfaceHolder.setKeepScreenOn(true);
        // 为surfaceHolder添加一个回调监听器
        surfaceHolder.addCallback(new SurfaceHolder.Callback()
        {
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height)
            {
            }
            public void surfaceCreated(SurfaceHolder holder)
            {
                // surface被创建时打开摄像头
                initCamera();
            }
            // surface摧毁时释放摄像头
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                // 如果camera不为null ,释放摄像头
                if (camera != null)
                {
                    //7.结束程序时，调用Camera的StopPriview()结束取景预览，并调用release()方法释放资源.
                    if (isPreview)
                        camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
        // 设置该SurfaceView自己不维护缓冲
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
*/
        mContext = this;
        // 初始化环信SDK
        initEasemob();
        // 判断sdk是否登录成功过，并没有退出和被踢，否则跳转到登陆界面
        if (!EMClient.getInstance().isLoggedInBefore()) {
            Intent intent = new Intent(MainActivity.this, ECLoginActivity.class);
            startActivity(intent);
            finish();
           // return;
        }
        setContentView(R.layout.activity_main);
        initView_talk();
    }
    /**
     *
     */
    private void initEasemob() {
        // 获取当前进程 id 并取得进程名
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processAppName == null || !processAppName.equalsIgnoreCase(mContext.getPackageName())) {
            // 则此application的onCreate 是被service 调用的，直接返回
            return;
        }
        if (isInit) {
            return;
        }
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        options.setAppKey("1103210625153405#demo");
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执，TODO 这个暂时有bug,上层收不到发送回执
        options.setRequireDeliveryAck(true);
        // 设置是否需要服务器收到消息确认
        //  options.setRequireServerAck(true);
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(false);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(false);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);
        // 设置google GCM推送id，国内可以不用设置
        // options.setGCMNumber(MLConstants.ML_GCM_NUMBER);
        // 设置集成小米推送的appid和appkey
        // options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);
        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(mContext, options);
        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);
        // 设置初始化已经完成
        isInit = true;
    }

    /**
     * 根据Pid获取当前进程的名字，一般就是当前app的包名
     *
     * @param pid 进程的id
     * @return 返回进程的名字
     */
    private String getAppName(int pid) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    // 根据进程的信息获取当前进程的名字
                    processName = info.processName;
                    // 返回当前进程名
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有匹配的项，返回为null
        return null;
    }
    private void initCamera()
    {
        if (!isPreview)
        {
           shiping.setText("打开摄像头");
            camera = Camera.open(cameraId);  //1.调用Camera的open()方法打开前置相机。
        }
        if (camera != null && !isPreview)
        {
            try
            {
                //2.调用Camera的setParameters()方法获取拍照参数。该方法返回一个Camera.Parameters对象。
                Camera.Parameters parameters = camera.getParameters();
                //3.调用Camera.Paramers对象方法设置拍照参数
                // 设置预览照片的大小
                parameters.setPreviewSize(1280, 720);
                // 每秒显示4帧
                // parameters.setPreviewFrameRate(3);
                // 设置图片格式
                parameters.setPictureFormat(PixelFormat.JPEG);
                // 设置JPG照片的质量
                parameters.set("jpeg-quality",85);
                //设置照片的大小
                parameters.setPictureSize(1280, 720);
                if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    // 自动对焦
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                //4.调用Camera的setParameters，并将Camera.Paramers作为参数传入，这样即可对相机的拍照参数进行控制
                camera.setParameters(parameters);
                camera.setDisplayOrientation(90);
                /**
                 *  5.调用Camera的startPreview()方法开始预览取景,在预览取景之前需要调用
                 *  Camera的setPreViewDisplay(SurfaceHolder holder)方法设置使用哪个SurfaceView来显示取景图片。
                 *  通过SurfaceView显示取景画面
                 */
                camera.setPreviewDisplay(surfaceHolder);
                // 开始预览
                camera.startPreview();
                // 自动对焦
                camera.autoFocus(afcb);
                shiping.setText("开始预览");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                shiping.setText("预览失败！"+e.toString());
            }
            isPreview = true;
        }
    }

    Camera.AutoFocusCallback afcb=new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            if(success){
                //takePhoto.setEnabled(true);
                System.out.println("可以拍照");
            }
        }
    };

    private void initData() {
      /*  mDatas=new ArrayList<>();
        mRssis=new ArrayList<>();
        mAdapter=new BleAdapter(MainActivity.this,mDatas,mRssis);
        bleListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();*/
        lvDevicesAdapter = new LVDevicesAdapter(MainActivity.this);
        lvDevices=  bleListView;
        lvDevices.setAdapter(lvDevicesAdapter);
    }

    private void initView(){
        pbSearchBle=findViewById(R.id.progress_ser_bluetooth);
        ivSerBleStatus=findViewById(R.id.iv_ser_ble_status);
        tvSerBindStatus=findViewById(R.id.tv_ser_bind_status);
        tvSerBleStatus=findViewById(R.id.tv_ser_ble_status);
        bleListView=findViewById(R.id.ble_list_view);
        operaView=findViewById(R.id.opera_view);
        btnWrite=findViewById(R.id.btnWrite);
        btnRead=findViewById(R.id.btnRead);
        etWriteContent=findViewById(R.id.et_write);
        tvResponse=findViewById(R.id.tv_response);
        controlResponse=findViewById(R.id.control_response);
        controlResponse1=findViewById(R.id.control_response1);
        control_reveal=findViewById(R.id.control_reveal);
        back=findViewById(R.id.breakk);
        close_all=findViewById(R.id.Close);
        rl_speed=findViewById(R.id.rl_speed);
        rl_electric=findViewById(R.id.rl_electric);
        rl_view1=findViewById(R.id.rl_view1);
        Kongzhi=findViewById(R.id.rl_kongzhi);
        shiping=findViewById(R.id.rl_shiping);
        video=findViewById(R.id.rl_video);
        Go_on=findViewById(R.id.control_Go_on);;//向前
        Back_off=findViewById(R.id.control_Back_off);//向后
        Turn_left=findViewById(R.id.control_left);;//向左
        Turn_right=findViewById(R.id.control_right);;//向右
        Go_straight=findViewById(R.id.control_Go_straight);;//直行
        Setup=findViewById(R.id.control_Setup);//设置
        Speedup=findViewById(R.id.control_Speedup);//加速
        Stop=findViewById(R.id.control_Stop);//停止
        Slowdown=findViewById(R.id.control_Slowdown);//减速
        //搜索
        ivSerBleStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSerBindStatus.setText("正在搜索");
                bleListView.setVisibility(View.VISIBLE);
                operaView.setVisibility(View.GONE);
                rl_view1.setVisibility(View.GONE);
                if (view==1){
                    Kongzhi.setVisibility(View.GONE);
                    shiping.setVisibility(View.GONE);
                    view=0;
                }
                if(curConnState) {
                    clearConnectedThread();
                }
                if (isScaning){
                    tvSerBindStatus.setText("停止搜索");
                    stopScanDevice();
                }else{
                   // checkPermissions();
                    searchBtDevice();
                }

            }
        });
        //连接
        bleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //连接设备
                tvSerBindStatus.setText("连接中");

                    BluetoothDevice bluetoothDevice = (BluetoothDevice) lvDevicesAdapter.getItem(position);
                   // tvName.setText(bluetoothDevice.getName());
                   // tvAddress.setText(bluetoothDevice.getAddress());
                    curBluetoothDevice = bluetoothDevice;
                if(curConnState) {
                    clearConnectedThread();
                    startConnectDevice(curBluetoothDevice, MY_BLUETOOTH_UUID, 10000);
                }else{
                    startConnectDevice(curBluetoothDevice, MY_BLUETOOTH_UUID, 10000);
                }
                    //连接设备
                   // tvSerBindStatus.setText("连接中");
                   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mBluetoothGatt = bluetoothDevice.connectGatt(MainActivity.this,
                                true, gattCallback, TRANSPORT_LE);
                    } else {
                        mBluetoothGatt = bluetoothDevice.connectGatt(MainActivity.this,
                                true, gattCallback);
                    }*/
                }


        });
        //读数据
        btnRead.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                tvResponse.setText("设备返回：");
                //tvResponse.setVisibility(View.VISIBLE);
                //controlResponse.setVisibility(View.GONE);
              //readData();
            }
        });
        //写指令
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
               // tvResponse.setVisibility(View.GONE);
              // controlResponse.setVisibility(View.VISIBLE);
                String content=etWriteContent.getText().toString();
                //writeData(content);
                sendData(content,false);
            }
        });
        //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view==2){
                    ShowView(1);
                }
                if (view==3){
                    ShowView(2);
                }
            }
        });
        //关闭
        close_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                if (camera != null)
                {
                    //7.结束程序时，调用Camera的StopPriview()结束取景预览，并调用release()方法释放资源.
                    if (isPreview)
                        camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
        //控制
        Kongzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowView(2);
            }
        });
        //向前
        Go_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                COMab="MOT(A,040)";
                //writeData(COMab);
                sendData(COMab,false);

            }
        });
        //向后
        Back_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                COMab="MOT(B,040)";
                //writeData(COMab);
                sendData(COMab,false);
            }
        });
        //向左
        Turn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                 COM="SEV(L,30)";
               // writeData(COM);
                sendData(COM,false);

            }
        });
        //向右
        Turn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                COM="SEV(R,30)";
               // writeData(COM);
                sendData(COM,false);
            }
        });
        //直行
        Go_straight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                 COM="SEV(M,00)";
               // writeData(COM);
                sendData(COM,false);
            }
        });
        //设置
        Setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
            }
        });
        //加速
        Speedup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                COM="MOT(U,020)";
                sendData(COM,false);
            }
        });
        //停止
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                COMab="MOT(S,000)";
                //writeData(COMab);
                sendData(COMab,false);
            }
        });
        //减速
        Slowdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行写入操作
                tvResponse.setText("设备返回：");
                COM="MOT(D,020)";
                //writeData(COMab);
                sendData(COM,false);
            }
        });
        //语音控制
        shiping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //ShowView(2);
                if( null == mIat ){
                    // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                    showMsg( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
                    return;
                }
                mIatResults.clear();//清除数据
                setParam(); // 设置参数
                mIatDialog.setListener(mRecognizerDialogListener);//设置监听
                mIatDialog.show();// 显示对话框
            }
        });
        //视讯
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, VideoChatViewActivity.class);
                startActivity(intent);
            }
        });

    }
    private void readData() {
        BluetoothGattCharacteristic characteristic=mBluetoothGatt.getService(read_UUID_service)
                .getCharacteristic(read_UUID_chara);
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    /**
     * 开始扫描 10秒后自动停止
     * */
    @SuppressLint("MissingPermission")
    private void scanDevice(){
        tvSerBindStatus.setText("正在搜索");
        isScaning=true;
        rl_view1.setVisibility(View.GONE);
        pbSearchBle.setVisibility(View.VISIBLE);
        mBluetoothAdapter.startLeScan(scanCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //结束扫描
                mBluetoothAdapter.stopLeScan(scanCallback);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isScaning=false;
                        pbSearchBle.setVisibility(View.GONE);
                        tvSerBindStatus.setText("搜索已结束");
                    }
                });
            }
        },10000);
    }
    /**
     * 停止扫描
     * */
    @SuppressLint("MissingPermission")
    private void stopScanDevice(){
        isScaning=false;
        pbSearchBle.setVisibility(View.GONE);
       //mBluetoothAdapter.stopLeScan(scanCallback);
        mBluetoothAdapter.cancelDiscovery();
    }
    private void searchBtDevice() {
        if (mBluetoothAdapter.isDiscovering()) { //当前正在搜索设备...
            mBluetoothAdapter.cancelDiscovery();
            return;
        }
        //开始搜索
        mBluetoothAdapter.startDiscovery();
        isConnecting=true;
    }
    BluetoothAdapter.LeScanCallback scanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e(TAG, "run: scanning...");
            if (!mDatas.contains(device)){
                mDatas.add(device);
                mRssis.add(rssi);
                mAdapter.notifyDataSetChanged();
            }

        }
    };

    private BluetoothGattCallback gattCallback=new BluetoothGattCallback() {
        /**
         * 断开或连接 状态发生变化时调用
         * */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e(TAG,"onConnectionStateChange()");
            if (status==BluetoothGatt.GATT_SUCCESS){
                //连接成功
                if (newState== BluetoothGatt.STATE_CONNECTED){
                    Log.e(TAG,"连接成功");
                    //发现服务
                    gatt.discoverServices();
                }
            }else{
                //连接失败
                Log.e(TAG,"失败=="+status);
                mBluetoothGatt.close();
                isConnecting=false;
            }
        }
        /**
         * 发现设备（真正建立连接）
         * */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //直到这里才是真正建立了可通信的连接
            isConnecting=false;
            Log.e(TAG,"onServicesDiscovered()---建立连接");
            //获取初始化服务和特征值
            initServiceAndChara();
            //订阅通知
            mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt
                    .getService(notify_UUID_service).getCharacteristic(notify_UUID_chara),true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   /* bleListView.setVisibility(View.GONE);
                    operaView.setVisibility(View.VISIBLE);*/
                    if (view==0){
                        Kongzhi.setVisibility(View.VISIBLE);
                        shiping.setVisibility(View.VISIBLE);
                        view=1;
                    }
                    ShowView(1);
                    tvSerBindStatus.setText("已连接");
                }
            });
        }
        /**
         * 读操作的回调
         * */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e(TAG,"onCharacteristicRead()");
        }
        /**
         * 写操作的回调
         * */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            Log.e(TAG,"onCharacteristicWrite()  status="+status+",value="+HexUtil.encodeHexStr(characteristic.getValue()));
        }
        /**
         * 接收到硬件返回的数据
         * */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e(TAG,"onCharacteristicChanged()"+characteristic.getValue());
            final byte[] data=characteristic.getValue();
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    Output_buffer=new String(data);
                    //addText(tvResponse,bytes2hex(data));
                    tvResponse.setText("设备返回："+ Output_buffer);
                   // tvResponse.setText("设备返回："+bytes2hex(data));
                  /*  String speed=Output_buffer.substring(0,2);
                    String electric="";
                    rl_speed.setText(speed);
                    rl_electric.setText(electric);*/
                }
            });

        }
    };
    /**
     * 检查权限
     */
    @SuppressLint("CheckResult")
    private void checkPermissions() {
        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
        rxPermissions.request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 用户已经同意该权限
                            scanDevice();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            ToastUtils.showLong("用户开启权限后才能使用");
                        }
                    }
                });
    }

    private void initServiceAndChara(){
        List<BluetoothGattService> bluetoothGattServices= mBluetoothGatt.getServices();
        for (BluetoothGattService bluetoothGattService:bluetoothGattServices){
            List<BluetoothGattCharacteristic> characteristics=bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristics){
                int charaProp = characteristic.getProperties();
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    read_UUID_chara=characteristic.getUuid();
                    read_UUID_service=bluetoothGattService.getUuid();
                    Log.e(TAG,"read_chara="+read_UUID_chara+"----read_service="+read_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    write_UUID_chara=characteristic.getUuid();
                    write_UUID_service=bluetoothGattService.getUuid();
                    Log.e(TAG,"write_chara="+write_UUID_chara+"----write_service="+write_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    write_UUID_chara=characteristic.getUuid();
                    write_UUID_service=bluetoothGattService.getUuid();
                    Log.e(TAG,"write_chara="+write_UUID_chara+"----write_service="+write_UUID_service);

                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    notify_UUID_chara=characteristic.getUuid();
                    notify_UUID_service=bluetoothGattService.getUuid();
                    Log.e(TAG,"notify_chara="+notify_UUID_chara+"----notify_service="+notify_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                    indicate_UUID_chara=characteristic.getUuid();
                    indicate_UUID_service=bluetoothGattService.getUuid();
                    Log.e(TAG,"indicate_chara="+indicate_UUID_chara+"----indicate_service="+indicate_UUID_service);

                }
            }
        }
    }

    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }

    private void writeData(String content){
        BluetoothGattService service=mBluetoothGatt.getService(write_UUID_service);
        BluetoothGattCharacteristic charaWrite=service.getCharacteristic(write_UUID_chara);
        byte[] data;
        if(!TextUtils.isEmpty(content)){
           // data=HexUtil.hexStringToBytes(content);
            data=content.getBytes();
        }else{
           // data=HexUtil.hexStringToBytes(hex);
            String str="OKokOK";
            data=content.getBytes();
        }
        if (data.length>20){//数据大于个字节 分批次写入
            Log.e(TAG, "writeData: length="+data.length);
            int num=0;
            if (data.length%20!=0){
                num=data.length/20+1;
            }else{
                num=data.length/20;
            }
            for (int i=0;i<num;i++){
                byte[] tempArr;
                if (i==num-1){
                    tempArr=new byte[data.length-i*20];
                    System.arraycopy(data,i*20,tempArr,0,data.length-i*20);
                }else{
                    tempArr=new byte[20];
                    System.arraycopy(data,i*20,tempArr,0,20);
                }
                charaWrite.setValue(tempArr);
                mBluetoothGatt.writeCharacteristic(charaWrite);
            }
        }else{
            charaWrite.setValue(data);
            mBluetoothGatt.writeCharacteristic(charaWrite);
        }
    }

    private static final String HEX = "0123456789abcdef";
    public static String bytes2hex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }

    //@Override
    /* protected void onDestroy() {
        super.onDestroy();
        mBluetoothGatt.disconnect();
        if (null != mIat) {
            // 退出时释放连接
           //mIat.cancel();
            mIat.destroy();
        }
    }*/
    private void ShowView(int i){
        switch (i)
        {
            case 1:
                view=i;
                bleListView.setVisibility(View.GONE);
                tvResponse.setVisibility(View.GONE);
                controlResponse.setVisibility(View.GONE);
                controlResponse1.setVisibility(View.GONE);
                operaView.setVisibility(View.GONE);
                control_reveal.setVisibility(View.GONE);
                rl_view1.setVisibility(View.VISIBLE);

                break;
            case 2:
                view=i;
               // tvSerBindStatus.setText("控制大厅");
                bleListView.setVisibility(View.GONE);
                rl_view1.setVisibility(View.GONE);

                //videoView.setVisibility(View.GONE);
                control_reveal.setVisibility(View.VISIBLE);
                controlResponse.setVisibility(View.VISIBLE);
                controlResponse1.setVisibility(View.VISIBLE);
               tvResponse.setVisibility(View.VISIBLE);
                operaView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void start(){
        //重置媒体录制器
        mediaRecorder.reset();
        //设置音频和视频来源
        //照相机
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //麦克风
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置保存格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置每秒几帧
        mediaRecorder.setVideoFrameRate(3);
        //设置保存的路径 Phone/DCIM/Camera
        mediaRecorder.setOutputFile("mnt/sdcard/DCIM/Camera/"+System.currentTimeMillis()+".mp4");
        //将画面展示到SurfaceView
        mediaRecorder.setPreviewDisplay(sView.getHolder().getSurface());
        try {
            //准备
            mediaRecorder.prepare();
            //启动
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop(){

        if (mediaRecorder!=null){
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
        }

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

        String date = "" + year + (month + 1) + day + hour + minute + second;
        Log.d(TAG, "date:" + date);

        return date;
    }

    /**
     * 获取SD path
     *
     * @return
     */
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        }

        return null;
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    /**
     * 权限申请回调，可以作进一步处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showMsg("初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };


    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {

        public void onResult(RecognizerResult results, boolean b) {
            printResult(results);//结果数据解析
            String COM_content=tvSerBindStatus.getText().toString();
            if (COM_content.contains("前")){
                if ( COMab=="MOT(B,020)"){
                    COMab="MOT(A,020)";
                    //writeData(COMab);
                    sendData(COMab,false);
                }
                COMab="MOT(A,020)";
               //writeData(COMab);
                sendData(COMab,false);
            }else if(COM_content.contains("后")){
                if ( COMab=="MOT(A,020)"){
                    COMab="MOT(B,020)";
                   // writeData(COMab);
                    sendData(COMab,false);
                }
                COMab="MOT(B,020)";
               // writeData(COMab);
                sendData(COMab,false);
            }else if(COM_content.contains("停")){
                //showMsg( COMab );
                COMab="MOT(S,000)";
                // writeData(COMab);
                sendData(COMab,false);

            }else if(COM_content.contains("左转")){
                COM="SEV(L,45)";
               // writeData(COM);
                sendData(COM,false);
            }else if(COM_content.contains("右转")){
                COM="SEV(R,45)";
               // writeData(COM);
                sendData(COM,false);
            }else if(COM_content.contains("直行")||COM_content.contains("执行")){
                COM="SEV(M,00)";
                //writeData(COM);
                sendData(COM,false);
            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showMsg(error.getPlainDescription(true));
        }

    };

    /**
     * 提示消息
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    /**
     * 数据解析
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        tvSerBindStatus.setText(resultBuffer.toString());//听写结果显示

    }
    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        mIat.setParameter( SpeechConstant.SUBJECT, null );
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mIat.setParameter(SpeechConstant.ENGINE_MODE, SpeechConstant.MODE_MSC);

        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);
        //设置离线语音资路径
       mIat.setParameter("asr_res_path",getResourcePath());

        if (language.equals("zh_cn")) {
            String lag = mSharedPreferences.getString("iat_language_preference",
                    "mandarin");
            Log.e(TAG, "language:" + language);// 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        } else {

            mIat.setParameter(SpeechConstant.LANGUAGE, language);
        }
        Log.e(TAG, "last language:" + mIat.getParameter(SpeechConstant.LANGUAGE));

        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }
   // 离线资源路径设置
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/common.jet"));
        tempBuffer.append(";");
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/sms_16k.jet"));
        return tempBuffer.toString();
    }

    /**
     * 开始连接设备
     * @param bluetoothDevice   蓝牙设备
     * @param uuid               发起连接的UUID
     * @param conOutTime        连接超时时间
     */
    public void startConnectDevice(final BluetoothDevice bluetoothDevice, String uuid, long conOutTime){
        if(bluetoothDevice == null){
            Log.e(TAG,"startConnectDevice-->bluetoothDevice == null");
            return;
        }
        if(mBluetoothAdapter == null){
            Log.e(TAG,"startConnectDevice-->bluetooth3Adapter == null");
            return;
        }
        //发起连接
        connectThread = new ConnectThread(mBluetoothAdapter,curBluetoothDevice,uuid);
        connectThread.setOnBluetoothConnectListener(new ConnectThread.OnBluetoothConnectListener() {
            @Override
            public void onStartConn() {
                Log.d(TAG,"startConnectDevice-->开始连接..." + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
            }


            @Override
            public void onConnSuccess(BluetoothSocket bluetoothSocket) {
                //移除连接超时
                mHandler.removeCallbacks(connectOuttimeRunnable);
                Log.d(TAG,"startConnectDevice-->移除连接超时");
                Log.w(TAG,"startConnectDevice-->连接成功");

                Message message = new Message();
                message.what = CONNECT_SUCCESS;
                mHandler.sendMessage(message);

                //标记当前连接状态为true
                curConnState = true;
                //管理连接，收发数据
                managerConnectSendReceiveData(bluetoothSocket);
            }

            @Override
            public void onConnFailure(String errorMsg) {
                Log.e(TAG,"startConnectDevice-->" + errorMsg);

                Message message = new Message();
                message.what = CONNECT_FAILURE;
                mHandler.sendMessage(message);

                //标记当前连接状态为false
                curConnState = false;

                //断开管理连接
                clearConnectedThread();
            }
        });

        connectThread.start();
        //设置连接超时时间
        mHandler.postDelayed(connectOuttimeRunnable,conOutTime);

    }

    //连接超时
    private Runnable connectOuttimeRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG,"startConnectDevice-->连接超时" );

            Message message = new Message();
            message.what = CONNECT_FAILURE;
            mHandler.sendMessage(message);

            //标记当前连接状态为false
            curConnState = false;
            //断开管理连接
            clearConnectedThread();
        }
    };

    ////////////////////////////////////// 断开连接  //////////////////////////////////////////////
    /**
     * 断开已有的连接
     */
    public void clearConnectedThread(){
        Log.d(TAG,"clearConnectedThread-->即将断开");

        //connectedThread断开已有连接
        if(connectedThread == null){
            Log.e(TAG,"clearConnectedThread-->connectedThread == null");
            return;
        }
        connectedThread.terminalClose(connectThread);

        //等待线程运行完后再断开
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectedThread.cancel();  //释放连接

                connectedThread = null;
            }
        },10);

        Log.w(TAG,"clearConnectedThread-->成功断开连接");
        Message message = new Message();
        message.what = DISCONNECT_SUCCESS;
        mHandler.sendMessage(message);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////  管理已有连接、收发数据  //////////////////////////////////

    /**
     * 管理已建立的连接，收发数据
     * @param bluetoothSocket   已建立的连接
     */
    public void managerConnectSendReceiveData(BluetoothSocket bluetoothSocket){
        //管理已有连接
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
        connectedThread.setOnSendReceiveDataListener(new ConnectedThread.OnSendReceiveDataListener() {
            @Override
            public void onSendDataSuccess(byte[] data) {
                Output_buffer=new String(data);
                Log.w(TAG,"发送数据成功,长度" + data.length + "->" + Output_buffer);
                Message message = new Message();
                message.what = SEND_SUCCESS;
                message.obj = "发送数据成功,长度" + data.length + "->" + Output_buffer;
                mHandler.sendMessage(message);
            }

            @Override
            public void onSendDataError(byte[] data,String errorMsg) {
                Output_buffer=new String(data);
                Log.e(TAG,"发送数据出错,长度" + data.length + "->" + Output_buffer);
                Message message = new Message();
                message.what = SEND_FAILURE;
                message.obj = "发送数据出错,长度" + data.length + "->" + Output_buffer;
                mHandler.sendMessage(message);
            }

            @Override
            public void onReceiveDataSuccess(byte[] buffer) {
                Output_buffer=new String(buffer);
                Log.w(TAG, "->" + Output_buffer);
                Message message = new Message();
                message.what = RECEIVE_SUCCESS;
                message.obj =  "->" + Output_buffer;
                mHandler.sendMessage(message);
            }

            @Override
            public void onReceiveDataError(String errorMsg) {
                Log.e(TAG,"接收数据出错：" + errorMsg);
                Message message = new Message();
                message.what = RECEIVE_FAILURE;
                message.obj = "接收数据出错：" + errorMsg;
                mHandler.sendMessage(message);
            }
        });
    }

    /////////////////////////////////   发送数据  /////////////////////////////////////////////////

    /**
     * 发送数据
     * @param data      要发送的数据 字符串
     * @param isHex     是否是16进制字符串
     * @return   true 发送成功  false 发送失败
     */
    public boolean sendData(String data,boolean isHex){
        if(connectedThread == null){
            Log.e(TAG,"sendData:string -->connectedThread == null");
            return false;
        }
        if(data == null || data.length() == 0){
            Log.e(TAG,"sendData:string-->要发送的数据为空");
            return false;
        }

        if(isHex){  //是16进制字符串
            data.replace(" ","");  //取消空格
            //检查16进制数据是否合法
            if(data.length() % 2 != 0){
                //不合法，最后一位自动填充0
                String lasts = "0" + data.charAt(data.length() - 1);
                data = data.substring(0,data.length() - 2) + lasts;
            }
            Log.d(TAG,"sendData:string -->准备写入：" + data);  //加空格显示
            return connectedThread.write(hexString2Bytes(data));
        }

        //普通字符串
        Log.d(TAG,"sendData:string -->准备写入：" + data);
        return connectedThread.write(data.getBytes());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////   数据类型转换  //////////////////////////////////////////////
    /**
     * 字节数组-->16进制字符串
     * @param b   字节数组
     * @param length  字节数组长度
     * @return 16进制字符串 有空格类似“0A D5 CD 8F BD E5 F8”
     */
    public static String bytes2HexString(byte[] b, int length) {
        StringBuffer result = new StringBuffer();
        String hex;
        for (int i = 0; i < length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase()).append(" ");
        }
        return result.toString();
    }

    /**
     * hexString2Bytes
     * 16进制字符串-->字节数组
     * @param src  16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
    /**
     * 初始化蓝牙广播
     */
    private void initBtBroadcast() {
        //注册广播接收
        btBroadcastReceiver = new BtBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //开始扫描
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//扫描结束
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);//搜索到设备
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //配对状态监听
        registerReceiver(btBroadcastReceiver,intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //注销广播接收
       // unregisterReceiver(btBroadcastReceiver);
    }

    /**
     * 蓝牙广播接收器
     */
    private class BtBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_STARTED)) { //开启搜索
                Log.d(TAG,"开启搜索...");
                Message message = new Message();
                message.what = START_DISCOVERY;
                mHandler.sendMessage(message);

            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {//完成搜素
                Log.d(TAG,"停止搜索...");
                Message message = new Message();
                message.what = STOP_DISCOVERY;
                mHandler.sendMessage(message);

            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_FOUND)) {  //3.0搜索到设备
                //蓝牙设备
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //信号强度
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                Log.w(TAG, "扫描到设备：" + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
                Message message = new Message();
                message.what = DISCOVERY_DEVICE;
                message.obj = bluetoothDevice;
                mHandler.sendMessage(message);

            }else if(TextUtils.equals(action,BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondSate = bluetoothDevice.getBondState();
                switch(bondSate) {
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "已解除配对");
                        Message message1 = new Message();
                        message1.what = DEVICE_BOND_NONE;
                        mHandler.sendMessage(message1);
                        break;

                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "正在配对...");
                        Message message2 = new Message();
                        message2.what = DEVICE_BONDING;
                        mHandler.sendMessage(message2);
                        break;

                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "已配对");
                        Message message3 = new Message();
                        message3.what = DEVICE_BONDED;
                        mHandler.sendMessage(message3);
                        break;
                }
            }
        }
    }
    //即时通讯
    /**
     * 初始化界面
     */
    /**
     * 退出登录
     */
    private void signOut() {
        // 调用sdk的退出登录方法，第一个参数表示是否解绑推送的token，没有使用推送或者被踢都要传false
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i("lzan13", "logout success");
                // 调用退出成功，结束app
                finish();
            }

            @Override
            public void onError(int i, String s) {
                Log.i("lzan13", "logout error " + i + " - " + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
    private void initView_talk(){
        mChatIdEdit = (EditText) findViewById(R.id.ec_edit_chat_id);
        channelName=findViewById(R.id.channelName);
        mStartChatBtn = (Button) findViewById(R.id.ec_btn_start_chat);
        mStartChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取我们发起聊天的者的username
                String chatId = mChatIdEdit.getText().toString().trim();
                String Vedio_channelName=channelName.getText().toString().trim();
                if (!TextUtils.isEmpty(chatId)) {
                    // 获取当前登录用户的 username
                    String currUsername = EMClient.getInstance().getCurrentUser();
                    if (chatId.equals(currUsername)) {
                        Toast.makeText(MainActivity.this, "不能和自己聊天", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 跳转到聊天界面，开始聊天
                    Intent intent = new Intent(MainActivity.this,VideoChatViewActivity.class);
                    intent.putExtra("ec_chat_id", chatId);
                    intent.putExtra("Vedio_channelName", Vedio_channelName);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Username 不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });

        mSignOutBtn = (Button) findViewById(R.id.ec_btn_sign_out);
        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    public  void WriteLogDate(File file,String sLog) {
        FileOutputStream fos = null;
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
}

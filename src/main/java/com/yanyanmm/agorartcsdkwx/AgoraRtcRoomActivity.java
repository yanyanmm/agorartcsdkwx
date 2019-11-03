package com.yanyanmm.agorartcsdkwx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AgoraRtcRoomActivity extends AppCompatActivity {
    private static final String TAG = AgoraRtcRoomActivity.class.getSimpleName();

    private AgoraRtcRoomLayout mRoomLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_room);

        // 初始化控件
        this.initViews();

        //初始化
        mRoomLayout.init(this, "1e0441cfe0674fb3920180e2646e91ee");
        //加入房间
        mRoomLayout.joinChannel("520", "", 520);
    }

    @Override
    protected void onDestroy() {

        if (mRoomLayout != null) {
            mRoomLayout.leaveChannel();
            mRoomLayout.destory();
        }

        super.onDestroy();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        mRoomLayout = (AgoraRtcRoomLayout)findViewById(R.id.roomLayout);

        findViewById(R.id.beautyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView)v;
                if (textView.getText().toString().equals("美颜开")) {
                    textView.setText("美颜关");
                    mRoomLayout.setBeauty(true, 2, 75, 50, 10);
                } else {
                    textView.setText("美颜开");
                    mRoomLayout.setBeauty(false, 0,0,0,0);
                }
            }
        });
        findViewById(R.id.switchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoomLayout.switchCamera();
            }
        });
        findViewById(R.id.videoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView)v;
                if (textView.getText().toString().equals("视频开")) {
                    textView.setText("视频关");
                    mRoomLayout.startBroadcast();
                } else {
                    textView.setText("视频开");
                    mRoomLayout.stopBroadcast();
                }
            }
        });
        findViewById(R.id.audioBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView)v;
                if (textView.getText().toString().equals("音频开")) {
                    textView.setText("音频关");
                    mRoomLayout.muteLocalAudioStream(true);
                } else {
                    textView.setText("音频开");
                    mRoomLayout.muteLocalAudioStream(false);
                }
            }
        });
        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoomLayout.leaveChannel();
            }
        });
    }

    /**
     * 消息提示
     * @param msg
     */
    public void showToast(String msg) {
        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
    }

}

package com.ywl5320.videoplayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ywl5320.libwlplayer.WlPlayer;
import com.ywl5320.libwlplayer.WlVideoView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    WlPlayer wlPlayer;
    WlVideoView wlVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        wlVideoView = findViewById(R.id.wlvideoview);
        wlPlayer = new WlPlayer();
        wlPlayer.setWlVideoView(wlVideoView);
//        wlPlayer.setSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cqz01.mp4");
        wlPlayer.setSource("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        wlPlayer.setOnErrorListener(new WlPlayer.OnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                System.out.println("code:" + code + ",msg:" + msg);
            }
        });

        wlPlayer.setOnPreparedListener(new WlPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                System.out.println("prepared success");
            }
        });
        wlPlayer.prepared();
        wlVideoView.setWlPlayer(wlPlayer);
    }

    public void start(View view) {
        wlPlayer.start();
    }

    public void pause(View view) {
        wlPlayer.pause();
    }

    public void play(View view) {
        wlPlayer.play();
    }

    public void stop(View view) {
        wlPlayer.stop2();
    }

}

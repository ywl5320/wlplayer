package com.ywl5320.player.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ywl5320.listener.WlOnCompleteListener;
import com.ywl5320.listener.WlOnCutVideoImgListener;
import com.ywl5320.listener.WlOnErrorListener;
import com.ywl5320.listener.WlOnInfoListener;
import com.ywl5320.listener.WlOnLoadListener;
import com.ywl5320.listener.WlOnPreparedListener;
import com.ywl5320.opengles.WlGlSurfaceView;
import com.ywl5320.player.R;
import com.ywl5320.player.base.BaseActivity;
import com.ywl5320.util.MyLog;
import com.ywl5320.util.WlTimeUtil;
import com.ywl5320.wlplayer.WlPlayer;
import com.ywl5320.wlplayer.WlTimeBean;

public class VideoLiveActivity extends BaseActivity {

    private WlGlSurfaceView surfaceview;
    private WlPlayer wlPlayer;
    private ProgressBar progressBar;
    private TextView tvTime;
    private ImageView ivPause;
    private SeekBar seekBar;
    private String pathurl;
    private LinearLayout lyAction;
    private ImageView ivCutImg;
    private ImageView ivShowImg;
    private boolean ispause = false;
    private int position;
    private boolean isSeek = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        surfaceview = findViewById(R.id.surfaceview);
        progressBar = findViewById(R.id.pb_loading);
        ivPause = findViewById(R.id.iv_pause);
        tvTime = findViewById(R.id.tv_time);
        seekBar = findViewById(R.id.seekbar);
        lyAction = findViewById(R.id.ly_action);
        ivCutImg = findViewById(R.id.iv_cutimg);
        ivShowImg = findViewById(R.id.iv_show_img);

        wlPlayer = new WlPlayer();
        wlPlayer.setOnlyMusic(false);

        pathurl = getIntent().getExtras().getString("url");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position = wlPlayer.getDuration() * progress / 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeek = true;
                wlPlayer.seek(position);
            }
        });
        wlPlayer.setDataSource(pathurl);
        wlPlayer.setOnlySoft(false);
        wlPlayer.setWlGlSurfaceView(surfaceview);
        wlPlayer.setWlOnErrorListener(new WlOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:" + code + ",msg:" + msg);
                Message message = Message.obtain();
                message.obj = msg;
                message.what = 3;
                handler.sendMessage(message);
            }
        });

        wlPlayer.setWlOnPreparedListener(new WlOnPreparedListener() {
            @Override
            public void onPrepared() {
                MyLog.d("starting......");
                wlPlayer.start();
            }
        });

        wlPlayer.setWlOnLoadListener(new WlOnLoadListener() {
            @Override
            public void onLoad(boolean load) {
                MyLog.d("loading ......>>>   " + load);
                Message message = Message.obtain();
                message.what = 1;
                message.obj = load;
                handler.sendMessage(message);
            }
        });

        wlPlayer.setWlOnInfoListener(new WlOnInfoListener() {
            @Override
            public void onInfo(WlTimeBean wlTimeBean) {
                Message message = Message.obtain();
                message.what = 2;
                message.obj = wlTimeBean;
                MyLog.d("nowTime is " +wlTimeBean.getCurrt_secds());
                handler.sendMessage(message);
            }
        });

        wlPlayer.setWlOnCompleteListener(new WlOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("complete .....................");
                wlPlayer.stop(true);
            }
        });

        wlPlayer.setWlOnCutVideoImgListener(new WlOnCutVideoImgListener() {
            @Override
            public void onCutVideoImg(Bitmap bitmap) {
                Message message = Message.obtain();
                message.what = 4;
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        });

        wlPlayer.prepared();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1)
            {
                boolean load = (boolean) msg.obj;
                if(load)
                {
                    progressBar.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(lyAction.getVisibility() != View.VISIBLE)
                    {
                        lyAction.setVisibility(View.VISIBLE);
                        ivCutImg.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
            else if(msg.what == 2)
            {
                WlTimeBean wlTimeBean = (WlTimeBean) msg.obj;
                if(wlTimeBean.getTotal_secds() > 0)
                {
                    seekBar.setVisibility(View.VISIBLE);
                    if(isSeek)
                    {
                        seekBar.setProgress(position * 100 / wlTimeBean.getTotal_secds());
                        isSeek = false;
                    }
                    else
                    {
                        seekBar.setProgress(wlTimeBean.getCurrt_secds() * 100 / wlTimeBean.getTotal_secds());
                    }
                    tvTime.setText(WlTimeUtil.secdsToDateFormat(wlTimeBean.getTotal_secds()) + "/" + WlTimeUtil.secdsToDateFormat(wlTimeBean.getCurrt_secds()));
                }
                else
                {
                    seekBar.setVisibility(View.GONE);
                    tvTime.setText(WlTimeUtil.secdsToDateFormat(wlTimeBean.getCurrt_secds()));
                }
            }
            else if(msg.what == 3)
            {
                String err = (String) msg.obj;
                Toast.makeText(VideoLiveActivity.this, err, Toast.LENGTH_SHORT).show();
            }
            else if(msg.what == 4)
            {
                Bitmap bitmap = (Bitmap) msg.obj;
                if(bitmap != null)
                {
                    ivShowImg.setVisibility(View.VISIBLE);
                    ivShowImg.setImageBitmap(bitmap);
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        if(wlPlayer != null)
        {
            wlPlayer.stop(true);
        }
        this.finish();
    }

    public void pause(View view) {
        if(wlPlayer != null)
        {
            ispause = !ispause;
            if(ispause)
            {
                wlPlayer.pause();
                ivPause.setImageResource(R.mipmap.ic_play_play);
            }
            else
            {
                wlPlayer.resume();
                ivPause.setImageResource(R.mipmap.ic_play_pause);
            }
        }

    }

    public void cutImg(View view) {
        if(wlPlayer != null)
        {
            wlPlayer.cutVideoImg();
        }
    }
}

package com.ywl5320.player.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl5320.listener.WlOnCompleteListener;
import com.ywl5320.listener.WlOnInfoListener;
import com.ywl5320.listener.WlOnLoadListener;
import com.ywl5320.listener.WlOnPreparedListener;
import com.ywl5320.listener.WlOnStopListener;
import com.ywl5320.player.R;
import com.ywl5320.player.base.BaseActivity;
import com.ywl5320.player.bean.RadioLiveChannelBean;
import com.ywl5320.player.widget.SquareImageView;
import com.ywl5320.util.WlTimeUtil;
import com.ywl5320.wlplayer.WlPlayer;
import com.ywl5320.wlplayer.WlTimeBean;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ywl on 2017-12-31.
 */

public class RadioLiveActivity extends BaseActivity{

    @BindView(R.id.siv_logo)
    SquareImageView squareImageView;
    @BindView(R.id.tv_live_name)
    TextView tvLiveName;
    @BindView(R.id.pb_load)
    ProgressBar pbLoad;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private List<RadioLiveChannelBean> datas;
    private int index = -1;
    private WlPlayer wlPlayer;
    private boolean isPlay = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_live_layout);
        setBackView();
        String data = getIntent().getExtras().getString("data");
        index = getIntent().getExtras().getInt("index");
        Gson gson = new Gson();
        datas = gson.fromJson(data, new TypeToken<List<RadioLiveChannelBean>>(){}.getType());

        wlPlayer = new WlPlayer();
        playRadio(datas.get(index));

        wlPlayer.setWlOnPreparedListener(new WlOnPreparedListener() {
            @Override
            public void onPrepared() {
                wlPlayer.start();
                Message message = Message.obtain();
                message.what = 4;
                handler.sendMessage(message);
            }
        });
        wlPlayer.setWlOnLoadListener(new WlOnLoadListener() {
            @Override
            public void onLoad(boolean load) {
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
                handler.sendMessage(message);
            }
        });

        wlPlayer.setWlOnCompleteListener(new WlOnCompleteListener() {
            @Override
            public void onComplete() {
                isPlay = false;
            }
        });

        wlPlayer.setWlOnStopListener(new WlOnStopListener() {
            @Override
            public void onStop() {
                Message message = Message.obtain();
                message.what = 3;
                handler.sendMessage(message);
            }
        });

        wlPlayer.prepared();

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1)
            {
                boolean load = (boolean) msg.obj;
                if(load)
                {
                    pbLoad.setVisibility(View.VISIBLE);
                    ivPlay.setVisibility(View.INVISIBLE);
                }
                else
                {
                    pbLoad.setVisibility(View.INVISIBLE);
                    ivPlay.setVisibility(View.VISIBLE);
                }
            }
            else if(msg.what == 2)
            {
                WlTimeBean wlTimeBean = (WlTimeBean) msg.obj;
                if(wlTimeBean.getTotal_secds() > 0)
                {
                    tvTime.setText(WlTimeUtil.secdsToDateFormat(wlTimeBean.getTotal_secds()) + "/" + WlTimeUtil.secdsToDateFormat(wlTimeBean.getCurrt_secds()));
                }
                else
                {
                    tvTime.setText(WlTimeUtil.secdsToDateFormat(wlTimeBean.getCurrt_secds()));
                }
            }
            else if(msg.what == 3)
            {
                playRadio(getPlayChannelBean());
            }
            else if(msg.what == 4)
            {
                ivPlay.setImageResource(R.mipmap.ic_play_pause);
            }
        }
    };

    @Override
    public void onClickBack() {
        super.onClickBack();
        this.finish();
        if(wlPlayer != null)
        {
            wlPlayer.stop(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        if(wlPlayer != null)
        {
            wlPlayer.stop(true);
        }
    }

    @OnClick(R.id.iv_pre)
    public void onClickPre(View view)
    {
        if(wlPlayer != null && index > 0)
        {
            index--;
            wlPlayer.stop(false);
        }
        else
        {
            showToast("已经到第一项了");
        }
    }

    @OnClick(R.id.iv_next)
    public void onClickNext(View view)
    {
        if(wlPlayer != null && index < datas.size() - 1)
        {
            index++;
            wlPlayer.stop(false);
        }
        else
        {
            showToast("已经到最后一项了");
        }
    }

    @OnClick(R.id.iv_play)
    public void onClickPlayPause(View view)
    {
        if(wlPlayer != null)
        {
            isPlay = !isPlay;
            if(isPlay)
            {
                ivPlay.setImageResource(R.mipmap.ic_play_pause);
                wlPlayer.resume();
            }
            else
            {
                ivPlay.setImageResource(R.mipmap.ic_play_play);
                wlPlayer.pause();
            }
        }
    }


    private void playRadio(RadioLiveChannelBean radioLiveChannelBean)
    {
        if(radioLiveChannelBean != null && wlPlayer != null)
        {
            try
            {
                pbLoad.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.INVISIBLE);
                wlPlayer.setDataSource(radioLiveChannelBean.getStreams().get(0).getUrl());
                wlPlayer.setOnlyMusic(true);
                tvLiveName.setText(radioLiveChannelBean.getLiveSectionName());
                setTitle(radioLiveChannelBean.getName());
                Glide.with(this).load(radioLiveChannelBean.getImg()).into(squareImageView);
                tvTime.setText("00:00:00");
                wlPlayer.prepared();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            showToast("不能再切换了");
        }
    }



    private RadioLiveChannelBean getPlayChannelBean()
    {
        if(index >= 0 && index < datas.size())
        {
            return datas.get(index);
        }
        return null;
    }

}

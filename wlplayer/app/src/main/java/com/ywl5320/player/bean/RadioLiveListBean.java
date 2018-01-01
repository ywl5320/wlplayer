package com.ywl5320.player.bean;

import com.ywl5320.player.base.BaseBean;

import java.util.List;

/**
 * Created by ywl on 2017-7-26.
 */

public class RadioLiveListBean extends BaseBean {

    private List<RadioLiveChannelBean> liveChannel;


    public List<RadioLiveChannelBean> getLiveChannel() {
        return liveChannel;
    }

    public void setLiveChannel(List<RadioLiveChannelBean> liveChannel) {
        this.liveChannel = liveChannel;
    }
}

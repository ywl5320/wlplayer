package com.ywl5320.player.bean;

import com.ywl5320.player.base.BaseBean;

/**
 * Created by ywl on 2017-7-26.
 */

public class RadioStreamsBean extends BaseBean {

    private String bitstreamType;
    private String resolution;
    private String url;

    public String getBitstreamType() {
        return bitstreamType;
    }

    public void setBitstreamType(String bitstreamType) {
        this.bitstreamType = bitstreamType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

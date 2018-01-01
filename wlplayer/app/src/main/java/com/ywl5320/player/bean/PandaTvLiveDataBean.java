package com.ywl5320.player.bean;

import com.ywl5320.player.base.BaseBean;

/**
 * Created by ywl on 2016/12/10.
 */

public class PandaTvLiveDataBean extends BaseBean {

    private Info info;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public class Info
    {
        HostInfo hostinfo;
        VideoInfo videoinfo;

        public HostInfo getHostinfo() {
            return hostinfo;
        }

        public void setHostinfo(HostInfo hostinfo) {
            this.hostinfo = hostinfo;
        }

        public VideoInfo getVideoinfo() {
            return videoinfo;
        }

        public void setVideoinfo(VideoInfo videoinfo) {
            this.videoinfo = videoinfo;
        }
    }

    public class HostInfo
    {
        String rid;
        String name;
        String avatar;
        String bamboos;

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getBamboos() {
            return bamboos;
        }

        public void setBamboos(String bamboos) {
            this.bamboos = bamboos;
        }
    }

    public class VideoInfo
    {
        String plflag;
        String room_key;
        String sign;
        String ts;

        public String getPlflag() {
            return plflag;
        }

        public void setPlflag(String plflag) {
            this.plflag = plflag;
        }

        public String getRoom_key() {
            return room_key;
        }

        public void setRoom_key(String room_key) {
            this.room_key = room_key;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTs() {
            return ts;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }
    }
}

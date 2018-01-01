package com.ywl5320.player.bean;

import com.ywl5320.player.base.BaseBean;

/**
 * Created by ywl on 2016/12/10.
 */

public class PandaTvListItemBean extends BaseBean {

    private String id;
    private String ver;
    private String createtime;
    private String updatetime;
    private String name;
    private String hostid;
    private String person_num;
    private String announcement;
    private Cfication classification;
    private Picture pictures;
    private String status;
    private String start_time;
    private String end_time;
    private String duration;
    private String schedule;
    private String remind_switch;
    private String remind_content;
    private String room_key;
    private User userinfo;

    public User getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(User userinfo) {
        this.userinfo = userinfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostid() {
        return hostid;
    }

    public void setHostid(String hostid) {
        this.hostid = hostid;
    }

    public String getPerson_num() {
        return person_num;
    }

    public void setPerson_num(String person_num) {
        this.person_num = person_num;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Cfication getClassification() {
        return classification;
    }

    public void setClassification(Cfication classification) {
        this.classification = classification;
    }

    public Picture getPictures() {
        return pictures;
    }

    public void setPictures(Picture pictures) {
        this.pictures = pictures;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRemind_switch() {
        return remind_switch;
    }

    public void setRemind_switch(String remind_switch) {
        this.remind_switch = remind_switch;
    }

    public String getRemind_content() {
        return remind_content;
    }

    public void setRemind_content(String remind_content) {
        this.remind_content = remind_content;
    }

    public String getRoom_key() {
        return room_key;
    }

    public void setRoom_key(String room_key) {
        this.room_key = room_key;
    }



    public class Cfication
    {
        String cname;
        String ename;

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;

        }

        public String getEname() {
            return ename;
        }

        public void setEname(String ename) {
            this.ename = ename;
        }
    }

    public class Picture
    {
        String img;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public class User
    {
        String nickName;
        int rid;
        String avatar;
        String userName;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getRid() {
            return rid;
        }

        public void setRid(int rid) {
            this.rid = rid;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

}

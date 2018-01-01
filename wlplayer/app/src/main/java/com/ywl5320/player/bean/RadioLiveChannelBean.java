package com.ywl5320.player.bean;

import com.ywl5320.player.base.BaseBean;

import java.util.List;

/**
 * Created by ywl on 2017-7-26.
 */

public class RadioLiveChannelBean extends BaseBean {

    private String id;
    private String name;
    private String img;
    private List<RadioStreamsBean> streams;
    private String shareUrl;
    private String liveSectionName;
    private String commentId;

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getLiveSectionName() {
        return liveSectionName;
    }

    public void setLiveSectionName(String liveSectionName) {
        this.liveSectionName = liveSectionName;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<RadioStreamsBean> getStreams() {
        return streams;
    }

    public void setStreams(List<RadioStreamsBean> streams) {
        this.streams = streams;
    }
}

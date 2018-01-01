package com.ywl5320.player.bean;

/**
 * Created by ywl on 2017-12-23.
 */

public class VideoListBean {

    private String parent;
    private String path;
    private String name;
    private boolean isFile;

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

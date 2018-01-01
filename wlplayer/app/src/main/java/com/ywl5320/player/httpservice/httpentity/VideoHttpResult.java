package com.ywl5320.player.httpservice.httpentity;

/**
 * Created by ywl on 2016/5/19.
 */
public class VideoHttpResult<T> {

    private int errno;
    private String errmsg;
    private T data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

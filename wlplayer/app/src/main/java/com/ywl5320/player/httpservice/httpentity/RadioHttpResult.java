package com.ywl5320.player.httpservice.httpentity;

/**
 * Created by ywl on 2016/5/19.
 */
public class RadioHttpResult<T> {

    private int status;
    private String message;
    private T data;
    private T liveChannel;
    private T token;

    public T getToken() {
        return token;
    }

    public void setToken(T token) {
        this.token = token;
    }

    public T getLiveChannel() {
        return liveChannel;
    }

    public void setLiveChannel(T liveChannel) {
        this.liveChannel = liveChannel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

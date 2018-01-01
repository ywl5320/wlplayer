package com.ywl5320.player.httpservice.serviceapi;



import com.ywl5320.player.bean.PandaTvDataBean;
import com.ywl5320.player.bean.PandaTvLiveDataBean;
import com.ywl5320.player.httpservice.service.VideoBaseApi;
import com.ywl5320.player.httpservice.service.HttpMethod;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by ywl on 2016/5/19.
 */
public class VideoApi extends VideoBaseApi {

    public static final String BASE_URL_PANDA = "http://api.m.panda.tv/";
    public static VideoApi videoApi;
    public VideoService videoService;
    public VideoApi()
    {
        videoService = HttpMethod.getInstance(BASE_URL_PANDA).createApi(VideoService.class);
    }

    public static VideoApi getInstance()
    {
        if(videoApi == null)
        {
            videoApi = new VideoApi();
        }
        return videoApi;
    }
    /*-------------------------------------获取的方法-------------------------------------*/

    public void getVideList(String cate,
                           int pageno,
                           int pagenum,
                           int room,
                           String version,
                           Observer<PandaTvDataBean> subscriber)
    {
        Observable observable = videoService.getVideList(cate, pageno, pagenum, room, version)
                .map(new HttpResultFunc<PandaTvDataBean>());

        toSubscribe(observable, subscriber);
    }

    public void getLiveUrl(String roomid,
                            String version,
                            Observer<PandaTvLiveDataBean> subscriber)
    {
        Observable observable = videoService.getLiveUrl(roomid, version, 1, "json", "android")
                .map(new HttpResultFunc<PandaTvLiveDataBean>());

        toSubscribe(observable, subscriber);
    }

}

package com.ywl5320.player.httpservice.serviceapi;


import com.ywl5320.player.bean.PandaTvDataBean;
import com.ywl5320.player.bean.PandaTvLiveDataBean;
import com.ywl5320.player.httpservice.httpentity.VideoHttpResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ywl on 2016/5/19.
 */
public interface VideoService {

    @GET("ajax_get_live_list_by_cate")
    Observable<VideoHttpResult<PandaTvDataBean>> getVideList(@Query("cate") String cate,
                                                             @Query("pageno") int pageno,
                                                             @Query("pagenum") int pagenum,
                                                             @Query("room") int room,
                                                             @Query("version") String version);

    @GET("ajax_get_liveroom_baseinfo")
    Observable<VideoHttpResult<PandaTvLiveDataBean>> getLiveUrl(@Query("roomid") String roomid,
                                                                @Query("__version") String version,
                                                                @Query("slaveflag") int slaveflag,
                                                                @Query("type") String type,
                                                                @Query("__plat") String __plat);
}

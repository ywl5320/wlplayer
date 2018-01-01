package com.ywl5320.player.httpservice.serviceapi;



import com.ywl5320.player.bean.RadioLiveChannelBean;
import com.ywl5320.player.bean.RadioLiveListBean;
import com.ywl5320.player.bean.RadioTokenBean;
import com.ywl5320.player.httpservice.service.RadioBaseApi;
import com.ywl5320.player.httpservice.service.HttpMethod;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by ywl on 2016/5/19.
 */
public class RadioApi extends RadioBaseApi {

    public static final String BASE_URL_PANDA = "http://pacc.radio.cn/";
    public static RadioApi radioApi;
    public RadioService radioService;
    public RadioApi()
    {
        radioService = HttpMethod.getInstance(BASE_URL_PANDA).createApi(RadioService.class);
    }

    public static RadioApi getInstance()
    {
        if(radioApi == null)
        {
            radioApi = new RadioApi();
        }
        return radioApi;
    }
    /*-------------------------------------获取的方法-------------------------------------*/

    public void getToken(Observer<Integer> subscriber)
    {
        Observable observable = radioService.getToken()
                .map(new HttpResultFunc<Integer>());

        toSubscribe(observable, subscriber);
    }

    public void getLiveByParam(String token, String channelPlaceId, int limit, int offset, Observer<List<RadioLiveChannelBean>> subscriber)
    {
        Observable observable = radioService.getLiveByParam(token, channelPlaceId, limit, offset)
                .map(new HttpResultFunc<List<RadioLiveChannelBean>>());

        toSubscribe(observable, subscriber);
    }

}

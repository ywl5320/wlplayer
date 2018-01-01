package com.ywl5320.player.httpservice.service;



import com.ywl5320.player.httpservice.httpentity.VideoHttpResult;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by ywl on 2016/5/19.
 */
public class VideoBaseApi {

    public <T> void toSubscribe(Observable<T> o, Observer<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T>   Subscriber真正需要的数据类型，也就是Data部分的数据类型
     *           Func1(I,O) 输入和输出
     */
    public class HttpResultFunc<T> implements Function<VideoHttpResult<T>, T> {

        @Override
        public T apply(VideoHttpResult<T> videoHttpResult) {
            if (videoHttpResult.getErrno() == 0) {
                return videoHttpResult.getData();
            }
            throw new ExceptionApi(videoHttpResult.getErrno(), videoHttpResult.getErrmsg());
        }
    }

}

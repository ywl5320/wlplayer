package com.ywl5320.player.httpservice.service;



import com.ywl5320.player.httpservice.httpentity.RadioHttpResult;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by ywl on 2016/5/19.
 */
public class RadioBaseApi {

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
    public class HttpResultFunc<T> implements Function<RadioHttpResult<T>, T> {

        @Override
        public T apply(RadioHttpResult<T> radioHttpResult) {
            if (radioHttpResult.getStatus() == 200) {
                if(radioHttpResult.getData() != null)
                {
                    return radioHttpResult.getData();
                }
                else if(radioHttpResult.getToken() != null)
                {
                    return radioHttpResult.getToken();
                }
                return radioHttpResult.getLiveChannel();
            }
            throw new ExceptionApi(radioHttpResult.getStatus(), radioHttpResult.getMessage());
        }
    }

}

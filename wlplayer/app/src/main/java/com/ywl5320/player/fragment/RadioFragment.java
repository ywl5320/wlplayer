package com.ywl5320.player.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ywl5320.player.R;
import com.ywl5320.player.activity.RadioLiveActivity;
import com.ywl5320.player.activity.WlMainActivity;
import com.ywl5320.player.adapter.RadioAdapter;
import com.ywl5320.player.adapter.WapHeaderAndFooterAdapter;
import com.ywl5320.player.base.BaseFragment;
import com.ywl5320.player.bean.RadioLiveChannelBean;
import com.ywl5320.player.bean.RadioLiveListBean;
import com.ywl5320.player.bean.RadioTokenBean;
import com.ywl5320.player.httpservice.serviceapi.RadioApi;
import com.ywl5320.player.httpservice.subscribers.HttpSubscriber;
import com.ywl5320.player.httpservice.subscribers.SubscriberOnListener;
import com.ywl5320.player.log.MyLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ywl on 2017-12-31.
 */

public class RadioFragment extends BaseFragment{

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.swipRefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private RadioAdapter radioAdapter;
    private WapHeaderAndFooterAdapter headerAndFooterAdapter;
    private List<RadioLiveChannelBean> datas;
    private String token;

    private TextView tvLoadMsg;

    private int pageSize = 10;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isOver = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_radio_layout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_ec4c48));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoading) {
                    isLoading = true;
                    isOver = false;
                    currentPage = 1;
                    getLiveByParam(token, "3225", pageSize, currentPage);
                }
            }
        });
        isLoading = true;
        initAdapter();
        getToken();
    }

    private void initAdapter()
    {
        datas = new ArrayList<>();
        radioAdapter = new RadioAdapter(getActivity(), datas);
        radioAdapter.setOnItemClickListener(new RadioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RadioLiveChannelBean liveChannelBean, int position) {
            }
        });
        headerAndFooterAdapter = new WapHeaderAndFooterAdapter(radioAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.footer_layout, recyclerView, false);
        tvLoadMsg = footerView.findViewById(R.id.tv_loadmsg);
        headerAndFooterAdapter.addFooter(footerView);

        recyclerView.setAdapter(headerAndFooterAdapter);
        headerAndFooterAdapter.notifyDataSetChanged();

        headerAndFooterAdapter.setOnloadMoreListener(recyclerView, new WapHeaderAndFooterAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(!isOver)
                {
                    if(!isLoading)
                    {
                        getLiveByParam(token, "3225", pageSize, currentPage);
                    }
                }
            }
        });

        radioAdapter.setOnItemClickListener(new RadioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RadioLiveChannelBean liveChannelBean, int position) {
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String data = gson.toJson(datas);
                bundle.putString("data", data);
                bundle.putInt("index", position);
                RadioLiveActivity.startActivity(getActivity(), RadioLiveActivity.class, bundle);
            }
        });

    }


    private void getToken()
    {
        RadioApi.getInstance().getToken(new HttpSubscriber<Integer>(new SubscriberOnListener<Integer>() {
            @Override
            public void onSucceed(Integer data) {
                token = data + "";
                getLiveByParam(token, "3225", 10, 1);
            }

            @Override
            public void onError(int code, String msg) {
                MyLog.d(msg);
            }
        }, getActivity()));
    }

    private void getLiveByParam(String token, String channelPlaceId, int limit, int offset)
    {
        RadioApi.getInstance().getLiveByParam(token, channelPlaceId, limit, offset, new HttpSubscriber<List<RadioLiveChannelBean>>(new SubscriberOnListener<List<RadioLiveChannelBean>>() {
            @Override
            public void onSucceed(List<RadioLiveChannelBean> data) {
                if(data != null && data.size() > 0)
                {
                    if(currentPage == 1) {
                        datas.clear();
                    }
                    if(data.size() < pageSize)
                    {
                        tvLoadMsg.setText("没有更多了");
                        isOver = true;
                    }
                    else {
                        tvLoadMsg.setText("加载更多");
                        isOver = false;
                        currentPage++;
                    }
                    if(data.size() > 0) {
                        datas.addAll(data);
                    }
                    headerAndFooterAdapter.notifyDataSetChanged();
                }
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int code, String msg) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                MyLog.d(msg);
            }
        }, getActivity()));
    }

}

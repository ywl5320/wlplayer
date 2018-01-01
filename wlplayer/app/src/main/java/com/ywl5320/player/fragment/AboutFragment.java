package com.ywl5320.player.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.ywl5320.player.R;
import com.ywl5320.player.activity.ShowImgActivity;
import com.ywl5320.player.base.BaseFragment;

import butterknife.OnClick;

/**
 * Created by ywl on 2018-1-1.
 */

public class AboutFragment extends BaseFragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about_layout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.ly_github)
    public void onClickGithub(View view)
    {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText("https://github.com/wanliyang1990/");
        Toast.makeText(getActivity(), "谢谢，已经复制 GitHub 地址!", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.ly_csdn)
    public void onClickCsdn(View view)
    {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText("http://blog.csdn.net/ywl5320");
        Toast.makeText(getActivity(), "谢谢，已经复制 CSDN 地址!", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.btn_alipay)
    public void onClickAlipay(View view)
    {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText("快来领取支付宝跨年红包！1月1日起还有机会额外获得专享红包哦！复制此消息，打开最新版支付宝就能领取！l9vaBR45d3");
        Toast.makeText(getActivity(), "谢谢您的支持!", Toast.LENGTH_LONG).show();
        try{
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        }catch(Exception e){
        }
    }

    @OnClick(R.id.btn_showimg)
    public void onClickShowImg(View view)
    {
        ShowImgActivity.startActivity(getActivity(), ShowImgActivity.class);
    }

}

package com.ywl5320.player.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.ywl5320.player.R;
import com.ywl5320.player.base.BaseActivity;
import com.ywl5320.player.util.CommonUtil;
import com.ywl5320.player.widget.SquareImageView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ywl on 2018-1-1.
 */

public class ShowImgActivity extends BaseActivity{

    @BindView(R.id.iv_bg)
    SquareImageView ivBg;
    @BindView(R.id.iv_wx_s)
    ImageView ivWxs;
    @BindView(R.id.iv_ali_s)
    ImageView ivAlis;
    private int falsg = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img_layout);
        setTitle("谢谢打赏支持");
        setBackView();
        ivAlis.setSelected(true);
        ivBg.setImageResource(R.mipmap.icon_ali);
    }

    @Override
    public void onClickBack() {
        super.onClickBack();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @OnClick(R.id.rl_ali)
    public void onClickAli(View view)
    {
        falsg = 0;
        ivAlis.setSelected(true);
        ivWxs.setSelected(false);
        ivBg.setImageResource(R.mipmap.icon_ali);
    }

    @OnClick(R.id.rl_wx)
    public void onClickWx(View view)
    {
        falsg = 1;
        ivAlis.setSelected(false);
        ivWxs.setSelected(true);
        ivBg.setImageResource(R.mipmap.icon_wx);
    }

    @OnClick(R.id.btn_save)
    public void onClickSaveImg(View view)
    {
        if(falsg == 0)
        {
            Resources res=getResources();
            Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.icon_ali);
            CommonUtil.saveImage(this, bmp);
            showToast("图片保存成功");
        }
        else
        {
            Resources res=getResources();
            Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.icon_wx);
            CommonUtil.saveImage(this, bmp);
            showToast("图片保存成功");
        }
    }

}

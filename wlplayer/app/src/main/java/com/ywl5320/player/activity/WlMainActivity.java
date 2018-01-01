package com.ywl5320.player.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ywl5320.player.R;
import com.ywl5320.player.adapter.ViewPagerAdapter;
import com.ywl5320.player.base.BaseActivity;
import com.ywl5320.player.fragment.AboutFragment;
import com.ywl5320.player.fragment.LocalFragment;
import com.ywl5320.player.fragment.RadioFragment;
import com.ywl5320.player.fragment.VideoFragment;
import com.ywl5320.player.widget.TabLayoutView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ywl on 2017-12-30.
 */

public class WlMainActivity extends BaseActivity{

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tb_nav)
    TabLayoutView tabLayoutView;

    private ViewPagerAdapter viewPagerAdapter;
    private VideoFragment videoFragment;
    private RadioFragment radioFragment;
    private LocalFragment localFragment;
    private AboutFragment aboutFragment;
    private List<Fragment> fragmentPages;
    private int currentIndex = -1;

    private String[] titles = {"视频", "广播", "本地", "关于"};
    private int[] imgs = {R.drawable.nav_video_selector, R.drawable.nav_audio_selector, R.drawable.nav_file_selector, R.drawable.nav_about_selector};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlmain_layout);
        setTitle("视频");
        initFragments();
        tabLayoutView.setDataSource(titles, imgs, 0);
        tabLayoutView.setImageStyle(24, 24);
        tabLayoutView.setTextStyle(10, R.color.color_333333,R.color.color_ec4c48);
        tabLayoutView.initDatas();
        tabLayoutView.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
            @Override
            public void onItemClick(int index) {
                viewPager.setCurrentItem(index, false);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position, false);
                tabLayoutView.setSelectStyle(position);
                currentIndex = position;
                if(position == 0)
                {
                    setTitle("视频");
                }
                else if(position == 1)
                {
                    setTitle("广播");
                }
                else if(position == 2)
                {
                    setTitle("本地");
                }
                else if(position == 3)
                {
                    setTitle("关于");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOffscreenPageLimit(fragmentPages.size());
    }

    private void initFragments()
    {
        fragmentPages = new ArrayList<>();
        videoFragment = new VideoFragment();
        radioFragment = new RadioFragment();
        localFragment = new LocalFragment();
        aboutFragment = new AboutFragment();

        fragmentPages.add(videoFragment);
        fragmentPages.add(radioFragment);
        fragmentPages.add(localFragment);
        fragmentPages.add(aboutFragment);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentPages);
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if(currentIndex == 2)
        {
            if(localFragment != null)
            {
                if(localFragment.backDir() != 0)
                {
                    finish();
                }
            }
        }
        else
        {
            finish();
        }
    }
}

package com.ywl5320.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ywl5320.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywl on 2016/11/2.
 */

public class TabLayoutView extends LinearLayout{

    private Context context;
    private String[] titles; //要显示的标题
    private int[] imgs; //图标
    private int imgwidth;
    private int imgheight;
    private int txtsize; //标题大小
    private int txtColor; //标题未选中颜色
    private int txtSelectedColor; //选择颜色

    private List<TextView> textViews; //保存标题
    private List<ImageView> imageViews; //保存图片
    private List<TextView> tvDots;//保存圆点
    private int currentIndex = 0;

    private OnItemOnclickListener onItemOnclickListener;

    public TabLayoutView(Context context) {
        this(context, null);
    }

    public TabLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setOnItemOnclickListener(OnItemOnclickListener onItemOnclickListener) {
        this.onItemOnclickListener = onItemOnclickListener;
    }

    /**
     * 设置标题、图片和当前选中的条目
     * @param tabtxts
     * @param imgs
     * @param currentIndex
     */
    public void setDataSource(String[] tabtxts, int[] imgs, int currentIndex)
    {
        this.titles = tabtxts;
        this.imgs = imgs;
        this.currentIndex = currentIndex;
    }

    /**
     * 设置图标大小
     * @param imgwidth
     * @param imgheight
     */
    public void setImageStyle(int imgwidth, int imgheight)
    {
        this.imgwidth = dip2px(context, imgwidth);
        this.imgheight = dip2px(context, imgheight);
    }

    /**
     * 设置标题颜色
     * @param txtsize
     * @param txtColor
     * @param txtSelectedColor
     */
    public void setTextStyle(int txtsize, int txtColor, int txtSelectedColor)
    {
        this.txtsize = txtsize;
        this.txtColor = txtColor;
        this.txtSelectedColor = txtSelectedColor;
    }

    /**
     * 动态布局
     * 1、外层为横向线下布局
     * 2、动态添加相对布局，平分父布局，使宽度一致，添加到横向布局中
     * 3、总线布局添加图标和标题，并添加到相对布局中
     * 4、添加圆点到相对布局中，并设置在3的右上角
     */
    public void initDatas()
    {
        textViews = new ArrayList<>();
        imageViews = new ArrayList<>();
        tvDots = new ArrayList<>();

        setOrientation(HORIZONTAL);
        LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;

        LayoutParams imglp = new LayoutParams(imgwidth, imgheight);
        imglp.gravity = Gravity.CENTER;

        LayoutParams txtlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtlp.gravity = Gravity.CENTER;

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        int size = titles.length;
        for(int i = 0; i < size; i++)
        {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(imglp);
            imageView.setImageResource(imgs[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            TextView textView = new TextView(context);
            textView.setText(titles[i]);
            textView.setLayoutParams(txtlp);
            textView.setTextSize(txtsize);

            LinearLayout cly = new LinearLayout(context);
            cly.setId(i + 100);
            cly.setGravity(Gravity.CENTER);
            cly.setOrientation(VERTICAL);
            cly.setLayoutParams(imglp);
            cly.addView(imageView);

            RelativeLayout prl = new RelativeLayout(context);

            RelativeLayout.LayoutParams rlDot = new RelativeLayout.LayoutParams(dip2px(context, 18), dip2px(context, 18));
            rlDot.addRule(RelativeLayout.RIGHT_OF, cly.getId());
            rlDot.addRule(RelativeLayout.ABOVE,cly.getId());
            rlDot.setMargins( -dip2px(context, 8), 0, 0,  -dip2px(context, 15));

            TextView tvDot = new TextView(context);
            tvDot.setText("0");
            tvDot.setTextSize(10);
            tvDot.setGravity(Gravity.CENTER);
            tvDot.setVisibility(GONE);
            tvDot.setTextColor(context.getResources().getColor(R.color.color_white));
            tvDot.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_dot_red_bg));


            final int index = i;
            prl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, titles[index], Toast.LENGTH_SHORT).show();
                    setSelectStyle(index);
                    if(onItemOnclickListener != null)
                    {
                        onItemOnclickListener.onItemClick(index);
                    }
                }
            });

            cly.addView(textView);
            prl.addView(cly, rlp);
            prl.addView(tvDot, rlDot);
            addView(prl, lp);

            textViews.add(textView);
            imageViews.add(imageView);
            tvDots.add(tvDot);
        }
        setSelectStyle(currentIndex);
    }

    public void setSelectStyle(int index)
    {
        int size = titles.length;
        for(int i = 0; i < size; i++)
        {
            if(i == index)
            {
                textViews.get(i).setTextColor(context.getResources().getColor(txtSelectedColor));
                imageViews.get(i).setSelected(true);
            }
            else
            {
                textViews.get(i).setTextColor(context.getResources().getColor(txtColor));
                imageViews.get(i).setSelected(false);
            }
        }
    }

    /**
     * 设置圆点
     * @param index 圆点索引
     * @param count 圆点个数
     */
    public void setDotsCount(int index, int count)
    {
        if(tvDots == null || index > tvDots.size() - 1)
            return;
        if(count > 0)
        {
            tvDots.get(index).setVisibility(VISIBLE);
            tvDots.get(index).setText(count + "");
        }
        else
        {
            tvDots.get(index).setVisibility(GONE);
        }
    }

    public interface OnItemOnclickListener
    {
        void onItemClick(int index);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

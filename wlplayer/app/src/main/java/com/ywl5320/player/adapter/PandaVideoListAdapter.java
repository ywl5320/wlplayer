package com.ywl5320.player.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ywl5320.player.R;
import com.ywl5320.player.bean.PandaTvListItemBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ywl on 2017-7-23.
 */

public class PandaVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<PandaTvListItemBean> datas;
    private OnItemClickListener onItemClickListener;

    public PandaVideoListAdapter(Context context, List<PandaTvListItemBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list_adapter_layout, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyHolder myHolder = (MyHolder) holder;
        if(position % 2 == 1)
        {
            myHolder.vLeft.setVisibility(View.GONE);
            myHolder.vRight.setVisibility(View.VISIBLE);
        }
        else
        {
            myHolder.vLeft.setVisibility(View.VISIBLE);
            myHolder.vRight.setVisibility(View.GONE);
        }
        myHolder.vButtom.setVisibility(View.GONE);
        if(datas.size() % 2 == 0)
        {
            if(position == datas.size() - 1 || position == datas.size() - 2)
            {
                myHolder.vButtom.setVisibility(View.VISIBLE);
            }
        }
        else if(datas.size() % 2 == 1)
        {
            if(position == datas.size() - 1)
            {
                myHolder.vButtom.setVisibility(View.VISIBLE);
            }
        }

        myHolder.tvName.setText(datas.get(position).getName());
        Glide.with(context).load(datas.get(position).getPictures().getImg()).into(myHolder.ivBg);
        myHolder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null)
                {
                    onItemClickListener.onItemClick(datas.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.v_left)
        View vLeft;
        @BindView(R.id.v_right)
        View vRight;
        @BindView(R.id.iv_img)
        ImageView ivBg;
        @BindView(R.id.v_buttom)
        View vButtom;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.rl_item)
        RelativeLayout rlItem;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(PandaTvListItemBean pandaTvListItemBean);
    }

}

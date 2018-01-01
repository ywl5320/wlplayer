package com.ywl5320.player.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;


import com.ywl5320.player.R;
import com.ywl5320.player.base.BaseDialog;

import butterknife.BindView;


/**
 * Created by ywl5320 on 2017/9/5.
 */

public class LoadingDialog extends BaseDialog {

    @BindView(R.id.tv_msg)
    TextView tvMsg;

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
    }

    public void setMsg(String msg)
    {
        if(tvMsg != null && !TextUtils.isEmpty(msg))
        {
            tvMsg.setText(msg);
        }
    }
}

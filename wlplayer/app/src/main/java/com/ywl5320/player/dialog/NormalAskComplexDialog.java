package com.ywl5320.player.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ywl5320.player.R;
import com.ywl5320.player.base.BaseDialog;

/**
 * Created by ywl on 2016/6/16.
 */
public class NormalAskComplexDialog extends BaseDialog {

    LinearLayout lyRoot;
    EditText eturl;
    Button no;
    Button yes;

    public NormalAskComplexDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_normal_ask_complex_layout);
        lyRoot = findViewById(R.id.ly_root);
        eturl = findViewById(R.id.message);
        no = findViewById(R.id.no);
        yes = findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                {
                    String url = eturl.getText().toString().trim();
                    listener.onUrl(url);
                }
                dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        lyRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        no.setText(nomsg);
        yes.setText(yesmsg);
    }
    private String titlemsg="提示";
    private String msg="提示内容";
    private String yesmsg="确定";
    private String nomsg="取消";
    private OnDalogListener listener;

    public void setDialogUtil(OnDalogListener listeners){
        listener=listeners;
    }

    public interface  OnDalogListener{
        void onUrl(String url);
    }
}

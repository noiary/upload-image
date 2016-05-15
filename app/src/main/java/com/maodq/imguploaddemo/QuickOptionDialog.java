package com.maodq.imguploaddemo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


/**
 * 拍照或选择本机相册选项弹窗dialog
 * Created by maodeqiang on 2015/9/29.
 */
public class QuickOptionDialog extends Dialog implements View.OnClickListener {

    private ImageView mClose;
    private OnQuickOptionFormClick mListener;


    public QuickOptionDialog(Context context) {
        this(context, R.style.quick_option_dialog);
    }

    public QuickOptionDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    @SuppressLint("InflateParams")
    private QuickOptionDialog(Context context, int defStyle) {
        super(context, defStyle);
        View dialog_quick_option = getLayoutInflater().inflate(R.layout.dialog_quick_option, null);

        dialog_quick_option.findViewById(R.id.tv_photograph).setOnClickListener(this);
        dialog_quick_option.findViewById(R.id.tv_select).setOnClickListener(this);
        dialog_quick_option.findViewById(R.id.tv_cancel).setOnClickListener(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_quick_option.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                QuickOptionDialog.this.dismiss();
                return true;
            }
        });
        super.setContentView(dialog_quick_option);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager wm = getWindow().getWindowManager();
        Display defaultDisplay = wm.getDefaultDisplay();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = defaultDisplay.getWidth();
        attributes.alpha = 0.95f;
        getWindow().setAttributes(attributes);
    }

    public void setOnQuickOptionformClickListener(OnQuickOptionFormClick lis) {
        mListener = lis;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onQuickOptionClick(v);
        }
        dismiss();
    }

    public interface OnQuickOptionFormClick {
        void onQuickOptionClick(View v);
    }
}

package com.livingspaces.proshopper.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.views.LSTextView;

/**
 * Created by alexeyredchets on 2017-08-22.
 */

public class DialogFrag extends DialogFragment {

    private static final String TAG = DialogFrag.class.getSimpleName();

    private LSTextView tv_header, tv_text, tv_button;
    private View pd_loading;
    private ICallback callback;
    private String choice;

    public DialogFrag newInstance(){
        return new DialogFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        choice = getArguments().getString("case");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.dialog_frag, container, false);

        Window window = getDialog().getWindow();

        if (window != null){
            window.setGravity(Gravity.TOP);
            WindowManager.LayoutParams param = window.getAttributes();
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            param.x = 0;
            param.y = 200;
            window.setAttributes(param);
        }

        tv_header = (LSTextView)view.findViewById(R.id.tv_header_dialog_frag);
        tv_text = (LSTextView)view.findViewById(R.id.tv_text_dialog_frag);
        tv_button = (LSTextView)view.findViewById(R.id.tv_button_dialog_frag);
        pd_loading = view.findViewById(R.id.pBar_dialog_frag);
        pd_loading.setVisibility(View.GONE);

        Typeface fontLight = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Light.ttf");
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Bold.otf");
        tv_header.setTypeface(fontBold);
        tv_text.setTypeface(fontLight);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //getDialog().getWindow().setLayout(680, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated");

        if (callback != null) callback.created();

        tv_button.setOnClickListener(view1 -> {
            if (callback != null) callback.onOk();
        });
    }

    public void setCont(
    ){
        switch (choice) {
            case "empty":
                Log.d(TAG, choice);
                tv_header.setText(R.string.form_error_head);
                tv_text.setText(R.string.form_error_txt);
                break;
            case "emptyField":
                Log.d(TAG, choice);
                tv_header.setText(R.string.form_error_head);
                tv_text.setText(R.string.form_empty_error_txt);
                break;
            case "notValid":
                Log.d(TAG, choice);
                tv_header.setText(R.string.inv_email_head);
                tv_text.setText(R.string.inv_email_txt);
                break;
            case "smallPass":
                Log.d(TAG, choice);
                tv_header.setText(R.string.pass_error_head);
                tv_text.setText(R.string.pass_error_txt);
                break;
            case "createFailed":
                Log.d(TAG, choice);
                tv_header.setText(R.string.log_fail_head);
                tv_text.setText(R.string.log_fail_txt);
                break;
            case "ok":
                Log.d(TAG, choice);
                tv_header.setText(R.string.log_success_head);
                tv_text.setText(R.string.log_success_txt);
                break;
            case "notMatch":
                Log.d(TAG, choice);
                tv_header.setText(R.string.pass_match_error_head);
                tv_text.setText(R.string.pass_match_error_txt);
                break;
            case "createSuccess":
                Log.d(TAG, choice);
                tv_header.setText(R.string.account_head);
                tv_text.setText(R.string.account_txt);
                break;
            case "emailSent":
                Log.d(TAG, choice);
                tv_header.setText(R.string.email_sent_head);
                tv_text.setText(R.string.email_sent_txt);
                break;
            case "invalidEmail":
                Log.d(TAG, choice);
                tv_header.setText(R.string.email_not_system_head);
                tv_text.setText(R.string.email_not_system_txt);
                break;
            case "loading":
                Log.d(TAG, choice);
                tv_header.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Light.ttf"));
                tv_header.setAllCaps(false);
                tv_header.setText(R.string.prog_bar_loading);
                tv_text.setVisibility(View.INVISIBLE);
                pd_loading.setVisibility(View.VISIBLE);
                tv_button.setText("Cancel");
                break;
            case "noNetwork":
                Log.d(TAG, choice);
                tv_header.setText(R.string.no_network_head);
                tv_text.setText(R.string.no_network_txt);
                break;
        }
    }

    public void setCallback(ICallback cb) {
        callback = cb;
    }

    public interface ICallback{
        void onOk();
        void created();
    }
}

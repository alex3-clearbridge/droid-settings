package com.livingspaces.proshopper.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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

    private LSTextView tv_header, tv_text, tv_button;

    public DialogFrag newInstance(){
        return new DialogFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_frag, container, false);

        getDialog().getWindow().setGravity(Gravity.TOP | Gravity.TOP);
        WindowManager.LayoutParams param = getDialog().getWindow().getAttributes();
        param.width = ViewGroup.LayoutParams.MATCH_PARENT;
        param.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        param.x = 0;
        param.y = 200;

        getDialog().getWindow().setAttributes(param);

        getDialog().getWindow()
                .getAttributes()
                .windowAnimations = R.style.DialogAnimation;

        tv_header = (LSTextView)view.findViewById(R.id.tv_header_dialog_frag);
        tv_text = (LSTextView)view.findViewById(R.id.tv_text_dialog_frag);
        tv_button = (LSTextView)view.findViewById(R.id.tv_button_dialog_frag);

        Typeface fontLight = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Light.ttf");
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Bold.otf");
        tv_header.setTypeface(fontBold);
        tv_text.setTypeface(fontLight);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_header.setText("Header");
        tv_text.setText("Stack Overflow is a community of 7.6 million programmers, just like you, helping each other. Join them; it only takes a minute:");
        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}

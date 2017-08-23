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
            window.setGravity(Gravity.TOP | Gravity.TOP);
            WindowManager.LayoutParams param = window.getAttributes();
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            param.width = ViewGroup.LayoutParams.MATCH_PARENT;
            param.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
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

        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) callback.onOk();
            }
        });
    }

    public void setCont(){
        switch (choice) {
            case "empty":
                Log.d(TAG, choice);
                tv_header.setText("Error");
                tv_text.setText("All fields are required.");
                break;
            case "notValid":
                Log.d(TAG, choice);
                tv_header.setText("Invalid email");
                tv_text.setText("Email is not valid. Try again.");
                break;
            case "ok":
                Log.d(TAG, choice);
                tv_header.setText("Success");
                tv_text.setText("You successfully logged in.");
                break;
            case "notMatch":
                Log.d(TAG, choice);
                tv_header.setText("Error");
                tv_text.setText("Passwords do not match.");
                break;
            case "smallPass":
                Log.d(TAG, choice);
                tv_header.setText("Error");
                tv_text.setText("Passwords cannot be less than 6 characters.");
                break;
            case "createFailed":
                Log.d(TAG, choice);
                tv_header.setText("Error");
                tv_text.setText("Server error or email is already taken.");
                break;
            case "createSuccess":
                Log.d(TAG, choice);
                tv_header.setText("Success");
                tv_text.setText("Account was created successfully.");
                break;
            case "emailSent":
                Log.d(TAG, choice);
                tv_header.setText("Email sent");
                tv_text.setText("We have sent an email with instructions on how to reset your password.");
                break;
            case "invalidEmail":
                Log.d(TAG, choice);
                tv_header.setText("INVALID EMAIL");
                tv_text.setText("Your email does not exist in our system, try again.");
                break;
            case "loading":
                Log.d(TAG, choice);
                tv_header.setText("Working on your request");
                tv_text.setVisibility(View.INVISIBLE);
                pd_loading.setVisibility(View.VISIBLE);
                tv_button.setText("Cancel");
                break;
            default:
                Log.d(TAG, choice);
                tv_header.setText("INVALID EMAIL");
                tv_text.setText("Your email does not exist in our system, try again.");
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

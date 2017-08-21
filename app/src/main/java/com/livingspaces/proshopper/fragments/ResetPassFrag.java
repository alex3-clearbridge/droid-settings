package com.livingspaces.proshopper.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.views.LSTextView;
import com.livingspaces.proshopper.views.LoginDialog;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class ResetPassFrag extends BaseStackFrag implements LoginDialog.ICallback{

    private static final String TAG = ResetPassFrag.class.getSimpleName();

    private EditText ed_email;
    private LSTextView tv_send;
    private LoginDialog mLoginDialog;
    private View overlay;
    private boolean isResetSuccess = false, isLoading = false;

    public static ResetPassFrag newInstance(){
        return new ResetPassFrag();
    }

    public ResetPassFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        ed_email = (EditText)view.findViewById(R.id.ed_forgotPass_email);
        tv_send = (LSTextView)view.findViewById(R.id.tv_forgotPass_button);
        mLoginDialog = (LoginDialog)view.findViewById(R.id.dialog_reset_pass);
        mLoginDialog.setVisibility(View.GONE);
        mLoginDialog.setCallback(this);
        overlay = view.findViewById(R.id.shade_reset_pass);
        overlay.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(ed_email)){
                    overlay(true);
                    mLoginDialog.show("empty");
                }
                else if (!isValidEmail(ed_email)){
                    overlay(true);
                    mLoginDialog.show("notValid");
                }
                else{
                    onResetCalled(ed_email.getText().toString());
                }
            }
        });
    }

    private void onResetCalled(String email){
        if (isLoading) return;

        overlay(true);
        mLoginDialog.show(true);
        isLoading = true;

        NetworkManager.makePostREQ(email, new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {
                Log.d(TAG, "onRSPSuccess");

                Log.d(TAG, "RESPONSE :: " + rsp);

                if (rsp.contains("Your email containing instructions to reset your password has been sent!")){
                    isLoading = false;
                    isResetSuccess = true;
                    mLoginDialog.hide();
                    mLoginDialog.show("emailSent");
                }
                else onRSPFail();
            }

            @Override
            public void onRSPFail() {
                Log.d(TAG, "onRSPFail");
                mLoginDialog.hide();
                mLoginDialog.show("invalidEmail");
            }

            @Override
            public String getURL() {
                return Services.API.ResetPassword.get();
            }
        });
    }

    @Override
    public String getTitle() {
        return "Forgot Password";
    }

    public void overlay(final boolean show) {
        if (overlay == null || (show && isViewShowing(overlay)) || (!show && !isViewShowing(overlay)) )
            return;
        Log.d(TAG, "OVERLAY");

        if (show) overlay.setVisibility(View.INVISIBLE);
        overlay.animate().setDuration(500).alpha(show ? 1 : 0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (show) overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) overlay.setVisibility(View.GONE);
            }
        }).start();
    }

    private boolean isViewShowing(View view) {
        return view.getVisibility() != View.GONE;
    }

    private boolean isEmpty(EditText ed){
        return ed.getText().length() == 0;
    }

    private boolean isValidEmail(EditText ed){
        String s = ed.getText().toString();
        return !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    @Override
    public void onOk() {
        overlay(false);
        mLoginDialog.hide();

        if (isResetSuccess) {
            isResetSuccess = false;
            Global.FragManager.popToHome();
        }
    }

    @Override
    public boolean handleBackPress() {
        Log.d(TAG, "handleBackPress");
        if (mLoginDialog != null && isViewShowing(mLoginDialog)){
            Log.d(TAG, "close dialog on back press");
            onOk();
            return true;
        }
        else return super.handleBackPress();

    }
}

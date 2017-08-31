package com.livingspaces.proshopper.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.networking.response.MessageResponse;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.views.LSTextView;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class ResetPassFrag extends BaseStackFrag implements DialogFrag.ICallback{

    private static final String TAG = ResetPassFrag.class.getSimpleName();

    private DialogFrag mDialogFrag;
    private EditText ed_email;
    private LSTextView tv_send;
    private Bundle args;
    private boolean isResetSuccess = false, isLoading = false;
    private boolean isDialogShowing = false;

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

        mDialogFrag = new DialogFrag();
        mDialogFrag.setCallback(this);
        args = new Bundle();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_send.setOnClickListener(view1 -> {
            if (!isConnectedToNetwork()){
                showDialog("noNetwork");
                return;
            }
            if (isEmpty(ed_email)){
                showDialog("empty");
            }
            else if (!isValidEmail(ed_email)){
                showDialog("notValid");
            }
            else{
                onResetCalled();
            }
        });
    }

    private void onResetCalled(){

        String email = ed_email.getText().toString();

        if (isLoading) return;

        showDialog("loading");
        isLoading = true;

        new Handler().postDelayed(() -> {
            Network.makeResetPassREQ(email, new IRequestCallback.Message() {
                @Override
                public void onSuccess(MessageResponse response) {
                    Log.d(TAG, "onRSPSuccess");

                    Log.d(TAG, "RESPONSE :: " + response.getMessage());

                    if (response.getMessage().equals("Your email containing instructions to reset your password has been sent!")){
                        onOk();
                        isLoading = false;
                        isResetSuccess = true;
                        new Handler().postDelayed(() -> {
                            showDialog("emailSent");
                        }, 500);
                    }
                    else onFailure("null message");
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onRSPFail " + message);
                    onOk();
                    isLoading = false;
                    new Handler().postDelayed(() -> {
                        showDialog("invalidEmail");
                    }, 500);
                }
            });


            }, 1000);
        /*new Handler().postDelayed(() -> {
            NetworkManager.makeResetPassREQ(email, new IREQCallback() {
                @Override
                public void onRSPSuccess(String rsp) {
                    Log.d(TAG, "onRSPSuccess");

                    Log.d(TAG, "RESPONSE :: " + rsp);

                    if (rsp.contains("Your email containing instructions to reset your password has been sent!")){
                        onOk();
                        isLoading = false;
                        isResetSuccess = true;
                        new Handler().postDelayed(() -> {
                            showDialog("emailSent");
                        }, 500);
                    }
                    else onRSPFail();
                }

                @Override
                public void onRSPFail() {
                    Log.d(TAG, "onRSPFail");
                    onOk();
                    isLoading = false;
                    new Handler().postDelayed(() -> {
                        showDialog("invalidEmail");
                    }, 500);
                }

                @Override
                public String getURL() {
                    return Services.API.ResetPassword.get();
                }
            });
        }, 1000);*/
    }

    @Override
    public String getTitle() {
        return "Forgot Password";
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

    private void showDialog(String choice){
        isDialogShowing = true;
        args.putString("case", choice);
        mDialogFrag.setArguments(args);
        mDialogFrag.show(getFragmentManager(), "dialogFragment");
    }

    @Override
    public void onOk() {
        if (mDialogFrag != null) {
            isDialogShowing = false;
            mDialogFrag.dismiss();
        }

        if (isResetSuccess) {
            isResetSuccess = false;
            Global.FragManager.popToHome();
        }
    }

    private boolean isConnectedToNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected();
    }

    @Override
    public void created() {
        mDialogFrag.setCont();
    }

    @Override
    public boolean handleBackPress() {
        Log.d(TAG, "handleBackPress");
        if (mDialogFrag != null && isDialogShowing){
            Log.d(TAG, "close dialog on back press");
            onOk();
            return true;
        }
        else return super.handleBackPress();
    }
}

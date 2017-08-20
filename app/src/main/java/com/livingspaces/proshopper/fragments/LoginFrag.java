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

import com.livingspaces.proshopper.LoginUtility;
import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.Token;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.views.LSTextView;
import com.livingspaces.proshopper.views.LoginDialog;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public class LoginFrag extends BaseStackFrag implements LoginDialog.ICallback {

    private static final String TAG = LoginFrag.class.getSimpleName();

    private LoginDialog mLoginDialog;
    private LSTextView tv_login, tv_createAccount, tv_forgotPass;
    private EditText ed_login, ed_password;
    private View overlay;

    public static LoginFrag newInstance(){
        return new LoginFrag();
    }

    public LoginFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginDialog = (LoginDialog)view.findViewById(R.id.dialog_login);
        mLoginDialog.setVisibility(View.GONE);
        mLoginDialog.setCallback(this);
        ed_login = (EditText)view.findViewById(R.id.ed_login_email);
        ed_password = (EditText)view.findViewById(R.id.ed_login_password);
        tv_login = (LSTextView)view.findViewById(R.id.tv_login);
        tv_createAccount = (LSTextView)view.findViewById(R.id.tv_login_create);
        tv_forgotPass = (LSTextView)view.findViewById(R.id.tv_login_forgotPassowrd);
        overlay = view.findViewById(R.id.shade_login);
        overlay.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Login button Clicked");

                if (isEmpty(ed_login) || isEmpty(ed_password)){
                    //Toast.makeText(getContext(), "Login and password could not be empty", Toast.LENGTH_SHORT).show();
                    overlay(true);
                    mLoginDialog.show("empty");
                    return;
                }
                if (!isValidEmail(ed_login)){
                    //Toast.makeText(getContext(), "Email is not valid", Toast.LENGTH_SHORT).show();
                    overlay(true);
                    mLoginDialog.show("notValid");
                    return;
                }
                onLoginClicked(ed_login.getText().toString(), ed_password.getText().toString());
            }
        });

        tv_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "createAccount button Clicked");
                Global.FragManager.stackFrag(CreateAccountFrag.newInstance());
            }
        });

        tv_forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Forgot Pass was clicked");
                Global.FragManager.stackFrag(ResetPassFrag.newInstance());
            }
        });
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

    @Override
    public String getTitle() {
        return "Login";
    }

    private void onLoginClicked(String name, String pass){


        NetworkManager.makePostREQ(name, pass, new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {
                Log.d(TAG, "onRSPSuccess");

                if (rsp.contains("access_token")){
                    Token token = new Token(rsp);
                    Global.Prefs.editToken(token.token);
                    overlay(true);
                    mLoginDialog.show("ok");
                    Global.FragManager.onBackPressed();
                }
                else {
                    overlay(true);
                    mLoginDialog.show("notInSystem");
                }
            }

            @Override
            public void onRSPFail() {
                Log.d(TAG, "onRSPFail");
                //Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                overlay(true);
                mLoginDialog.show("notInSystem");
            }

            @Override
            public String getURL() {
                return Services.API.Token.get();            }
        });
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

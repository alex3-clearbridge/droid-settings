package com.livingspaces.proshopper.fragments;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.Token;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.views.LSTextView;
import com.livingspaces.proshopper.views.LoginDialog;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public class CreateAccountFrag extends BaseStackFrag implements LoginDialog.ICallback{

    private static final String TAG = CreateAccountFrag.class.getSimpleName();

    private EditText ed_firstName, ed_lastName, ed_email, ed_pass, ed_confirmPass;
    private LSTextView tv_terms, tv_createBtn;
    private LoginDialog mLoginDialog;
    private View overlay;

    public static CreateAccountFrag newInstance(){
        return new CreateAccountFrag();
    }

    public CreateAccountFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view =  inflater.inflate(R.layout.fragment_create_account, container, false);

        mLoginDialog = (LoginDialog)view.findViewById(R.id.dialog_create_account);
        mLoginDialog.setVisibility(View.GONE);
        mLoginDialog.setCallback(this);
        ed_firstName = (EditText)view.findViewById(R.id.ed_first_name);
        ed_lastName = (EditText)view.findViewById(R.id.ed_last_name);
        ed_email = (EditText)view.findViewById(R.id.ed_create_email);
        ed_pass = (EditText)view.findViewById(R.id.ed_create_password);
        ed_confirmPass = (EditText)view.findViewById(R.id.ed_create_confirm_password);
        tv_terms = (LSTextView)view.findViewById(R.id.tv_privacy_terms);
        tv_createBtn = (LSTextView)view.findViewById(R.id.tv_create_account);
        overlay = view.findViewById(R.id.shade_create_account);
        overlay.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTerms();

        tv_createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Create account clicked");

                if (isEmpty(ed_firstName)
                        || isEmpty(ed_lastName)
                        || isEmpty(ed_email)
                        || isEmpty(ed_pass)
                        || isEmpty(ed_confirmPass)){
                    overlay(true);
                    mLoginDialog.show("empty");
                }
                else if (!isValidEmail(ed_email)){
                    overlay(true);
                    mLoginDialog.show("notValid");
                }
                else if (!match(ed_pass, ed_confirmPass)){
                    overlay(true);
                    mLoginDialog.show("notMatch");
                }

                else {
                    createAccountCall(ed_firstName.getText().toString(),
                            ed_lastName.getText().toString(),
                            ed_email.getText().toString(),
                            ed_pass.getText().toString(),
                            ed_confirmPass.getText().toString());
                }

            }
        });
    }

    private void createAccountCall(String fname,
                                   String lname,
                                   String email,
                                   String pass,
                                   String confPass){

        NetworkManager.makePostREQ(fname, lname, email, pass, confPass, new IREQCallback() {
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
                return Services.API.CreateAccount.get();
            }
        });
    }

    private boolean isFieldsValid(){
        if (isEmpty(ed_firstName)
                || isEmpty(ed_lastName)
                || isEmpty(ed_email)
                || isEmpty(ed_pass)
                || isEmpty(ed_confirmPass)){
            overlay(true);
            mLoginDialog.show("empty");
            return false;
        }
        if (!isValidEmail(ed_email)){
            overlay(true);
            mLoginDialog.show("notValid");
            return false;
        }
        if (!match(ed_pass, ed_confirmPass)){
            overlay(true);
            mLoginDialog.show("notMatch");
            return false;
        }
        return true;
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


    private void onPolicyClicked(String choice){

        if (choice.equals("policy")) Global.FragManager.stackFrag(WebViewFrag.newInstance("Policies", Services.URL.Policy.get()));
        else Global.FragManager.stackFrag(WebViewFrag.newInstance("Terms", Services.URL.Terms.get()));
    }

    private void setupTerms(){
        SpannableString ss = new SpannableString("By clicking “Create Account”, I agree to the Privacy Policy and Terms of Use.");
        ClickableSpan policySpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                onPolicyClicked("policy");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                onPolicyClicked("terms");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ForegroundColorSpan policyColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.TextView_Link_color));
        ForegroundColorSpan termsColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.TextView_Link_color));

        ss.setSpan(policySpan, 44, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(termsSpan, 64, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(policyColorSpan, 44, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(termsColorSpan, 64, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_terms.setText(ss);
        tv_terms.setMovementMethod(LinkMovementMethod.getInstance());
        tv_terms.setHighlightColor(Color.TRANSPARENT);
    }

    private boolean isEmpty(EditText ed){
        return ed.getText().length() == 0;
    }

    private boolean isValidEmail(EditText ed){
        String s = ed.getText().toString();
        return !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    private boolean match(EditText pass, EditText confPass){
        return pass.getText().toString().equals(confPass.getText().toString());
    }

    @Override
    public String getTitle() {
        return "Create an Account";
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

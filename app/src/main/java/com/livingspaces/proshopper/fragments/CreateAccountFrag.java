package com.livingspaces.proshopper.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.Token;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.views.LSTextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public class CreateAccountFrag extends BaseStackFrag implements DialogFrag.ICallback{

    private static final String TAG = CreateAccountFrag.class.getSimpleName();

    private EditText ed_firstName, ed_lastName, ed_email, ed_pass, ed_confirmPass;
    private LSTextView tv_terms, tv_createBtn;
    private DialogFrag mDialogFrag;
    private Bundle args;
    private boolean isDialogShowing = false;
    private boolean isCreatedAndLogged = false, isLoading = false;

    private Date firstTime, secondTime;

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

        ed_firstName = (EditText)view.findViewById(R.id.ed_first_name);
        ed_lastName = (EditText)view.findViewById(R.id.ed_last_name);
        ed_email = (EditText)view.findViewById(R.id.ed_create_email);
        ed_pass = (EditText)view.findViewById(R.id.ed_create_password);
        ed_confirmPass = (EditText)view.findViewById(R.id.ed_create_confirm_password);
        tv_terms = (LSTextView)view.findViewById(R.id.tv_privacy_terms);
        tv_createBtn = (LSTextView)view.findViewById(R.id.tv_create_account);

        mDialogFrag = new DialogFrag();
        mDialogFrag.setCallback(this);
        args = new Bundle();

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
                    showDialog("empty");
                }
                else if (!isValidEmail(ed_email)){
                    showDialog("notValid");
                }
                else if (!isPass(ed_pass)){
                    showDialog("smallPass");
                }
                else if (!match(ed_pass, ed_confirmPass)){
                    showDialog("notMatch");
                }

                else {
                    createAccountCall();
                }
            }
        });
    }

    private void createAccountCall(){

        String fname = ed_firstName.getText().toString();
        String lname = ed_lastName.getText().toString();
        String email = ed_email.getText().toString();
        String pass = ed_pass.getText().toString();
        String confPass = ed_confirmPass.getText().toString();

        if (isLoading) return;

        isLoading = true;
        showDialog("loading");

        NetworkManager.makePostREQ(fname, lname, email, pass, confPass, new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {
                Log.d(TAG, "onRSPSuccess");

                Log.d(TAG, "RESPONSE :: " + rsp);

                if (rsp.contains("User account Created Successfully")){
                   tokenRequest();
                }
                else onRSPFail();
            }

            @Override
            public void onRSPFail() {
                Log.d(TAG, "onRSPFail");
                onOk();
                showDialog("createFailed");
            }

            @Override
            public String getURL() {
                return Services.API.CreateAccount.get();
            }
        });
    }

    private void tokenRequest(){
        NetworkManager.makePostREQ(ed_email.getText().toString(), ed_pass.getText().toString(), new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {
                Log.d(TAG, "onRSPSuccess");

                onOk();
                if (rsp.contains("access_token")){
                    Token token = new Token(rsp);
                    Global.Prefs.editToken(token.access_token);
                    isCreatedAndLogged = true;
                    showDialog("createSuccess");
                }
                else {
                    showDialog("createFailed");
                }
            }

            @Override
            public void onRSPFail() {
                Log.d(TAG, "onRSPFail");
                onOk();
                showDialog("notInSystem");
            }

            @Override
            public String getURL() {
                return Services.API.Token.get();
            }
        });
    }

    private void showDialog(String choice){
        isDialogShowing = true;
        args.putString("case", choice);
        mDialogFrag.setArguments(args);
        mDialogFrag.show(getFragmentManager(), "dialogFragment");

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

    private boolean isPass(EditText ed){
        return ed.getText().toString().length() >= 6;
    }

    @Override
    public String getTitle() {
        return "Create an Account";
    }

    @Override
    public void onOk() {
        Log.d(TAG, "onOk clicked");

        if (mDialogFrag != null) {
            isDialogShowing = false;
            mDialogFrag.dismiss();
            if (isLoading) isLoading = false;
        }

        if (isCreatedAndLogged) {
            isCreatedAndLogged = false;
            Global.FragManager.popToHome();
        }
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

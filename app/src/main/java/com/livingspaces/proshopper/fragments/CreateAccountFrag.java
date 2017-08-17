package com.livingspaces.proshopper.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.views.LSTextView;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public class CreateAccountFrag extends BaseLoginFrag{

    private static final String TAG = CreateAccountFrag.class.getSimpleName();

    private EditText ed_firstName, ed_lastName, ed_email, ed_pass, ed_confirmPass;
    private LSTextView tv_terms, tv_createBtn;

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

                Toast.makeText(getContext(), "Create account clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void onPolicyClicked(){
        NetworkManager.makeREQ(new IREQCallback() {

            @Override
            public void onRSPSuccess(String rsp) {

            }

            @Override
            public void onRSPFail() {

            }

            @Override
            public String getURL() {
                return Services.URL.Website + "privacy-policy";
            }
        });
    }

    private void setupTerms(){
        SpannableString ss = new SpannableString("By clicking “Create Account”, I agree to the Privacy Policy and Terms of Use.");
        ClickableSpan policySpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Link to policy", Toast.LENGTH_SHORT).show();
                //onPolicyClicked();
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
                Toast.makeText(getContext(), "Link to terms", Toast.LENGTH_SHORT).show();
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

    @Override
    public String getTitle() {
        return "Create an Account";
    }
}

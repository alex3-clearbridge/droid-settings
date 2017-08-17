package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.views.LSTextView;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class ResetPassFrag extends BaseLoginFrag {

    private static final String TAG = ResetPassFrag.class.getSimpleName();

    private EditText ed_email;
    private LSTextView tv_send;

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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Send clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public String getTitle() {
        return "Forgot Password";
    }
}

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
import com.livingspaces.proshopper.data.response.CustomerInfoResponse;
import com.livingspaces.proshopper.data.response.Product;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.data.response.LoginResponse;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.views.LSTextView;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public class LoginFrag extends BaseStackFrag implements DialogFrag.ICallback {

    private static final String TAG = LoginFrag.class.getSimpleName();

    private DialogFrag mDialogFrag;
    private LSTextView tv_login, tv_createAccount, tv_forgotPass;
    private EditText ed_login, ed_password;
    private boolean isLoading = false, isLogged = false, isDialogShowing = false, isForCart = false;
    private Bundle args;
    private String currentSku;

    public static LoginFrag newInstance(boolean forCart, String sku) {
        LoginFrag lf = new LoginFrag();
        lf.isForCart = forCart;
        lf.currentSku = sku;
        return lf;
    }

    public LoginFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ed_login = (EditText) view.findViewById(R.id.ed_login_email);
        ed_password = (EditText) view.findViewById(R.id.ed_login_password);
        tv_login = (LSTextView) view.findViewById(R.id.tv_login);
        tv_createAccount = (LSTextView) view.findViewById(R.id.tv_login_create);
        tv_forgotPass = (LSTextView) view.findViewById(R.id.tv_login_forgotPassowrd);

        mDialogFrag = new DialogFrag();
        mDialogFrag.setCallback(this);
        args = new Bundle();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_login.setOnClickListener(view1 -> {
            Log.d(TAG, "LoginResponse button Clicked");

            if (!isConnectedToNetwork()){
                showDialog("noNetwork");
                return;
            }
            if (isEmpty(ed_login) || isEmpty(ed_password)) {
                showDialog("empty");
                return;
            }
            if (!isValidEmail(ed_login)) {
                showDialog("notValid");
                return;
            } else if (!isPass(ed_password)) {
                showDialog("smallPass");
                return;
            }
            onLoginClicked(ed_login.getText().toString(), ed_password.getText().toString());
        });

        tv_createAccount.setOnClickListener(view12 -> {
            Log.d(TAG, "createAccount button Clicked");
            Global.FragManager.stackFrag(CreateAccountFrag.newInstance());
        });

        tv_forgotPass.setOnClickListener(view13 -> {
            Log.d(TAG, "Forgot Pass was clicked");
            Global.FragManager.stackFrag(ResetPassFrag.newInstance());
        });
    }

    private void onLoginClicked(String name, String pass) {

        if (isLoading) return;

        showDialog("loading");
        isLoading = true;

        new Handler().postDelayed(() -> {
            Network.makeLoginREQ(name, pass, new IRequestCallback.Login() {
                @Override
                public void onSuccess(LoginResponse response) {
                    Log.d(TAG, "onRSPSuccess");

                    if (response.getAccess_token() != null
                            && response.getRefresh_token() != null
                            && response.getUser_name() != null) {
                        onOk();
                        isLoading = false;
                        Global.Prefs.editToken(response.getAccess_token(),
                                response.getRefresh_token(),
                                response.getUser_name());
                        isLogged = true;
                        getCustomerInfo();
                        isDialogShowing = false;
                        if (isForCart) {
                            isForCart = false;
                            Product item = new Product();
                            item.setSku(currentSku);
                            Global.FragManager.popToHome();
                            Global.FragManager.stackFrag(WebViewFrag.newInstance(Services.URL.Product.get()).withProduct(item));
                        }
                        else Global.FragManager.popToHome();
                    } else {
                        onFailure("null message");
                    }
                }

                @Override
                public void onFailure(String message) {
                    onOk();
                    isLoading = false;
                    new Handler().postDelayed(() -> showDialog("createFailed"), 500);
                    Log.d(TAG, "onRSPFail :: " + message);
                }
            });
        }, 1000);
    }

    private void getCustomerInfo(){
        Network.makeGetInfoREQ(new IRequestCallback.Customer() {
            @Override
            public void onSuccess(CustomerInfoResponse response) {
                if (response.getShippingAddress().getZipCode() != null && !response.getShippingAddress().getZipCode().isEmpty()){
                    Global.Prefs.saveUserZip(response.getShippingAddress().getZipCode());
                }
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private boolean isEmpty(EditText ed) {
        return ed.getText().length() == 0;
    }

    private boolean isValidEmail(EditText ed){
        String s = ed.getText().toString();
        return !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    private boolean isPass(EditText ed){
        return ed.getText().toString().length() >= 6;
    }

    private void showDialog(String choice){
        isDialogShowing = true;
        args.putString("case", choice);
        mDialogFrag.setArguments(args);
        mDialogFrag.show(getFragmentManager(), "dialogFragment");
    }

    @Override
    public String getTitle() {
        return "Login";
    }

    @Override
    public void onOk() {
        if (mDialogFrag != null) {
            isDialogShowing = false;
            mDialogFrag.dismiss();
        }

        if (isLogged) {
            isLogged = false;
            Global.FragManager.onBackPressed();
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
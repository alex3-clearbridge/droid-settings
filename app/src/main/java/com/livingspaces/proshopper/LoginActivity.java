package com.livingspaces.proshopper;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.livingspaces.proshopper.fragments.BaseLoginFrag;
import com.livingspaces.proshopper.fragments.BaseStackFrag;
import com.livingspaces.proshopper.fragments.LoginFrag;
import com.livingspaces.proshopper.interfaces.ILogin;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.views.ActionBarLogin;

import java.util.Stack;

public class LoginActivity extends AppCompatActivity implements ILogin{

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActionBarLogin mActionBar;
    private Stack<BaseLoginFrag> mStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginUtility.Init(this);
        NetworkManager.Init(this);

        mStack = new Stack<>();
        mActionBar = (ActionBarLogin)findViewById(R.id.action_bar_login);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_login, LoginFrag.newInstance())
                .commit();
    }

    @Override
    public FragmentManager getFragMan() {
        Log.d(TAG, "getFragMan");
        return getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        //if (!mStack.isEmpty() && mStack.peek().handleBackPress()) return;

        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {

            Log.d(TAG, "hasFrag in stack");

            popFrag(false);
        }
        else {
            Log.d(TAG, "Has not frag in stag");
            onCloseClicked();
        }

    }

    @Override
    public void refreshActionBar() {
        Log.d(TAG, "refreshActionBar");
        if (!mStack.isEmpty()) mActionBar.update(mStack.peek());
    }

    @Override
    public void startFrag(BaseLoginFrag frag) {
        Log.d(TAG, "startFrag");
        popToHome();
        stackFrag(frag);
    }

    @Override
    public void stackFrag(final BaseLoginFrag frag) {
        Log.d(TAG, "stackFrag");
        if (mStack.size() > 0 && mStack.peek().getClass().equals(frag.getClass())) return;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new Handler().post(new Runnable() {
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
                        .add(R.id.container_login, frag)
                        .addToBackStack(frag.getTitle())
                        .commitAllowingStateLoss();

                mStack.push(frag);
                mActionBar.post(new Runnable() {
                    @Override
                    public void run() {
                        mActionBar.update(frag);
                    }
                });
            }
        });

    }

    @Override
    public void swapFrag(BaseLoginFrag frag) {
        Log.d(TAG, "swapFrag");
        popFrag(true);
        stackFrag(frag);
    }

    @Override
    public void popToFrag(String fragTitle) {
        Log.d(TAG, "popToFrag");
        while (!mStack.isEmpty() && !mStack.peek().getTitle().equals(fragTitle) && popFrag(true))
            ;
        updateViewsForFrag();

    }

    @Override
    public void popToHome() {
        Log.d(TAG, "popToHome");
        while (popFrag(true)) ;
        updateViewsForFrag();
    }

    @Override
    public boolean popFrag(boolean quickPop) {
        Log.d(TAG, "popFrag");

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) return false;
        getSupportFragmentManager().popBackStackImmediate();

        BaseLoginFrag poppedFrag = mStack.pop();
        Log.d(TAG, "Popped Fragment :: " + poppedFrag.getTitle());

        if (!quickPop) updateViewsForFrag();

        return true;

    }

    private void updateViewsForFrag() {
        Log.d(TAG, "updateViewsForFrag");

        if (mStack.isEmpty()) mActionBar.update(null);
        else {
            mActionBar.update(mStack.peek());
            mStack.peek().onResurface();
        }
    }

    public void onCloseClicked() {
        Log.d(TAG, "onCloseClicked");

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

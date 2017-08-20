package com.livingspaces.proshopper;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.livingspaces.proshopper.analytics.AnalyticsApplication;
import com.livingspaces.proshopper.fragments.BaseStackFrag;
import com.livingspaces.proshopper.fragments.LoginFrag;
import com.livingspaces.proshopper.fragments.NavigationFrag;
import com.livingspaces.proshopper.fragments.SettingsFrag;
import com.livingspaces.proshopper.interfaces.IMainFragManager;
import com.livingspaces.proshopper.networking.GpsManager;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.views.ActionBar;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements IMainFragManager {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActionBar actionBar;
    private Stack<BaseStackFrag> fragStack;
    private boolean hasToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkManager.Init(this);
        Global.Init(this);
        Layout.Init(this);
        Init();
    }

    private void Init() {
        fragStack = new Stack<>();
        actionBar = (ActionBar) findViewById(R.id.actionbar);

        hasToken = Global.Prefs.hasToken();

        getSupportFragmentManager().beginTransaction().add(R.id.container_main, NavigationFrag.newInstance()).commit();

        if (!hasToken) {
            Global.FragManager.stackFrag(LoginFrag.newInstance());
        }

        Utility.activity = this;
        GpsManager.getInstance().setContext(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Utility.gaTracker = application.getDefaultTracker();
    }

    private void updateViewsForFrag() {
        Log.d(TAG, "updateViewsForFrag");
        if (fragStack.isEmpty()){
            Log.d(TAG, "fragStack.isEmpty()");
            actionBar.update(null);
        }
        else {
            Log.d(TAG, "fragStack.is not Empty()");
            actionBar.update(fragStack.peek());
            fragStack.peek().onResurface();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: " + Integer.toString(requestCode));
        if (!fragStack.isEmpty())
            fragStack.peek().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public FragmentManager getFragMan() {
        return getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (!fragStack.isEmpty() && fragStack.peek().handleBackPress()){
            Log.d(TAG, "fragStack is not Empty() & handleBackPress");
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            Log.d(TAG, "getBackStackEntryCount >= 1");
            popFrag(false);
        }
        else {
            Log.d(TAG, "super.OnBackPress");
            super.onBackPressed();
        }
    }

    @Override
    public void refreshActionBar() {
        Log.d(TAG, "refreshActionBar");
        if (!fragStack.isEmpty()){
            Log.d(TAG, "fragStack is not Empty()");
            actionBar.update(fragStack.peek());
        }
    }

    @Override
    public boolean popFrag(boolean quickPop) {
        Log.d(TAG, "popFrag");
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Log.d(TAG, "getBackStackEntryCount() == 0");
            return false;
        }
        getSupportFragmentManager().popBackStackImmediate();

        BaseStackFrag poppedFrag = fragStack.pop();
        Log.d(TAG, "Popped Fragment :: " + poppedFrag.getTitle());

        if (!quickPop) {
            Log.d(TAG, "!quickPop");
            updateViewsForFrag();
        }

        return true;
    }

    @Override
    public void popToFrag(String fragTitle) {
        Log.d(TAG, "popToFrag");
        while (!fragStack.isEmpty() && !fragStack.peek().getTitle().equals(fragTitle) && popFrag(true))
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
    public void stackFrag(final BaseStackFrag frag) {
        Log.d(TAG, "stackFrag");
        if (fragStack.size() > 0 && fragStack.peek().getClass().equals(frag.getClass())) {
            Log.d(TAG, "fragStack.size() > 0 && fragStack.peek().getClass().equals(frag.getClass())");
            return;
        }
        Log.d(TAG, "set NotTouchable");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new Handler().post(new Runnable() {
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
                        .add(R.id.container_main, frag)
                        .addToBackStack(frag.getTitle())
                        .commitAllowingStateLoss();

                fragStack.push(frag);
                Log.d(TAG, "Pushed fragment :: " + frag.getTitle());
                actionBar.post(new Runnable() {
                    @Override
                    public void run() {
                        actionBar.update(frag);
                    }
                });
            }
        });
    }

    @Override
    public void startFrag(BaseStackFrag frag) {
        Log.d(TAG, "startFrag");
        popToHome();
        stackFrag(frag);
    }

    @Override
    public void swapFrag(BaseStackFrag frag) {
        Log.d(TAG, "swapFrag");
        popFrag(true);
        stackFrag(frag);
    }

    private boolean hasToken(){
        return hasToken;
    }
}

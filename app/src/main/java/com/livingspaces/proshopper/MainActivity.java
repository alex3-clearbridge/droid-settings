package com.livingspaces.proshopper;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.livingspaces.proshopper.analytics.AnalyticsApplication;
import com.livingspaces.proshopper.fragments.BaseStackFrag;
import com.livingspaces.proshopper.fragments.NavigationFrag;
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
        getSupportFragmentManager().beginTransaction().add(R.id.container_main, NavigationFrag.newInstance()).commit();
        Utility.activity = this;
        GpsManager.getInstance().setContext(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Utility.gaTracker = application.getDefaultTracker();
    }

    private void updateViewsForFrag() {
        if (fragStack.isEmpty()) actionBar.update(null);
        else {
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
        if (!fragStack.isEmpty() && fragStack.peek().handleBackPress()) return;

        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) popFrag(false);
        else super.onBackPressed();
    }

    @Override
    public void refreshActionBar() {
        if (!fragStack.isEmpty()) actionBar.update(fragStack.peek());
    }

    @Override
    public boolean popFrag(boolean quickPop) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) return false;
        getSupportFragmentManager().popBackStackImmediate();

        BaseStackFrag poppedFrag = fragStack.pop();
        Log.d(TAG, "Popped Fragment :: " + poppedFrag.getTitle());

        if (!quickPop) updateViewsForFrag();

        return true;
    }

    @Override
    public void popToFrag(String fragTitle) {
        while (!fragStack.isEmpty() && !fragStack.peek().getTitle().equals(fragTitle) && popFrag(true))
            ;
        updateViewsForFrag();
    }

    @Override
    public void popToHome() {
        while (popFrag(true)) ;
        updateViewsForFrag();
    }

    @Override
    public void stackFrag(final BaseStackFrag frag) {
        if (fragStack.size() > 0 && fragStack.peek().getClass().equals(frag.getClass())) return;
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
        popToHome();
        stackFrag(frag);
    }

    @Override
    public void swapFrag(BaseStackFrag frag) {
        popFrag(true);
        stackFrag(frag);
    }
}

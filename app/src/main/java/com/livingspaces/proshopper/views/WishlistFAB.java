package com.livingspaces.proshopper.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.sizes.PhoneSizes;

/**
 * Created by rugvedambekar on 15-09-28.
 */
public class WishlistFAB extends RelativeLayout {

    private ICallback callback;

    private View shadow;
    private LSTextView tv_deleteAll, tv_cancelEdit;
    private LinearLayout ll_editButtons;
    private ImageView iv_menu, iv_edit, iv_scan, iv_share;

    private float startX = 0, startY = 0;
    private double radius, deltaXY;
    private boolean inEditMode = false;

    public WishlistFAB(Context context) {
        super(context);
        init();
    }

    public WishlistFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WishlistFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.wishlist_fab, this);

        radius = Layout.Calc(PhoneSizes.fabContainerSize).height;
        deltaXY = radius * Math.cos(Math.toRadians(45));

        shadow = findViewById(R.id.fabShadow);

        ll_editButtons = (LinearLayout) findViewById(R.id.ll_editButtons);
        tv_deleteAll = (LSTextView) findViewById(R.id.tv_deleteAll);
        tv_cancelEdit = (LSTextView) findViewById(R.id.tv_cancelEdit);

        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);

        iv_menu.setOnClickListener(expandCollapseListener);

        setOnClickListener(expandCollapseListener);
        setClickable(false);

        iv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_menu.callOnClick();
                if (callback != null) callback.onShare();
            }
        });
        iv_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_menu.callOnClick();
                setForEdit();
                if (callback != null) callback.onEdit();
            }
        });
        iv_scan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_menu.callOnClick();
                if (callback != null) callback.onScan();
            }
        });
        tv_cancelEdit.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                setForMenu();
                if (callback != null) callback.onEditCancel();
            }
        });
        tv_deleteAll.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                setForMenu();
                                if (callback != null) callback.onDeleteAll();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    public void setCallback(ICallback cb) { callback = cb; }
    public boolean inEditMode() { return inEditMode; }

    public void setForEdit() {
        inEditMode = true;

        iv_menu.animate().setDuration(250).alpha(0).start();
        ll_editButtons.setVisibility(INVISIBLE);
        ll_editButtons.animate().setDuration(250).alpha(1).withStartAction(new Runnable() {
            @Override public void run() {
                ll_editButtons.setVisibility(VISIBLE);
            }
        }).start();

        Global.FragManager.refreshActionBar();
    }
    public void setForMenu() {
        inEditMode = false;

        iv_menu.animate().setDuration(250).alpha(1).start();
        ll_editButtons.animate().setDuration(250).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                ll_editButtons.setVisibility(GONE);
            }
        }).start();

        Global.FragManager.refreshActionBar();
    }

    public void hideMenuFAB() {
        iv_menu.animate().setDuration(250).yBy(100).start();
    }
    public void showMenuFAB() {
        iv_menu.animate().setDuration(250).yBy(-100).start();
    }

    private OnClickListener expandCollapseListener = new View.OnClickListener() {
        boolean animating = false, expanded = false;

        @Override
        public void onClick(View v) {
            if (startX == 0 || startY == 0) {
                startX = iv_menu.getX();
                startY = iv_menu.getY();
            }

            if (animating) return;
            animating = true;

            expanded = !expanded;
            setClickable(expanded);

            shadow.animate().setDuration(250).scaleX(expanded ? 5 : 1).scaleY(expanded ? 5 : 1).alpha(expanded ? 1 : 0).start();
            iv_share.animate().setDuration(250).x(expanded ? (startX - (int) radius) : startX).alpha(expanded ? 1 : 0).start();
            iv_edit.animate().setDuration(250).y(expanded ? (startY - (int) radius) : startY).alpha(expanded ? 1 : 0).start();
            iv_scan.animate().setDuration(250).x(expanded ? (startX - (int) deltaXY) : startX).y(expanded ? (startY - (int) deltaXY) : startY).alpha(expanded ? 1 : 0)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            animating = false;
                        }
                    }).start();

        }
    };

    public interface ICallback {
        void onShare();
        void onScan();
        void onEdit();
        void onEditCancel();
        void onDeleteAll();
    }
}

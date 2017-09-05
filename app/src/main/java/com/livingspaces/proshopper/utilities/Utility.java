package com.livingspaces.proshopper.utilities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
//import com.livingspaces.proshopper.data.Item;
import com.livingspaces.proshopper.networking.Services;
import com.google.android.gms.analytics.Tracker;
import com.livingspaces.proshopper.data.response.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by justinwong on 2015-10-08.
 */
public class Utility {
    private final String TAG = Utility.class.getSimpleName();
    public static Activity activity;
    public static Tracker gaTracker;


    public static float applyBase(float dip) {
        if (activity == null) return dip;

        return dip / activity.getResources().getDisplayMetrics().density;
    }

    /**
     * Returns screen width
     *
     * @return
     */
    public static int getScreenWidth() {
        if (activity == null)
            return -1;

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    /**
     * Returns screen height
     *
     * @return
     */
    public static int getScreenHeight() {
        if (activity == null)
            return -1;

        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int id = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (id != 0) {
            int actionBarHeight = (int) applyBase(activity.getResources().getDimensionPixelSize(id));
            return size.y - actionBarHeight;
        }
        return size.y;
    }

    /**
     * Hide software keyboard
     */
    public static void hideSoftKeyboard(View view) {
        if (view == null || activity == null) {
            return;
        }

        InputMethodManager inputManager = (InputMethodManager) Utility.activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        /*
         * Using 0 instead of HIDE_NOT_ALWAYS
         */
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Hide software keyboard
     */
    public static void showSoftKeyboard(View view) {
        if (view == null || activity == null) return;

        InputMethodManager inputManager = (InputMethodManager) Utility.activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void openGoogleMaps(String latitude, String longitude, String name) {
        double latValue = Double.parseDouble(latitude);
        double longValue = Double.parseDouble(longitude);

        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latValue, longValue, name);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                activity.startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(activity, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void shareUrl(Context context, List<Product> items) {
        Resources resources = context.getResources();
        String itemListString = "";
        String urlBody = "";
        for (Product item : items) {
            if (itemListString.isEmpty()) {
                itemListString += item.getSku();
            } else {
                itemListString += "," + item.getSku();
            }
            urlBody += "<br>Item: <a href=\"" + Services.URL.Product.get() + item.getSku() + "\">" + item.getSku() + "</a><br>" +
                    "Sku: " + item.getSku() + "<br>" +
                    "Price: " + item.getPrice() + "<br>";
        }

        String shareWishListBody = resources.getString(R.string.shareMsgText1)
                + " <a href=\"" + Services.URL.ShareProduct.get()
                + "items=" + itemListString + "\">"
                + resources.getString(R.string.shareMsgText2)
                + "</a>"
                + resources.getString(R.string.shareMsgText3)
                + "<br>"
                + urlBody;


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<html><body>" + shareWishListBody + "</body></html>"));

        PackageManager pm = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.shareChooserTitle));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("mms") || packageName.contains("android.gm") || packageName.contains("sms")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if (packageName.contains("mms") || packageName.contains("sms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.shareMsgText1)
                            + resources.getString(R.string.shareMsgText2)
                            + resources.getString(R.string.shareMsgText3)
                            + " " + Services.URL.ShareProduct.get()
                            + "items=" + itemListString);
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_subject));
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<html><body>" + shareWishListBody + "</body></html>"));
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        context.startActivity(openInChooser);
    }
}

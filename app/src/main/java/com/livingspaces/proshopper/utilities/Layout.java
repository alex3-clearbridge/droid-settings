package com.livingspaces.proshopper.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.utilities.sizes.Size;

/**
 * Created by rugvedambekar on 2015-06-04.
 */

public class Layout {
    public static final String TAG = "LAYOUT";

    public enum SizeType {WIDTH, HEIGHT, BOTH}

    public static DisplayMetrics screenMetrics;

    private static void initScreenParams(Activity mainActivity) {
        DisplayMetrics screenSize = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(screenSize);

        screenMetrics = screenSize;

        Log.d(TAG, "screenMetrics: " + screenSize.widthPixels + "x" + screenSize.heightPixels);
    }

    public static void Init(Activity context) {
        initScreenParams(context);

        Color.backgroundLight = ContextCompat.getColor(context, android.R.color.background_light);
        Color.dialogError_txt = ContextCompat.getColor(context, R.color.Dialog_Error_txt);
        Color.wishlistCream = ContextCompat.getColor(context, R.color.Wish_Back_Cream);
        Color.main_txt = ContextCompat.getColor(context, R.color.Main_txt);

        Font.light = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Light.ttf");
        Font.regular = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Regular.ttf");
        Font.semibold = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Semibold.ttf");
        Font.bold = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Bold.otf");
        Font.italic = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-It.otf");
    }

    public static Size Calc(Size size) {
        return new Size(screenMetrics.widthPixels * size.width, screenMetrics.heightPixels * size.height);
    }

    public static void setViewSize(View view, Size size) {
        setViewSize(view, size, SizeType.BOTH);
    }

    public static void setViewSize(View view, Size size, SizeType type) {
        if (view == null) return;

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Size sizeInPx = Calc(size);

        if (type == SizeType.BOTH || type == SizeType.WIDTH) params.width = (int) sizeInPx.width;
        if (type == SizeType.BOTH || type == SizeType.HEIGHT) params.height = (int) sizeInPx.height;

        view.setLayoutParams(params);
    }

    public static void matchMeasuredViewSize(View deltaView, View template, SizeType type) {
        if (deltaView == null || template == null) return;

        ViewGroup.LayoutParams params = deltaView.getLayoutParams();
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (type == SizeType.BOTH || type == SizeType.HEIGHT) params.height = template.getMeasuredHeight();
        if (type == SizeType.BOTH || type == SizeType.WIDTH) params.width = template.getMeasuredWidth();

        deltaView.setLayoutParams(params);
    }

    public static void matchViewSize(View deltaView, View template, SizeType type) {
        if (deltaView == null || template == null) return;

        ViewGroup.LayoutParams params = deltaView.getLayoutParams();
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (type == SizeType.BOTH || type == SizeType.HEIGHT) params.height = template.getHeight();
        if (type == SizeType.BOTH || type == SizeType.WIDTH) params.width = template.getWidth();

        deltaView.setLayoutParams(params);
    }

    public static void setPadding(View view, int dpVal) {
        int pxVal = dpToPx(dpVal);
        view.setPadding(pxVal, pxVal, pxVal, pxVal);
    }

    public static void setMargin(View view, int dpVal) {
        int pxVal = dpToPx(dpVal);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(pxVal, pxVal, pxVal, pxVal);
    }

    public static void setDialog(Dialog dialog, Size size, SizeType type) {
        WindowManager.LayoutParams dialogParams = new WindowManager.LayoutParams();
        Size sizeInPx = Calc(size);

        dialogParams.copyFrom(dialog.getWindow().getAttributes());
        if (type == SizeType.BOTH || type == SizeType.WIDTH)
            dialogParams.width = (int) sizeInPx.width;
        else dialogParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        if (type == SizeType.BOTH || type == SizeType.HEIGHT)
            dialogParams.height = (int) sizeInPx.height;
        else dialogParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(dialogParams);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = Global.Resources.getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(int dp, DisplayMetrics displayMetrics) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Global.Resources.getDisplayMetrics();
        return dpToPx(dp, displayMetrics);
    }

    public static Bitmap getBitmap(Size imgSize, String fPath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        imgSize = Calc(imgSize);

        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fPath, bmOptions);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = CalcSampleSize(bmOptions, (int) imgSize.width, (int) imgSize.height);
        return BitmapFactory.decodeFile(fPath, bmOptions);
    }

    public static Bitmap setImageViewWithFile(ImageView imgView, String fPath) {
        // Get the dimensions of the View
        int targetW = imgView.getWidth();
        int targetH = imgView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(fPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(fPath, bmOptions);

        imgView.setImageBitmap(bitmap);
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return bitmap;
    }

    public static int CalcSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static class Color {

        public static int backgroundLight;
        public static int dialogError_txt;
        public static int main_txt;
        public static int wishlistCream;

    }

    public static class Font {

        public static Typeface regular, light, semibold, bold, italic;

        public static Typeface get(int f) {
            switch (f) {
                case 0:
                    return regular;
                case 1:
                    return light;
                case 2:
                    return semibold;
                case 3:
                    return bold;
                case 4:
                    return italic;
                default:
                    return regular;
            }
        }

    }

}

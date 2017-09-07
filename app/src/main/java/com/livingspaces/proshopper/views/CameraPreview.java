package com.livingspaces.proshopper.views;

/**
 * Created by rugvedambekar on 15-09-22.
 */

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.livingspaces.proshopper.utilities.Global;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getSimpleName();

    private ICallback callback;

    private ImageScanner imageScanner;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private boolean paused;

    public CameraPreview(Context context, ICallback cb) {
        super(context);

        callback = cb;
        mCamera = Global.getCameraInstance();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        imageScanner = new ImageScanner();
        imageScanner.setConfig(0, Config.X_DENSITY, 3);
        imageScanner.setConfig(0, Config.Y_DENSITY, 3);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        new StartCamTask().execute(holder == null ? mHolder : holder);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        new ReleaseCamTask().execute();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) return;
    }

    public boolean pause() {
        if (mCamera == null) return false;

        try {
            mCamera.stopPreview();
            paused = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean resume() {
        if (mCamera == null || mHolder == null) return false;

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            paused = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private class StartCamTask extends AsyncTask<SurfaceHolder, Void, Void> {

        @Override
        protected Void doInBackground(SurfaceHolder... params) {
            final SurfaceHolder finalHolder = params[0];
            Log.d(TAG, "doInBackground: StartCamTask");

            try {
                //if (mCamera == null)
                mCamera = Global.getCameraInstance();

                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(finalHolder);
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    int result = 0;

                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if (paused) return;

                        try {
                            Camera.Parameters parameters = camera.getParameters();
                            Camera.Size size = parameters.getPreviewSize();

                            Image barcode = new Image(size.width, size.height, "Y800");
                            barcode.setData(data);

                            result = imageScanner.scanImage(barcode);
                            if (result != 0) {
                                pause();

                                SymbolSet syms = imageScanner.getResults();
                                final String barcodeData = syms.iterator().next().getData();
                                if (callback != null) callback.onBarcodeFound(barcodeData);
                            }
                        } catch (RuntimeException t){
                            Log.e(TAG, "onPreviewFrame: " + t.getMessage());
                            }

                    }
                });

                Camera.Parameters canParams = mCamera.getParameters();
                canParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(canParams);

                mCamera.startPreview();
            } catch (NullPointerException | IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }

            return null;
        }
    }

    private class ReleaseCamTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: ReleaseCamTask");
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }

            return null;
        }
    }

    public interface ICallback {
        void onBarcodeFound(String barcodeData);
    }
}

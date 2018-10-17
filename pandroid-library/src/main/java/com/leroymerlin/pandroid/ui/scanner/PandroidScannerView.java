package com.leroymerlin.pandroid.ui.scanner;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;

import androidx.annotation.RequiresPermission;

/**
 * Created by florian on 09/12/15.
 */
public class PandroidScannerView extends ViewGroup {

    private static final String TAG = "PandroidScannerView";

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSource mCameraSource;
    private ScaleType mScaleType = ScaleType.CROP;


    public PandroidScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource) throws IOException, SecurityException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
            requestLayout();
            invalidate();
        }

    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void startIfReady() throws IOException, SecurityException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @RequiresPermission(Manifest.permission.CAMERA)
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            invalidate();
            try {
                startIfReady();
            } catch (SecurityException se) {
                Log.e(TAG, "Do not have permission to start the camera", se);
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    public void setScaleType(ScaleType scaleType) {
        if (mScaleType != scaleType) {
            mScaleType = scaleType;
            requestLayout();
            invalidate();
        }
    }


    public enum ScaleType {
        FIT,
        CROP
    }


    @RequiresPermission(Manifest.permission.CAMERA)
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 320;
        int height = 240;
        if (mCameraSource != null) {
            Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = width;
            //noinspection SuspiciousNameCombination
            width = height;
            height = tmp;
        }

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;

        switch (mScaleType) {

            case FIT: {

                float layoutRapport = ((float) layoutWidth) / layoutHeight;
                float rapport = ((float) width) / height;

                int childLeft = 0, childTop = 0, childRight = 0, childBottom = 0;
                if (layoutRapport > rapport) { // width will have border
                    childBottom = layoutHeight;
                    int childWidth = (int) (rapport * layoutHeight);
                    childLeft = (layoutWidth - childWidth) / 2;
                    childRight = (layoutWidth + childWidth) / 2;
                } else {// width will be crop
                    childRight = layoutWidth;
                    int childHeight = (int) (layoutWidth / rapport);
                    childTop = (layoutHeight - childHeight) / 2;
                    childBottom = (layoutHeight + childHeight) / 2;
                }

                for (int i = 0; i < getChildCount(); ++i) {
                    getChildAt(i).layout(childLeft, childTop, childRight, childBottom);
                }


            }
            break;
            case CROP: {
                // Computes height and width for potentially doing fit width.

                float layoutRapport = ((float) layoutWidth) / layoutHeight;
                float rapport = ((float) width) / height;

                int childLeft = 0, childTop = 0, childRight = 0, childBottom = 0;
                if (layoutRapport > rapport) { // height will be crop
                    childRight = layoutWidth;
                    int childHeight = (int) (layoutWidth / rapport);
                    childTop = (layoutHeight - childHeight) / 2;
                    childBottom = (layoutHeight + childHeight) / 2;
                } else {// width will be crop
                    childBottom = layoutHeight;
                    int childWidth = (int) (rapport * layoutHeight);
                    childLeft = (layoutWidth - childWidth) / 2;
                    childRight = (layoutWidth + childWidth) / 2;
                }

                for (int i = 0; i < getChildCount(); ++i) {
                    getChildAt(i).layout(childLeft, childTop, childRight, childBottom);
                }

            }
            break;
        }


        try {
            startIfReady();
        } catch (SecurityException se) {
            Log.e(TAG, "Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }


    public static abstract class SimpleProcessor implements Detector.Processor<Barcode> {


        private final Handler handler;
        private boolean isResume = true;

        public SimpleProcessor() {
            handler = new Handler();
        }

        @Override
        public void release() {
            isResume = false;
        }

        @Override
        public void receiveDetections(final Detector.Detections<Barcode> var1) {
            if (isResume && var1.getDetectedItems().size() > 0) {
                pauseDetector();
                final Barcode barcode = var1.getDetectedItems().valueAt(0);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!handleBarcode(barcode)){
                            resumeDetector();
                        }
                    }
                });

            }
        }

        /**
         * @param barcode detected
         * @return true to pause detection callBack. false otherwise
         */
        public abstract boolean handleBarcode(Barcode barcode);

        public void pauseDetector() {
            isResume = false;
        }

        public void resumeDetector() {
            isResume = true;
        }
    }
}

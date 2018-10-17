package com.leroymerlin.pandroid.demo.main.scanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import androidx.fragment.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.event.opener.FragmentOpener;
import com.leroymerlin.pandroid.ui.scanner.PandroidScannerView;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by florian on 16/12/15.
 */
public class ScannerFragment extends PandroidFragment<FragmentOpener> {

    public static final int RC_HANDLE_CAMERA_PERM = 100;
    private static final String TAG = "ScannerFragment";
    @BindView(R.id.scanner_psv)
    PandroidScannerView pandroidScannerView;


    @Inject
    ToastManager toastManager;

    private CameraSource mCameraSource;
    private PandroidScannerView.SimpleProcessor processor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }


    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    //tag::Vision[]

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.


        if (mCameraSource != null) {
            try {
                pandroidScannerView.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void createCameraSource() {

        Context context = getActivity().getApplicationContext();

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getActivity());
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, 100).show();
        } else {

            // A barcode detector is created to track barcodes.  An associated multi-processor instance
            // is set to receive the barcode detection results, track the barcodes, and maintain
            // graphics for each barcode on screen.  The factory is used by the multi-processor to
            // create a separate tracker instance for each barcode.
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
            processor = new PandroidScannerView.SimpleProcessor() {

                @Override
                public boolean handleBarcode(Barcode barcode) {
                    toastManager.makeToast(getActivity(), "Detected: " + barcode.displayValue, new ToastManager.ToastListener() {
                        @Override
                        public void onDismiss() {
                            processor.resumeDetector();
                        }

                        @Override
                        public void onActionClicked() {

                        }
                    });
                    return true;
                }
            };
            barcodeDetector.setProcessor(processor);

            if (!barcodeDetector.isOperational()) {
                // Note: The first time that an app using the barcode or face API is installed on a
                // device, GMS will download a native libraries to the device in order to do detection.
                // Usually this completes before the app is run for the first time.  But if that
                // download has not yet completed, then the above call will not detect any barcodes
                // and/or faces.
                //
                // isOperational() can be used to check if the required native libraries are currently
                // available.  The detectors will automatically become operational once the library
                // downloads complete on device.
                Log.w(TAG, "Detector dependencies are not yet available.");
            }

            // Creates and starts the camera.  Note that this uses a higher resolution in comparison
            // to other detection examples to enable the barcode detector to detect small barcodes
            // at long distances.
            CameraSource.Builder builder = new CameraSource.Builder(context, barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1600, 1024)
                    .setRequestedFps(30.0f)
                    .setAutoFocusEnabled(true);

            mCameraSource = builder.build();
        }

    }
    //end::Vision[]


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            requestPermissions(permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(getView(), R.string.scanner_permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            logWrapper.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            logWrapper.d(TAG, "Camera permission granted - initialize the camera source");
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        requestCameraPermission();
    }
}

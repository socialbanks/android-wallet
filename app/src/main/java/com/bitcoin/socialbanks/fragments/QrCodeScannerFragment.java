package com.bitcoin.socialbanks.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitcoin.socialbanks.application.ApplicationConfig;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QrCodeScannerFragment extends Fragment implements ZBarScannerView.ResultHandler,OnQrCodeDetectedListener {

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String CAMERA_ID = "CAMERA_ID";

    private ZBarScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;

    private int mCameraId = -1;

    String objId;

    private OnQrCodeDetectedListener onQrCodeDetectedCallback;

    public static QrCodeScannerFragment newInstance(String objId) {
        QrCodeScannerFragment fragment = new QrCodeScannerFragment();
        Bundle args = new Bundle();
        args.putString("objId",objId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if(getArguments() != null){
            objId = getArguments().getString("objId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {

        mScannerView = new ZBarScannerView(getActivity());

        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            // Auto Focus foi desabilitado pois houve exceção em um aparelho Sony
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, false);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = false;
            mCameraId = -1;
        }

        setupFormats();

        attachOnQrCodeDetectedListener(this);
        return mScannerView;
    }

    public void attachOnQrCodeDetectedListener(
            OnQrCodeDetectedListener onQrCodeDetectedListener) {
        this.onQrCodeDetectedCallback = onQrCodeDetectedListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public void handleResult(Result rawResult) {

        try {
            // Reinicia a camera para ler outro QR-Code
           /* mScannerView.startCamera(mCameraId);
            mScannerView.setFlash(mFlash);
            mScannerView.setAutoFocus(mAutoFocus);*/

            // Encaminha a string capturada ao fragment
            if (onQrCodeDetectedCallback != null) {
                onQrCodeDetectedCallback.onQrCodeDetected(rawResult.getContents());
            }

        } catch (Exception exception) {
        }
    }

    private void setupFormats() {

        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        formats.add(BarcodeFormat.QRCODE);

        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    public void startScan(){
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onQrCodeDetected(String qrCodeData) {

        ApplicationConfig.getConfig().getRootActivity().switchFragment(BuyFragment.newInstance(objId,qrCodeData));

    }
}

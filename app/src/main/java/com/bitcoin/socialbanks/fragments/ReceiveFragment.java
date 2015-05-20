package com.bitcoin.socialbanks.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitcoin.socialbanks.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumMap;

public class ReceiveFragment extends Fragment implements OnQrCodeDetectedListener {

    private static final String ARG_PARAM1 = "param1";

    private String bitCoinReceiverAddress;
    private String mParam2;

    Button gerarQR;
    EditText descricaoEt;


    public static ReceiveFragment newInstance(String param1) {
        ReceiveFragment fragment = new ReceiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ReceiveFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bitCoinReceiverAddress = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_receive, container, false);


        final ImageView img = (ImageView) rootView.findViewById(R.id.image_qrcode);

        gerarQR = (Button) rootView.findViewById(R.id.receiver_generate_qr_bt);
        descricaoEt = (EditText) rootView.findViewById(R.id.receiver_description_et);


        gerarQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();
                try {
                    json.put("bitcoin", bitCoinReceiverAddress);
                    json.put("receiverDescription", descricaoEt.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.v("Json", "Json QRCODE -> " + json.toString());
                Bitmap bmp = generateQR(json.toString());
                img.setImageBitmap(bmp);

            }
        });

        return rootView;
    }



    @Override
    public void onQrCodeDetected(String qrCodeData) {


        Toast.makeText(getActivity(), "qrCodeData = " + qrCodeData, Toast.LENGTH_SHORT).show();

    }
    private Bitmap generateQR(String message) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(message, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Bitmap generateQRcode() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            EnumMap<EncodeHintType, Object> hint = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = writer.encode("teste", BarcodeFormat.QR_CODE, 300, 300);


            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    // pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000
                    // : 0xFFFFFFFF;
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0 : 255;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}

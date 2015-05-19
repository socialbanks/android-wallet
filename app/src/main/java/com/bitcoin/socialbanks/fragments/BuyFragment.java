package com.bitcoin.socialbanks.fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class BuyFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    boolean isScanning = false;

    ProgressDialog dialog;

    String bitcoinReceiverAddress = "";
    String walletIdReceiver = "";
    String descriptionReceiver = "";
    String nameReceiver = "";
    String emailReceiver = "";
    Double value = 0.0;

    private TextView priceEt;
    private EditText descriptionEt;
    private TextView nameTv;
    private TextView emailTv;
    private Button buyBt;
    private Button cancelBt;


    private OnFragmentInteractionListener mListener;

    String walletSenderId;
    private String walletReceiver;

    public static BuyFragment newInstance(String param1, String param2) {
        BuyFragment fragment = new BuyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BuyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            walletSenderId = mParam1;
        }

        JSONObject json;

        try {
            json = new JSONObject(mParam2);
            bitcoinReceiverAddress = json.getString("bitcoin");
            descriptionReceiver = json.getString("receiverDescription");
            value = Double.parseDouble(json.getString("value"));
            value = Double.parseDouble(priceEt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Valor inválido", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_buy, container, false);

        priceEt = (TextView) rootView.findViewById(R.id.buy_price_et);
        descriptionEt = (EditText) rootView.findViewById(R.id.buy_description_et);
        nameTv = (TextView) rootView.findViewById(R.id.buy_fragment_name_tv);
        emailTv = (TextView) rootView.findViewById(R.id.buy_fragment_email_et);

        buyBt = (Button) rootView.findViewById(R.id.buy_fragment_buy_bt);
        cancelBt = (Button) rootView.findViewById(R.id.buy_fragment_cancel_bt);

        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConfig.getConfig().getRootActivity().switchFragment(BanksFragment.newInstance("", ""));
            }
        });
        buyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Enviando Valor...");
                dialog.show();
                dialog.setCancelable(false);

                value = Double.parseDouble(priceEt.getText().toString());

                ParseObject transaction = new ParseObject("Transaction");
                transaction.put("senderDescription", descriptionEt.getText().toString());
                transaction.put("receiverDescription", descriptionReceiver);
                transaction.put("value", value * 100);
                transaction.put("senderWallet", ParseObject.createWithoutData("Wallet", walletSenderId));
                transaction.put("receiverWallet", ParseObject.createWithoutData("Wallet", walletIdReceiver));
                //    transaction.put("bitcoinTx", walletIdReceiver);

                transaction.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {

                            ApplicationConfig.getConfig().getRootActivity().switchFragment(BanksFragment.newInstance("", ""));
                            Toast.makeText(getActivity(), "Efetuado com sucesso!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();

                    }
                });

            }
        });


        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Obtendo dados...");
        dialog.show();
        dialog.setCancelable(false);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ParseQuery query = new ParseQuery("Wallet");
                query.whereEqualTo("bitcoinAddress", bitcoinReceiverAddress);
                query.include("user");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {

                        if (dialog != null)
                            dialog.dismiss();

                        if (e == null) {

                            walletIdReceiver = list.get(0).getObjectId();

                            ParseObject user = list.get(0).getParseObject("user");
                            nameReceiver = user.getString("firstName") + " " + user.getString("lastName");
                            emailReceiver = user.getString("email");

                            nameTv.setText(nameReceiver);
                            emailTv.setText(emailReceiver);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

   /* @Override
    public void onQrCodeDetected(String qrCodeData) {

        if (!isScanning)
            return;

        isScanning = false;
        JSONObject json = null;
        String walletReceiverId = "";
        String descriptionReceiver = "";
        String nameReceiver = "";
        Double value = 0.0;


        startScanBt.setVisibility(View.VISIBLE);
        try {
            json = new JSONObject(qrCodeData);
            walletReceiverId = json.getString("walletReceiverId");
            descriptionReceiver = json.getString("description");
            nameReceiver = json.getString("name");

            value = Double.parseDouble(priceEt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Valor inv�lido", Toast.LENGTH_LONG).show();
        }

        String descriptionSender = descriptionEt.getText().toString();


        String walletSender = walletSenderId;
        String walletReceiver = walletReceiverId;

        // String sBanckWalletR = ((ParseObject)walletR.get("socialBank")).getObjectId();
        //  String sBanckWalletS = ((ParseObject)walletS.get("socialBank")).getObjectId();

        //    if(!sBanckWalletR.equals(sBanckWalletS)){
        //        Toast.makeText(getActivity(),"As moedas devem ser iguais",Toast.LENGTH_LONG).show();
        //        return;
        //    }


        final ParseObject transaction = new ParseObject("Transaction");
        transaction.put("senderDescription", descriptionSender);
        transaction.put("receiverDescription", descriptionReceiver);
        transaction.put("value", value * 100);
        transaction.put("senderWallet", ParseObject.createWithoutData("Wallet", walletSenderId));
        transaction.put("receiverWallet", ParseObject.createWithoutData("Wallet", walletReceiver));
        transaction.put("bitcoinTx", walletReceiver);


        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Confirma?");
        dialog.setMessage("Valor: " + value + "\nDestino: " + nameReceiver);
        dialog.setPositiveButton("Sim", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                transaction.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null)
                            Toast.makeText(ApplicationConfig.getConfig().getRootActivity(),"Transação efetuada com sucesso!",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(ApplicationConfig.getConfig().getRootActivity(),"" + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

                getActivity().onBackPressed();
            }
        });


        dialog.setNegativeButton("Não", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }*/

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

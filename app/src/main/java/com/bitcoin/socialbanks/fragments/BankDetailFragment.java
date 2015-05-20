package com.bitcoin.socialbanks.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitcoin.socialbanks.Model.Transaction;
import com.bitcoin.socialbanks.Model.Wallet;
import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.adapters.TransactionsAdapter;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BankDetailFragment extends Fragment {

    TextView nameTextView;
    TextView balanceTextView;

    ListView transactionListView;
    TransactionsAdapter transactionsAdapter;

    Wallet wallet;

    ApplicationConfig appConfig;

    ArrayList<Transaction> listTransaction = new ArrayList<Transaction>();

    Button buyBt;
    Button receiverBt;
    Button sendBt;

    ParseQuery<ParseObject> mainQueryLocal;
    ParseQuery<ParseObject> mainQuery;
    ParseQuery<ParseObject> query;


    ProgressBar progressBar;
    ParseObject walletBuffer;

    ImageView imageBank;


    private OnFragmentInteractionListener mListener;
    private String objId;

    public static BankDetailFragment newInstance(String param1) {
        BankDetailFragment fragment = new BankDetailFragment();
        Bundle args = new Bundle();
        args.putString("objId", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public BankDetailFragment() {
        // Required empty public constructor
    }

    List<ParseObject> resultsLocal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appConfig = ApplicationConfig.getConfig();

        walletBuffer = appConfig.getBufferWallet();


    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        ParseQuery queryReceiverLocal = ParseQuery.getQuery("Transaction");
        queryReceiverLocal.whereEqualTo("receiverWallet", walletBuffer);

        ParseQuery querySenderLocal = ParseQuery.getQuery("Transaction");
        querySenderLocal.whereEqualTo("senderWallet", walletBuffer);

        List<ParseQuery<ParseObject>> queriesLocal = new ArrayList<ParseQuery<ParseObject>>();
        queriesLocal.add(querySenderLocal);
        queriesLocal.add(queryReceiverLocal);
/*
        mainQueryLocal = ParseQuery.or(queriesLocal);
        mainQueryLocal.fromLocalDatastore();
        mainQueryLocal.include("receiverWallet");
        mainQueryLocal.include("senderWallet");
        mainQueryLocal.orderByDescending("createdAt");
        mainQueryLocal.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {

                if (e == null) {
                    resultsLocal = results;
                    listTransaction.clear();
                    for (ParseObject obj : results) {

                        Double value = obj.getDouble("value");
                        String comment = "";

                        ParseObject sender = obj.getParseObject("senderWallet");

                        if(sender != null && walletBuffer !=null) {

                            if (sender.getObjectId().toString().equals(walletBuffer.getObjectId().toString())) {
                                value *= -1;
                                value /= 100;
                                comment = obj.getString("senderDescription");
                            } else {
                                comment = obj.getString("receiverDescription");
                            }

                            Date data = obj.getCreatedAt();
                            Transaction tr = new Transaction(data, value, comment);
                            listTransaction.add(tr);
                        }

                    }
                    transactionsAdapter.notifyDataSetChanged();
                }
            }
        });
*/
        ParseQuery queryReceiver = ParseQuery.getQuery("Transaction");
        queryReceiver.whereEqualTo("receiverWallet", walletBuffer);

        ParseQuery querySender = ParseQuery.getQuery("Transaction");
        querySender.whereEqualTo("senderWallet", walletBuffer);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        queries.add(querySender);
        queries.add(queryReceiver);

        mainQuery = ParseQuery.or(queries);
        mainQuery.include("receiverWallet");
        mainQuery.include("senderWallet");
        mainQuery.orderByDescending("createdAt");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> results, ParseException e) {
                if (e == null) {


                    if (resultsLocal != null && resultsLocal.size() > 0) {

                        ParseObject.unpinAllInBackground(resultsLocal, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                ParseObject.pinAllInBackground(results);
                            }
                        });
                    }

                    progressBar.setVisibility(View.GONE);
                    listTransaction.clear();
                    for (final ParseObject obj : results) {

                        Double value = obj.getDouble("value");
                        String comment = "";

                        ParseObject sender = obj.getParseObject("senderWallet");

                        if (sender == null) {
                            comment = obj.getString("senderDescription");

                        }
                        if (walletBuffer == null)
                            comment = obj.getString("senderDescription");


                        if (sender != null && walletBuffer != null) {
                            if (sender.getObjectId().toString().equals(walletBuffer.getObjectId().toString())) {
                                value *= -1;
                                comment = obj.getString("senderDescription");
                            } else {
                                comment = obj.getString("receiverDescription");
                            }

                            Date data = obj.getCreatedAt();
                            Transaction tr = new Transaction(data, value, comment);
                            listTransaction.add(tr);
                        }
                    }
                    transactionsAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bank_detail, container, false);

        nameTextView = (TextView) rootView.findViewById(R.id.bank_detail_name_tv);
        balanceTextView = (TextView) rootView.findViewById(R.id.bank_detail_balance_tv);

        buyBt = (Button) rootView.findViewById(R.id.bank_detail_buy);
        receiverBt = (Button) rootView.findViewById(R.id.bank_detail_receiver);
        sendBt = (Button) rootView.findViewById(R.id.bank_detail_send);

        imageBank = (ImageView) rootView.findViewById(R.id.bank_detail_image);

        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_bank_detail_progressbar);

        final ParseObject walletbuff = appConfig.getBufferWallet();

        wallet = new Wallet(walletbuff);


        buyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConfig.getConfig().getRootActivity().switchFragment(QrCodeScannerFragment.newInstance(wallet.getIdObject()));

            }
        });

        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConfig.getConfig().getRootActivity().switchFragment(SendFragment.newInstance(wallet.getIdObject(), wallet.getSocialBank().getObjectId()));

            }
        });

        receiverBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConfig.getConfig().getRootActivity().switchFragment(ReceiveFragment.newInstance(wallet.getBitcoinAdress()));
            }
        });

        nameTextView.setText(wallet.getName());

        Double valor = Double.valueOf(String.format(Locale.US, "%.2f", wallet.getBalance() / 100));

        balanceTextView.setText("" + valor);

        transactionListView = (ListView) rootView.findViewById(R.id.bank_detail_transactions_lv);

        transactionsAdapter = new TransactionsAdapter(getActivity(), R.layout.item_list_transaction, listTransaction);
        transactionListView.setAdapter(transactionsAdapter);


        ParseFile file = wallet.getSocialBank().getParseFile("image");

        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    imageBank.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));


                    bitmap.recycle();
                }
            });

        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

    }

    @Override
    public void onPause() {
        super.onPause();

        if (query != null)
            query.cancel();

        if (mainQuery != null)
            mainQuery.cancel();

        if (mainQueryLocal != null)
            mainQueryLocal.cancel();


    }


}


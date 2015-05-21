package com.bitcoin.socialbanks.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.activities.RootActivity;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.bitcoin.socialbanks.bitcoin.BitCoinUtils;
import com.parse.ParseUser;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.spongycastle.util.encoders.Hex;

import java.util.ArrayList;

public class RescueWalletFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText seedWordsEt;

    private Button rescueStartBt;
    String words;



    NetworkParameters params = MainNetParams.get();


    public static RescueWalletFragment newInstance(String param1, String param2) {
        RescueWalletFragment fragment = new RescueWalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RescueWalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rescue_wallet, container, false);


        seedWordsEt = (EditText) rootView.findViewById(R.id.rescue_wallet_seed_et);


        rescueStartBt = (Button) rootView.findViewById(R.id.rescue_wallet_seed_bt);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Redeem...");

        rescueStartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor = getActivity().getSharedPreferences(getActivity().getPackageName() + ParseUser.getCurrentUser().getEmail(), getActivity().MODE_PRIVATE).edit();

                if (!seedWordsEt.getText().toString().equals("")) {
                    editor.putString("seedWords", seedWordsEt.getText().toString());


                    words = seedWordsEt.getText().toString();

                    dialog.show();

                    BitcoionAddress run = new BitcoionAddress(seedWordsEt.getText().toString());

                    run.execute();
                }
            }
        });
        return rootView;
    }
    ProgressDialog dialog;

    public class BitcoionAddress extends AsyncTask<Integer, Integer, Void>{



        public BitcoionAddress(String word) {

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Integer... p) {

            //String teste = "virus decorate ahead sail hint buyer hollow smoke joke amused alert easy";
            DeterministicSeed seed = null;
            try {
                seed = new DeterministicSeed(words, null, "", 0);
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }

            Wallet wallet = Wallet.fromSeed(params, seed);
            //Wallet.SendRequest.


            ChildNumber number = new ChildNumber(0, true);
            ArrayList<ChildNumber> list = new ArrayList<ChildNumber>();
            list.add(number);

            String bitcoinAddress = wallet.getKeyByPath(list).toAddress(params).toString();

            String privKey = Hex.toHexString(wallet.getKeyByPath(list).getPrivKeyBytes());
            final String wifRemove = BitCoinUtils.generatePrivKeyWIFFromPrivateKeyHex(privKey);


            ApplicationConfig.getConfig().setBitcoinAddress(bitcoinAddress);
            ApplicationConfig.getConfig().setWifRemore(wifRemove);

            editor.putString("addressBitCoin", bitcoinAddress);
            editor.putString("wif_remove",wifRemove);
            editor.commit();

      //      Log.v("Login", "BitCoinJ words -> " + seedWordsEt.getText().toString());
       //     Log.v("Login", "BitCoinJ address -> " + ApplicationConfig.getConfig().getBitcoinAddress());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(dialog != null)
                dialog.dismiss();

            Intent i = new Intent(getActivity(),RootActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        }
    }




}

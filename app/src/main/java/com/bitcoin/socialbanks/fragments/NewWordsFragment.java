package com.bitcoin.socialbanks.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.activities.RootActivity;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.parse.ParseUser;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.spongycastle.util.encoders.Hex;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class NewWordsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String wifRemove;

    String bitcoinAddress;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewWords.
     */
    // TODO: Rename and change types and number of parameters

    TextView seedTv;
    Button saveSeedBt;

    public static NewWordsFragment newInstance(String param1, String param2) {
        NewWordsFragment fragment = new NewWordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewWordsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //  rescueSeed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_new_words, container, false);


        seedTv = (TextView) rootView.findViewById(R.id.new_word_seed_tv);
        saveSeedBt = (Button) rootView.findViewById(R.id.new_word_save_bt);

        final ParseUser user = ParseUser.getCurrentUser();


    //    Log.v("Login", "BitCoinJ words -> " + finalMnemonicConcat);
    //    Log.v("Login", "BitCoinJ address -> " + ApplicationConfig.getConfig().getBitcoinAddress());

        saveSeedBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(getActivity().getPackageName() + user.getEmail(), getActivity().MODE_PRIVATE).edit();

                if (!seedTv.getText().toString().equals("")) {
                    editor.putString("seedWords", seedTv.getText().toString());
                    editor.putString("addressBitCoin", bitcoinAddress);
                    editor.putString("wif_remove",wifRemove);
                }
                editor.commit();

                Intent i = new Intent(getActivity(), RootActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });


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


        BitcoionAddress btAddrss = new BitcoionAddress();
        btAddrss.execute();
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


    View.OnClickListener restore = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


        }
    };

    String mnemonicConcat = "";
    public class BitcoionAddress extends AsyncTask<Integer, Integer, Void> {



        public BitcoionAddress() {
        }

ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Generate...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Integer... p) {

            final NetworkParameters params = MainNetParams.get();

            SecureRandom random = new SecureRandom();
            DeterministicSeed seed2 = new DeterministicSeed(random, 128, "", 0);

            List<String> mnemonic = seed2.getMnemonicCode();


            for (String item : mnemonic) {
                mnemonicConcat = mnemonicConcat + " " + item;
            }

            final String finalMnemonicConcat = mnemonicConcat;


            DeterministicSeed seed = null;
            try {
                seed = new DeterministicSeed(finalMnemonicConcat, null, "", 0);
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }

            Wallet wallet = Wallet.fromSeed(params, seed);
            //Wallet.SendRequest.

            ChildNumber number = new ChildNumber(0, true);
            ArrayList<ChildNumber> list = new ArrayList<ChildNumber>();
            list.add(number);

            bitcoinAddress = wallet.getKeyByPath(list).toAddress(params).toString();

            wifRemove = Hex.toHexString(wallet.getKeyByPath(list).getPrivKeyBytes());

            ApplicationConfig.getConfig().setBitcoinAddress(bitcoinAddress);
            ApplicationConfig.getConfig().setWifRemore(wifRemove);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(dialog != null)
                dialog.dismiss();

            seedTv.setText(mnemonicConcat);


        }
    }
  /*
*/
}

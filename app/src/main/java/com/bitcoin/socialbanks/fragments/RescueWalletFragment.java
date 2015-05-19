package com.bitcoin.socialbanks.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.parse.ParseUser;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.wallet.DeterministicSeed;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RescueWalletFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RescueWalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    private OnFragmentInteractionListener mListener;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rescue_wallet, container, false);

        final NetworkParameters params = MainNetParams.get();

        seedWordsEt = (EditText) rootView.findViewById(R.id.rescue_wallet_seed_et);


        rescueStartBt = (Button) rootView.findViewById(R.id.rescue_wallet_seed_bt);

        rescueStartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(getActivity().getPackageName() + ParseUser.getCurrentUser().getEmail(), getActivity().MODE_PRIVATE).edit();

                if (!seedWordsEt.getText().toString().equals(""))
                    editor.putString("seedWords", seedWordsEt.getText().toString());
                editor.commit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DeterministicSeed seed = null;
                        try {
                            seed = new DeterministicSeed(seedWordsEt.getText().toString(), null, "", 0);
                        } catch (UnreadableWalletException e) {
                            e.printStackTrace();
                        }

                        Wallet wallet = Wallet.fromSeed(params, seed);
                        //Wallet.SendRequest.

                        ChildNumber number = new ChildNumber(0, true);
                        ArrayList<ChildNumber> list = new ArrayList<ChildNumber>();
                        list.add(number);

                        String bitcoinAddress = wallet.getKeyByPath(list).toAddress(params).toString();

                        ApplicationConfig.getConfig().setBitcoinAddress(bitcoinAddress);
                    }
                }).start();

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

}

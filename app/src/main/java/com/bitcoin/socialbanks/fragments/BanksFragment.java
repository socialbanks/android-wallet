package com.bitcoin.socialbanks.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bitcoin.socialbanks.Model.Wallet;
import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.adapters.BanksAdapter;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class BanksFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;

    private AbsListView mListView;

    private ArrayList<Wallet> listWallets = new ArrayList<Wallet>();
    private BanksAdapter mAdapter;

    ApplicationConfig appConfig;

    ParseUser user;

    ParseQuery<ParseObject> queryCloud;
    ParseQuery<ParseObject> queryLocal;

    AddFloatingActionButton addButtom;


    // TODO: Rename and change types of parameters
    public static BanksFragment newInstance(String param1, String param2) {
        BanksFragment fragment = new BanksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BanksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConfig = ApplicationConfig.getConfig();
        mAdapter = new BanksAdapter(getActivity(), R.layout.item_list_bank, listWallets);

        String bitcoinAddress = appConfig.getBitcoinAddress();
        queryLocal = new ParseQuery("Wallet");
        queryLocal.fromLocalDatastore();
        queryLocal.include("socialBank");
        queryLocal.whereEqualTo("user", ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId()));
        queryLocal.whereEqualTo("bitcoinAddress",bitcoinAddress);
        queryLocal.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                listLocalObjects = list;
                if (e == null) {
                    ParseObject socialB = new ParseObject("SocialBank");

                    listWallets.clear();
                    for (ParseObject obj : list) {

                        socialB = (ParseObject) obj.get("socialBank");

                        ParseFile logoBSocial = null;
                        if (socialB != null)
                            logoBSocial = socialB.getParseFile("image");

                        String btcAddress = obj.getString("bitcoinAddress");
                        double balance = obj.getDouble("balance");

                        listWallets.add(new Wallet(obj, obj.getObjectId(), socialB, btcAddress, balance));
                    }
                    mAdapter.notifyDataSetChanged();

                }
            }
        });



    }

    List<ParseObject> listLocalObjects;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_banks, container, false);

        user = ParseUser.getCurrentUser();

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);


        addButtom = (AddFloatingActionButton) view.findViewById(R.id.add_wallet_bt);

        addButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                appConfig.getRootActivity().switchFragment(SearchBankFragment.newInstance("", ""));
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Wallet wallet = mAdapter.getItem(position);

        appConfig.setBufferWallet(wallet.getParseObject());
        appConfig.getRootActivity().switchFragment(BankDetailFragment.newInstance(wallet.getIdObject()));


    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public void onPause() {
        super.onPause();


        if (queryLocal != null)
            queryLocal.cancel();
        if (queryCloud != null)
            queryCloud.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        String bitcoinAddress = appConfig.getBitcoinAddress();
        queryCloud = new ParseQuery("Wallet");
        queryCloud.include("socialBank");
        queryCloud.whereEqualTo("user", ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId()));
        queryCloud.whereEqualTo("bitcoinAddress",bitcoinAddress);
        queryCloud.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {

                if (e == null) {

                    ParseObject socialB;

                    ParseObject.unpinAllInBackground(listLocalObjects, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            ParseObject.pinAllInBackground(list);
                        }
                    });

                    listWallets.clear();
                    for (ParseObject obj : list) {

                        socialB = (ParseObject) obj.get("socialBank");
                        String btcAddress = obj.getString("bitcoinAddress");
                        double balance = obj.getDouble("balance");

                        listWallets.add(new Wallet(obj, obj.getObjectId(), socialB, btcAddress, balance));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}

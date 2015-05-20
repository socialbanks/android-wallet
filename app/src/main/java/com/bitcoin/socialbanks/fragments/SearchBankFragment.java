package com.bitcoin.socialbanks.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bitcoin.socialbanks.Model.SearchBankModel;
import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.adapters.SearchBankAdapter;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class SearchBankFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;



    EditText emailEt;
    Button searchBt;
    ListView listView;
    ProgressBar progressBar;
    String socialBankId;


    ArrayList<SearchBankModel> usersList = new ArrayList<>();

    SearchBankAdapter adapter;
    ApplicationConfig appConfig;

    public static SearchBankFragment newInstance(String param1, String param2) {
        SearchBankFragment fragment = new SearchBankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    public SearchBankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            socialBankId = mParam2;
        }

        appConfig = ApplicationConfig.getConfig();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_bank, container, false);

        emailEt = (EditText) rootView.findViewById(R.id.send_fragment_email_et);
        searchBt = (Button) rootView.findViewById(R.id.send_fragment_search_bt);
        listView = (ListView) rootView.findViewById(R.id.send_fragment_listview);
        progressBar = (ProgressBar) rootView.findViewById(R.id.send_fragment_progress);


        adapter = new SearchBankAdapter(getActivity(), R.layout.item_list_search_bank, usersList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(selecUserListener);
        searchBt.setOnClickListener(searchListener);

        return rootView;
    }

    AdapterView.OnItemClickListener selecUserListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            final SearchBankModel searchSB = adapter.getItem(i);


            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
            build.setTitle("Confirm?");
            build.setMessage(searchSB.getName());
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    final ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Building...");
                    dialog.setCancelable(false);
                    dialog.show();

                    final ParseObject wallet = new ParseObject("Wallet");
                    wallet.put("balance", 0);
                    wallet.put("wif_remove",appConfig.getWifRemore());
                    wallet.put("bitcoinAddress", appConfig.getBitcoinAddress());
                    wallet.put("socialBank", ParseObject.createWithoutData("SocialBank", searchSB.getObj().getObjectId()));
                    //       wallet.put("user", ParseUser.getCurrentUser());
                    wallet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                getActivity().onBackPressed();
                            } else {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });


                }
            });
            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            build.show();
        }
    };

    View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            progressBar.setVisibility(View.VISIBLE);
            final String nameSB = emailEt.getText().toString();

            ParseQuery query = new ParseQuery("SocialBank");
            query.whereContains("name", nameSB);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {

                    progressBar.setVisibility(View.GONE);

                    if (e == null) {

                        usersList.clear();
                        for (ParseObject obj : list) {
                            usersList.add(new SearchBankModel(obj));
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    };

}

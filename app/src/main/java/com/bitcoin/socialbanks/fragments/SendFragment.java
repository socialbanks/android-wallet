package com.bitcoin.socialbanks.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bitcoin.socialbanks.Model.SendUser;
import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.adapters.SendAdapter;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;



    EditText emailEt;
    Button searchBt;
    ListView listView;
    ProgressBar progressBar;
    String socialBankId;


    ArrayList<SendUser> usersList = new ArrayList<>();

    SendAdapter adapter;

    public static SendFragment newInstance(String param1, String param2) {
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    public SendFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send, container, false);

        emailEt = (EditText) rootView.findViewById(R.id.send_fragment_email_et);
        searchBt = (Button) rootView.findViewById(R.id.send_fragment_search_bt);
        listView = (ListView) rootView.findViewById(R.id.send_fragment_listview);
        progressBar = (ProgressBar) rootView.findViewById(R.id.send_fragment_progress);


        adapter = new SendAdapter(getActivity(), R.layout.item_list_send, usersList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(selecUserListener);
        searchBt.setOnClickListener(searchListener);

        return rootView;
    }


    AdapterView.OnItemClickListener selecUserListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            SendUser sendUser = adapter.getItem(i);

            ParseObject wallet = sendUser.getWalletParse();

            JSONObject json = new JSONObject();
            try {
                json.put("bitcoin", wallet.get("bitcoinAddress"));
                json.put("receiverDescription", "recebido de " + ParseUser.getCurrentUser().getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApplicationConfig.getConfig().getRootActivity().switchFragment(BuyFragment.newInstance(mParam1,json.toString()));


        }
    };

    View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            progressBar.setVisibility(View.VISIBLE);
            final String subEmail = emailEt.getText().toString();

            ParseQuery userQueryFilter = new ParseQuery("User");
            userQueryFilter.whereContains("email",subEmail);

            ParseQuery query = new ParseQuery("Wallet");
            query.include("user");
            query.whereEqualTo("socialBank", ParseObject.createWithoutData("SocialBank", socialBankId));
       //     query.whereMatchesQuery("user",userQueryFilter);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {

                    progressBar.setVisibility(View.GONE);

                    if (e == null) {

                        usersList.clear();

                        for (ParseObject obj : list) {
                            ParseObject user = obj.getParseObject("user");
                            ParseObject socialB = obj.getParseObject("socialBank");

                            if (user.getString("email").toLowerCase().contains(subEmail.toLowerCase())) {

                                Log.v("send", "send user" + user.toString());

                                usersList.add(new SendUser(user,obj));

                            }
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

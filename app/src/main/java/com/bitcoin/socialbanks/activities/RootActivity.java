package com.bitcoin.socialbanks.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.bitcoin.socialbanks.fragments.BanksFragment;
import com.parse.FunctionCallback;
import com.parse.LogOutCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;


public class RootActivity extends ActionBarActivity {

    //  private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    ApplicationConfig appConfig;

    private int currtentPosition = 0;

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_nodrawer);

        user = ParseUser.getCurrentUser();

        appConfig = ApplicationConfig.getConfig();
        appConfig.setRootActivity(this);

        /*mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);*/

        mTitle = getTitle();

        // Set up the drawer.
      /*  mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));*/

        final ParseUser user = ParseUser.getCurrentUser();

        boolean fbNewUser = true;

        if (fbNewUser) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Para logar com email e senha, � necess�rio definir uma senha")
                    .setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setNegativeButton("Agora n�o", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            builder.show();
        }


        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("address", "1FTuKcjGUrMWatFyt8i1RbmRzkY2V9TDMG");

        ParseCloud.callFunctionInBackground("get_balances", params, new FunctionCallback<Map<String, Object>>() {
            public void done(Map<String, Object> mapObject, ParseException e) {

                Log.v("cloud", "parsecloud");

            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, BanksFragment.newInstance("", ""))
                .commit();
    }

    /*@Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        if(position == currtentPosition)
            return;
        currtentPosition = position;
        switch (position) {
            case 0:

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BanksFragment.newInstance("", ""))
                        .commit();
                break;
            case 1:
                break;
            case 2:


                break;
        }
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }*/

    public void switchFragment(Fragment frag) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.replace(R.id.container, frag);
        fragmentTransaction.commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.root, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            final ProgressDialog diag = new ProgressDialog(RootActivity.this);
            diag.setMessage("Saindo...");
            diag.show();
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    diag.dismiss();
                    Intent i = new Intent(RootActivity.this, LoginActivity.class);
                    startActivity(i);
                }

            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void goToPrimaryAccess() {
        Intent i = new Intent(RootActivity.this, WordsSecretsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}

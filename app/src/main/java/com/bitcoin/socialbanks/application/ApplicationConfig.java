package com.bitcoin.socialbanks.application;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.bitcoin.socialbanks.activities.RootActivity;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class ApplicationConfig extends Application {

    static public ApplicationConfig appConfig;

    private ParseObject bufferWallet;
    private RootActivity rootActivity;

    private Fragment lastFragment;
    private Fragment atualFragment;
    private String bitcoinAddress;
    private String wifRemore;


    public void setWifRemore(String wifRemore) {
        this.wifRemore = wifRemore;
    }

    public String getWifRemore() {
        return wifRemore;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appConfig = new ApplicationConfig();
        initSingletons();
    }

    private void initSingletons() {

        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this,
                "bCOd9IKjrpxCPGYQfyagabirn7pYFjYTvJqkq1x1",
                "ug8CJXOxrkKZXlHIGKYAMaINXX9gCb1kwMgMr0ye");


        FacebookSdk.sdkInitialize(getApplicationContext());

        ParseFacebookUtils.initialize(getApplicationContext());
        return;
    }

    static public ApplicationConfig getConfig() {
        return appConfig;
    }

    public RootActivity getRootActivity() {
        if (rootActivity != null)
            return rootActivity;
        else
            return null;
    }

    public void setRootActivity(RootActivity ra) {
        rootActivity = ra;
    }

    public ParseObject getBufferWallet() {
        return bufferWallet;
    }

    public void setBufferWallet(ParseObject bufferWallet) {
        this.bufferWallet = bufferWallet;
    }

    public Fragment getLastFragment() {
        return lastFragment;
    }

    public void setLastFragment(Fragment frag) {
        lastFragment = frag;
    }

    public String getBitcoinAddress() {
        return bitcoinAddress;
    }

    public void setBitcoinAddress(String bitcoinAddress) {
        this.bitcoinAddress = bitcoinAddress;
    }
}

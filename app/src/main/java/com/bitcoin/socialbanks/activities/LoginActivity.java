package com.bitcoin.socialbanks.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.common.collect.ImmutableList;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity {

    EditText emailTv;
    EditText passwordTv;

    Button loginBt;
    Button loginFacebookBt;
    Button newUserBt;


    Dialog diag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBt = (Button) findViewById(R.id.login_button);
        loginBt.setOnClickListener(myOnClicks);


        emailTv = (EditText) findViewById(R.id.login_email_et);
        passwordTv = (EditText) findViewById(R.id.login_password_et);

        emailTv.setOnFocusChangeListener(focusEt);
        passwordTv.setOnFocusChangeListener(focusEt);

        loginFacebookBt = (Button) findViewById(R.id.login_witch_facebook_bt);
        loginFacebookBt.setOnClickListener(myOnClicks);

        newUserBt = (Button) findViewById(R.id.login_new_register);
        newUserBt.setOnClickListener(myOnClicks);

        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {

            SharedPreferences prefs = getSharedPreferences(getPackageName() + user.getEmail(), MODE_PRIVATE);
            final String restoredWords = prefs.getString("seedWords", null);


            if (restoredWords == null) {
                goToPrimaryAccess();
            } else {

                final NetworkParameters params = MainNetParams.get();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DeterministicSeed seed = null;
                        try {
                            seed = new DeterministicSeed(restoredWords, null, "", 0);
                        } catch (UnreadableWalletException e) {
                            e.printStackTrace();
                        }

                        Wallet wallet = Wallet.fromSeed(params, seed);
                        //Wallet.SendRequest.

                        ChildNumber number = new ChildNumber(0, true);
                        ArrayList<ChildNumber> list = new ArrayList<ChildNumber>();
                        list.add(number);

                        String bitcoinAddress = wallet.getKeyByPath(list).toAddress(params).toString();

                        Log.v("Login","BitCoin words -> " + restoredWords);
                        Log.v("Login","BitCoin address -> " + bitcoinAddress);
                        ApplicationConfig.getConfig().setBitcoinAddress(bitcoinAddress);
                    }
                }).start();


                goToRootActivity();
            }
        }

      //  rescueSeed();

    }


    View.OnFocusChangeListener focusEt = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus)
                ((EditText) v).setError(null);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }


    View.OnClickListener myOnClicks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.login_button:

                    QRCodeWriter qrCodeWriter = new QRCodeWriter();

                    try {
                        BitMatrix byteMatrix = qrCodeWriter.encode("teste", BarcodeFormat.QR_CODE, 300, 300);

                        QRCode code = new QRCode();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                    String email = emailTv.getText().toString();
                    String password = passwordTv.getText().toString();

                    if (email == null || email.equals("")) {
                        emailTv.setError("É necessario preencher o usuario");
                        break;
                    }

                    if (password == null || password.equals("")) {
                        passwordTv.setError("� necessario preencher a senha");
                        break;
                    }

                    showDialog("", "Logando");
                    ParseUser.logInInBackground(email, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {

                            if (e == null) {
                                goToPrimaryAccess();
                            } else {
                                if (diag != null)
                                    diag.dismiss();

                                AlertDialog.Builder adiag = new AlertDialog.Builder(LoginActivity.this);
                                adiag.setTitle("Aviso!");
                                adiag.setMessage("Nãjoaoo foi possivel logar");
                                adiag.show();
                            }
                        }
                    });


                    break;

                case R.id.login_witch_facebook_bt:

                    loginFacebook();
                    showDialog("", "Logando");

                    break;

                case R.id.login_new_register:


                    Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(i);


                    break;
            }
        }
    };


    public void loginFacebook() {

        final List<String> permissions = Arrays.asList("public_profile", "email");


        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException err) {

                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");

                    Toast.makeText(getApplicationContext(), "Log-out from Facebook and try again please!", Toast.LENGTH_SHORT).show();

                    ParseUser.logOut();

                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");


                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {

                                    try {
                                        String name = object.getString("name");
                                        String email = object.getString("email");

                                        user.setUsername(name);
                                        user.setEmail(email);
                                        user.saveInBackground();

                                        goToPrimaryAccess();
                                        if (diag != null)
                                            diag.dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                } else {
                    Log.d("MyApp", "User logged in through Facebook!");

                    goToPrimaryAccess();

                }
            }
        });
    }

    private void showDialog(String title, String message) {
        diag = ProgressDialog.show(this, title, message);
        diag.show();
    }

    private void goToRootActivity() {
        Intent i = new Intent(LoginActivity.this, RootActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void goToPrimaryAccess() {
        Intent i = new Intent(LoginActivity.this, WordsSecretsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void createwallet() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("SocialBank");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                ParseObject obj = new ParseObject("Wallet");
                obj.put("socialBank", list.get(0));
                obj.put("user", ParseUser.getCurrentUser());
                obj.put("balance", 0.0);
                obj.saveInBackground();

            }
        });
    }

    public void rescueSeed() {
        final NetworkParameters params = MainNetParams.get();

        // Bitcoinj supports hierarchical deterministic wallets (or "HD Wallets"): https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
        // HD wallets allow you to restore your wallet simply from a root seed. This seed can be represented using a short mnemonic sentence as described in BIP 39: https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki

        // Here we restore our wallet from a seed with no passphrase. Also have a look at the BackupToMnemonicSeed.java example that shows how to backup a wallet by creating a mnemonic sentence.

        String seedCode = "select scout crash enforce riot rival spring whale hollow radar rule sentence";

        String passphrase = "";

        DeterministicSeed seed = null;
        try {
            seed = new DeterministicSeed(seedCode, null, passphrase, 0);
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }


        Wallet wallet = Wallet.fromSeed(params, seed);
        //Wallet.SendRequest.

        ChildNumber number = new ChildNumber(0, true);
        ArrayList<ChildNumber> list = new ArrayList<ChildNumber>();
        list.add(number);

        String privKeyClient1 = "KxyACdWtFEY6p2nAbSAZv9NXgmJNm4i6HDUjgoy1YtVFTskV75KX";
        String pubKeyServer = "0213cc3e8aa13da9fdced6ac55737984b71a0ea6a9c1817cc15f687163813e44c8";

        DumpedPrivateKey pk = null;
        try {
            pk = new DumpedPrivateKey(params, privKeyClient1);
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

        BigInteger pkey = new BigInteger(pubKeyServer, 16);

        ECKey clientKey = pk.getKey();
        ECKey serverKey = ECKey.fromPublicOnly(pkey.toByteArray());


        Transaction spendTx = new Transaction(params);
        Script multisigScript = ScriptBuilder.createP2SHOutputScript(2, Arrays.asList(clientKey, serverKey));


        Sha256Hash sha256Hash = new Sha256Hash("0d2d6157088dc1c2efd826dbba2d647df2dcedaa3f0cd13834bc030d9552173d");
        TransactionOutPoint outPoint = new TransactionOutPoint(params, 0, sha256Hash);

        TransactionInput input = new TransactionInput(params, null, multisigScript.getProgram(), outPoint);
        spendTx.addInput(input);

        Log.v("Transaction", "Transaction: " + Hex.toHexString(spendTx.bitcoinSerialize()));

        //output
        Address address = null;
        try {
            address = new Address(params, "1FTuKcjGUrMWatFyt8i1RbmRzkY2V9TDMG");
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
        Script outputScript = ScriptBuilder.createOutputScript(address);

        Log.v("cloud code example", "Wallet multsig address: " + multisigScript.getToAddress(params));

        Address addressChange = multisigScript.getToAddress(params, true);
        Script outputScriptChange = ScriptBuilder.createOutputScript(addressChange);

        spendTx.addOutput(Coin.valueOf(10000), outputScript);
        spendTx.addOutput(Coin.valueOf(10000), outputScriptChange);

        Log.v("cloud code example", "Wallet multsig serialize: " + Hex.toHexString(spendTx.bitcoinSerialize()));


        Sha256Hash sighash = spendTx.hashForSignature(0, multisigScript, Transaction.SigHash.ALL, false);


        //   Create p2sh multisig input script (client key only)
        ECKey.ECDSASignature party1Signature = clientKey.sign(sighash);
        TransactionSignature party1TransactionSignature = new TransactionSignature(party1Signature, Transaction.SigHash.ALL, false);
        Script inputScript = ScriptBuilder.createP2SHMultiSigInputScript(ImmutableList.of(party1TransactionSignature), multisigScript);


        input.setScriptSig(inputScript);

        Log.v("cloud code example", "Wallet multsig serialize: " + Hex.toHexString(spendTx.bitcoinSerialize()));


        //  spendTx.addin
        //   TransactionOutput input = new TransactionOutput(params,null, Coin.CENT,inputScript.getProgram());


    /*    OutPoint(txid = < constante >, idx)
        script = inputScript */

        //     spendTx.addInput(input);
        //  spendTx.addInput(sha256Hash,0,inputScript);



























/*
        Script multisigScript = ScriptBuilder.createP2SHOutputScript(2, Arrays.asList(clientKey, serverKey));

       // byte[] bytes = Hex.decode("0d2d6157088dc1c2efd826dbba2d647df2dcedaa3f0cd13834bc030d9552173d");

        String test1 = "0d2d6157088dc1c2efd826dbba2d647df2dcedaa3f0cd13834bc030d9552173d";
        BigInteger teste = new BigInteger(test1,16);

        Sha256Hash txHash = Sha256Hash.create(teste.toByteArray());
        TransactionOutPoint top = new TransactionOutPoint(params, 0, txHash);


        Transaction transaction = new Transaction(params);

        transaction.add
        TransactionOutput output = new TransactionOutput()

        transaction.addOutput(Coin.valueOf(00100000),multisigScript.getToAddress(params));
        transaction.addSignedInput(top, multisigScript, clientKey);
    //    transaction.add
        Transaction spendTx = new Transaction(params);

        Address address = null;
        try {
            address = new Address(params, "1FTuKcjGUrMWatFyt8i1RbmRzkY2V9TDMG");
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

        Script outputScript = ScriptBuilder.createOutputScript(address);
        spendTx.addOutput(output.getValue(), outputScript);
        spendTx.addInput(output);
        Sha256Hash sighash = spendTx.hashForSignature(0, multisigScript, Transaction.SigHash.ALL, false);
        ECKey.ECDSASignature party1Signature = clientKey.sign(sighash);
        TransactionSignature party1TransactionSignature = new TransactionSignature(party1Signature, Transaction.SigHash.ALL, false);

        Log.v("cloud code example", "Wallet multsig address: " + party1TransactionSignature.toString());
*/

 /*       Transaction contract = new Transaction(params);
        List<ECKey> keys = ImmutableList.of(clientKey, serverKey);
        Script script = ScriptBuilder.createP2SHOutputScript(2, keys);
        Coin amount = Coin.valueOf(1);
        contract.addOutput(amount, script);

        TransactionOutput multisigOutput = contract.getOutput(0);
        Script multisigScript = multisigOutput.getScriptPubKey();


        String hashTransactionOutput = "967f947b7f995d7f45c4ce1f6eb42baf58376d8f9ba768322d2abe858f3bd272";
        BigInteger hashBig = new BigInteger(hashTransactionOutput, 16);
        TransactionOutput out = new TransactionOutput(params, null, Coin.MICROCOIN, hashBig.toByteArray());


        Log.v("cloud code example", "Wallet multsig address: " + script.getToAddress(params));

        Coin value = multisigOutput.getValue();

        Address address = null;
        try {
            address = new Address(params, "1FTuKcjGUrMWatFyt8i1RbmRzkY2V9TDMG");
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
        Script outputScript = ScriptBuilder.createOutputScript(address);


        Transaction spendTx = new Transaction(params);
        spendTx.addOutput(out.getValue(), outputScript);
        spendTx.addInput(multisigOutput);

        Sha256Hash sighash = spendTx.hashForSignature(0, multisigScript, Transaction.SigHash.ALL, false);

        ECKey.ECDSASignature clientSign = clientKey.sign(sighash);

        Log.v("cloud code example", "Wallet Transaction signature: " + Hex.toHexString(clientSign.encodeToDER()));*/
// We have calculated a valid signature, so send it back to the client:
        //  sendToClientApp(signature);




   /*     Map<String, Object> map = new HashMap<String, Object>();
        map.put("source", bitcoinAddress);
        map.put("destination", "1Ko36AjTKYh6EzToLU737Bs2pxCsGReApK");
        map.put("quantity", 10^8);
        map.put("asset", "BRAZUCA");
        map.put("pubkey", pubKey);

        Log.v("cloud code example", "Wallet pubKey: " + pubKey);


        ParseCloud.callFunctionInBackground("send", map, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if (exc == null)
                    Log.v("cloud code example", "Wallet response: " + response);
                else
                    Log.v("cloud code example", "Wallet response error: " + exc.getMessage());

            }
        });*/
/*

        Address addr = null;
        try {
            addr = new Address(params,(String)"1Ko36AjTKYh6EzToLU737Bs2pxCsGReApK");
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

        Wallet.SendRequest request = Wallet.SendRequest.to(addr, Coin.valueOf(1));
        request.feePerKb = Coin.valueOf(0);
        Wallet.SendResult result = wallet.sendCoins()
        Log.v("", "Wallet fee ->" + request.feePerKb);
*/

        //      String teste = "010000000227d08e0e7d640677eb096ea280e664244cc91c2ad1aa307eb31e6e9241b645e7000000001976a9141c7ee8ce06b7ee1b5421afba5f93b37864cd21dc88acffffffff1b4e667cf0b715fa95be6baa6b50578fd9c3fa15fb0a5554aeb5f3991e672c64000000001976a9141c7ee8ce06b7ee1b5421afba5f93b37864cd21dc88acffffffff0436150000000000001976a914ce27246a0a6ca54dfa1f780ccd5cb3d0c73a75b288ac36150000000000001976a9142e9f1df92b565bf18d7f4aa1d902c4696bb773ac88ac36150000000000001976a9142c9f1df92b565bf18d334aa1d902c4696bab093188ac36150000000000001976a9141c7ee8ce06b7ee1b5421afba5f93b37864cd21dc88ac00000000";

        //13bfxC3tUnEWPknH69yafGnompvgu2defY
/*
        Transaction tx = new Transaction(params);



        tx.addOutput(Coin.ZERO, new ScriptBuilder().op(ScriptOpCodes.OP_RETURN).data(teste.getBytes()).build());

        Wallet.SendRequest request = Wallet.SendRequest.forTx(tx);
        request.feePerKb = Coin.ZERO;

        Log.v("", "Wallet balance ->" +  wallet.getBalance());

        wallet.signTransaction(request);

        try {
            Transaction result = wallet.sendCoinsOffline(request);

           for(TransactionOutput out : result.getOutputs()){

               Log.v("", "Wallet out ->" + bytesToHexString(out.getScriptBytes()));


           }

        } catch (Exception e) {
            Log.v("", "Wallet out error ->" + e.getMessage());
        }
/*


        TransactionConfidence tc = new TransactionConfidence()
        Transaction transaction = new Transaction(params);
        TransactionInput input = new TransactionInput(params,transaction,teste.getBytes());
        transaction.addInput(input);
        Wallet.SendRequest send = Wallet.SendRequest.forTx(transaction);
        wallet.setTransactionBroadcaster(new TransactionBroadcaster() {
            @Override
            public ListenableFuture<Transaction> broadcastTransaction(Transaction transaction) {
                Log.v("", "Wallet bcaster ->");

                return null;
            }
        });

        send.feePerKb = Coin.valueOf(0);
        try {
            wallet.sendCoins(send);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
*/

        //  TransactionInput ti = new TransactionInput()
        //     wallet.getKeyByPath(list);
        //     Log.v("", "Wallet path ->" + bitcoinAddress);



      /*  Transaction tr = new Transaction(params,teste.getBytes(),teste.length()-1);
        Wallet.SendRequest send = Wallet.SendRequest.to();
        send.fee.longValue();



        Log.v("", "Wallet fee ->" + send.fee.longValue());
*/
    }

    public String openFileToString(byte[] _bytes) {
        String file_string = "";

        for (int i = 0; i < _bytes.length; i++) {
            file_string += (char) _bytes[i];
        }

        return file_string;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    private BigInteger sum(String newNumber) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(newNumber.getBytes());
        return new BigInteger(md.digest());
    }


}


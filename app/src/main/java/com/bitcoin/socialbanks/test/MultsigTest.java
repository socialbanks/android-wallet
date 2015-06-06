package com.bitcoin.socialbanks.test;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.common.collect.ImmutableList;

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
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;


/**
 * Created by fabriciomatos on 6/6/15.
 */
public class MultsigTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 5;
        final int reality = 5;
        assertEquals(expected, reality);
    }

    // Create an multsig P2SH address
    // More information about that in https://bitcoin.org/en/developer-guide#multisig
    public void testCreateMultsigAddress() throws Exception {
        final NetworkParameters params = MainNetParams.get();
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

        Script redeemScript = ScriptBuilder.createP2SHOutputScript(2, Arrays.asList(clientKey, serverKey));
        Address addressChange2 = redeemScript.getToAddress(params, true);

        assertEquals("3Qx7v3AQshdKGCqu81QYtkQFDwHKDqaNBi", addressChange2.toString());
    }

    public void testCreateAndSignClientTransaction() throws Exception {
        final NetworkParameters params = MainNetParams.get();
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

        Script redeemMultisigScript = ScriptBuilder.createP2SHOutputScript(2, Arrays.asList(clientKey, serverKey));
        Address addressChange = redeemMultisigScript.getToAddress(params, true);


        ///////////// Create the transaction //////////////////

        Transaction spendTx = new Transaction(params);

        //prepare the input
        Sha256Hash sha256Hash = new Sha256Hash("b6cf378b95a14eef35b979c01fecbcf432f6fa220858d3c1c18ca1b5ea1741dd");
        TransactionOutPoint outPoint = new TransactionOutPoint(params, 0, sha256Hash);

        // Create p2sh multisig input script and sign input
        TransactionInput input = new TransactionInput(params, null, redeemMultisigScript.getProgram(), outPoint, Coin.valueOf(101000));
        spendTx.addInput(input);

        Sha256Hash sighash = spendTx.hashForSignature(0, redeemMultisigScript, Transaction.SigHash.ALL, false); //test
        ECKey.ECDSASignature party1Signature = clientKey.sign(sighash);
        TransactionSignature party1TransactionSignature = new TransactionSignature(party1Signature, Transaction.SigHash.ALL, false);

        Script inputScript = ScriptBuilder.createP2SHMultiSigInputScript(ImmutableList.of(party1TransactionSignature), redeemMultisigScript);
        input.setScriptSig(inputScript);

        //Log.v("Transaction", "Wallet signed Input: " + Hex.toHexString(spendTx.bitcoinSerialize()));

        //define the receiver address
        Address receiverAddress = null;
        try {
            receiverAddress = new Address(params, "1FTuKcjGUrMWatFyt8i1RbmRzkY2V9TDMG");
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

        Script outputScriptChange = ScriptBuilder.createOutputScript(addressChange);
        Script outputScript = ScriptBuilder.createOutputScript(receiverAddress);


        spendTx.addOutput(Coin.valueOf(1000), outputScriptChange);
        spendTx.addOutput(Coin.valueOf(100000), outputScript);

        //spendTx.addSignedInput(outPoint, scriptPublicKey, clientKey);
        //Log.v("cloud code example", "Wallet multsig serialize: " + Hex.toHexString(spendTx.bitcoinSerialize()));



        assertEquals("0100000001dd4117eab5a18cc1c1d3580822faf632f4bcec1fc079b935ef4ea1958b37cfb6000000009200483045022100a17271d87dc1ab36ebf9aa449cd1daae33aa4ad44b55f4a661b1a01e90b6411002200384b19d8246f8cdb8f5d7ac04a1e25730023dc912d57b1c9a8c70eb587787c8014752210213cc3e8aa13da9fdced6ac55737984b71a0ea6a9c1817cc15f687163813e44c82103d4e7ffa6ebedc601a5e9ca48b9d9110bef80c15ce45039a08a513801712579de52aeffffffff02e80300000000000017a914ff26223bbaa71dbaec1693059c1feb5d1e14b8f487a0860100000000001976a9149ea84056a5a9e294d93f11300be51d51868da69388ac00000000",
                Hex.toHexString(spendTx.bitcoinSerialize()));
    }




}

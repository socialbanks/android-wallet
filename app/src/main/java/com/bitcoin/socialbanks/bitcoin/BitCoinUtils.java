package com.bitcoin.socialbanks.bitcoin;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by Marcos L Cardoso on 20/05/2015.
 */
public class BitCoinUtils {


    static public String generateMultSigAddressFromPubKeys(String pubKeyClient, String pubKeyServer) {

        NetworkParameters params = MainNetParams.get();

        BigInteger pkeyClient = new BigInteger(pubKeyClient, 16);
        BigInteger pkeyServer = new BigInteger(pubKeyServer, 16);

        ECKey clientKey = ECKey.fromPublicOnly(pkeyClient.toByteArray());
        ECKey serverKey = ECKey.fromPublicOnly(pkeyServer.toByteArray());

        Script multisigScript = ScriptBuilder.createP2SHOutputScript(2, Arrays.asList(clientKey, serverKey));


        String multSigAddress = multisigScript.getToAddress(params).toString();

        return multSigAddress;
    }

    static public String getPrivkeyFromPrivateKeyWIF(String prvateKeyWIF) {

        NetworkParameters params = MainNetParams.get();

        DumpedPrivateKey pk = null;
        try {
            pk = new DumpedPrivateKey(params, prvateKeyWIF);
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

        if(pk == null)
            return "";

        ECKey clientKey = pk.getKey();

        String publickey = Hex.toHexString(clientKey.getPrivKeyBytes());

        return publickey;
    }

    static public String getPubkeyFromPrivateKeyWIF(String prvateKeyWIF) {

        NetworkParameters params = MainNetParams.get();

        DumpedPrivateKey pk = null;
        try {
            pk = new DumpedPrivateKey(params, prvateKeyWIF);
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }

        if(pk == null)
            return "";

        ECKey clientKey = pk.getKey();

        String publickey = Hex.toHexString(clientKey.getPubKey());

        return publickey;
    }
    static public String generatePrivKeyWIFFromPrivateKeyHex(String privateKeyHex) {

        NetworkParameters params = MainNetParams.get();
        BigInteger pkey = new BigInteger(privateKeyHex, 16);
        ECKey key = ECKey.fromPrivate(pkey);
        DumpedPrivateKey keyEncoded = key.getPrivateKeyEncoded(params);

        return keyEncoded.toString();
    }

}

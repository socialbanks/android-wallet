package com.bitcoin.socialbanks.test;

import android.test.InstrumentationTestCase;
import java.util.List;

import com.bitcoin.socialbanks.util.BitcoinUtils;

public class BitcoinUtilsTest extends InstrumentationTestCase {

    public void testFindUnspendTransactionsWorks() throws Exception {
        String address = "3Qx7v3AQshdKGCqu81QYtkQFDwHKDqaNBi";

        List<String> trans = BitcoinUtils.findUnspendTransactions(address);

        assertTrue(trans.size() > 0);
    }
}

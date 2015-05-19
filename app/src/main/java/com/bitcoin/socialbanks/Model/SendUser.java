package com.bitcoin.socialbanks.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Marcos L Cardoso on 16/05/2015.
 */
public class SendUser {

    String name;
    String email;
    Bitmap photo;
    ParseObject objParse;
    ParseObject walletParse;


    public SendUser(String nName, String nEmail, Bitmap nPhoto) {
        name = nName;
        email = nEmail;
        photo = nPhoto;
    }

    public SendUser(ParseObject obj,ParseObject wallet) {
        name = obj.getString("firstName") + " " + obj.getString("lastName");
        email = obj.getString("email");

        ParseFile file = obj.getParseFile("image");

        if(file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    photo = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    bitmap.recycle();
                }
            });
        }

        objParse = obj;
        walletParse = wallet;
    }

    public String getName() {
        return name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getEmail() {
        return email;
    }

    public ParseObject getObjParse() {
        return objParse;
    }

    public ParseObject getWalletParse() {
        return walletParse;
    }
}

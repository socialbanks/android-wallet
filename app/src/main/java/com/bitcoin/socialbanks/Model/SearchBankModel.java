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
public class SearchBankModel {

    String name;
    Bitmap image;
    ParseObject obj;

    public SearchBankModel(ParseObject obj){
        this.obj = obj;
        name = obj.getString("name");

        ParseFile file = obj.getParseFile("image");

        if(file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    bitmap.recycle();
                }
            });
        }
    }


    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public ParseObject getObj() {
        return obj;
    }
}

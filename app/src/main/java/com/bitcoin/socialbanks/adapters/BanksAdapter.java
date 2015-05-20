package com.bitcoin.socialbanks.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitcoin.socialbanks.Model.Wallet;
import com.bitcoin.socialbanks.R;
import com.bitcoin.socialbanks.fragments.SendFragment;
import com.bitcoin.socialbanks.application.ApplicationConfig;
import com.bitcoin.socialbanks.fragments.BankDetailFragment;
import com.bitcoin.socialbanks.fragments.QrCodeScannerFragment;
import com.bitcoin.socialbanks.fragments.ReceiveFragment;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;
import java.util.Locale;

public class BanksAdapter extends ArrayAdapter<Wallet> {


    static class ViewHolderItem {

        TextView title;
        TextView balance;

        Button pagarConta;
        Button enviar;
        Button receber;
        Button extrato;

        ImageView logoIV;
    }

    public BanksAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BanksAdapter(Context context, int resource, List<Wallet> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolderItem viewHolder;

        final Wallet p = getItem(position);


        if (convertView == null) {

            LayoutInflater vi = LayoutInflater.from(getContext());

            convertView = vi.inflate(R.layout.item_list_bank, null);

            viewHolder = new ViewHolderItem();

            viewHolder.title = (TextView) convertView.findViewById(R.id.item_list_bank_name_tv);
            viewHolder.balance = (TextView) convertView.findViewById(R.id.item_list_bank_name_balance_tv);

            viewHolder.pagarConta = (Button) convertView.findViewById(R.id.item_bank_pagar_conta_bt);
            viewHolder.enviar = (Button) convertView.findViewById(R.id.item_bank_pagar_bt);
            viewHolder.receber = (Button) convertView.findViewById(R.id.item_bank_receber_bt);
            viewHolder.extrato = (Button) convertView.findViewById(R.id.item_bank_extrato_bt);

            viewHolder.logoIV = (ImageView) convertView.findViewById(R.id.item_list_search_photo_et);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        if (p != null) {


            // ImageView image = (ImageView) v.findViewById(R.id.item_list_half_price_img);


            //  TextView smallDescription = (TextView) v.findViewById(R.id.item_list_half_price_small_description);


            if (viewHolder.title != null) {
                viewHolder.title.setText(p.getName());
            }
            if (viewHolder.balance != null) {

                Double valor = Double.valueOf(String.format(Locale.US, "%.2f", p.getBalance() / 100));

                viewHolder.balance.setText("" + valor);
            }
            if (viewHolder.logoIV != null) {

                if (p.getSocialBank() != null) {
                    ParseFile file = p.getSocialBank().getParseFile("image");

                    if(file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {

                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                viewHolder.logoIV.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));


                                bitmap.recycle();
                            }
                        });
                    }
                }

            }

            p.getIdObject();

            viewHolder.pagarConta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationConfig.getConfig().getRootActivity().switchFragment(QrCodeScannerFragment.newInstance(p.getIdObject()));

                }
            });
            viewHolder.enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationConfig.getConfig().setBufferWallet(p.getParseObject());
                    ApplicationConfig.getConfig().getRootActivity().switchFragment(SendFragment.newInstance(p.getIdObject(), p.getSocialBank().getObjectId()));

                }
            });
            viewHolder.receber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ApplicationConfig.getConfig().getRootActivity().switchFragment(ReceiveFragment.newInstance(p.getBitcoinAdress()));
                }
            });
            viewHolder.extrato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationConfig.getConfig().setBufferWallet(p.getParseObject());
                    ApplicationConfig.getConfig().getRootActivity().switchFragment(BankDetailFragment.newInstance(p.getIdObject()));

                }
            });
        }
        return convertView;
    }

}

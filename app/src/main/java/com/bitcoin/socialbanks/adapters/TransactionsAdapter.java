package com.bitcoin.socialbanks.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bitcoin.socialbanks.Model.Transaction;
import com.bitcoin.socialbanks.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionsAdapter extends ArrayAdapter<Transaction> {

    Context context;


    static class ViewHolderItem {

        TextView description;
        TextView balance;
        TextView date;

    }

    public TransactionsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;

    }

    public TransactionsAdapter(Context context, int resource, List<Transaction> items) {
        super(context, resource, items);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Transaction p = getItem(position);

        ViewHolderItem viewHolder;

        if (convertView == null) {

            LayoutInflater vi = LayoutInflater.from(getContext());

            convertView = vi.inflate(R.layout.item_list_transaction, null);

            viewHolder = new ViewHolderItem();

            viewHolder.description = (TextView) convertView.findViewById(R.id.item_list_transaction_description_tv);
            viewHolder.balance = (TextView) convertView.findViewById(R.id.item_list_transaction_balance_tv);
            viewHolder.date = (TextView) convertView.findViewById(R.id.item_list_transaction_date_tv);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.description.setText(p.getDescription());

        Double valor = Double.valueOf(String.format(Locale.US, "%.2f",p.getValue()/100));

        viewHolder.balance.setText("R$ " + valor);

        String[] meses = context.getResources().getStringArray(R.array.meses);

        Calendar cld = Calendar.getInstance();
        cld.setTime(p.getDateTransaction());

        int currentDaycmp = cld.get(Calendar.DAY_OF_MONTH);
        int currentMonthcmp = cld.get(Calendar.MONTH);
        int currentYearcmp = cld.get(Calendar.YEAR);

        String data = currentDaycmp + " de " + meses[currentMonthcmp - 1] + " de " + currentYearcmp;

        if (viewHolder.date != null) {
            viewHolder.date.setText(data);
        }

        if (p.getValue() < 0.0) {
            viewHolder.description.setTextColor(Color.RED);
            viewHolder.balance.setTextColor(Color.RED);
        } else {
            viewHolder.description.setTextColor(Color.BLACK);
            viewHolder.balance.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}

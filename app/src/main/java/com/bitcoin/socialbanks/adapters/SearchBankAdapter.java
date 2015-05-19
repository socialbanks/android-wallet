package com.bitcoin.socialbanks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitcoin.socialbanks.Model.SearchBankModel;
import com.bitcoin.socialbanks.R;

import java.util.List;

public class SearchBankAdapter extends ArrayAdapter<SearchBankModel> {

    Context context;


    static class ViewHolderItem {

        TextView nameEt;
        ImageView photo;

    }

    public SearchBankAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;

    }

    public SearchBankAdapter(Context context, int resource, List<SearchBankModel> items) {
        super(context, resource, items);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchBankModel p = getItem(position);

        ViewHolderItem viewHolder;

        if (convertView == null) {

            LayoutInflater vi = LayoutInflater.from(getContext());

            convertView = vi.inflate(R.layout.item_list_search_bank, null);

            viewHolder = new ViewHolderItem();

            viewHolder.nameEt = (TextView) convertView.findViewById(R.id.item_list_search_name_et);
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.item_list_search_photo_et);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.nameEt.setText(p.getName());
        viewHolder.photo.setImageBitmap(p.getImage());

        return convertView;
    }
}

package com.bitcoin.socialbanks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitcoin.socialbanks.Model.SendUser;
import com.bitcoin.socialbanks.R;

import java.util.List;

public class SendAdapter extends ArrayAdapter<SendUser> {

    Context context;


    static class ViewHolderItem {

        TextView nameEt;
        TextView emailEt;
        ImageView photo;

    }

    public SendAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;

    }

    public SendAdapter(Context context, int resource, List<SendUser> items) {
        super(context, resource, items);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SendUser p = getItem(position);

        ViewHolderItem viewHolder;

        if (convertView == null) {

            LayoutInflater vi = LayoutInflater.from(getContext());

            convertView = vi.inflate(R.layout.item_list_send, null);

            viewHolder = new ViewHolderItem();

            viewHolder.nameEt = (TextView) convertView.findViewById(R.id.item_list_send_name_et);
            viewHolder.emailEt = (TextView) convertView.findViewById(R.id.item_list_send_email_et);
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.item_list_send_photo_et);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.nameEt.setText(p.getName());
        viewHolder.emailEt.setText(p.getEmail());
        viewHolder.photo.setImageBitmap(p.getPhoto());

        return convertView;
    }
}

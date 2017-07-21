package com.mog.kontax.kontax;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mateogarcia on 7/20/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListAdapterViewHolder> {

    @Override
    public ContactListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ContactListAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ContactListAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView icon;
        TextView firstName;
        TextView lastName;

        public ContactListAdapterViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.personIcon);
            firstName = (TextView) itemView.findViewById(R.id.firstName);
            lastName = (TextView) itemView.findViewById(R.id.lastName);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int clickedIndex = getAdapterPosition();
            // Toast.makeText(this, "CLICKED" + String.valueOf(clickedIndex), Toast.LENGTH_LONG).show();
        }
    }
}

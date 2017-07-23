package com.mog.kontax.kontax;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mog.kontax.kontax.databinding.ContactListItemBinding;

/**
 * Created by mateogarcia on 7/20/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListAdapterViewHolder> {

    private static final String TAG = ContactListAdapter.class.getSimpleName();

    final private ListItemClickListener mOnClickListener;

    private Contact[] mContacts;

    public ContactListAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public class ContactListAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView photoImageView;
        TextView firstNameTextView;
        TextView lastNameTextView;

        public ContactListAdapterViewHolder(View itemView) {
            super(itemView);

            photoImageView = (ImageView) itemView.findViewById(R.id.photoImageView);
            firstNameTextView = (TextView) itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = (TextView) itemView.findViewById(R.id.lastNameTextView);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int clickedIndex = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedIndex);
        }
    }

    @Override
    public ContactListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.contact_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ContactListAdapterViewHolder viewHolder = new ContactListAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mContacts.length;
    }

    @Override
    public void onBindViewHolder(ContactListAdapterViewHolder contactListAdapterViewHolder,
                                 int position) {
        Log.d(TAG, "#" + position);

        Contact contact = mContacts[position];
        // contactListAdapterViewHolder.photoImageView = // TODO: Set photo for contact in list

        String[] firstAndLastName = contact.getName().split(" ", 2);
        String firstName = firstAndLastName[0];
        String lastName = firstAndLastName[1];
        contactListAdapterViewHolder.firstNameTextView.setText(firstName);
        contactListAdapterViewHolder.firstNameTextView.setText(lastName);
    }

    public void setContacts(Contact[] contacts) {
        mContacts = contacts;
        notifyDataSetChanged();
    }
}

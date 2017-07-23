package com.mog.kontax.kontax;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mog.kontax.kontax.databinding.ActivityContactDetailBinding;

public class ContactDetailActivity extends AppCompatActivity {

    ActivityContactDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_detail);

        Log.d("contact detail", "APPEARED");

        Intent initiatingIntent = getIntent();
        if (initiatingIntent.hasExtra(Contact.class.getName())) {
            Contact contact = (Contact) initiatingIntent.getSerializableExtra(Contact.class.getName());

            Log.d("contact detail", "SET CONTACT");

            // TODO: Set contact photo.

            mBinding.tvName.setText(contact.getName());
            mBinding.tvPhone.setText(contact.getPhone());
            mBinding.tvEmail.setText(contact.getEmail());
        }
    }
}

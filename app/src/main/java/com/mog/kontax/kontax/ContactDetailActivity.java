package com.mog.kontax.kontax;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mog.kontax.kontax.databinding.ActivityContactDetailBinding;
import com.parse.GetDataCallback;
import com.parse.ParseException;

public class ContactDetailActivity extends AppCompatActivity {

    ActivityContactDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_detail);

        Intent initiatingIntent = getIntent();
        Bundle bundle = initiatingIntent.getExtras();
        Log.d("contact detail", "BUNDLE: " + bundle);

        Contact contact = bundle.getParcelable("BANANA");

        Log.d("contact detail", "APPEARED: " + contact.getName());

        mBinding.tvName.setText(contact.getName());
        mBinding.tvPhone.setText(contact.getPhone());
        mBinding.tvEmail.setText(contact.getEmail());

        // Load photo in background.
        contact.getImageFile().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException exception) {
                if (exception == null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    mBinding.ivPhoto.setImageBitmap(bitmap);

                    // Access the array of results here
                    Toast.makeText(getApplicationContext(), "Phew, loaded photo.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("contact detail", "Error: " + exception.getMessage());
                }
            }
        });
    }
}

package com.mog.kontax.kontax;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mog.kontax.kontax.databinding.ActivityNewContactBinding;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class NewContactActivity extends AppCompatActivity {

    ActivityNewContactBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveContact();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveContact() {
        Contact newContact = new Contact();

        String name = mBinding.nameEditText.getText().toString();
        newContact.setName(name);

        String phone = mBinding.phoneEditText.getText().toString();
        newContact.setPhone(phone);

        String email = mBinding.emailEditText.getText().toString();
        newContact.setEmail(email);

        newContact.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException exception) {
                if (exception == null) {
                    Toast.makeText(getApplicationContext(), "Contact saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("new contact", "Error: " + exception.getMessage());
                }
            }
        });
    }
}

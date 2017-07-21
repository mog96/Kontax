package com.mog.kontax.kontax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ContactListAdapter mAdapter;

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.tv_json_display);

        // mRecyclerView = (RecyclerView) findViewById(R.id.contactsRecyclerView);

        // LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // mRecyclerView.setLayoutManager(layoutManager);

        ParseQuery<Contact> query = ParseQuery.getQuery(Contact.class);

        query.findInBackground(new FindCallback<Contact>() {
            public void done(List<Contact> itemList, ParseException exception) {
                if (exception == null) {
                    // Access the array of results here
                    Contact firstContact = itemList.get(0);
                    Toast.makeText(MainActivity.this, firstContact.getName() + " and many others are now available.", Toast.LENGTH_SHORT).show();
                    mTextView.setText(itemList.toString());
                } else {
                    Log.d("item", "Error: " + exception.getMessage());
                }
            }
        });
    }
}

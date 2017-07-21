package com.mog.kontax.kontax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

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

        mTextView.setText("");
    }
}

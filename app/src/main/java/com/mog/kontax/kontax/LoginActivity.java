package com.mog.kontax.kontax;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.mog.kontax.kontax.databinding.ActivityLoginBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        mBinding.etEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mBinding.etPassword.requestFocus();
                }
                return false;
            }
        });

        mBinding.etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    signUp(mBinding.etEmail.getText().toString(), mBinding.etPassword.getText().toString());
                }
                return false;
            }
        });

        // If user is logged in, skip to main activity.
        if (ParseUser.getCurrentUser() != null) {
            Context context = LoginActivity.this;
            Class destinationActivity = MainActivity.class;
            Intent intent = new Intent(context, destinationActivity);
            startActivity(intent);
        }
    }

    public void signUp(String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);

        Toast.makeText(getApplicationContext(), "Signing up...", Toast.LENGTH_SHORT).show();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException exception) {
                if (exception == null) {
                    Context context = LoginActivity.this;
                    Class destinationActivity = MainActivity.class;
                    Intent intent = new Intent(context, destinationActivity);
                    startActivity(intent);
                } else {
                    Log.d("signup", "Error: " + exception.getMessage());

                    Toast.makeText(getApplicationContext(), "Signup failed :[\nPlease try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

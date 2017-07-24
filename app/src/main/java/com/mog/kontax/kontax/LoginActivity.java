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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

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
                    loginInformationSubmitted();
                }
                return false;
            }
        });

        // If user is logged in, skip to main activity.
        if (ParseUser.getCurrentUser() != null) {
            loginSuccess();
        }
    }

    private void loginInformationSubmitted() {
        String email = mBinding.etEmail.getText().toString();
        String password = mBinding.etPassword.getText().toString();
        if (mBinding.switchSignupToggle.isChecked()) {
            signUp(email, password);
        } else {
            logIn(email, password);
        }
    }

    private void signUp(String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);

        Toast.makeText(getApplicationContext(), "Signing up...", Toast.LENGTH_SHORT).show();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException exception) {
                if (exception == null) {
                    loginSuccess();
                } else {
                    Log.d("signup", "Error: " + exception.getMessage());

                    Toast.makeText(getApplicationContext(), "Signup failed :[\nPlease try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logIn(String email, String password) {
        Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException exception) {
                if (exception == null) {
                    loginSuccess();
                } else {
                    Log.d("login", "Error: " + exception.getMessage());

                    Toast.makeText(getApplicationContext(), "Login failed :[\nPlease try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginSuccess() {
        Context context = LoginActivity.this;
        Class destinationActivity = MainActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }
}

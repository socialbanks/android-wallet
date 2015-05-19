package com.bitcoin.socialbanks.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitcoin.socialbanks.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity {

    EditText firstNameEt;
    EditText lastNameEt;
    EditText emailEt;
    EditText userNameEt;
    EditText passwordEt;
    EditText repeatPasswordEt;

    Button registerBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameEt = (EditText) findViewById(R.id.register_fistname_et);
        lastNameEt = (EditText) findViewById(R.id.register_lastname_et);
        emailEt = (EditText) findViewById(R.id.register_email_et);
        userNameEt = (EditText) findViewById(R.id.register_username_et);
        passwordEt = (EditText) findViewById(R.id.register_password_et);
        repeatPasswordEt = (EditText) findViewById(R.id.register_repeat_password_et);

        registerBt = (Button) findViewById(R.id.register_bt);
        registerBt.setOnClickListener(registerListener);


    }

    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (firstNameEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this,getResources().getString(R.string.verify_registrer_firstname_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (lastNameEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this,getResources().getString(R.string.verify_registrer_lastname_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (emailEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this,getResources().getString(R.string.verify_registrer_email_error), Toast.LENGTH_LONG).show();
                return;
            }

            if (!isValidEmail(emailEt.getText().toString())) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_email_invalid_error), Toast.LENGTH_LONG).show();
                return;
            }

            if (userNameEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_username_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (passwordEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_password_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (repeatPasswordEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_repeat_password_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (!passwordEt.getText().toString().equals(repeatPasswordEt.getText().toString())) {
                Toast.makeText(RegisterActivity.this,getResources().getString(R.string.verify_registrer_diferent_password_error), Toast.LENGTH_LONG).show();
                return;
            }


            ParseUser newUser = new ParseUser();
            newUser.put("username",userNameEt.getText().toString());
            newUser.put("password",passwordEt.getText().toString());
            newUser.put("email",emailEt.getText().toString());
            newUser.put("firstName", firstNameEt.getText().toString());
            newUser.put("lastName", lastNameEt.getText().toString());

            final ProgressDialog pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Registrando...");
            pDialog.show();

            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {

                    if(e == null) {
                        pDialog.dismiss();

                        Intent i = new Intent(RegisterActivity.this, WordsSecretsActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else{
                        pDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });



        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}

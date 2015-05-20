package com.bitcoin.socialbanks.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitcoin.socialbanks.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class RegisterActivity extends Activity {

    EditText firstNameEt;
    EditText lastNameEt;
    EditText emailEt;
    EditText userNameEt;
    EditText passwordEt;
    EditText repeatPasswordEt;

    ImageView photo;

    Button registerBt;


    Bitmap uploadBitmap = null;

    private File dir, destImage, f;
    private String cameraFile = null;

    private static final int CAPTURE_FROM_CAMERA = 1;

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
        photo = (ImageView) findViewById(R.id.photoRegister);
        registerBt = (Button) findViewById(R.id.register_bt);
        registerBt.setOnClickListener(registerListener);

        photo.setOnClickListener(clickPhoto);


        if (ParseUser.getCurrentUser() != null) {
            ParseUser user = ParseUser.getCurrentUser();

            userNameEt.setText(user.getString("username"));
            passwordEt.setText(user.getString("password"));
            repeatPasswordEt.setText(user.getString("password"));
            emailEt.setText(user.getString("email"));
            firstNameEt.setText(user.getString("firstName"));
            lastNameEt.setText(user.getString("lastName"));

            ParseFile file = user.getParseFile("image");

            if (file != null) {
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
                        bitmap.recycle();
                        photo.setImageBitmap(bmp);
                    }
                });
            }
        }


    }


    View.OnClickListener clickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Dialog dialog = new Dialog(RegisterActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.choose_photo);

            Button cameraBt = (Button) dialog
                    .findViewById(R.id.choose_photo_camera_bt);

            Button galeryBt = (Button) dialog
                    .findViewById(R.id.choose_photo_galery_bt);

            cameraBt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    dir = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath(), "MyApp");
                    if (!dir.isDirectory())
                        dir.mkdir();

                    destImage = new File(dir, new Date().getTime() + ".jpg");
                    cameraFile = destImage.getAbsolutePath();
                    try {
                        if (!destImage.createNewFile())
                            Log.e("check", "unable to create empty file");

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    f = new File(destImage.getAbsolutePath());
                    Intent i = new Intent(
                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destImage));
                    startActivityForResult(i, 0);

                    dialog.dismiss();

                }
            });

            galeryBt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent pickPhoto = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                    dialog.dismiss();

                }
            });

            dialog.show();

        }
    };


    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (firstNameEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_firstname_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (lastNameEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_lastname_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (emailEt.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_email_error), Toast.LENGTH_LONG).show();
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
            if (passwordEt.getText().toString().equals("") && ParseUser.getCurrentUser() == null) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_password_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (repeatPasswordEt.getText().toString().equals("") && ParseUser.getCurrentUser() == null) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_repeat_password_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (!passwordEt.getText().toString().equals(repeatPasswordEt.getText().toString())) {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.verify_registrer_diferent_password_error), Toast.LENGTH_LONG).show();
                return;
            }

            final ParseUser newUser;

            if (ParseUser.getCurrentUser() == null)
                newUser = new ParseUser();
            else
                newUser = ParseUser.getCurrentUser();
            newUser.put("username", userNameEt.getText().toString());
            newUser.put("password", passwordEt.getText().toString());
            newUser.put("email", emailEt.getText().toString());
            newUser.put("firstName", firstNameEt.getText().toString());
            newUser.put("lastName", lastNameEt.getText().toString());

            final ProgressDialog pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating...");
            pDialog.show();


            if (uploadBitmap != null) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                uploadBitmap.compress(Bitmap.CompressFormat.PNG, 80,
                        stream);

                byte[] data = stream.toByteArray();

                final ParseFile file = new ParseFile("photo.jpg", data);

                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        newUser.put("photofile", file);


                    }
                });


            }

            if (ParseUser.getCurrentUser() == null) {
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            pDialog.dismiss();

                            Intent i = new Intent(RegisterActivity.this, WordsSecretsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                newUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent i = new Intent(RegisterActivity.this, RootActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
            }


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

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 0:

                if (resultCode == RESULT_OK) {
                    if (f == null) {
                        if (cameraFile != null)
                            f = new File(cameraFile);
                        else
                            Log.e("check", "camera file object null line no 279");
                    } else
                        Log.e("check", f.getAbsolutePath());
                    uploadBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                    photo.setImageBitmap(uploadBitmap);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    Bitmap bm = BitmapFactory.decodeFile(picturePath, options);
                    photo.setImageBitmap(bm);

                    uploadBitmap = bm;
                }
                break;
        }

    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}

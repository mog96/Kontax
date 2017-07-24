package com.mog.kontax.kontax;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mog.kontax.kontax.databinding.ActivityNewContactBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewContactActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_IMAGE = 2;

    ActivityNewContactBinding mBinding;

    private String mCurrentPhotoPath;

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

        // Sets owner to current user.
        newContact.setOwnerId();

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

    public void presentPhotoSelectionOptions(View view) {
        // dispatchImageCaptureIntent();
        dispatchPickImageIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("new contact", "ENTERED ACTIVITY RESULT");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("new contact", "HANDLE REQUEST_IMAGE_CAPTURE");

            // Photo has been written to the file name specified in mCurrentPhotoPath.
            // Set the photo on the image view using this helper, defined below.
            setPhotoImageViewImage();

        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Log.d("new contact", "HANDLE REQUEST_PICK_IMAGE");

            // URI of the photo selected by the user
            mCurrentPhotoPath = data.getData().getPath();
            setPhotoImageViewImage();
        }
    }

    // MARK: - Pick Image from Gallery

    private void dispatchPickImageIntent() {
        Intent intent = new Intent();
        // Show only images. No videos or anything else.
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available).
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
    }

    // MARK: - Capture Image with Camera

    private void dispatchImageCaptureIntent() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (imageCaptureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException exception) {
                // Error occurred while creating the File
                exception.printStackTrace();
                Log.d("image capture", "Error: " + exception.getMessage());
            }

            // Continue only if the File was successfully created
            if (photoFile == null) {
                Toast.makeText(getApplicationContext(), "An error occurred while saving your photo :[", Toast.LENGTH_LONG).show();
            } else {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mog.kontax.fileprovider",
                        photoFile);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(imageCaptureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents.
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPhotoImageViewImage() {
        // Get the dimensions of the View
        int targetW = mBinding.photoImageView.getWidth();
        int targetH = mBinding.photoImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.d("rotate image", "Error: " + exception.getMessage());
        }

        if (exifInterface == null) {
            Toast.makeText(getApplicationContext(), "An error occurred while saving your photo :[", Toast.LENGTH_LONG).show();
        } else {
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }

            mBinding.photoImageView.setImageBitmap(rotatedBitmap);
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}

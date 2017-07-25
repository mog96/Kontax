package com.mog.kontax.kontax;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewContactActivity extends AppCompatActivity implements SelectImageSourceDialogFragment.SelectImageSourceDialogListener {

    static final int RESULT_IMAGE_CAPTURE = 1;
    static final int RESULT_PICK_IMAGE = 2;

    ActivityNewContactBinding mBinding;

    private String mCurrentPhotoPath;
    private ParseFile mPhotoImageFile;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_contact);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {

            // Called when a new location is found by the network location provider.
            public void onLocationChanged(Location location) {
                mCurrentLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Call twice to get updates from both network _and_ GPS.
        // Second parameter is the minimum interval between notifications, third is the minimum
        // change in distance between notifications.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mLocationManager.removeUpdates(mLocationListener);
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
        mLocationManager.removeUpdates(mLocationListener);

        Contact newContact = new Contact();

        // Sets owner to current user.
        newContact.setOwnerId();

        String name = mBinding.nameEditText.getText().toString();
        newContact.setName(name);

        String phone = mBinding.phoneEditText.getText().toString();
        newContact.setPhone(phone);

        String email = mBinding.emailEditText.getText().toString();
        newContact.setEmail(email);

        if (mPhotoImageFile != null) {
            newContact.setPhotoImageFile(mPhotoImageFile);
        }

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

    // MARK: - Location

    // MARK: - Select Photo

    // TODO NOTE: How to select an image from the camera or the gallery.
    // Don't forget to add the necessary permissions. See AndroidManifest.xml
    public void presentPhotoSelectionOptions(View view) {
        DialogFragment dialogFragment = new SelectImageSourceDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "selectImageSource");
    }

    // MARK: - Receive User's Source Decision and Launch Camera/Gallery

    // Methods implemented from dialog interface. See SelectImageSourceDialogFragment.java
    @Override
    public void onTakePictureSelected() {
        dispatchImageCaptureIntent();
    }

    @Override
    public void onChoosePictureSelected() {
        dispatchPickImageIntent();
    }

    // Allows user to choose image source.
    private void dispatchPickImageIntent() {
        Intent intent = new Intent();
        // Show only images. No videos or anything else.
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available).
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_PICK_IMAGE);
    }

    // Opens camera app and passes it a filename for the captured photo.
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
                startActivityForResult(imageCaptureIntent, RESULT_IMAGE_CAPTURE);
            }
        }
    }

    // MARK: - Receive Captured/Selected Image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("new contact", "ENTERED ACTIVITY RESULT");

        if (requestCode == RESULT_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("new contact", "HANDLE REQUEST_IMAGE_CAPTURE");

            // Photo has been written to the file name specified in mCurrentPhotoPath.
            Bitmap imageBitmap = getImageViewSizedBitmap(mCurrentPhotoPath);

            // Photo is newly taken and has not been fully processed,
            // so we must rotate it ourselves.
            Bitmap rotatedBitmap = getRotatedImageBitmap(imageBitmap);
            saveImageFile(rotatedBitmap);
            mBinding.photoImageView.setImageBitmap(rotatedBitmap);

        } else if (requestCode == RESULT_PICK_IMAGE && resultCode == RESULT_OK) {
            Log.d("new contact", "HANDLE REQUEST_PICK_IMAGE");

            Uri selectedImage = data.getData();
            String selectedImagePath = getRealPathFromURI(getApplicationContext(), selectedImage);

            // Selected image is not guaranteed to have a standard file path, e.g. if selected from
            // Dropbox or another application.
            if (selectedImagePath == null) {
                retrievePhotoError();
                // FIXME: Handle case where image selected from application other than gallery.
            } else {
                Bitmap imageBitmap = getImageViewSizedBitmap(selectedImagePath);
                saveImageFile(imageBitmap);
                mBinding.photoImageView.setImageBitmap(imageBitmap);
            }
        }
    }

    // MARK: - Save Image File to Parse Object

    private void saveImageFile(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,
                100, stream);
        byte[] image = stream.toByteArray();
        mPhotoImageFile = new ParseFile(image);
    }

    // MARK: - ImageView Set Image Helpers

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String imagePath = null;

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                imagePath =  cursor.getString(column_index);
            }
            cursor.close();
        }
        return imagePath;
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

    private Bitmap getImageViewSizedBitmap(String imageFilePath) {
        // Get the dimensions of the View
        int targetW = mBinding.photoImageView.getWidth();
        int targetH = mBinding.photoImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Log.d("new contact", "SIZED BITMAP ");

        return BitmapFactory.decodeFile(imageFilePath, bmOptions);
    }

    private Bitmap getRotatedImageBitmap(Bitmap imageBitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.d("rotate image", "Error: " + exception.getMessage());
        }

        if (exifInterface == null) {
            retrievePhotoError();
            return null;

        } else {
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(imageBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(imageBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(imageBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }

            Log.d("new contact", "ROTATED BITMAP");

            return rotatedBitmap;
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void retrievePhotoError() {
        Toast.makeText(getApplicationContext(), "An error occurred while retreiving your photo :[", Toast.LENGTH_LONG).show();
    }
}

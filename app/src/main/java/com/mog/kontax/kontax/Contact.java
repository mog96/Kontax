package com.mog.kontax.kontax;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by mateogarcia on 7/21/17.
 */

@ParseClassName("Contact")
public class Contact extends ParseObject {
    // Ensure that your subclass has a public default constructor
    public Contact() {
        super();
    }

    // Add a constructor that contains core properties
    public Contact(String name) {
        super();
        setName(name);
    }

    // Use put to modify field values
    public void setOwnerId() {
        put("ownerId", ParseUser.getCurrentUser().getObjectId());
    }

    // Use getString and others to access fields
    public String getOwnerId() {
        return getString("ownerId");
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getName() {
        return getString("name");
    }

    public void setPhone(String value) {
        put("phone", value);
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setEmail(String value) {
        put("email", value);
    }

    public String getEmail() {
        return getString("email");
    }

    public void setPhotoImageFile(ParseFile imageFile) {
        put("image_file", imageFile);
    }

    public ParseFile getImageFile() {
        return getParseFile("image_file");
    }

    public void setWhereYouMet(ParseGeoPoint location) {
        put("where_you_met", location);
    }

    public ParseGeoPoint getWhereYouMet() {
        return getParseGeoPoint("where_you_met");
    }
}

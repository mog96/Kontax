package com.mog.kontax.kontax;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Created by mateogarcia on 7/21/17.
 */

@ParseClassName("Contact")
public class Contact extends ParseObject implements Serializable {
    // Ensure that your subclass has a public default constructor
    public Contact() {
        super();
    }

    // Add a constructor that contains core properties
    public Contact(String name) {
        super();
        setName(name);
    }

    public void setName(String value) {
        put("name", value);
    }

    public String getName() {
        return getString("name");
    }

    // Use getString and others to access fields
    public String getPhone() {
        return getString("phone");
    }

    // Use put to modify field values
    public void setPhone(String value) {
        put("phone", value);
    }

    // Use getString and others to access fields
    public String getEmail() {
        return getString("email");
    }

    // Use put to modify field values
    public void setEmail(String value) {
        put("email", value);
    }
}

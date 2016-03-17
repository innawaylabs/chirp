package com.mandalalabs.chirp.model;

import com.mandalalabs.chirp.utils.Constants;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName(Constants.TABLE_USER_DETAILS)
public class UserDetails extends ParseObject {

    private static final String FIRST_NAME = "firstName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String LAST_NAME = "lastName";
    private static final String CONTACT_NUMBER = "contactNumber";
    private static final String GENDER = "gender";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String ABOUT_ME = "aboutMe";

    public UserDetails() {}

    public String getFirstName() {
        return getString(FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        put(FIRST_NAME, firstName);
    }

    public String getMiddleName() {
        return getString(MIDDLE_NAME);
    }

    public void setMiddleName(String middleName) {
        put(MIDDLE_NAME, middleName);
    }

    public String getLastName() {
        return getString(LAST_NAME);
    }

    public void setLastName(String lastName) {
        put(LAST_NAME, lastName);
    }

    public String getGender() {
        return getString(GENDER);
    }

    public void setGender(String gender) {
        put(GENDER, gender);
    }

    public String getContactNumber() {
        return getString(CONTACT_NUMBER);
    }

    public void setContactNumber(String contactNumber) {
        put(CONTACT_NUMBER, contactNumber);
    }

    public Date getDateOfBirth() {
        return getDate(DATE_OF_BIRTH);
    }

    public void setDateOfBirth(Date dateOfBirth) {
        put(DATE_OF_BIRTH, dateOfBirth);
    }

    public String getAboutMe() {
        return getString(ABOUT_ME);
    }

    public void setAboutMe(String aboutMe) {
        put(ABOUT_ME, aboutMe);
    }
}

package com.mandalalabs.chirp.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.UserSession;
import com.mandalalabs.chirp.fragment.DatePickerFragment;
import com.mandalalabs.chirp.model.UserDetails;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public class ViewHolder {
        TextView tvUsername;
        EditText etFirstName;
        EditText etMiddleName;
        EditText etLastName;
        EditText etContactNumber;
        EditText etGender;
        EditText etDateOfBirth;
        EditText etAboutMe;
        UserDetails userDetails;
    }
    ViewHolder holder = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        String userId = getIntent().getStringExtra(Constants.USER_ID_KEY);
        ParseQuery<UserDetails> user = ParseQuery.getQuery(UserDetails.class);
        user.getInBackground(userId, new GetCallback<UserDetails>() {
            @Override
            public void done(UserDetails userDetails, ParseException e) {
                if (e == null) {
                    populateUserDetails(userDetails);
                } else {
//                    e.printStackTrace();
                    holder.userDetails = new UserDetails();
                }
            }
        });

        return super.onCreateView(parent, name, context, attrs);
    }

    private void populateUserDetails(UserDetails userDetails) {
        if (userDetails != null) {
            holder.userDetails = userDetails;
            holder.tvUsername = (TextView) findViewById(R.id.tvUsername);
            holder.etFirstName = (EditText) findViewById(R.id.etFirstName);
            holder.etMiddleName = (EditText) findViewById(R.id.etMiddleName);
            holder.etLastName = (EditText) findViewById(R.id.etLastName);
            holder.etContactNumber = (EditText) findViewById(R.id.etContactNumber);
            holder.etGender = (EditText) findViewById(R.id.etGender);
            holder.etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
            holder.etAboutMe = (EditText) findViewById(R.id.etAboutMe);

            holder.tvUsername.setText(UserSession.loggedInUser.getUsername());
            if (!(userDetails.getFirstName() == null || userDetails.getFirstName().isEmpty()))
                holder.etFirstName.setText(userDetails.getFirstName());
            if (!(userDetails.getMiddleName() == null || userDetails.getMiddleName().isEmpty()))
                holder.etMiddleName.setText(userDetails.getMiddleName());
            if (!(userDetails.getLastName() == null || userDetails.getLastName().isEmpty()))
                holder.etLastName.setText(userDetails.getLastName());
            if (!(userDetails.getContactNumber() == null || userDetails.getContactNumber().isEmpty()))
                holder.etContactNumber.setText(userDetails.getContactNumber());
            if (!(userDetails.getGender() == null || userDetails.getGender().isEmpty()))
                holder.etGender.setText(userDetails.getGender());

            if (userDetails.getDateOfBirth() != null) {
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                Calendar cal = Calendar.getInstance();
                cal.setTime(userDetails.getDateOfBirth());
                holder.etDateOfBirth.setText(df.format(cal.getTime()));
            }
            if (!(userDetails.getAboutMe() == null || userDetails.getAboutMe().isEmpty()))
                holder.etAboutMe.setText(userDetails.getAboutMe());
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onPasswordReset(View view) {

    }

    public void onSave(View view) {
        holder.userDetails.setFirstName(holder.etFirstName.getText().toString());
        holder.userDetails.setMiddleName(holder.etMiddleName.getText().toString());
        holder.userDetails.setLastName(holder.etLastName.getText().toString());
        holder.userDetails.setContactNumber(holder.etContactNumber.getText().toString());
        holder.userDetails.setGender(holder.etGender.getText().toString());
        // Date of birth is set by date picker as and when it changes
        holder.userDetails.setAboutMe(holder.etAboutMe.getText().toString());
        holder.userDetails.saveInBackground();

        setResult(RESULT_OK);
        finish();
    }

    public void onCancel(View view) {
        finish();
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        EditText etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        if (holder.userDetails != null)
            holder.userDetails.setDateOfBirth(cal.getTime());
        etDateOfBirth.setText(df.format(cal.getTime()));
    }
}

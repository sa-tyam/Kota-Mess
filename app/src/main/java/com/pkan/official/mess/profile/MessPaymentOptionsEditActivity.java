package com.pkan.official.mess.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pkan.official.R;
import com.pkan.official.mess.MessMainActivity;

public class MessPaymentOptionsEditActivity extends AppCompatActivity {

    // variables to be used in activity
    ImageView messPaymentOptionsEditBackImageView;

    EditText messPaymentOptionsEditUpiInputEditText, messPaymentOptionsEditAccountNumberInputEditText,
            messPaymentOptionsEditIfscInputEditText, messPaymentOptionsEditBankNameInputEditText,
            messPaymentOptionsEditAccountHolderNameInputEditText;

    Button messPaymentOptionsSaveButton, messPaymentOptionsEditSkipButton;

    // firebase variables to be used in functions
    FirebaseUser user;
    DatabaseReference databaseReference;

    // Progress dialog
    ProgressDialog progressDialog;

    // variables to be used to save data on firebase
    String upi_id = "", account_number = "", ifsc_code = "", bank_name = "", account_holder_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_payment_options_edit);

        // initialize views and variables
        initViews ();

        // set onClicks to be used in activity
        setOnClicks ();
    }

    private void initViews () {

        // initialize the views used in activity
        messPaymentOptionsEditBackImageView = findViewById(R.id.messPaymentOptionsEditBackImageView);
        messPaymentOptionsEditUpiInputEditText = findViewById(R.id
                .messPaymentOptionsEditUpiInputEditText);
        messPaymentOptionsEditAccountNumberInputEditText = findViewById(R.id
                .messPaymentOptionsEditAccountNumberInputEditText);
        messPaymentOptionsEditIfscInputEditText = findViewById(R.id
                .messPaymentOptionsEditIfscInputEditText);
        messPaymentOptionsEditBankNameInputEditText = findViewById(R.id
                .messPaymentOptionsEditBankNameInputEditText);
        messPaymentOptionsEditAccountHolderNameInputEditText = findViewById(R.id
                .messPaymentOptionsEditAccountHolderNameInputEditText);
        messPaymentOptionsSaveButton = findViewById(R.id.messPaymentOptionsSaveButton);
        messPaymentOptionsEditSkipButton = findViewById(R.id.messPaymentOptionsEditSkipButton);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(MessPaymentOptionsEditActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setOnClicks () {
        // finish the activity when back imageView is clicked
        messPaymentOptionsEditBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //check for validity of data when save button is clicked
        messPaymentOptionsSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSave ();
            }
        });

        // move to home page when skip button is pressed
        messPaymentOptionsEditSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToHome();
            }
        });

    }

    private void checkForSave () {
        // get upi data
        upi_id = messPaymentOptionsEditUpiInputEditText.getText().toString();

        // get bank data
        account_number = messPaymentOptionsEditAccountNumberInputEditText.getText().toString();
        ifsc_code = messPaymentOptionsEditIfscInputEditText.getText().toString();
        bank_name = messPaymentOptionsEditBankNameInputEditText.getText().toString();
        account_holder_name = messPaymentOptionsEditAccountHolderNameInputEditText.getText()
                .toString();

        if (upi_id.length() > 0) {
            if (account_number.length() > 0 && ifsc_code.length() > 0 && bank_name.length() > 0
                    && account_holder_name.length() > 0) {

                // both set of data ae valid so, save both
                saveUpi();
                saveBankDetails();

            } else if (account_number.length() == 0 && ifsc_code.length() == 0 && bank_name.length() == 0
                    && account_holder_name.length() == 0) {

                // only upi data set is valid
                saveUpi();
            } else {

                // show user bank data is incomplete

                Toast.makeText(getApplicationContext(), "Incomplete Bank details",
                        Toast.LENGTH_SHORT).show();
            }

        }

        else {

            if (account_number.length() > 0 && ifsc_code.length() > 0 && bank_name.length() > 0
                    && account_holder_name.length() > 0) {

                // bank data set is valid, so save it
                saveBankDetails();

            } else{
                // show user bank data is incomplete
                Toast.makeText(getApplicationContext(), "Incomplete Bank details",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveUpi () {
        // disable screen and show progress dialog
        startProgressDialog();

        // save data on firebase
        DatabaseReference upi_ref = databaseReference.child("Mess").child(user.getUid())
                .child("Payment Details").child("Upi");

        upi_ref.child("Upi Id").setValue(upi_id).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // enable screen and stop progress dialog
                stopProgressDialog();

                Toast.makeText(getApplicationContext(), "Upi data saved", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void saveBankDetails () {
        // disable screen and show progress dialog
        startProgressDialog();

        // save data on firebase
        DatabaseReference bank_ref = databaseReference.child("Mess").child(user.getUid())
                .child("Payment Details").child("Bank Details");

        bank_ref.child("Account Number").setValue(account_number);
        bank_ref.child("IFSC Code").setValue(ifsc_code);
        bank_ref.child("Bank Name").setValue(bank_name);
        bank_ref.child("Account Holder Name").setValue(account_holder_name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // enable screen and stop progress dialog
                stopProgressDialog();

                Toast.makeText(getApplicationContext(), "Bank details saved", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void moveToHome () {
        Intent intent = new Intent(getApplicationContext(), MessMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startProgressDialog () {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void stopProgressDialog () {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
package com.pkan.official.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.R;
import com.pkan.official.customer.CustomerMainActivity;
import com.pkan.official.customer.profile.CustomerProfileEditActivity;
import com.pkan.official.mess.MessMainActivity;
import com.pkan.official.mess.profile.MessBasicProfileEditActivity;

import java.util.concurrent.TimeUnit;

public class LoginActivityVerifyOtp extends AppCompatActivity {

    // phone number to be used to verify the user
    String phone_number = "";

    // Views used in the activity
    EditText loginOtpEditText;
    Button loginVerifyOtpButton;
    TextView loginVerifyOtpResendOtpTextView;
    ImageView loginVerifyOtpBackImageView;

    // tag for logcat debugging
    String TAG = "LoginActivityVerifyOtp_java";


    // Firebase auth to be used in various functions
    FirebaseAuth mAuth;

    // Callbacks for firebase verification
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    // variables to be used after verification is complete
    String mVerificationId = "";
    PhoneAuthProvider.ForceResendingToken mResendToken;

    // credential for verifying user after button is clicked
    PhoneAuthCredential button_credential;

    // Progress Dialog
    ProgressDialog progressDialog;

    // variable to specify login as customer or mess
    String login_as = "";

    // user variable to move to next activity after signing in
    FirebaseUser user;

    // shared preference variable to remember user
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "loginAs";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify_otp);

        initViews();
        startProgressDialog();
        setCallbacks();
        sendVerificationCode();
        setOnClicks();

    }

    private void initViews () {
        loginOtpEditText = findViewById(R.id.loginOtpEditText);
        loginVerifyOtpButton = findViewById(R.id.loginVerifyOtpButton);
        loginVerifyOtpResendOtpTextView = findViewById(R.id.loginVerifyOtpResendOtpTextView);
        loginVerifyOtpBackImageView = findViewById(R.id.loginVerifyOtpBackImageView);

        // disable views until code is sent
        loginVerifyOtpButton.setEnabled(false);

        // initialize mAuth to be used in other functions
        mAuth = FirebaseAuth.getInstance();

        // initialize progressDialog
        progressDialog = new ProgressDialog(LoginActivityVerifyOtp.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // initialize shared preferences
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    private void sendVerificationCode () {
        phone_number = getIntent().getStringExtra("phone_number");


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone_number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setCallbacks () {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                startProgressDialog();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // make the views clickable
                loginVerifyOtpButton.setEnabled(true);
                stopProgressDialog();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = task.getResult().getUser();
                            afterLogin();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            stopProgressDialog();
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Invalid Otp",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    // Set OnClick for views used in activity
    private void setOnClicks () {

        loginVerifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginVerifyOtpButton.setFocusable(false);

                String code = "";
                code = loginOtpEditText.getText().toString();

                if (code.length() >= 6) {
                    startProgressDialog();
                    button_credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(button_credential);
                } else {
                    Toast.makeText(getApplicationContext(), "OTP too short",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginVerifyOtpResendOtpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOtp();
            }
        });

        loginVerifyOtpBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void afterLogin () {

        // for debugging in logcat
        Log.d("afterLogin", "reached");

        login_as = getIntent().getStringExtra("login_as");

        String user_id = user.getUid();

        // for debugging in logcat
        Log.d("login_as", login_as);
        Log.d("user_id", user_id);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // shared preference editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (login_as.equals("Customers")) {

            databaseReference.child(login_as).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (! snapshot.hasChild(user_id)) {
                        // customer profile does not exist, i.e. new customer
                        // send to profile page

                        stopProgressDialog();

                        // save shared preferences
                        editor.putString(Name, login_as);
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(),
                                CustomerProfileEditActivity.class);
                        intent.putExtra("newUser", "new_user");
                        startActivity(intent);
                        finish();
                    }

                   else {

                        // customer profile exists, i.e. old customer
                        // send directly to home page

                        // save shared preferences
                        editor.putString(Name, login_as);
                        editor.commit();

                        stopProgressDialog();
                        Intent intent = new Intent(getApplicationContext(),
                                CustomerMainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("afterLogin", error.getDetails());
                    Toast.makeText(getApplicationContext(), "Something Went Wrong",
                            Toast.LENGTH_SHORT).show();
                    stopProgressDialog();
                    finish();
                }
            });

        } else if (login_as.equals("Mess")) {

            databaseReference.child(login_as).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (! snapshot.hasChild(user_id)) {
                        // mess profile does not exist, i.e. new mess
                        // send to profile page

                        // save shared preferences
                        editor.putString(Name, login_as);
                        editor.commit();

                        stopProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), MessBasicProfileEditActivity.class);
                        intent.putExtra("newUser", "new_user");
                        startActivity(intent);
                        finish();
                    }

                    else {

                        // mess profile exists, i.e. old mess
                        // send directly to home page

                        // save shared preferences
                        editor.putString(Name, login_as);
                        editor.commit();

                        stopProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), MessMainActivity.class);
                        startActivity(intent);
                        finish();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("afterLogin", error.getDetails());
                    Toast.makeText(getApplicationContext(), "Something Went Wrong",
                            Toast.LENGTH_SHORT).show();
                    stopProgressDialog();
                    finish();
                }
            });

        }
    }

    private void resendOtp() {
        sendVerificationCode();
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
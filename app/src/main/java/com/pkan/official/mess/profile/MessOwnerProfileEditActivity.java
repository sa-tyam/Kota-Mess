package com.pkan.official.mess.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pkan.official.R;

public class MessOwnerProfileEditActivity extends AppCompatActivity {

    // views to be used in activity
    ImageView messOwnerProfileEditBackImageView, messOwnerProfileEditSelectPictureImageView;

    EditText messOwnerProfileEditNameEditText, messOwnerProfileEditAddressEditText,
            messBasicProfilePhoneNumbersEditText;

    TextView messOwnerProfileEditUploadPicStatusTextView;

    Button messOwnerProfileNextButton;

    // firebase variables to be used
    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    // Progress Dialog
    ProgressDialog progressDialog;

    // variables to be used to save data to firebase
    String name, address, phone_number;

    // variable to check if new user or existing user
    String new_user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_owner_profile_edit);

        // initialize the views and variables
        initViews ();

        // set status bar color
        setStatusBarColor();

        // set onClicks to be used in activity
        setOnClicks ();
    }

    private void initViews () {

        // initialize the views
        messOwnerProfileEditBackImageView = findViewById(R.id.messOwnerProfileEditBackImageView);
        messOwnerProfileEditSelectPictureImageView = findViewById(R.id.messOwnerProfileEditSelectPictureImageView);
        messOwnerProfileEditNameEditText = findViewById(R.id.messOwnerProfileEditNameEditText);
        messOwnerProfileEditAddressEditText = findViewById(R.id.messOwnerProfileEditAddressEditText);
        messBasicProfilePhoneNumbersEditText = findViewById(R.id.messBasicProfilePhoneNumbersEditText);
        messOwnerProfileEditUploadPicStatusTextView = findViewById(R.id.messOwnerProfileEditUploadPicStatusTextView);
        messOwnerProfileNextButton = findViewById(R.id.messOwnerProfileNextButton);

        // initialize the firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(MessOwnerProfileEditActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setStatusBarColor () {

        // check if android version is greater than or equal to 21
        // it works only for API level 21 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.activity_mess_owner_profile_edit_background));
        }

    }

    private void setOnClicks () {
        // finish the activity if back button is pressed
        messOwnerProfileEditBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // select picture when select picture imageView is clicked
        messOwnerProfileEditSelectPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPicture ();
            }
        });

        // check for validity of data when next button is pressed
        messOwnerProfileNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSave ();
            }
        });
    }

    private void selectPicture () {
        // create intent to select image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);

        // start activity to select image
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if file selected is correct and data is not null
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData()!=null)
        {
            // get the uri
            Uri uri = data.getData();

            // for debugging purpose, in case of error
            Log.e("uri","uri : "+uri.toString());

            // upload the file to firebase storage
            uploadFile(uri);
        }
    }

    private void uploadFile (Uri uri) {
        // start uploading, make screen inactive and start progress dialog
        startProgressDialog();

        // upload file on firebase storage
        storageReference.child("Pictures").child("Mess").child(user.getUid()).child("Owner Id")
                .child("Image").putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // file successfully uploaded, get the download url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri url = uriTask.getResult();

                        // for debugging purpose in case of error
                        Log.d("download url", url.toString());

                        // save download url on firebase
                        databaseReference.child("Mess").child(user.getUid()).child("Owner").child("Owner Id Download Link")
                                .setValue(url.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // make screen active and stop progress dialog
                                stopProgressDialog();
                                messOwnerProfileEditUploadPicStatusTextView.setText("successfully uploaded");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // failed to save download url

                                stopProgressDialog();
                                // for debugging purpose
                                Log.e("save download url", e.getMessage());
                                Toast.makeText(getApplicationContext(), "Failed to save image :(",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // failed to upload

                stopProgressDialog();
                // for debugging purpose
                Log.e("upload file", e.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to upload image :(",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkForSave () {
        name = messOwnerProfileEditNameEditText.getText().toString();
        address = messOwnerProfileEditAddressEditText.getText().toString();
        phone_number = messBasicProfilePhoneNumbersEditText.getText().toString();

        if (name.length() == 0 || address.length() == 0 || phone_number.length() == 0) {

            // show toast to warn user to fill the details
            Toast.makeText(getApplicationContext(), "Fill the detail...", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // data is valid, so save them to database
            saveData ();
        }
    }

    private void saveData () {
        // disable the screen and show progress dialog
        startProgressDialog();

        DatabaseReference owner_ref = databaseReference.child("Mess").child(user.getUid()).child("Owner");
        owner_ref.child("Owner Name").setValue(name);
        owner_ref.child("Owner Address").setValue(address);
        owner_ref.child("Owner Phone Number").setValue(phone_number).addOnSuccessListener(
                new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // stop the progress dialog and enable screen
                stopProgressDialog();

                // check if new user oe existing one
                new_user = getIntent().getStringExtra("newUser");

                if (new_user.length() > 0) {

                    // new user, send it to payment options activity
                    Intent intent = new Intent(getApplicationContext(), MessPaymentOptionsEditActivity.class);
                    intent.putExtra("newUser", "newUser");
                    startActivity(intent);
                } else {

                    // existing user, simply finish the activity
                }
                finish();
            }
        });
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
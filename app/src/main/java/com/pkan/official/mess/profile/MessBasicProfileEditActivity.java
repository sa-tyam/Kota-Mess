package com.pkan.official.mess.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pkan.official.R;

import java.util.ArrayList;

public class MessBasicProfileEditActivity extends AppCompatActivity {

    // views used in activity

    ImageView messBasicProfileEditBackImageView, messBasicProfileEditSelectPictureImageView;

    EditText messBasicProfileEditNameEditText, messBasicProfileEditDescriptionEditText,
            messBasicProfileEditAddressEditText, messBasicProfileEditTimingsEditText,
            messBasicProfileEditMonthlyChargeEditText;

    Spinner messBasicProfileEditCitySpinner, messBasicProfileEditAreaSpinner;

    TextView messBasicProfileEditUploadPicStatusTextView;

    Button messBasicProfileEditNextButton;

    CheckBox messBasicProfileEditHomeDeliveryCheckBox, messBasicProfileEditInMessCheckBox;

    // firebase variables to be used in functions
    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    // array lists for city, area and their ids
    ArrayList<String> cityArrayList, cityIdArrayList, areaArrayList, areaIdArrayList;

    // Progress Dialog
    ProgressDialog progressDialog;

    // variables to be used to save mess data on database
    String name ="", description = "", city = "", city_id = "", area = "", area_id = "",
            address = "", mess_timings = "",
            monthly_charge = "";

    int home_delivery = 1, in_mess = 1;

    // variable to check if new user or existing user
    String new_user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_basic_profile_edit);

        // initialize the views and variables
        initViews();

        // disable the screen, show progress dialog and set city spinner
        startProgressDialog();
        setCitySpinner ();

        // set the onClicks to be used in activity
        setOnClicks();


    }

    private void initViews () {
        // init views used in activity
        messBasicProfileEditBackImageView = findViewById(R.id.messBasicProfileEditBackImageView);
        messBasicProfileEditSelectPictureImageView = findViewById(R.id
                .messBasicProfileEditSelectPictureImageView);
        messBasicProfileEditNameEditText = findViewById(R.id.messBasicProfileEditNameEditText);
        messBasicProfileEditDescriptionEditText = findViewById(R.id
                .messBasicProfileEditDescriptionEditText);
        messBasicProfileEditAddressEditText = findViewById(R.id.messBasicProfileEditAddressEditText);
        messBasicProfileEditTimingsEditText = findViewById(R.id.messBasicProfileEditTimingsEditText);
        messBasicProfileEditMonthlyChargeEditText = findViewById(R.id
                .messBasicProfileEditMonthlyChargeEditText);
        messBasicProfileEditCitySpinner = findViewById(R.id.messBasicProfileEditCitySpinner);
        messBasicProfileEditAreaSpinner = findViewById(R.id.messBasicProfileEditAreaSpinner);
        messBasicProfileEditUploadPicStatusTextView = findViewById(R.id
                .messBasicProfileEditUploadPicStatusTextView);
        messBasicProfileEditNextButton = findViewById(R.id.messBasicProfileEditNextButton);
        messBasicProfileEditHomeDeliveryCheckBox = findViewById(R.id
                .messBasicProfileEditHomeDeliveryCheckBox);
        messBasicProfileEditInMessCheckBox = findViewById(R.id
                .messBasicProfileEditInMessCheckBox);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // initialize array lists
        cityArrayList = new ArrayList<>();
        cityIdArrayList = new ArrayList<>();
        areaArrayList = new ArrayList<>();
        areaIdArrayList = new ArrayList<>();

        // initialize progressDialog
        progressDialog = new ProgressDialog(MessBasicProfileEditActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setCitySpinner () {
        // clear the existing cities in the city array list
        cityArrayList.clear();
        cityIdArrayList.clear();

        // get the list of cities available from database
        databaseReference.child("City").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot keyNode : snapshot.getChildren()) {

                    // add the available cities and its id to the array lists
                    cityIdArrayList.add(keyNode.getKey());
                    cityArrayList.add(keyNode.child("Name").getValue(String.class));
                }

                // list is complete, now create adapter using it
                // layout used is present in the res/layout/ directory with name profile_spinner_item
                ArrayAdapter cityAdapter = new ArrayAdapter(getApplicationContext(),
                        R.layout.profile_spinners_item, cityArrayList);

                messBasicProfileEditCitySpinner.setAdapter(cityAdapter);

                // now we can stop the progress dialog as city spinner is ready
                stopProgressDialog();

                // set the events to perform when item in the spinner is selected
                messBasicProfileEditCitySpinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view,
                                                       int i, long l) {
                                // here 'i' is the position which was selected
                                city = cityArrayList.get(i);
                                city_id = cityIdArrayList.get(i);

                                // since the city is selected, we can now set the corresponding areas
                                // disable the screen and start progress dialog till areas are loaded
                                startProgressDialog();

                                // set the area spinner now
                                setAreaSpinner(city_id);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                                // if nothing is selected, select first item by default
                                city = cityArrayList.get(0);
                                city_id = cityIdArrayList.get(0);

                                startProgressDialog();
                                setAreaSpinner(city_id);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // for debugging in case of errors
                Log.e("setCitySpinner", error.getDetails());
            }
        });
    }

    private void setAreaSpinner (String city_id) {

        // clear the existing values in the area array lists
        areaArrayList.clear();
        areaIdArrayList.clear();

        // fetch the areas available for the given city
        databaseReference.child("Areas").child(city_id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for ( DataSnapshot keyNode : snapshot.getChildren()) {

                            // add the areas and its ids to the array lists
                            areaIdArrayList.add(keyNode.getKey());
                            areaArrayList.add(keyNode.child("Name").getValue(String.class));
                        }

                        // now the list is ready, create an dapter using it
                        ArrayAdapter areaAdapter = new ArrayAdapter(getApplicationContext(),
                                R.layout.profile_spinners_item, areaArrayList);

                        messBasicProfileEditAreaSpinner.setAdapter(areaAdapter);

                        // area spinner is ready, so now we can stop the progress dialog
                        stopProgressDialog();

                        // set the events to perform when item in the spinner is selected
                        messBasicProfileEditAreaSpinner.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                                               int i, long l) {

                                        // her 'i' is the position which was selected
                                        area = areaArrayList.get(i);
                                        area_id = areaIdArrayList.get(i);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                        // in case no item is selected, select the first item by default
                                        area = areaArrayList.get(0);
                                        area_id = areaIdArrayList.get(0);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // for debugging in case of errors
                        Log.e("setAreaSpinner", error.getDetails());
                    }
                });
    }


    private void setOnClicks () {
        // finish the activity when back image view is clicked
        messBasicProfileEditBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // select picture to be uploaded when select image imageView is clicked
        messBasicProfileEditSelectPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPicture ();
            }
        });

        // check for validity when next button is pressed
        messBasicProfileEditNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSave();
            }
        });

        // toggle radio buttons on clicked
        messBasicProfileEditHomeDeliveryCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messBasicProfileEditHomeDeliveryCheckBox.toggle();
            }
        });

        messBasicProfileEditInMessCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messBasicProfileEditInMessCheckBox.toggle();
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
        storageReference.child("Pictures").child("Mess").child(user.getUid()).child("Mess Image")
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
                databaseReference.child("Mess").child(user.getUid()).child("Profile").child("Mess Image")
                        .setValue(url.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // make screen active and stop progress dialog
                        stopProgressDialog();
                        messBasicProfileEditUploadPicStatusTextView.setText("successfully uploaded");
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

        // take the inputs from edit texts
        name = messBasicProfileEditNameEditText.getText().toString();
        description = messBasicProfileEditDescriptionEditText.getText().toString();
        address = messBasicProfileEditAddressEditText.getText().toString();
        mess_timings = messBasicProfileEditTimingsEditText.getText().toString();
        monthly_charge = messBasicProfileEditMonthlyChargeEditText.getText().toString();

        // check if home delivery is selected or not
        if (messBasicProfileEditHomeDeliveryCheckBox.isChecked()) {
            home_delivery = 1;
        } else {
            home_delivery = 0;
        }

        // check if in mess is selected or not
        if (messBasicProfileEditInMessCheckBox.isChecked()) {
            in_mess = 1;
        } else {
            in_mess = 0;
        }

        if (name.length() == 0 || description.length() == 0 || city.length() == 0
                || area.length()==0 || address.length() == 0 || mess_timings.length() == 0
                || monthly_charge.length() == 0) {

            // show the toast to users to warn them to fill the details
            Toast.makeText(getApplicationContext(), "Enter the details...", Toast.LENGTH_SHORT)
                    .show();

        } else {
            // details are valid, so disable screen, show progress dialog and upload them to database
            startProgressDialog();
            saveData();
        }

    }

    private void saveData () {

        // save the profile data
        DatabaseReference profile_ref = databaseReference.child("Mess")
                .child(user.getUid()).child("Profile");
        profile_ref.child("Name").setValue(name);
        profile_ref.child("Description").setValue(description);
        profile_ref.child("Address").setValue(address);
        profile_ref.child("City").setValue(city);
        profile_ref.child("City Id").setValue(city_id);
        profile_ref.child("Area").setValue(area);
        profile_ref.child("Area Id").setValue(area_id);
        profile_ref.child("Phone Number").setValue(user.getPhoneNumber());
        profile_ref.child("Mess Timings").setValue(mess_timings);
        profile_ref.child("Home Delivery").setValue(home_delivery);
        profile_ref.child("In Mess").setValue(in_mess);
        profile_ref.child("Monthly Price").setValue(monthly_charge).addOnSuccessListener(
                new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // enable the screen and stop progress dialog
                stopProgressDialog();

                // check if new user
                new_user = getIntent().getStringExtra("newUser");

                if (new_user.length() > 0) {

                    // new User, so send it to owner profile edit activity
                    Intent intent = new Intent(getApplicationContext(),
                            MessOwnerProfileEditActivity.class);
                    intent.putExtra("newUser", "newUser");
                    startActivity(intent);
                } else {

                    // existing user, so simply finish the activity
                }
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // failed to save data
                Log.e("save data", e.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to save data:(",
                        Toast.LENGTH_LONG).show();
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
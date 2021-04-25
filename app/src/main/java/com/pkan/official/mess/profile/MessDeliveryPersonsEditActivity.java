package com.pkan.official.mess.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.R;

import java.util.ArrayList;

public class MessDeliveryPersonsEditActivity extends AppCompatActivity {

    // views used in activity
    ImageView messDeliveryPersonsEditBackImageView;

    TextView messDeliveryPersonsEditMessNameTextView;

    LinearLayout messDeliveryPersonsEditNumbersLinearLayout;

    EditText messDeliveryPersonsEditNewNumberNumberEditText,
            messDeliveryPersonsEditNewNumberNameEditText;

    Button messDeliveryPersonsEditAddButton;

    // firebase variables used in activity
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // variables used in activity
    String new_number = "", new_name = "";

    // array list to contain delivery numbers
    ArrayList<DeliveryNumbers> deliveryNumbersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_delivery_persons_edit);

        // initialize views and variables
        initViews();

        // set status bar color
        setStatusBarColor();

        // set on clicks
        setOnClicks();

        // disable screen, show progress dialog and get delivery numbers
        getDeliveryNumbers();


    }

    private void initViews () {

        // initialize the views used in activity
        messDeliveryPersonsEditBackImageView = findViewById(R.id
                .messDeliveryPersonsEditBackImageView);
        messDeliveryPersonsEditMessNameTextView = findViewById(R.id
                .messDeliveryPersonsEditMessNameTextView);
        messDeliveryPersonsEditNumbersLinearLayout = findViewById(R.id
                .messDeliveryPersonsEditNumbersLinearLayout);
        messDeliveryPersonsEditNewNumberNumberEditText = findViewById(R.id
                .messDeliveryPersonsEditNewNumberNumberEditText);
        messDeliveryPersonsEditNewNumberNameEditText = findViewById(R.id
                .messDeliveryPersonsEditNewNumberNameEditText);
        messDeliveryPersonsEditAddButton = findViewById(R.id
                .messDeliveryPersonsEditAddButton);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(MessDeliveryPersonsEditActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // initialize delivery numbers array list
        deliveryNumbersArrayList = new ArrayList<>();
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
                    R.color.activity_mess_delivery_persons_edit_background));
        }

    }

    private void setOnClicks () {

        // simply finish the activity when back image view is clicked
        messDeliveryPersonsEditBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // add new number when add button is clicked
        messDeliveryPersonsEditAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumber();
            }
        });
    }

    private void addNumber () {

        new_number = messDeliveryPersonsEditNewNumberNumberEditText.getText().toString();
        new_name = messDeliveryPersonsEditNewNumberNameEditText.getText().toString();

        new_number = "+91" + new_number;

        if (new_number.length() < 13 || new_name.length() < 1) {

            // for debugging purpose
            Log.d("number or name", "invalid");

            // alert user to input valid data
            Toast.makeText(getApplicationContext(), "Enter valid data",
                    Toast.LENGTH_LONG).show();
        } else {

            // start progress dialog
            startProgressDialog();

            databaseReference.child("Mess").child(user.getUid()).child("Delivery Phone Numbers")
                    .child(new_number).child("Phone Number").setValue(new_number);
            databaseReference.child("Mess").child(user.getUid()).child("Delivery Phone Numbers")
                    .child(new_number).child("Name").setValue(new_name);

            databaseReference.child("Delivery Numbers").child(new_number).child("Mess Id")
                    .setValue(user.getUid());

            databaseReference.child("Delivery Numbers").child(new_number).child("Name")
                    .setValue(new_name);

            databaseReference.child("Delivery Numbers").child(new_number).child("Phone Number")
                    .setValue(new_number).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    // stop progress dialog
                    stopProgressDialog();

                    // alert user new number is added
                    Toast.makeText(getApplicationContext(), "Number added",
                            Toast.LENGTH_LONG).show();

                    // set the number text and name text to null
                    messDeliveryPersonsEditNewNumberNameEditText.setText("");
                    messDeliveryPersonsEditNewNumberNumberEditText.setText("");

                    // get delivery numbers again
                    getDeliveryNumbers();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // stop progress dialog
                    stopProgressDialog();

                    // alert user new number not added
                    Toast.makeText(getApplicationContext(), "Oops! please try again",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void getDeliveryNumbers () {

        // clear the array list
        deliveryNumbersArrayList.clear();

        databaseReference.child("Mess").child(user.getUid()).child("Delivery Phone Numbers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot numNode : snapshot.getChildren()) {
                    if (numNode.hasChild("Phone Number")) {
                        String phone_number = numNode.child("Phone Number").getValue(String.class);
                        String name = numNode.child("Name").getValue(String.class);

                        if (phone_number != null && name != null) {

                            // for debugging purpose
                            Log.d("phone number", phone_number);

                            // add this number to array list
                            deliveryNumbersArrayList.add(new DeliveryNumbers(phone_number, name));
                        } else {

                            // for debugging purpose
                            Log.e("phone number", "not found");
                        }
                    }
                }

                // set the views in layout
                setNumbersInLayout();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // stop progress dialog
                stopProgressDialog();

                // for debugging purpose
                Log.e("get numbers", error.getDetails());
            }
        });
    }

    private void setNumbersInLayout () {

        // remove all the added views
        messDeliveryPersonsEditNumbersLinearLayout.removeAllViews();

        // iterate through all delivery numbers

        for ( int i = 0; i < deliveryNumbersArrayList.size(); i++) {

            // inflate view
            View view = getLayoutInflater().inflate(R.layout.mess_delivery_person_number_item,
                    messDeliveryPersonsEditNumbersLinearLayout, false);

            // declare and initialize views used inside the item view
            TextView messDeliveryPersonNumberItemNameTextView = view.findViewById(R
                    .id.messDeliveryPersonNumberItemNameTextView);
            TextView messDeliveryPersonNumberItemNumberTextView = view.findViewById(R
                    .id.messDeliveryPersonNumberItemNumberTextView);
            ImageView messDeliveryPersonNumberItemRemoveImageView = view.findViewById(R
                    .id.messDeliveryPersonNumberItemRemoveImageView);

            messDeliveryPersonNumberItemNameTextView.setText(deliveryNumbersArrayList
                    .get(i).getName());

            messDeliveryPersonNumberItemNumberTextView.setText(deliveryNumbersArrayList
                    .get(i).getNumber());

            DeliveryNumbers deliveryNumber = deliveryNumbersArrayList.get(i);

            messDeliveryPersonNumberItemRemoveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeNumber(deliveryNumber);
                }
            });

            messDeliveryPersonsEditNumbersLinearLayout.addView(view);

        }

        // stop the progress dialog
        stopProgressDialog();

    }

    private void removeNumber (DeliveryNumbers deliveryNumber) {

        // set the alert dialog
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MessDeliveryPersonsEditActivity.this);
        builder1.setMessage("Do you want to add this number.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // delete the delivery number
                        databaseReference.child("Delivery Numbers")
                                .child(deliveryNumber.getNumber()).removeValue();

                        // delete the delivery number from mess data
                        databaseReference.child("Mess").child(user.getUid())
                                .child("Delivery Phone Numbers").child(deliveryNumber.getNumber())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                // for debugging purpose
                                Log.d("delete number", "deleted");

                                getDeliveryNumbers();
                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                // for debugging purpose
                                Log.e("delete number", e.getMessage());

                                getDeliveryNumbers();
                                dialog.cancel();
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
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
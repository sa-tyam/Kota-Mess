package com.pkan.official.mess.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MessDeliveryAreaEditActivity extends AppCompatActivity {

    // views used in activity
    ImageView messDeliveryEditBackImageView;

    LinearLayout deliveryAreaEditAreasLinearLayout;

    Spinner messDeliveryAreaEditAreaSpinner;

    Button deliveryAreaEditAddButton;

    // firebase variables
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // area array lists
    ArrayList<String> mess_area_list;
    ArrayList<String> mess_area_id_list;
    ArrayList<String> other_area_list;
    ArrayList<String> other_area_id_list;

    // city id of mess
    String city_id;

    // selected area id and name
    String selected_area_id = "", selected_area_name = "";

    // spinner adapter
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_delivery_area_edit);

        // initialize views and variables
        initViews();

        // set on clicks
        setOnClicks();

        // start progress dialog
        startProgressDialog();

        // get city id
        getCityId();

        // set adapter
        setSpinner();

    }

    private void initViews() {

        // initialize views
        messDeliveryEditBackImageView = findViewById(R.id.messDeliveryEditBackImageView);
        deliveryAreaEditAreasLinearLayout = findViewById(R.id.deliveryAreaEditAreasLinearLayout);
        messDeliveryAreaEditAreaSpinner = findViewById(R.id.messDeliveryAreaEditAreaSpinner);
        deliveryAreaEditAddButton = findViewById(R.id.deliveryAreaEditAddButton);

        // initialize the firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(MessDeliveryAreaEditActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // initialize area array lists
        mess_area_list = new ArrayList<>();
        mess_area_id_list = new ArrayList<>();
        other_area_list = new ArrayList<>();
        other_area_id_list = new ArrayList<>();
    }

    private void setOnClicks() {

        // simply finish the activity when back image view is clicked
        messDeliveryEditBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // add new area when add button is clicked
        deliveryAreaEditAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArea();
            }
        });
    }

    private void addArea() {

        if (selected_area_id != null) {

            // start progress dialog
            startProgressDialog();

            databaseReference.child("Areas").child(city_id).child(selected_area_id).child("Mess")
                    .child(user.getUid()).child("Mess Id").setValue(user.getUid());

            databaseReference.child("Mess").child(user.getUid()).child("Areas")
                    .child(selected_area_id).child("Area Id").setValue(selected_area_id);

            databaseReference.child("Mess").child(user.getUid()).child("Areas")
                    .child(selected_area_id).child("Name").setValue(selected_area_name)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(), "Area added",
                                    Toast.LENGTH_LONG).show();
                            getMessAreas();

                            // stop progress dialog
                            stopProgressDialog();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "Oops! Please retry...",
                            Toast.LENGTH_LONG).show();
                    getMessAreas();

                    // stop progress dialog
                    stopProgressDialog();
                }
            });
        }
    }

    private void getCityId() {
        databaseReference.child("Mess").child(user.getUid()).child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        city_id = snapshot.child("City Id").getValue(String.class);

                        if (city_id != null) {

                            // for debugging purpose
                            Log.e("city id", city_id);

                            // get mess areas
                            getMessAreas();

                        } else {

                            // for debugging purpose
                            Log.e("city id", "not found");

                            // stop progress dialog
                            stopProgressDialog();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("city id", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();
                    }
                });
    }

    private void getMessAreas() {

        // clear array list
        mess_area_list.clear();
        mess_area_id_list.clear();

        databaseReference.child("Mess").child(user.getUid()).child("Areas")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot areaNode : snapshot.getChildren()) {
                            String area_name = areaNode.child("Name").getValue(String.class);
                            String area_id = areaNode.child("Area Id").getValue(String.class);

                            if (area_name != null && area_id != null) {

                                // for debugging purpose
                                Log.d("mess area name", area_name);

                                // add these values to arrays
                                mess_area_list.add(area_name);
                                mess_area_id_list.add(area_id);

                            } else {

                                // for debugging purpose
                                Log.e("mess area name", "not found");

                                // stop progress dialog
                                stopProgressDialog();
                            }
                        }

                        // get other areas
                        getOtherAreas();

                        // set mess areas
                        setMessAreas();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("mess area name", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();
                    }
                });
    }

    private void getOtherAreas() {

        // clear area lists
        other_area_list.clear();
        other_area_id_list.clear();

        // get the areas
        databaseReference.child("Areas").child(city_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot areaNode : snapshot.getChildren()) {
                            String area_id = areaNode.getKey();
                            String area_name = areaNode.child("Name").getValue(String.class);

                            if (area_id != null && area_name != null) {

                                // for debugging purpose
                                Log.d("area id", area_id);

                                if (!mess_area_id_list.contains(area_id)) {

                                    other_area_id_list.add(area_id);
                                    other_area_list.add(area_name);
                                }
                            } else {

                                // for debugging purpose
                                Log.d("area id", "not found");
                            }

                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.d("area id", error.getDetails());
                    }
                });
    }

    private void setSpinner() {
        adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.profile_spinners_item, other_area_list);

        messDeliveryAreaEditAreaSpinner.setAdapter(adapter);

        messDeliveryAreaEditAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // here i is the position
                selected_area_id = other_area_id_list.get(i);
                selected_area_name = other_area_list.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                if (other_area_id_list.size() > 0) {
                    // set the first item as default selected
                    selected_area_id = other_area_id_list.get(0);
                    selected_area_name = other_area_list.get(0);
                } else {
                    selected_area_id = "";
                    selected_area_name = "";
                }
            }
        });

        // stop progress dialog
        stopProgressDialog();
    }

    private void setMessAreas() {

        // remove all initial views
        deliveryAreaEditAreasLinearLayout.removeAllViews();

        // iterate through all areas
        for (int i = 0; i < mess_area_list.size(); i++) {

            // inflate view
            View view = getLayoutInflater().inflate(R.layout.mess_delivery_area_edit_area_item,
                    deliveryAreaEditAreasLinearLayout, false);

            TextView messDeliveryAreaEditAreaItemNameTextView;
            ImageView messDeliveryAreaEditAreaItemRemoveImageView;

            // initialize views
            messDeliveryAreaEditAreaItemNameTextView = view.findViewById(R.id
                    .messDeliveryAreaEditAreaItemNameTextView);
            messDeliveryAreaEditAreaItemRemoveImageView = view.findViewById(R.id
                    .messDeliveryAreaEditAreaItemRemoveImageView);

            // set text
            messDeliveryAreaEditAreaItemNameTextView.setText(mess_area_list.get(i));

            // get area id to remove
            String area_id = mess_area_id_list.get(i);

            // set on click
            messDeliveryAreaEditAreaItemRemoveImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeArea(area_id);
                        }
                    });

            // add the view to linear layout
            deliveryAreaEditAreasLinearLayout.addView(view);

        }
    }

    private void removeArea(String area_id) {
        // set the alert dialog
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MessDeliveryAreaEditActivity.this);
        builder1.setMessage("Do you remove this area.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // delete the delivery number
                        deleteDataOfArea(area_id);
                        dialog.cancel();
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

    private void deleteDataOfArea(String area_id) {
        // start progress dialog
        startProgressDialog();

        databaseReference.child("Areas").child(city_id).child(area_id).child("Mess")
                .child(user.getUid()).removeValue();

        databaseReference.child("Mess").child(user.getUid()).child("Areas")
                .child(area_id).removeValue();

        databaseReference.child("Mess").child(user.getUid()).child("Areas")
                .child(area_id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Area removed",
                                Toast.LENGTH_LONG).show();
                        getMessAreas();

                        // stop progress dialog
                        stopProgressDialog();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Oops! Please retry...",
                        Toast.LENGTH_LONG).show();
                getMessAreas();

                // stop progress dialog
                stopProgressDialog();
            }
        });
    }

    private void startProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void stopProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
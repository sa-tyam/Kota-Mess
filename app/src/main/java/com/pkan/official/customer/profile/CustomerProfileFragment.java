package com.pkan.official.customer.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.LandingPageActivity;
import com.pkan.official.R;
import com.pkan.official.customer.history.CustomerHistoryActivity;
import com.pkan.official.customer.plans.CustomerPlanSelectActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerProfileFragment extends Fragment {

    // these are default generated code
    // start coding from line 64

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerProfileFragment newInstance(String param1, String param2) {
        CustomerProfileFragment fragment = new CustomerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    // declare view to be used at all places
    View view;

    // declare views used in fragment
    ImageView customerProfileFragmentProfileEditImageView, customerProfileFragmentPlanEditImageView;

    TextView customerProfileFragmentNameTextView, customerProfileFragmentGenderTextView,
            customerProfileFragmentHouseNumberTextView, customerProfileFragmentRoomNumberTextView,
            customerProfileFragmentLandmarkTextView, customerProfileFragmentBalanceTextView,
            customerProfileFragmentPlanTextView, customerProfileFragmentCityTextView,
            customerProfileFragmentAreaTextView, customerProfileFragmentReferralCodeTextView,
            customerProfileFragmentReferralCodeDetailTextView;

    Button historyButton, logOutButton;

    // firebase variables
    FirebaseUser user;
    DatabaseReference databaseReference;

    // Progress dialog
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        // initialize the views and variables
        initViews ();

        // set onClicks
        setOnClicks ();

        // disable screen, show progress dialog and set data
        startProgressDialog();
        setDataInViews ();

        return view;
    }

    private void initViews () {

        // initialise the views used in fragment
        customerProfileFragmentProfileEditImageView = view.findViewById(R.id
                .customerProfileFragmentProfileEditImageView);
        customerProfileFragmentPlanEditImageView = view.findViewById(R.id
                .customerProfileFragmentPlanEditImageView);
        customerProfileFragmentNameTextView = view.findViewById(R.id
                .customerProfileFragmentNameTextView);
        customerProfileFragmentGenderTextView = view.findViewById(R.id
                .customerProfileFragmentGenderTextView);
        customerProfileFragmentHouseNumberTextView = view.findViewById(R.id
                .customerProfileFragmentHouseNumberTextView);
        customerProfileFragmentRoomNumberTextView = view.findViewById(R.id
                .customerProfileFragmentRoomNumberTextView);
        customerProfileFragmentLandmarkTextView = view.findViewById(R.id
                .customerProfileFragmentLandmarkTextView);
        customerProfileFragmentBalanceTextView = view.findViewById(R.id
                .customerProfileFragmentBalanceTextView);
        customerProfileFragmentPlanTextView = view.findViewById(R.id
                .customerProfileFragmentPlanTextView);
        customerProfileFragmentCityTextView = view.findViewById(R.id
                .customerProfileFragmentCityTextView);
        customerProfileFragmentAreaTextView = view.findViewById(R.id
                .customerProfileFragmentAreaTextView);
        customerProfileFragmentReferralCodeTextView = view.findViewById(R.id
                .customerProfileFragmentReferralCodeTextView);
        customerProfileFragmentReferralCodeDetailTextView = view.findViewById(R.id
                .customerProfileFragmentReferralCodeDetailTextView);

        // for temporary production
        historyButton = view.findViewById(R.id.historyButton);
        logOutButton = view.findViewById(R.id.logOutButton);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progress Dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setOnClicks () {

        // move to edit profile activity
        customerProfileFragmentProfileEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerProfileEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // move to select plan activity
        customerProfileFragmentPlanEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerPlanSelectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // temporary
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // temporary
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LandingPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void setDataInViews () {
        setReferralData();
        setProfileData();
        setAddressData();
        setPlanData();
    }

    private void setReferralData () {
        // fetch referral detail data
        databaseReference.child("Referral Benefits")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String details = snapshot.child("Details").getValue(String.class);

                        if (details != null) {
                            // for debugging purpose in case of error
                            Log.d("referral details", details);
                            customerProfileFragmentReferralCodeDetailTextView.setText(details);
                        } else {
                            customerProfileFragmentReferralCodeDetailTextView.setText("Not Available Currently");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // for debugging purpose in case of error
                        Log.e("referral details", error.getDetails());
                    }
                });
    }

    private void setProfileData () {

        // fetch profile data
        databaseReference.child("Customers").child(user.getUid()).child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String referral_code = "", name = "", gender = "";
                        name = snapshot.child("Name").getValue(String.class);
                        referral_code = snapshot.child("Referral Code").getValue(String.class);
                        gender = snapshot.child("Gender").getValue(String.class);

                        // check for validity of data
                        if (name != null && gender != null && referral_code != null) {

                            // data is valid, set values
                            customerProfileFragmentReferralCodeTextView.setText(referral_code);
                            customerProfileFragmentNameTextView.setText(name);
                            customerProfileFragmentGenderTextView.setText(gender);
                        } else {

                            // profile not found, so send to set profile page
                            Intent intent = new Intent(getContext(), CustomerProfileEditActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging purpose
                        Log.e("fetch profile data", error.getDetails());
                    }
                });
    }

    private void setAddressData () {

        // set address data
        databaseReference.child("Customers").child(user.getUid()).child("Address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String city = "", area = "", house_number = "", room_number = "", landmark = "";

                        city = snapshot.child("City").getValue(String.class);
                        area = snapshot.child("Area").getValue(String.class);
                        house_number = snapshot.child("House Number").getValue(String.class);
                        room_number = snapshot.child("Room Number").getValue(String.class);
                        landmark = snapshot.child("Landmark").getValue(String.class);

                        // check for validity of data
                        if (city != null && area != null && house_number != null && room_number != null
                                && landmark != null) {

                            // valid data, set values
                            customerProfileFragmentCityTextView.setText(city);
                            customerProfileFragmentAreaTextView.setText(area);
                            customerProfileFragmentHouseNumberTextView.setText(house_number);
                            customerProfileFragmentRoomNumberTextView.setText(room_number);
                            customerProfileFragmentLandmarkTextView.setText(landmark);
                        } else {

                            // address not found, send to edit profile to set data
                            Intent intent = new Intent(getContext(), CustomerProfileEditActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // for debugging purpose
                        Log.e("Address data", error.getDetails());
                    }
                });
    }

    private void setPlanData () {

        // set plans data
        databaseReference.child("Customers").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int balance = -1;
                String plan = "";

                balance = snapshot.child("Balance").getValue(Integer.class);
                plan = snapshot.child("Plan").child("Plan Name").getValue(String.class);

                if (balance > -1 && plan != null) {
                    customerProfileFragmentBalanceTextView.setText("\u20B9" + " " +
                            String.valueOf(balance));
                    customerProfileFragmentPlanTextView.setText(plan);
                    stopProgressDialog();
                } else {
                    stopProgressDialog();
                    Intent intent = new Intent(getContext(), CustomerPlanSelectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // for debugging purpose
                Log.e("Plan data", error.getDetails());
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
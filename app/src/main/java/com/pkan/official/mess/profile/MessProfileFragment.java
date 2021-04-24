package com.pkan.official.mess.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessProfileFragment extends Fragment {

    // default generated code

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessProfileFragment newInstance(String param1, String param2) {
        MessProfileFragment fragment = new MessProfileFragment();
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


    // view used in this fragment
    View view;


    // other views used in the fragment's view

    ImageView messProfileImageView, messProfileEditProfileImageView,
            messProfileViewPaymentDetailToggleImageView, messProfileEditPaymentDetailsImageView,
            messProfileViewOwnerDetailToggleImageView, messProfileEditOwnerProfileImageView,
            messProfileViewDeliveryNumbersToggleImageView, messProfileLogOutImageView;

    TextView messProfileNameTextView, messProfileCityTextView, messProfileAreaTextView,
            messProfilePhoneNumberTextView, messProfileAddressTextView,
            messProfileMessTimingTextView, messProfileMonthlyChargeTextView,
            messProfileViewPaymentUpiIdTextView, messProfileViewPaymentAccountNumberTextView,
            messProfileViewPaymentIfscCodeTextView, messProfileViewPaymentBankNameTextView,
            messProfileViewPaymentAccountHolderNameTextView,
            messProfileViewOwnerDetailNameTextView, messProfileViewOwnerDetailAddressTextView,
            messProfileViewOwnerDetailPhoneNumberTextView, messProfileBalanceTextView;

    ConstraintLayout messProfilePaymentDetailConstraintLayout,
            messProfileOwnerDetailConstraintLayout;

    CardView messProfileViewPaymentDetailCardView, messProfileViewOwnerDetailCardView,
            messProfileViewDeliveryNumbersCardView, messProfileViewDeliveryAreasCardView;

    LinearLayout messProfileViewDeliveryNumbersLinearLayout;

    // firebase variables used in activity
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // variables used for toggling details
    int payment_detail_toggle = 0, owner_detail_toggle = 0, delivery_detail_toggle = 0;

    // variables used in activity
    String upi_id = "", account_number = "", ifsc_code = "", bank_name = "", account_holder_name = "";

    String owner_name = "", owner_address = "", owner_number = "";

    String mess_name = "", mess_city = "", mess_area = "", mess_phone = "", mess_address = "",
            mess_timing = "",  mess_image_link = "";

    int mess_balance = 0, mess_monthly_charge = 2500;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mess_profile, container, false);

        // initialize views and variables used
        initViews();

        // set on clicks
        setOnClicks();

        // get basic details
        getBasicDetails();

        return view;
    }

    private void initViews () {

        // initialize views
        messProfileImageView = view.findViewById(R.id.messProfileImageView);
        messProfileEditProfileImageView = view.findViewById(R.id.messProfileEditProfileImageView);
        messProfileViewPaymentDetailToggleImageView = view.findViewById(R.
                id.messProfileViewPaymentDetailToggleImageView);
        messProfileEditPaymentDetailsImageView = view.findViewById(R.id
                .messProfileEditPaymentDetailsImageView);
        messProfileViewOwnerDetailToggleImageView = view.findViewById(R.id
                .messProfileViewOwnerDetailToggleImageView);
        messProfileEditOwnerProfileImageView = view.findViewById(R.id
                .messProfileEditOwnerProfileImageView);
        messProfileViewDeliveryNumbersToggleImageView = view.findViewById(R.id
                .messProfileViewDeliveryNumbersToggleImageView);
        messProfileNameTextView = view.findViewById(R.id.messProfileNameTextView);
        messProfileCityTextView = view.findViewById(R.id.messProfileCityTextView);
        messProfileAreaTextView = view.findViewById(R.id.messProfileAreaTextView);
        messProfilePhoneNumberTextView = view.findViewById(R.id.messProfilePhoneNumberTextView);
        messProfileAddressTextView = view.findViewById(R.id.messProfileAddressTextView);
        messProfileMessTimingTextView = view.findViewById(R.id.messProfileMessTimingTextView);
        messProfileMonthlyChargeTextView = view.findViewById(R.id
                .messProfileMonthlyChargeTextView);
        messProfileViewPaymentUpiIdTextView = view.findViewById(R.id
                .messProfileViewPaymentUpiIdTextView);
        messProfileViewPaymentAccountNumberTextView = view.findViewById(R
                .id.messProfileViewPaymentAccountNumberTextView);
        messProfileViewPaymentIfscCodeTextView = view.findViewById(R.id
                .messProfileViewPaymentIfscCodeTextView);
        messProfileViewPaymentBankNameTextView = view.findViewById(R.id
                .messProfileViewPaymentBankNameTextView);
        messProfileViewPaymentAccountHolderNameTextView = view.findViewById(R
                .id.messProfileViewPaymentAccountHolderNameTextView);
        messProfileViewOwnerDetailNameTextView = view.findViewById(R.id
                .messProfileViewOwnerDetailNameTextView);
        messProfileViewOwnerDetailAddressTextView = view.findViewById(R.id
                .messProfileViewOwnerDetailAddressTextView);
        messProfileViewOwnerDetailPhoneNumberTextView = view.findViewById(R
                .id.messProfileViewOwnerDetailPhoneNumberTextView);
        messProfilePaymentDetailConstraintLayout = view.findViewById(R.id
                .messProfilePaymentDetailConstraintLayout);
        messProfileOwnerDetailConstraintLayout = view.findViewById(R.id
                .messProfileOwnerDetailConstraintLayout);
        messProfileViewPaymentDetailCardView = view.findViewById(R.id
                .messProfileViewPaymentDetailCardView);
        messProfileViewOwnerDetailCardView = view.findViewById(R.id
                .messProfileViewOwnerDetailCardView);
        messProfileViewDeliveryNumbersCardView = view.findViewById(R.id
                .messProfileViewDeliveryNumbersCardView);
        messProfileViewDeliveryAreasCardView = view.findViewById(R.id
                .messProfileViewDeliveryAreasCardView);
        messProfileBalanceTextView = view.findViewById(R.id.messProfileBalanceTextView);
        messProfileLogOutImageView = view.findViewById(R.id.messProfileLogOutImageView);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progress Dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // hide constraint layouts
        messProfilePaymentDetailConstraintLayout.setVisibility(View.GONE);
        messProfileOwnerDetailConstraintLayout.setVisibility(View.GONE);
    }

    private void setOnClicks () {

        // logout user when log out image view is clicked
        messProfileLogOutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                // move to landing page
                Intent intent = new Intent(getContext(), LandingPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // send to mess profile edit
        messProfileEditProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessBasicProfileEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // send to payments profile
        messProfileEditPaymentDetailsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessPaymentOptionsEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // send to owner profile edit
        messProfileEditOwnerProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessOwnerProfileEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // send to mess delivery numbers edit page
        messProfileViewDeliveryNumbersToggleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessDeliveryPersonsEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // toggle payment view
        messProfileViewPaymentDetailCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePaymentDetail();
            }
        });

        // toggle owner details
        messProfileViewOwnerDetailCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleOwnerDetail();
            }
        });

        // send to delivery numbers when delivery card view is clicked
        messProfileViewDeliveryNumbersCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessDeliveryPersonsEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // send to delivery areas when delivery areas is clicked
        messProfileViewDeliveryAreasCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessDeliveryAreaEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void togglePaymentDetail () {

        // check if payment detail is showing
        if (payment_detail_toggle == 0) {

            // get the payment details
            getPaymentDetails();

            // toggle visibility
            messProfilePaymentDetailConstraintLayout.setVisibility(View.VISIBLE);

            // toggle the switch
            payment_detail_toggle = 1;
        } else {
            messProfilePaymentDetailConstraintLayout.setVisibility(View.GONE);

            // toggle the switch
            payment_detail_toggle = 0;
        }
    }

    private void toggleOwnerDetail () {

        // check if owner detail is showing
        if (owner_detail_toggle == 0) {

            // get the owner details
            getOwnerDetails();

            // toggle the visibility
            messProfileOwnerDetailConstraintLayout.setVisibility(View.VISIBLE);

            // toggle the switch
            owner_detail_toggle = 1;
        } else {
            messProfileOwnerDetailConstraintLayout.setVisibility(View.GONE);

            // toggle the switch
            owner_detail_toggle = 0;
        }
    }

    private void getPaymentDetails () {
        databaseReference.child("Mess").child(user.getUid()).child("Payment Details")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                upi_id = snapshot.child("Upi").child("Upi Id").getValue(String.class);
                account_number = snapshot.child("Bank Details").child("Account Number")
                        .getValue(String.class);
                ifsc_code = snapshot.child("Bank Details").child("IFSC Code")
                        .getValue(String.class);
                bank_name = snapshot.child("Bank Details").child("Bank Name")
                        .getValue(String.class);
                account_holder_name = snapshot.child("Bank Details").child("Account Holder Name")
                        .getValue(String.class);

                if (upi_id != null || account_number != null) {
                    setPaymentDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setPaymentDetails () {

        if (upi_id != null) {
            messProfileViewPaymentUpiIdTextView.setText(upi_id);
        } else {
            messProfileViewPaymentUpiIdTextView.setText("");
        }

        if (account_number != null && ifsc_code != null && bank_name != null &&
                account_holder_name != null) {

            messProfileViewPaymentAccountNumberTextView.setText(account_number);
            messProfileViewPaymentIfscCodeTextView.setText(ifsc_code);
            messProfileViewPaymentBankNameTextView.setText(bank_name);
            messProfileViewPaymentAccountHolderNameTextView.setText(account_holder_name);
        } else {
            messProfileViewPaymentAccountNumberTextView.setText("");
            messProfileViewPaymentIfscCodeTextView.setText("");
            messProfileViewPaymentBankNameTextView.setText("");
            messProfileViewPaymentAccountHolderNameTextView.setText("");
        }
    }

    private void getOwnerDetails () {

        // disable screen, show progress dialog
        startProgressDialog();

        databaseReference.child("Mess").child(user.getUid()).child("Owner")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                owner_name = snapshot.child("Owner Name").getValue(String.class);
                owner_address = snapshot.child("Owner Address").getValue(String.class);
                owner_number = snapshot.child("Owner Phone Number").getValue(String.class);

                //check if data is valid
                if (owner_name != null) {

                    // for debugging purpose
                    Log.d("owner name", owner_name);

                    setOwnerDetails();
                } else {

                    // for debugging purpose
                    Log.e("owner details", "not found");

                    // stop progress dialog
                    stopProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("owner details", error.getDetails());

                // stop progress dialog
                stopProgressDialog();
            }
        });
    }

    private void setOwnerDetails () {

        // set the data in views
        messProfileViewOwnerDetailNameTextView.setText(owner_name);
        messProfileViewOwnerDetailAddressTextView.setText(owner_address);
        messProfileViewOwnerDetailPhoneNumberTextView.setText(owner_number);

        // stop the progress dialog
        stopProgressDialog();
    }

    private void getBasicDetails () {

        // disable screen and show progress dialog
        startProgressDialog();

        databaseReference.child("Mess").child(user.getUid()).child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get data
                mess_name = snapshot.child("Name").getValue(String.class);
                mess_city = snapshot.child("City").getValue(String.class);
                mess_area = snapshot.child("Area").getValue(String.class);
                mess_phone = snapshot.child("Phone Number").getValue(String.class);
                mess_address = snapshot.child("Address").getValue(String.class);
                mess_timing = snapshot.child("Mess Timings").getValue(String.class);

                if (snapshot.child("Monthly Price").getValue(Integer.class) != null) {
                    mess_monthly_charge = snapshot.child("Monthly Price").getValue(Integer.class);
                }


                mess_image_link = snapshot.child("Mess Image").getValue(String.class);

                if (snapshot.child("Balance").getValue(Integer.class) != null) {
                    mess_balance = snapshot.child("Balance").getValue(Integer.class);
                }

                // check if data is valid
                if (mess_name != null) {

                    // for debugging purpose
                    Log.d("mess name", mess_name);

                    setBasicDetails();
                } else {

                    // for debugging purpose
                    Log.e("mess basic profile", "not found");

                    // stop progress dialog
                    stopProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("mess profile", error.getDetails());

                // stop progress dialog
                stopProgressDialog();
            }
        });
    }

    private void setBasicDetails () {

        if (mess_name != null) {
            messProfileNameTextView.setText(mess_name);
        } else {
            messProfileNameTextView.setText("");
        }
        if (mess_city != null) {
            messProfileCityTextView.setText(mess_city);
        } else {
            messProfileCityTextView.setText("");
        }
        if (mess_area != null) {
            messProfileAreaTextView.setText(mess_area);
        } else {
            messProfileAreaTextView.setText("");
        }
        if (mess_phone != null) {
            messProfilePhoneNumberTextView.setText(mess_phone);
        } else {
            messProfilePhoneNumberTextView.setText("");
        }
        if (mess_address != null) {
            messProfileAddressTextView.setText(mess_address);
        } else {
            messProfileAddressTextView.setText("");
        }
        if (mess_timing != null) {
            messProfileMessTimingTextView.setText(mess_timing);
        } else {
            messProfileMessTimingTextView.setText("");
        }
        if (mess_monthly_charge > 0) {
            messProfileMonthlyChargeTextView.setText(String.valueOf(mess_monthly_charge));
        } else {
            messProfileMonthlyChargeTextView.setText("");
        }

        messProfileBalanceTextView.setText("Rs " + String.valueOf(mess_balance));

        Picasso.get()
                .load(mess_image_link)
                .into(messProfileImageView);

        // stop the progress dialog
        stopProgressDialog();
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
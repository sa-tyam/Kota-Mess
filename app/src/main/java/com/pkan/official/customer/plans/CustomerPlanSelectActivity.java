package com.pkan.official.customer.plans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkan.official.R;
import com.pkan.official.customer.CustomerMainActivity;
import com.pkan.official.payments.PaymentsActivity;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.util.ArrayList;

public class CustomerPlanSelectActivity extends AppCompatActivity {

    // variables used for upi transaction
    final String COMPANY_UPI_ID = "skdbsp123@okhdfcbank";
    final String PAYEE_NAME = "Satyam Kumar";

    // views to be used in activity
    ImageView customerSelectPlanBackImageView;
    TextView customerSelectPlanRegularPlanTitleTextView,
            customerSelectPlanRegularPlanDescriptionTextView;
    Button customerPlanSelectRegularPlanAddButton, customerPlanSelectSkipButton;
    EditText customerPlanSelectRegularPlanInputEditText;
    RecyclerView customerPlanSelectRecyclerView;

    // Progress Dialog
    ProgressDialog progressDialog;

    // firebase variables to be used in functions
    FirebaseUser user;
    DatabaseReference databaseReference;

    // array list to set other plans
    ArrayList<OtherPlans> mPlansList;

    // check if user is new or existing
    String new_user = "";

    // customer balance
    int customer_balance = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_plan_select);

        // initialize the views used in the activity
        initViews();

        // set status bar color
        setStatusBarColor();

        // set On Clicks
        setOnClicks();

        // disable screen and start progress dialog until plans are set
        startProgressDialog();
        setRegularPlan();

        // regular plan is loaded, now load other plans
        setOtherPlans();
    }

    private void initViews () {

        // initializing all the views used in activity
        customerSelectPlanBackImageView = findViewById(R.id.customerSelectPlanBackImageView);
        customerSelectPlanRegularPlanTitleTextView = findViewById(R.id
                .customerSelectPlanRegularPlanTitleTextView);
        customerSelectPlanRegularPlanDescriptionTextView = findViewById(R.id
                .customerSelectPlanRegularPlanDescriptionTextView);
        customerPlanSelectRegularPlanAddButton = findViewById(R.id
                .customerPlanSelectRegularPlanAddButton);
        customerPlanSelectSkipButton = findViewById(R.id.customerPlanSelectSkipButton);
        customerPlanSelectRegularPlanInputEditText = findViewById(R.id
                .customerPlanSelectRegularPlanInputEditText);
        customerPlanSelectRecyclerView = findViewById(R.id.customerPlanSelectRecyclerView);

        // initialize progressDialog
        progressDialog = new ProgressDialog(CustomerPlanSelectActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize other plans array list
        mPlansList = new ArrayList<>();
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
                    R.color.activity_customer_plan_select_background));
        }

    }

    private void setOnClicks () {
        // finish the activity when back image view is clicked
        customerSelectPlanBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish the activity
                finish();
            }
        });

        // skip to home page if skip button is clicked
        customerPlanSelectSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // disable screen and start progress dialog
                startProgressDialog();

                // check if user is new or existing one
                databaseReference.child("Customers").child(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("Plan")) {

                                    // existing user
                                    new_user = "";
                                } else {

                                    // new user
                                    new_user = "newUser";
                                }

                                if (snapshot.child("Balance").getValue(Integer.class) != null) {
                                    customer_balance = snapshot.child("Balance")
                                            .getValue(Integer.class);

                                    // for debugging purpose
                                    Log.d("customer balance", String.valueOf(customer_balance));
                                } else {

                                    // for debugging purpose
                                    Log.e("customer balance", "not found");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                // for debugging in case of errors
                                Log.e("new user check", error.getDetails());
                            }
                        });

                if (new_user.length() > 0) {
                    databaseReference.child("Customers").child(user.getUid())
                            .child("Plan").child("Plan Name").setValue("Regular Plan").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // stop progress dialog
                            stopProgressDialog();

                            // go to home activity
                            Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                            startActivity(intent);

                            // finish this activity
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // stop progress dialog
                            stopProgressDialog();

                           Toast.makeText(getApplicationContext(), "Please Try Again",
                                   Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    // stop progress dialog
                    stopProgressDialog();

                    // user already exists, simply finish the activity
                    finish();
                }

            }
        });

        customerPlanSelectRegularPlanAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForAmount ();
            }
        });
    }

    private void checkForAmount () {
        // variable to check if amount is valid
        String amount = customerPlanSelectRegularPlanInputEditText.getText().toString();
        if (amount.length() > 0) {
            // amount is valid, so proceed to add balance
            addBalance (amount);
        } else {
            // make toast to warn user to enter amount
            Toast.makeText(getApplicationContext(), "Enter Valid amount", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void addBalance (String amount) {

        makeUpiPayment((float)Integer.parseInt(amount));

    }

    private void setRegularPlan () {

        databaseReference.child("Plans").child("Regular Plan")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String title, description;

                title = snapshot.child("Title").getValue(String.class);
                description = snapshot.child("Detail").getValue(String.class);

                if (title.length() > 0 && description.length() > 0) {

                    // regular plan loaded
                    customerSelectPlanRegularPlanTitleTextView.setText(title);
                    customerSelectPlanRegularPlanDescriptionTextView.setText(description);

                } else {

                    // database error, close the app
                    stopProgressDialog();
                    Toast.makeText(getApplicationContext(), "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // for debugging
                Log.e("setRegularPlan", error.getDetails());
            }
        });
    }

    private void setOtherPlans () {
        // clear other plans array list
        mPlansList.clear();

        // fetch data of other plans present on firebase
        databaseReference.child("Plans").child("Other Plans").addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // for debugging purpose
                Log.e("reached", "inside");
                Log.e("children", String.valueOf(snapshot.getChildrenCount()));

                // iterate through all available plans adding them to array list
                for (DataSnapshot keyNode : snapshot.getChildren()) {
                    String planId = "", title = "", description = "";
                    int diets = -1, price = -1;

                    planId = keyNode.getKey();
                    title = keyNode.child("Title").getValue(String.class);
                    description = keyNode.child("Details").getValue(String.class);
                    diets = keyNode.child("Diets").getValue(Integer.class);
                    price = keyNode.child("Price").getValue(Integer.class);

                    // check for validity and add plan to the array list
                    if (planId != null && title != null && description != null
                            && diets > 0 && price > 0) {
                        OtherPlans plan = new OtherPlans(planId, title, description, diets, price);
                        mPlansList.add(plan);
                    }

                    Log.d("iterator item", String.valueOf(price));
                }

                // now the list is complete and we can set recycler view
                Log.e("out of iterator loop", "reached");
                setOtherPlansRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // for debugging in case of error
                Log.e("setOtherPlans", error.getDetails());
            }
        });
    }

    private void setOtherPlansRecyclerView () {

        // for debugging purpose
        Log.e("set recyclerview", "reached");

        customerPlanSelectRecyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        customerPlanSelectRecyclerView.setHasFixedSize(true);

        CustomerPlanSelectAdapter adapter = new CustomerPlanSelectAdapter(getApplicationContext(), mPlansList);
        customerPlanSelectRecyclerView.setAdapter(adapter);
        stopProgressDialog();
    }

    private void makeUpiPayment (float amount) {

        // for debugging purpose
        Log.e("start upi", "reached");

        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(COMPANY_UPI_ID)
                .setPayeeName(PAYEE_NAME)
                .setTransactionId(user.getUid())
                .setTransactionRefId(user.getUid())
                .setDescription("for app Balance")
                .setAmount(String.valueOf(amount))
                .build();

        easyUpiPayment.startPayment();

        easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
            @Override
            public void onTransactionCompleted(TransactionDetails transactionDetails) {

                // for debugging purpose
                Log.d("payment", "completed");
                Log.d("transaction id", transactionDetails.getTransactionId());
                Log.d("transaction status", transactionDetails.getStatus());

                addBalance((int) amount);
            }

            @Override
            public void onTransactionSuccess() {

                // for debugging purpose
                Log.d("payment", "success");

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionSubmitted() {

                // for debugging purpose
                Log.d("payment", "submitted");

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Submitted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionFailed() {

                // for debugging purpose
                Log.d("payment", "failed");

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancelled() {

                // for debugging purpose
                Log.d("payment", "cancelled");

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAppNotFound() {

                // for debugging purpose
                Log.d("payment", "app not found");

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "App Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBalance(int amount) {
        databaseReference.child("Customers").child(user.getUid()).child("Balance")
                .setValue(customer_balance + amount);
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
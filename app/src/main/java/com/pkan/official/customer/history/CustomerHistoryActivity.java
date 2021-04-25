package com.pkan.official.customer.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.pkan.official.R;
import com.pkan.official.customer.order.OrderItem;

import java.util.ArrayList;

import static com.pkan.official.customer.history.GetCustomerHistoryList.orderItemArrayList;

public class CustomerHistoryActivity extends AppCompatActivity {

    // declare views to be used in activity
    RecyclerView customerHistoryRecyclerView;

    ImageView customerHistoryBackImageView;

    // Progress Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history);

        // initialize views and variables
        initViews ();

        // set status bar color
        setStatusBarColor();

        // set on clicks
        setOnClicks();

        // call retrieve data to set recycler view, disable screen and show progress dialog
        startProgressDialog();
        setRecyclerView();

        callRetrieveData();
    }

    private void initViews () {
        // initialize the views used in fragment
        customerHistoryRecyclerView = findViewById(R.id.customerHistoryRecyclerView);
        customerHistoryBackImageView = findViewById(R.id.customerHistoryBackImageView);

        // initialize progressDialog
        progressDialog = new ProgressDialog(CustomerHistoryActivity.this);
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
                    R.color.activity_customer_history_background));
        }

    }

    private void setOnClicks () {

        // simply finish the activity when back image view is clicked
        customerHistoryBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void callRetrieveData() {
        GetCustomerHistoryList.getDataFromDatabase(new GetCustomerHistoryList.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OrderItem> orderItemArrayList) {
                setRecyclerView();
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    private void setRecyclerView () {

        // enable the screen and stop progress dialog
        stopProgressDialog();

        // set the recycler view
        customerHistoryRecyclerView.setHasFixedSize(true);
        customerHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false));

        // use the static orderArrayList used in getCustomerHistoryList
        CustomerHistoryAdapter adapter = new CustomerHistoryAdapter (getApplicationContext(),
                orderItemArrayList);
        customerHistoryRecyclerView.setAdapter(adapter);
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
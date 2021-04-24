package com.pkan.official.mess.meals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pkan.official.R;
import com.pkan.official.mess.profile.MessPaymentOptionsEditActivity;

import java.util.ArrayList;

import static com.pkan.official.mess.meals.GetMessMealList.messMealArrayList;

public class MessSelectMealActivity extends AppCompatActivity {

    // views used in activity
    ImageView messSelectMealBackImageView;

    TextView messSelectMealHeaderTextView;

    SearchView messSelectMealSearchView;

    RecyclerView messSelectMealRecyclerView;

    Button messSelectMealAddNewMealButton;

    // firebase variables
    FirebaseUser user;
    DatabaseReference databaseReference;

    // progress dialog
    ProgressDialog progressDialog;

    // recycler view adapter
    SelectMealRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_select_meal);

        // initialize views and variables
        initViews();

        // set on clicks
        setOnClicks();

        // set recycler view
        setRecyclerView();

        // get meals data
        getMeals();

        // set search view
        setSearchView();
    }

    private void initViews () {

        // initialize views
        messSelectMealBackImageView = findViewById(R.id.messSelectMealBackImageView);
        messSelectMealHeaderTextView = findViewById(R.id.messSelectMealHeaderTextView);
        messSelectMealSearchView = findViewById(R.id.messSelectMealSearchView);
        messSelectMealRecyclerView = findViewById(R.id.messSelectMealRecyclerView);
        messSelectMealAddNewMealButton = findViewById(R.id.messSelectMealAddNewMealButton);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(MessSelectMealActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setOnClicks () {

        // simply finish the activity when back image view is clicked
        messSelectMealBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // move to add new meal activity when add new meal button is clicked
        messSelectMealAddNewMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessAddNewMealActivity.class);
                startActivity(intent);
            }
        });
    }

    // get data
    private void getMeals () {
        GetMessMealList.getDataFromDatabase(new GetMessMealList.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<MessMeal> messMealArrayList) {
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

    // set recycler view
    private void setRecyclerView () {
        messSelectMealRecyclerView.setHasFixedSize(true);
        messSelectMealRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false));
        adapter = new SelectMealRecyclerAdapter(getApplicationContext(),
                messMealArrayList);
        messSelectMealRecyclerView.setAdapter(adapter);
    }

    private void setSearchView () {
        messSelectMealSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String user_input = s.toLowerCase();
                ArrayList<MessMeal> searchArrayList = new ArrayList<>();

                for (MessMeal item : messMealArrayList) {
                    if (item.getItem_names().contains(user_input)) {
                        searchArrayList.add(item);
                    }
                }

                adapter.searchData(searchArrayList);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String user_input = s.toLowerCase();
                ArrayList<MessMeal> searchArrayList = new ArrayList<>();

                for (MessMeal item : messMealArrayList) {
                    if (item.getItem_names().contains(user_input)) {
                        searchArrayList.add(item);
                    }
                }

                adapter.searchData(searchArrayList);

                return true;
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
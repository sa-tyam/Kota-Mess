package com.pkan.official.mess.meals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.pkan.official.customer.meals.Meal;
import com.pkan.official.customer.order.MealItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessMealDetailActivity extends AppCompatActivity {

    // views used in activity
    ImageView messMealDetailBackImageView, messMealDetailAddImageView,
            messMealDetailImageView;

    TextView messMealDetailMessNameTextView, messMealDetailLabelTextView, mealDetailPriceTextView;

    LinearLayout messMealDetailItemsLinearLayout;

    Button messMealDetailSaveButton;

    ConstraintLayout messMealDetailAddNewMealConstraintLayout, mealDetailSpecialConstraintLayout;

    // firebase variables
    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    // progress dialog
    ProgressDialog progressDialog;

    // variables to be used in functions
    String upcoming_l_or_d = "", upcoming_date = "";
    String l_or_d = "";

    String next_meal_l_or_d = "", next_meal_date = "";

    // meal id to be used in activity
    String meal_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_meal_detail);

        // initialize views and variables
        initViews();

        // set status bar color
        setStatusBarColor();

        // set onClicks
        setOnClicks();

        // disable screen and start progress dialog
        startProgressDialog();

        // get upcoming lunch or dinner
        getUpcomingLunchOrDinner();
    }

    private void initViews () {

        // initialize views
        messMealDetailBackImageView = findViewById(R.id.messMealDetailBackImageView);
        messMealDetailAddImageView = findViewById(R.id.messMealDetailAddImageView);
        messMealDetailImageView = findViewById(R.id.messMealDetailImageView);
        messMealDetailMessNameTextView = findViewById(R.id.messMealDetailMessNameTextView);
        messMealDetailLabelTextView = findViewById(R.id.messMealDetailLabelTextView);
        mealDetailPriceTextView = findViewById(R.id.mealDetailPriceTextView);
        messMealDetailItemsLinearLayout = findViewById(R.id.messMealDetailItemsLinearLayout);
        messMealDetailSaveButton = findViewById(R.id.messMealDetailSaveButton);
        messMealDetailAddNewMealConstraintLayout = findViewById(R.id
                .messMealDetailAddNewMealConstraintLayout);
        mealDetailSpecialConstraintLayout = findViewById(R.id.mealDetailSpecialConstraintLayout);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // get meal id from intent
        meal_id = getIntent().getStringExtra("mealId");

        // initialize progress dialog
        progressDialog = new ProgressDialog(MessMealDetailActivity.this);
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
                    R.color.activity_meal_detail_background));
        }

    }

    private void setOnClicks () {

        // simply finish the activity when back image view is clicked
        messMealDetailBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // pick up an image for meal when add image image view is clicked
        messMealDetailAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        // go to add new meal when add new meal is clicked
        messMealDetailAddNewMealConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessAddNewMealActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveMeal () {

        // disable screen and start progress dialog
        startProgressDialog();

        databaseReference.child("Mess").child(user.getUid()).child("Current Meals").child(next_meal_date)
                .child(next_meal_l_or_d).child("Meal Id").setValue(meal_id);

        databaseReference.child("Mess").child(user.getUid()).child("Current Meals").child(next_meal_date)
                .child(next_meal_l_or_d).child("Available").setValue(1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // stop progress dialog
                stopProgressDialog();

                // alert user meal is saved
                Toast.makeText(getApplicationContext(), "Meal Saved!",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // stop progress dialog
                stopProgressDialog();

                // alert user meal is saved
                Toast.makeText(getApplicationContext(), "Something went wrong :(",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUpcomingLunchOrDinner () {
        databaseReference.child("Time Management Status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get the upcoming lunch or dinner
                upcoming_l_or_d = snapshot.child("Upcoming Lunch or Dinner").getValue(String.class);

                // get upcoming date
                upcoming_date = snapshot.child("Upcoming Date").getValue(String.class);

                // get next meal lunch or dinner
                next_meal_l_or_d = snapshot.child("Mess Next Lunch or Dinner").getValue(String.class);
                next_meal_date = snapshot.child("Mess Next Date").getValue(String.class);

                // check if meal of mess is set or not
                checkMealSetOrNot();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // stop progress dialog
                stopProgressDialog();

                // for debugging purpose
                Log.e("get upcoming l or d", error.getDetails());

                // finish the activity
                finish();
            }
        });
    }

    private void checkMealSetOrNot () {

        // check if mess upcoming meal has same date as upcoming date
        databaseReference.child("Mess").child(user.getUid()).child("Current Meals").child(next_meal_date)
                .child(next_meal_l_or_d).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("Meal Id")) {

                    // meal is already set
                    disableSaveMeal();
                } else {

                    // meal is not set
                    enableSaveMeal();
                }

                // set mess data
                setMessData();

                // get meal details
                getMealDetails();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("mess lunch or dinner", error.getDetails());
            }
        });
    }

    private void disableSaveMeal () {
        messMealDetailLabelTextView.setVisibility(View.GONE);
        messMealDetailSaveButton.setVisibility(View.GONE);
    }

    private void enableSaveMeal () {
        // save meal for corresponding time
        messMealDetailSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMeal();
            }
        });

        messMealDetailLabelTextView.setText(upcoming_l_or_d + " for " + upcoming_date);
    }

    private void setMessData () {

        databaseReference.child("Mess").child(user.getUid()).child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String mess_name = snapshot.child("Name").getValue(String.class);

                if (mess_name != null) {

                    // for debugging purpose, in case of error
                    Log.d("Mess name", mess_name);

                    messMealDetailMessNameTextView.setText(mess_name);
                } else {

                    // for debugging purpose, in case of error
                    Log.e("mess name", "not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("mess data", error.getDetails());
            }
        });
    }

    private void getMealDetails () {

        // get the meal
        databaseReference.child("Meals").child(meal_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // variables to be used
                        String mealId, mess_id, mess_name, meal_image_link, special_or_regular;
                        int meal_price = -1;
                        ArrayList<MealItem> mealItemArrayList = new ArrayList<>();

                        // get the required values
                        mealId = meal_id;
                        mess_id = snapshot.child("Mess Id").getValue(String.class);
                        mess_name = snapshot.child("Mess Name").getValue(String.class);
                        meal_image_link = snapshot.child("Picture Download Link").getValue(String.class);
                        special_or_regular = snapshot.child("Special or Normal").getValue(String.class);

                        if (snapshot.child("Price").getValue(Integer.class) != null) {
                            meal_price = snapshot.child("Price").getValue(Integer.class);
                        }
                        for (DataSnapshot itemNode : snapshot.child("Items").getChildren()) {

                            // create a meal item
                            String item_name = itemNode.child("Name").getValue(String.class);
                            String amount = itemNode.child("Amount").getValue(String.class);

                            MealItem mealItem = new MealItem(item_name, amount);

                            // add this item to array list
                            mealItemArrayList.add(mealItem);
                        }

                        if (meal_price > 0 && meal_id != null && mess_id != null && mess_name != null &&
                                mealItemArrayList.size() > 0) {

                            // for debugging in case of error
                            Log.d("mess name", mess_name);

                            // create a new meal
                            Meal meal = new Meal(meal_id, mess_id, mess_name, special_or_regular,
                                    meal_price, mealItemArrayList);

                            // set image link if available
                            if (meal_image_link != null) {
                                meal.setMeal_image_link(meal_image_link);
                            }

                            if (meal != null ) {
                                setMealDetails(meal);
                            } else {
                                // stop progress dialog
                                stopProgressDialog();

                                // warn user something went wrong and finish activity
                                Toast.makeText(getApplicationContext(), "Something went wrong :(",
                                        Toast.LENGTH_LONG).show();

                                finish();
                            }

                        } else {

                            // show user something went wrong
                            Toast.makeText(getApplicationContext(), "Something went wrong",
                                    Toast.LENGTH_LONG).show();

                            // enable screen
                            stopProgressDialog();

                            // finish
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        // for debugging in case of error
                        Log.e("get meal", error.getDetails());

                        // stop progress dialog
                        stopProgressDialog();

                        // warn user something went wrong and finish activity
                        Toast.makeText(getApplicationContext(), "Something went wrong :(",
                                Toast.LENGTH_LONG).show();

                        finish();
                    }
                });
    }

    // set meal details
    private void setMealDetails (Meal meal) {

        // set the texts
        if (!meal.getSpecial_or_regular().toLowerCase().equals("special")) {
            mealDetailSpecialConstraintLayout.setVisibility(View.GONE);
        }

        // set meal price
        mealDetailPriceTextView.setText("\u20B9" + " " +
                String.valueOf(meal.getMeal_price()));

        // set meal items
        for (MealItem mealItem : meal.getMealItemArrayList()) {
            // create view object
            View view = getLayoutInflater().inflate(
                    R.layout.customer_meal_item_item,
                    messMealDetailItemsLinearLayout, false);

            // declare and initialize textViews used
            TextView name, quantity;
            name = view.findViewById(R.id.customerMealItemItemNameTextView);
            quantity = view.findViewById(R.id.customerMealItemItemAmountTextView);

            // set texts
            name.setText(mealItem.getItem_name());
            quantity.setText(mealItem.getQuantity());

            // add this view to linear layout
            messMealDetailItemsLinearLayout.addView(view);
        }

        if (meal.getMeal_image_link() != null) {

            // configure the visibility
            messMealDetailAddImageView.setVisibility(View.GONE);
            messMealDetailImageView.setVisibility(View.VISIBLE);

            // set the meal image
            Picasso.get()
                    .load(meal.getMeal_image_link())
                    .into(messMealDetailImageView);
        } else {

            // configure the visibility
            messMealDetailAddImageView.setVisibility(View.VISIBLE);
            messMealDetailImageView.setVisibility(View.GONE);
        }

        // stop progress dialog
        stopProgressDialog();

    }

    private void pickImage () {
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
        storageReference.child("Pictures").child("Mess").child(user.getUid()).child("Meals")
                .child(meal_id).child("Image").putFile(uri)
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
                        databaseReference.child("Meals").child(meal_id).child("Picture Download Link")
                                .setValue(url.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // make screen active and stop progress dialog
                                stopProgressDialog();

                                // alert user image saved
                                Toast.makeText(getApplicationContext(), "image saved",
                                        Toast.LENGTH_LONG).show();

                                // set the image in image view
                                messMealDetailImageView.setVisibility(View.VISIBLE);
                                messMealDetailAddImageView.setVisibility(View.GONE);
                                Picasso.get()
                                        .load(url.toString())
                                        .into(messMealDetailImageView);

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
package com.pkan.official.mess.meals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessAddNewMealActivity extends AppCompatActivity {

    // views used in activity
    ImageView messAddNewMealIBackImageView, messAddNewMealAddImageView,
            messAddNewMealImageView, messAddNewMealNewItemAddItemImageView;

    LinearLayout messAddNewMealItemsLinearLayout;

    EditText messAddNewMealNewItemNameTextView, messAddNewMealNewItemQuantityTextView,
            messAddNewMealPriceTextView;

    Spinner messAddNewMealSpecialOrRegularSpinner;

    Button messAddNewMealAddButton;

    // firebase variables
    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    // regular or special spinner array list
    ArrayList<String> regularOrSpecialArrayList;

    // variables to be used to save the meal
    String special_or_regular = "";

    int monthly_price = -1;

    // array list of meal items
    ArrayList<MessMealItem> mealItemArrayList;

    // progress dialog
    ProgressDialog progressDialog;

    // meal id
    String meal_id = "", mess_name = "";

    Uri meal_image_uri;

    int price = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_add_new_meal);

        // initialize views and variables used in activity
        initViews();

        // set status bar color
        setStatusBarColor();

        // set onClicks
        setOnClicks();

        // set regular or special spinner
        setSpinner();

        // disable screen, show progress dialog and get mess name
        startProgressDialog();
        getMessName();

    }

    private void initViews () {

        // initialize views
        messAddNewMealIBackImageView = findViewById(R.id.messAddNewMealIBackImageView);
        messAddNewMealAddImageView = findViewById(R.id.messAddNewMealAddImageView);
        messAddNewMealImageView = findViewById(R.id.messAddNewMealImageView);
        messAddNewMealNewItemAddItemImageView = findViewById(R.id
                .messAddNewMealNewItemAddItemImageView);
        messAddNewMealItemsLinearLayout = findViewById(R.id.messAddNewMealItemsLinearLayout);
        messAddNewMealNewItemNameTextView = findViewById(R.id.messAddNewMealNewItemNameTextView);
        messAddNewMealNewItemQuantityTextView = findViewById(R.id
                .messAddNewMealNewItemQuantityTextView);
        messAddNewMealPriceTextView = findViewById(R.id.messAddNewMealPriceTextView);
        messAddNewMealSpecialOrRegularSpinner = findViewById(R.id
                .messAddNewMealSpecialOrRegularSpinner);
        messAddNewMealAddButton = findViewById(R.id.messAddNewMealAddButton);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // initialize array list
        regularOrSpecialArrayList = new ArrayList<>();
        regularOrSpecialArrayList.add("Regular");
        regularOrSpecialArrayList.add("Special");

        // initialize meal item array list
        mealItemArrayList = new ArrayList<>();

        // initialize progress dialog
        progressDialog = new ProgressDialog(MessAddNewMealActivity.this);
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
                    R.color.activity_mess_add_new_meal_background));
        }

    }

    private void setOnClicks () {

        // simply finish the activity when back image view is clicked
        messAddNewMealIBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // pick up an image for meal when add image image view is clicked
        messAddNewMealAddImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        // add the item to meal when add item image view is clicked
        messAddNewMealNewItemAddItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        // save the meal when add button is clicked
        messAddNewMealAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMeal();
            }
        });
    }

    private void getMessName () {
        databaseReference.child("Mess").child(user.getUid()).child("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mess_name = snapshot.child("Name").getValue(String.class);

                if (snapshot.child("Monthly Price").getValue(Integer.class) != null) {
                    monthly_price = snapshot.child("Monthly Price").getValue(Integer.class);
                }

                // set meal price
                price = monthly_price / 60;
                messAddNewMealPriceTextView.setText("Rs. " + String.valueOf(price));

                if (mess_name != null) {

                    // for debugging purpose, in case of error
                    Log.d("mess name", mess_name);

                    // stop progress dialog
                    stopProgressDialog();

                } else {

                    // for debugging purpose
                    Log.e("mess name", "no found");

                    // stop progress dialog
                    stopProgressDialog();

                    // warn user something went wrong
                    Toast.makeText(getApplicationContext(), "Something went wrong :(",
                            Toast.LENGTH_LONG).show();

                    // finish the activity
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // for debugging purpose
                Log.e("mess name", error.getDetails());
            }
        });
    }

    private void addItem () {
        String item_name = messAddNewMealNewItemNameTextView.getText().toString();
        String quantity = messAddNewMealNewItemQuantityTextView.getText().toString();

        if (item_name == null || item_name.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter Name", Toast.LENGTH_LONG).show();
        } else if (quantity == null || quantity.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter Quantity", Toast.LENGTH_LONG).show();
        } else {
            MessMealItem mealItem = new MessMealItem(item_name, quantity);
            if (mealItem != null) {
                mealItemArrayList.add(mealItem);

                // set texts to null
                messAddNewMealNewItemNameTextView.setText("");
                messAddNewMealNewItemQuantityTextView.setText("");

                setItemsInLayout();
            }
        }
    }

    private void setItemsInLayout () {

        // remove all views from the linear layout
        messAddNewMealItemsLinearLayout.removeAllViews();

        // iterate through all added items and add them to view
        for (int i = 0; i < mealItemArrayList.size(); i++) {

            // inflate the item view
            View view = getLayoutInflater().inflate(R.layout.mess_add_new_meal_added_item,
                    messAddNewMealItemsLinearLayout, false);

            // declare and initialize the views used
            TextView messAddNewMealAddedItemNameTextView = view.findViewById(R
                    .id.messAddNewMealAddedItemNameTextView);
            TextView messAddNewMealAddedItemQuantityTextView = view.findViewById(R.id
                    .messAddNewMealAddedItemQuantityTextView);
            ImageView messAddNewMealAddedItemRemoveImageView = view.findViewById(R.id
                    .messAddNewMealAddedItemRemoveImageView);

            // set the text
            messAddNewMealAddedItemNameTextView.setText(mealItemArrayList.get(i).getName());
            messAddNewMealAddedItemQuantityTextView.setText(mealItemArrayList.get(i).getQuantity());

            MessMealItem item = mealItemArrayList.get(i);

            // set on click listener on remove image view
            messAddNewMealAddedItemRemoveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // delete the selected item and reload items
                    mealItemArrayList.remove(item);
                    setItemsInLayout();
                }
            });

            // add this view to linear layout
            messAddNewMealItemsLinearLayout.addView(view);
        }

    }

    private void saveMeal () {

        // get the meal id
        meal_id = databaseReference.child("Meals").push().getKey();

        if (meal_id != null && price > 0 && mealItemArrayList.size() > 2) {

            // start progress dialog
            startProgressDialog();

            // mess reference
            DatabaseReference mess_ref = databaseReference.child("Mess").child(user.getUid())
                    .child("Meals").child(meal_id);

            // set meal data in mess section
            mess_ref.child("Mess Id").setValue(user.getUid());
            mess_ref.child("Mess Name").setValue(mess_name);
            mess_ref.child("Price").setValue(price);

            for (MessMealItem item : mealItemArrayList) {

                // get the item id
                String item_id = mess_ref.child("Items").push().getKey();

                // add the items
                mess_ref.child("Items").child(item_id).child("Name").setValue(item.getName());
                mess_ref.child("Items").child(item_id).child("Amount").setValue(item.getQuantity());
            }

            if (meal_image_uri != null) {
                uploadFile(meal_image_uri);
            }

            mess_ref.child("Special or Normal").setValue(special_or_regular);

            // meal reference
            DatabaseReference meal_ref = databaseReference.child("Meals").child(meal_id);

            meal_ref.child("Mess Id").setValue(user.getUid());
            meal_ref.child("Mess Name").setValue(mess_name);
            meal_ref.child("Price").setValue(price);

            for (MessMealItem item : mealItemArrayList) {

                // get the item id
                String item_id = meal_ref.child("Items").push().getKey();

                // add the items
                meal_ref.child("Items").child(item_id).child("Name").setValue(item.getName());
                meal_ref.child("Items").child(item_id).child("Amount").setValue(item.getQuantity());
            }

            if (meal_image_uri != null) {
                uploadFile(meal_image_uri);
            }

            meal_ref.child("Special or Normal").setValue(special_or_regular)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    // stop progress dialog
                    stopProgressDialog();

                    // alert user that meal is saved
                    Toast.makeText(getApplicationContext(), "Meal Saved",
                            Toast.LENGTH_LONG).show();

                    // send to meal detail activity
                    Intent intent = new Intent(getApplicationContext(), MessMealDetailActivity.class);
                    intent.putExtra("mealId", meal_id);
                    startActivity(intent);

                    // finish the activity
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // stop progress dialog
                    stopProgressDialog();

                    // warn user that meal not saved
                    Toast.makeText(getApplicationContext(), "Oops! Please Retry",
                            Toast.LENGTH_LONG).show();
                }
            });

        } else {

            // alert user to fill correct info
            Toast.makeText(getApplicationContext(), "Fill the information",
                    Toast.LENGTH_LONG).show();
        }


    }

    private void setSpinner () {

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.profile_spinners_item, regularOrSpecialArrayList);

        messAddNewMealSpecialOrRegularSpinner.setAdapter(adapter);

        messAddNewMealSpecialOrRegularSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // her 'i' is the position which was selected
                special_or_regular = regularOrSpecialArrayList.get(i);

                if ( i == 0) {
                    price = monthly_price / 60;
                } else {
                    price = (monthly_price / 60) + 5;
                }

                messAddNewMealPriceTextView.setText("Rs. " + String.valueOf(price));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // in case no item is selected, select the first item by default
                special_or_regular = regularOrSpecialArrayList.get(0);

                price = monthly_price / 60;
                messAddNewMealPriceTextView.setText("Rs. " + String.valueOf(price));
            }
        });
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

            // set the meal image uri
            meal_image_uri = uri;

            // set the image in image view
            messAddNewMealAddImageView.setVisibility(View.GONE);
            messAddNewMealImageView.setVisibility(View.VISIBLE);
            messAddNewMealImageView.setImageURI(uri);
        }
    }

    private void uploadFile (Uri uri) {

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

                        // save the download url in mess data
                        databaseReference.child("Mess").child(user.getUid()).child("Meals")
                                .child(meal_id).child("Picture Download Link")
                                .setValue(url.toString());

                        // save download url on firebase
                        databaseReference.child("Meals").child(meal_id).child("Picture Download Link")
                                .setValue(url.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // alert user image saved
                                Toast.makeText(getApplicationContext(), "image saved",
                                        Toast.LENGTH_LONG).show();

                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // failed to save download url
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
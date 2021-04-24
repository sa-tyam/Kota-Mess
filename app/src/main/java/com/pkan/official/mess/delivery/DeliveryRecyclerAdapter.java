package com.pkan.official.mess.delivery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pkan.official.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeliveryRecyclerAdapter extends RecyclerView.Adapter<DeliveryRecyclerAdapter.ViewHolder> {

    // variables to be used in class
    Context mContext;
    ArrayList<DeliveryOrder> deliveryOrderArrayList;

    // declare and initialize firebase variables
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // constructor
    public DeliveryRecyclerAdapter(Context mContext, ArrayList<DeliveryOrder> deliveryOrderArrayList) {
        this.mContext = mContext;
        this.deliveryOrderArrayList = deliveryOrderArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.delivery_main_recycler_view_item,
                parent, false);

        return new DeliveryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryOrder deliveryOrder = deliveryOrderArrayList.get(position);

        // set the texts
        holder.deliveryMainScrollViewItemCustomerNameTextView.setText(deliveryOrder.getCustomer_name());
        holder.deliveryMainScrollViewItemHouseNumberTextView.setText(deliveryOrder.getHouse_number());
        holder.deliveryMainScrollViewItemRoomNumberTextView.setText(deliveryOrder.getRoom_number());
        holder.deliveryMainScrollViewItemLandmarkTextView.setText(deliveryOrder.getLandmark());
        holder.deliverMainScrollViewItemPhoneNumberTextView.setText(deliveryOrder.getPhone_number());

        // set on clicks
        holder.deliveryMainScrollViewItemDeliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the alert dialog
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Are you sure to deliver");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                orderDelivered(deliveryOrder.getOrder_id());
                                dialog.cancel();
                            }
                        });
                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        // set the call action when phone number text view is clicked
        holder.deliverMainScrollViewItemPhoneNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ deliveryOrder.getPhone_number()));
                mContext.startActivity(intent);
            }
        });

    }

    public void orderDelivered (String order_id) {
        // get the current time
        Date currentTime = Calendar.getInstance().getTime();

        // mark order status as attended
        databaseReference.child("Orders").child(order_id).child("Status").setValue("attended at "
                + currentTime.toString());
        databaseReference.child("Orders").child(order_id).child("Delivered Time")
                .setValue(currentTime.toString());

        // alert mess that customer is attended
        Toast.makeText(mContext, "Attended", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return deliveryOrderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView deliveryMainScrollViewItemCustomerNameTextView,
                deliveryMainScrollViewItemHouseNumberTextView,
                deliveryMainScrollViewItemRoomNumberTextView,
                deliveryMainScrollViewItemLandmarkTextView,
                deliverMainScrollViewItemPhoneNumberTextView;

        Button deliveryMainScrollViewItemDeliverButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deliveryMainScrollViewItemCustomerNameTextView = itemView.findViewById(R.id
                    .deliveryMainScrollViewItemCustomerNameTextView);
            deliveryMainScrollViewItemHouseNumberTextView = itemView.findViewById(R.id
                    .deliveryMainScrollViewItemHouseNumberTextView);
            deliveryMainScrollViewItemRoomNumberTextView = itemView.findViewById(R.id
                    .deliveryMainScrollViewItemRoomNumberTextView);
            deliveryMainScrollViewItemLandmarkTextView = itemView.findViewById(R.id
                    .deliveryMainScrollViewItemLandmarkTextView);
            deliverMainScrollViewItemPhoneNumberTextView = itemView.findViewById(R.id
                    .deliverMainScrollViewItemPhoneNumberTextView);
            deliveryMainScrollViewItemDeliverButton = itemView.findViewById(R.id
                    .deliveryMainScrollViewItemDeliverButton);
        }
    }

    public void searchData (ArrayList<DeliveryOrder> searchArrayList) {
        deliveryOrderArrayList = new ArrayList<>();
        deliveryOrderArrayList.addAll(searchArrayList);
        notifyDataSetChanged();
    }
}

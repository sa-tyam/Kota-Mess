package com.pkan.official.mess.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pkan.official.R;
import com.pkan.official.customer.order.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessHistoryRecyclerAdapter extends RecyclerView.Adapter<MessHistoryRecyclerAdapter.ViewHolder> {

    Context mContext;
    ArrayList<OrderItem> orderItemArrayList;

    public MessHistoryRecyclerAdapter(Context mContext, ArrayList<OrderItem> orderItemArrayList) {
        this.mContext = mContext;
        this.orderItemArrayList = orderItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.mess_history_item, parent,
                false);

        return new MessHistoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderItem orderItem = orderItemArrayList.get(position);

        // set the texts in text views
        holder.messHistoryItemHeader.setText(orderItem.getLunch_or_dinner() + " for " +
                orderItem.getOrder_date());

        holder.messHistoryItemCustomerNameTextView.setText(orderItem.getCustomer_name());
        holder.messHistoryItemAddressTextView.setText(orderItem.getAddress());
        holder.messHistoryItemPriceTextView.setText("\u20B9" + " " +
                String.valueOf(orderItem.getOrder_price()));
        holder.messHistoryItemStatusTextView.setText(orderItem.getStatus());

        // set image in image view
        Picasso.get()
                .load(orderItem.getMeal_image_link())
                .into(holder.messHistoryItemImageView);
    }

    @Override
    public int getItemCount() {
        return orderItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // views used in item view
        TextView messHistoryItemHeader, messHistoryItemCustomerNameTextView,
                messHistoryItemAddressTextView, messHistoryItemPriceTextView,
                messHistoryItemStatusTextView;

        ImageView messHistoryItemImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize the views used in item view
            messHistoryItemHeader = itemView.findViewById(R.id.messHistoryItemHeader);
            messHistoryItemCustomerNameTextView = itemView.findViewById(R.id
                    .messHistoryItemCustomerNameTextView);
            messHistoryItemAddressTextView = itemView.findViewById(R.id
                    .messHistoryItemAddressTextView);
            messHistoryItemPriceTextView = itemView.findViewById(R.id
                    .messHistoryItemPriceTextView);
            messHistoryItemStatusTextView = itemView.findViewById(R.id
                    .messHistoryItemStatusTextView);
            messHistoryItemImageView = itemView.findViewById(R.id
                    .messHistoryItemImageView);

        }
    }
}

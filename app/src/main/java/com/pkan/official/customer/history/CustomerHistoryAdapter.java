package com.pkan.official.customer.history;

import android.content.Context;
import android.content.Intent;
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

public class CustomerHistoryAdapter extends RecyclerView.Adapter<CustomerHistoryAdapter.ViewHolder> {

    Context mContext;
    ArrayList<OrderItem> mOrderList;

    public CustomerHistoryAdapter(Context mContext, ArrayList<OrderItem> mOrderList) {
        this.mContext = mContext;
        this.mOrderList = mOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.customer_history_item, parent,
                false);
        return new CustomerHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // set text views accordingly in the customer history item
        OrderItem orderItem = mOrderList.get(position);
        holder.customerHistoryItemHeader.setText(orderItem.getLunch_or_dinner() + " for " +
                orderItem.getOrder_date());
        holder.customerHistoryItemMessNameTextView.setText(orderItem.getMess_name());

        // prepare string for item names
        String items = "";
        items = orderItem.getItemArrayList().get(0).getItem_name();

        for ( int i = 1; i < orderItem.getItemArrayList().size(); i++) {
            items += " + " + orderItem.getItemArrayList().get(i).getItem_name();
        }

        holder.customerHistoryItemItemNamesTextView.setText(items);

        holder.customerHistoryItemPriceTextView.setText("\u20B9" + " " +
               String.valueOf( orderItem.getOrder_price()));
        holder.customerHistoryItemDeliveredAtTextView.setText("delivered at " +
                orderItem.getDelivered_time());

        Picasso.get()
                .load(orderItem.getMeal_image_link())
                .into(holder.customerHistoryItemImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CustomerHistoryDetailActivity.class);
                intent.putExtra("orderId", String.valueOf(orderItem.getOrder_id()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView customerHistoryItemHeader, customerHistoryItemMessNameTextView,
                customerHistoryItemItemNamesTextView, customerHistoryItemPriceTextView,
                customerHistoryItemDeliveredAtTextView;

        ImageView customerHistoryItemImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerHistoryItemHeader = itemView.findViewById(R.id.customerHistoryItemHeader);
            customerHistoryItemMessNameTextView = itemView.findViewById(R.id
                    .customerHistoryItemMessNameTextView);
            customerHistoryItemItemNamesTextView = itemView.findViewById(R.id
                    .customerHistoryItemItemNamesTextView);
            customerHistoryItemPriceTextView = itemView.findViewById(R.id
                    .customerHistoryItemPriceTextView);
            customerHistoryItemDeliveredAtTextView = itemView.findViewById(R.id
                    .customerHistoryItemDeliveredAtTextView);
            customerHistoryItemImageView = itemView.findViewById(R.id
                    .customerHistoryItemImageView);

        }
    }
}

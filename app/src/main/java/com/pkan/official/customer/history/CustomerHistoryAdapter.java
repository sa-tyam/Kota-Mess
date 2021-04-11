package com.pkan.official.customer.history;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pkan.official.customer.order.OrderItem;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

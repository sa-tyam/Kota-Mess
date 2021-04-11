package com.pkan.official.customer.plans;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pkan.official.R;
import com.pkan.official.payments.PaymentsActivity;

import java.util.ArrayList;

public class CustomerPlanSelectAdapter extends RecyclerView.Adapter<CustomerPlanSelectAdapter.ViewHolder> {
    Context mContext;
    ArrayList<OtherPlans> mPlansList;

    public CustomerPlanSelectAdapter(Context mContext, ArrayList<OtherPlans> mPlansList) {
        this.mContext = mContext;
        this.mPlansList = mPlansList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.customer_plan_select_recycler_view_item,
                parent, false);
        return new CustomerPlanSelectAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String planId = mPlansList.get(position).getPlanId();
        String title = mPlansList.get(position).getTitle();
        String description = mPlansList.get(position).getDescription();
        int price = mPlansList.get(position).getPrice();
        int validity = mPlansList.get(position).getDiets();

        // for debugging purpose
        Log.d("adapter - price", String.valueOf(price));

        holder.customerSelectPlanItemPlanTitleTextView.setText(title);
        holder.customerSelectPlanItemDescriptionTextView.setText(description);
        holder.customerPlanSelectItemPriceTextView.setText(String.valueOf(price));

        holder.customerPlanSelectItemSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PaymentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("planId", planId);
                intent.putExtra("amount", price);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlansList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerSelectPlanItemPlanTitleTextView, customerSelectPlanItemDescriptionTextView,
                customerPlanSelectItemPriceTextView;
        Button customerPlanSelectItemSelectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerSelectPlanItemPlanTitleTextView = itemView.findViewById(R.id
                    .customerSelectPlanItemPlanTitleTextView);
            customerSelectPlanItemDescriptionTextView = itemView.findViewById(R.id
                    .customerSelectPlanItemDescriptionTextView);
            customerPlanSelectItemPriceTextView = itemView.findViewById(R.id
                    .customerPlanSelectItemPriceTextView);
            customerPlanSelectItemSelectButton = itemView.findViewById(R.id
                    .customerPlanSelectItemSelectButton);
        }
    }
}

package com.pkan.official.customer.meals;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pkan.official.R;
import com.pkan.official.customer.order.CustomerConfirmOrderActivity;
import com.pkan.official.customer.order.CustomerOrderDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MealRecyclerAdapter extends RecyclerView.Adapter<MealRecyclerAdapter.ViewHolder> {

    Context mContext;
    static ArrayList<Meal> mealArrayList;

    public MealRecyclerAdapter(Context mContext, ArrayList<Meal> mealArrayList) {
        this.mContext = mContext;
        this.mealArrayList = mealArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.customer_select_meal_item,
                parent, false);

        return new MealRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // set the texts
        Meal meal = mealArrayList.get(position);

        holder.customerHomeSelectMealItemMessNameTextView.setText(meal.getMess_name());

        // create item string
        String item_string = meal.getMealItemArrayList().get(0).getItem_name();

        for ( int i = 1; i < meal.getMealItemArrayList().size(); i++) {
            item_string += " + " + meal.getMealItemArrayList().get(i).getItem_name();
        }

        // set the items text
        holder.customerHomeSelectMealItemItemNamesTextView.setText(item_string);

        holder.customerHomeSelectMealItemPriceTextView.setText("\u20B9" + " " +
                String.valueOf(meal.getMeal_price()));

        // check if meal is special or regular

        String special_or_not = meal.getSpecial_or_regular().toLowerCase();

        // set meal image
        Picasso.get()
                .load(meal.getMeal_image_link())
                .into(holder.customerHomeNextItemImageView);

        // if meal is not special, hide special image view and text view
        if (!special_or_not.equals("special")) {
            holder.customerHomeSelectMealSpecialTextView.setVisibility(View.GONE);
            holder.customerHomeSelectOrderSpecialImageView.setVisibility(View.GONE);
        }

        // set on click listener on view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CustomerOrderDetailsActivity.class);
                intent.putExtra("mealId", meal.getMeal_id());
                mContext.startActivity(intent);
            }
        });

        // set on click listener to button
        holder.customerHomeSelectMealSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CustomerConfirmOrderActivity.class);
                intent.putExtra("mealId", meal.getMeal_id());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mealArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // declare views to be used
        TextView customerHomeSelectMealItemMessNameTextView,
                customerHomeSelectMealItemItemNamesTextView,
                customerHomeSelectMealItemPriceTextView, customerHomeSelectMealSpecialTextView;

        ImageView customerHomeNextItemImageView, customerHomeSelectOrderSpecialImageView;

        Button customerHomeSelectMealSelectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize the views
            customerHomeSelectMealItemMessNameTextView = itemView.findViewById(R.id
                    .customerHomeSelectMealItemMessNameTextView);
            customerHomeSelectMealItemItemNamesTextView = itemView.findViewById(R.id
                    .customerHomeSelectMealItemItemNamesTextView);
            customerHomeSelectMealItemPriceTextView = itemView.findViewById(R.id
                    .customerHomeSelectMealItemPriceTextView);
            customerHomeSelectMealSpecialTextView = itemView.findViewById(R.id
                    .customerHomeSelectMealSpecialTextView);
            customerHomeNextItemImageView = itemView.findViewById(R.id
                    .customerHomeNextItemImageView);
            customerHomeSelectOrderSpecialImageView = itemView.findViewById(R.id
                    .customerHomeSelectOrderSpecialImageView);
            customerHomeSelectMealSelectButton = itemView.findViewById(R.id
                    .customerHomeSelectMealSelectButton);
        }
    }
    public void searchData(ArrayList<Meal> searchArrayList) {
        mealArrayList = new ArrayList<>();
        mealArrayList.addAll(searchArrayList);
        notifyDataSetChanged();
    }
}

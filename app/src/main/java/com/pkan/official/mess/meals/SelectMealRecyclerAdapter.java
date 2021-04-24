package com.pkan.official.mess.meals;

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
import com.pkan.official.customer.meals.Meal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SelectMealRecyclerAdapter extends RecyclerView.Adapter<SelectMealRecyclerAdapter.ViewHolder> {

    // variables to be used in class
    Context mContext;
    ArrayList<MessMeal> mealArrayList;

    public SelectMealRecyclerAdapter(Context mContext, ArrayList<MessMeal> mealArrayList) {
        this.mContext = mContext;
        this.mealArrayList = mealArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mess_select_meal_meal_item,
                parent, false);
        return new SelectMealRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessMeal meal = mealArrayList.get(position);

        // set the texts
        holder.messSelectMealItemItemNamesTextView.setText(meal.getItem_names());
        holder.messSelectMealItemPriceTextView.setText("\u20B9" + " " +
                String.valueOf(meal.getMeal_price()));

        // set if special or not
        if (!meal.getSpecial_or_regular().toLowerCase().equals("special")) {
            holder.messSelectMealSpecialTextView.setVisibility(View.GONE);
            holder.messSelectOrderSpecialImageView.setVisibility(View.GONE);
        }

        // set the image
        Picasso.get()
                .load(meal.getMeal_image_link())
                .into(holder.messSelectMealItemImageView);

        // set on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // move to meal detail activity

                Intent intent = new Intent(mContext, MessMealDetailActivity.class);
                intent.putExtra("mealId", meal.getMeal_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        holder.messSelectMealSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // move to meal detail activity

                Intent intent = new Intent(mContext, MessMealDetailActivity.class);
                intent.putExtra("mealId", meal.getMeal_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // declare views used in the item view
        ImageView messSelectMealItemImageView, messSelectOrderSpecialImageView;
        TextView messSelectMealItemItemNamesTextView, messSelectMealItemPriceTextView,
                messSelectMealSpecialTextView;
        Button messSelectMealSelectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize the views used in item view
            messSelectMealItemImageView = itemView.findViewById(R.id.messSelectMealItemImageView);
            messSelectOrderSpecialImageView = itemView.findViewById(R.id.messSelectOrderSpecialImageView);
            messSelectMealItemItemNamesTextView = itemView.findViewById(R.id.messSelectMealItemItemNamesTextView);
            messSelectMealItemPriceTextView = itemView.findViewById(R.id.messSelectMealItemPriceTextView);
            messSelectMealSpecialTextView = itemView.findViewById(R.id.messSelectMealSpecialTextView);
            messSelectMealSelectButton = itemView.findViewById(R.id.messSelectMealSelectButton);
        }
    }

    public void searchData (ArrayList<MessMeal> searchArrayList) {
        mealArrayList = new ArrayList<>();
        mealArrayList.addAll(searchArrayList);
        notifyDataSetChanged();
    }
}

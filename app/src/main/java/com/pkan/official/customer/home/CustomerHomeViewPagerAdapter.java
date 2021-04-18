package com.pkan.official.customer.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerHomeViewPagerAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<String> imageUrls;

    public CustomerHomeViewPagerAdapter(Context mContext, ArrayList<String> imageUrls) {
        this.mContext = mContext;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        // create a new image view object to add in view pager
        ImageView imageview = new ImageView(mContext);

        // set image view parameters
        imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // load the image in image view
        Picasso.get()
                .load(imageUrls.get(position))
                .fit()
                .into(imageview);
        container.addView(imageview);
        return imageview;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

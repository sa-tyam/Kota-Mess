package com.pkan.official.payments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pkan.official.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentsActivity extends AppCompatActivity implements PaymentResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        float amount = getIntent().getFloatExtra("amount", 5);
        amount *= 100;
        startPayment(amount);

    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG);
    }

    public void startPayment (float amount) {
        Checkout checkout = new Checkout();
        try {

            Log.e("amount", String.valueOf(amount));

            JSONObject options = new JSONObject();
            options.put("name", "Merchant Name");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", amount);//pass amount in currency subunits

            checkout.open(this, options);

        } catch (Exception e) {
            Log.e("payment error", e.getMessage());
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG);
    }
}
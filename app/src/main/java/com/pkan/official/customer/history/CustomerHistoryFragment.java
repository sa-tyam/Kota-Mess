package com.pkan.official.customer.history;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pkan.official.R;
import com.pkan.official.customer.order.OrderItem;

import java.util.ArrayList;

import static com.pkan.official.customer.history.GetCustomerHistoryList.orderItemArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerHistoryFragment extends Fragment {

    // default generated code, start code from line 64

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerHistoryFragment newInstance(String param1, String param2) {
        CustomerHistoryFragment fragment = new CustomerHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    // declare views to be used in fragment
    RecyclerView customerHistoryFragmentRecyclerView;

    // Progress Dialog
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_history, container,
                false);

        // initialize views and variables
        initViews (view);

        // call retrieve data to set recycler view, disable screen and show progress dialog
        startProgressDialog();
        callRetrieveData();

        return view;
    }

    private void initViews (View view) {
        // initialize the views used in fragment
        customerHistoryFragmentRecyclerView = view.findViewById(R.id
                .customerHistoryFragmentRecyclerView);

        // initialize progressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);

    }

    private void callRetrieveData() {
        GetCustomerHistoryList.getDataFromDatabase(new GetCustomerHistoryList.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<OrderItem> orderItemArrayList) {
                setRecyclerView();
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    private void setRecyclerView () {

        // enable the screen and stop progress dialog
        stopProgressDialog();

        // set the recycler view
        customerHistoryFragmentRecyclerView.setHasFixedSize(true);
        customerHistoryFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        // use the static orderArrayList used in getCustomerHistoryList
        CustomerHistoryAdapter adapter = new CustomerHistoryAdapter (getContext(),
                orderItemArrayList);
        customerHistoryFragmentRecyclerView.setAdapter(adapter);
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
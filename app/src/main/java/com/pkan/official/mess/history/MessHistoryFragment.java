package com.pkan.official.mess.history;

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

import static com.pkan.official.mess.history.GetMessHistoryList.messOrderItemArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessHistoryFragment extends Fragment {

    // auto generated code

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessHistoryFragment newInstance(String param1, String param2) {
        MessHistoryFragment fragment = new MessHistoryFragment();
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


    // view to be used in fragment
    View view;

    // views used in fragment view
    RecyclerView messHistoryRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_mess_history, container, false);

        // initialize views and variables
        initViews();

        // set recycler view
        setRecyclerView();

        // get history data
        getHistoryData();

        return view;
    }

    private void initViews () {
        messHistoryRecyclerView = view.findViewById(R.id.messHistoryRecyclerView);
    }

    private void setRecyclerView () {
        messHistoryRecyclerView.setHasFixedSize(true);
        messHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        MessHistoryRecyclerAdapter adapter = new MessHistoryRecyclerAdapter(getContext(),
                messOrderItemArrayList);

        messHistoryRecyclerView.setAdapter(adapter);
    }

    private void getHistoryData () {
        GetMessHistoryList.getDataFromDatabase(new GetMessHistoryList.DataStatus() {
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
}
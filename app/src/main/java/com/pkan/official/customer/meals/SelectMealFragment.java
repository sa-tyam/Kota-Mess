package com.pkan.official.customer.meals;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pkan.official.R;

import java.util.ArrayList;

import static com.pkan.official.customer.meals.GetMealList.mealArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectMealFragment extends Fragment {

    // default generated code
    // start coding from line 64

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SelectMealFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectMealFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectMealFragment newInstance(String param1, String param2) {
        SelectMealFragment fragment = new SelectMealFragment();
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


    // declare view to be used in fragment
    View view;

    // views used in fragment
     SearchView mealSelectFragmentSearchView;
     RecyclerView mealSelectFragmentRecyclerView;

    // firebase variables to be used in functions
    FirebaseUser user;
    DatabaseReference databaseReference;

    // Progress Dialog
    ProgressDialog progressDialog;

    // declare recycler view adapter
    MealRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select_meal, container, false);

        // initialize views and variables to be used in fragment
        initViews();

        // disable screen, show progress dialog and set the meals
        startProgressDialog();
        setSelectMeal();

        // set recycler view Adapter
        setRecyclerView();

        //set search view
        setSearchView();

        return view;
    }

    private void initViews() {

        // initialize views
        mealSelectFragmentSearchView = view.findViewById(R.id.mealSelectFragmentSearchView);
        mealSelectFragmentRecyclerView = view.findViewById(R.id.mealSelectFragmentRecyclerView);

        // initialize firebase variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize progressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
    }

    private void setSelectMeal () {

        GetMealList.getDataFromDataBase(new GetMealList.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<Meal> mealArrayList) {

                // update recycler view every time a new data is added
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
        // stop progress dialog
        stopProgressDialog();

        // set the recycler vew
        mealSelectFragmentRecyclerView.setHasFixedSize(true);
        mealSelectFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        adapter = new MealRecyclerAdapter(getContext(),
                mealArrayList);
        mealSelectFragmentRecyclerView.setAdapter(adapter);
    }

    private void setSearchView () {
        mealSelectFragmentSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String user_input = s.toLowerCase();
                ArrayList<Meal> searchArrayList = new ArrayList<>();

                for (Meal meal_item : mealArrayList) {
                    if (meal_item.getMess_name().toLowerCase().contains(user_input)) {
                        searchArrayList.add(meal_item);
                    }
                }

                adapter.searchData(searchArrayList);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                String user_input = s.toLowerCase();
                ArrayList<Meal> searchArrayList = new ArrayList<>();

                for (Meal meal_item : mealArrayList) {
                    if (meal_item.getMess_name().toLowerCase().contains(user_input)) {
                        searchArrayList.add(meal_item);
                    }
                }

               adapter.searchData(searchArrayList);
                return true;
            }
        });
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
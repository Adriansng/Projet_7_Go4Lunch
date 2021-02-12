package com.adriansng.projet_7_go4lunch.view.mainActivity.fragment.restaurantList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.viewModel.Injection;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModeMainActivity;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModelFactoryMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantListFragment extends Fragment {

    // --- FOR DATA ---

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private TextView textViewRecyclerViewEmpty;

    private List<Restaurant> restaurantList = new ArrayList<>();

    private ViewModeMainActivity viewModeMainActivity;

    // --- FRAGMENT INSTANCE ---

    public RestaurantListFragment() {
    }

    public static RestaurantListFragment newInstance() {
        return new RestaurantListFragment();
    }

    // ------------------
    // TO CREATE
    // ------------------

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.restaurant_list_fragment, container, false);
        configViewModel();
        mSwipeRefresh = root.findViewById(R.id.fragment_restaurants);
        mRecyclerView = root.findViewById(R.id.recycler_view_restaurants);
        textViewRecyclerViewEmpty = root.findViewById(R.id.restaurants_text_view_recycler_view_empty);
        configRecyclerView(restaurantList);
        configObsIsChargedList();
        return root;
    }

    // ------------------
    // VIEW MODEL
    // ------------------

    private void configViewModel() {
        ViewModelFactoryMainActivity viewModelFactoryMainActivity = Injection.viewModelFactoryMainActivity();
        viewModeMainActivity = new ViewModelProvider(requireActivity(), viewModelFactoryMainActivity).get(ViewModeMainActivity.class);
    }

    // ------------------
    // PLACES
    // ------------------

    private void configObsIsChargedList() {
        viewModeMainActivity.IsChargedList().observe(getViewLifecycleOwner(), isCharged -> {
            if (isCharged) {
                getPlaces();
            }
        });
    }

    private void getPlaces() {
        viewModeMainActivity.getListPlaces().observe(requireActivity(), restaurants -> {
            restaurantList = restaurants;
            configRecyclerView(restaurantList);
        });
    }

    // ------------------
    // REFRESH PLACES
    // ------------------

    private void refreshPlaces() {
        viewModeMainActivity.refreshList(requireContext(), requireActivity());
    }

    // ------------------
    // UI
    // ------------------

    // --- RECYCLERVIEW ---

    private void configRecyclerView(List<Restaurant> restaurants) {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.requireContext(), LinearLayoutManager.VERTICAL));
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(restaurants);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Objects.requireNonNull(textViewRecyclerViewEmpty).setVisibility(restaurantAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        mRecyclerView.setAdapter(new RestaurantAdapter(restaurants));
        configRefreshView();
    }


    private void configRefreshView() {
        mSwipeRefresh.setOnRefreshListener(() -> {
            refreshPlaces();
            mSwipeRefresh.setRefreshing(false);
        });
    }
}
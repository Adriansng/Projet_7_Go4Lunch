package com.adriansng.projet_7_go4lunch.view.mainActivity.fragment.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.viewModel.Injection;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModeMainActivity;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModelFactoryMainActivity;

import java.util.List;
import java.util.Objects;

public class WorkmatesFragment extends Fragment  {

    // --- FOR DATA ---

    private RecyclerView mRecyclerView;
    private TextView textViewRecyclerViewEmpty;

    private List<Workmate> workmateList;

    private ViewModeMainActivity viewModeMainActivity;

    // --- FRAGMENT INSTANCE ---

    public WorkmatesFragment() {
    }

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    // ------------------
    // TO CREATE
    // ------------------

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.workmates_fragment, container, false);
        configViewModel();
        mRecyclerView = root.findViewById(R.id.recycler_view_workmates);
        textViewRecyclerViewEmpty = root.findViewById(R.id.workmate_text_view_recycler_view_empty);
        getWorkmates();
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
    // WORKMATES
    // ------------------

    private void getWorkmates() {
        viewModeMainActivity.getWorkmates().observe(getViewLifecycleOwner(), workmates -> {
            workmateList = workmates;
            viewModeMainActivity.deleteWorkmateLoggedToList(workmateList);
            configRecyclerView();
        });
    }

    // ------------------
    // UI
    // ------------------

    // --- RECYCLER VIEW ---

    private void configRecyclerView() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.requireContext(), LinearLayoutManager.VERTICAL));
        WorkmatesListAdapter workmatesListAdapter = new WorkmatesListAdapter(workmateList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Objects.requireNonNull(textViewRecyclerViewEmpty).setVisibility(workmatesListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        mRecyclerView.setAdapter(workmatesListAdapter);
    }
}
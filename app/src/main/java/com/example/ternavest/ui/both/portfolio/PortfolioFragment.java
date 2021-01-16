package com.example.ternavest.ui.both.portfolio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ternavest.R;
import com.example.ternavest.adapter.recycler.PortfolioAdapter;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.viewmodel.PortfolioViewModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;

public class PortfolioFragment extends Fragment {
    private static final String TAG = PortfolioFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private PortfolioViewModel portfolioViewModel;
    private PortfolioAdapter adapter;

    public PortfolioFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);

        UserPreference userPreference = new UserPreference(getContext());

        recyclerView = view.findViewById(R.id.rv_portfolio_portfolio);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new PortfolioAdapter();
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar1);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Portofolio");
        else if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Peminat");

        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
        portfolioViewModel.loadData(userPreference.getUserLevel());
        portfolioViewModel.getData().observe(this, new Observer<ArrayList<Portfolio>>() {
            @Override
            public void onChanged(ArrayList<Portfolio> portfolioList) {
                adapter.setData(portfolioList);
            }
        });
        portfolioViewModel.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) Log.w(TAG, "Listen failed", error);
                else if (value != null && !value.isEmpty()){
                    portfolioViewModel.loadData(userPreference.getUserLevel());
                    Log.d(TAG, "Changes detected");
                }
            }
        });
    }
}
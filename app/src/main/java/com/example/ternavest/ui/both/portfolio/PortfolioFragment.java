package com.example.ternavest.ui.both.portfolio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imgIlustrasi;

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
        imgIlustrasi = view.findViewById(R.id.img_ilustrasi);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new PortfolioAdapter();
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar1);

        if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) toolbar.setTitle("Portofolio");
        else if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) toolbar.setTitle("Peminat");

        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
        portfolioViewModel.loadData(userPreference.getUserLevel());
        portfolioViewModel.getData().observe(this, new Observer<ArrayList<Portfolio>>() {
            @Override
            public void onChanged(ArrayList<Portfolio> portfolioList) {
                adapter.setData(portfolioList);
                if (portfolioList.isEmpty()) imgIlustrasi.setVisibility(View.VISIBLE);
                else imgIlustrasi.setVisibility(View.INVISIBLE);
            }
        });
        portfolioViewModel.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) Log.w(TAG, "Listen failed", error);
                else if (value != null){
                    portfolioViewModel.loadData(userPreference.getUserLevel());
                    Log.d(TAG, "Changes detected");
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void getData() {

        UserPreference userPreference = new UserPreference(getContext());
        portfolioViewModel.loadData(userPreference.getUserLevel());
        portfolioViewModel.getData().observe(this, new Observer<ArrayList<Portfolio>>() {
            @Override
            public void onChanged(ArrayList<Portfolio> portfolioList) {
                adapter.setData(portfolioList);
                if (portfolioList.isEmpty()) imgIlustrasi.setVisibility(View.VISIBLE);
                else imgIlustrasi.setVisibility(View.INVISIBLE);
            }
        });
        portfolioViewModel.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) Log.w(TAG, "Listen failed", error);
                else if (value != null){
                    portfolioViewModel.loadData(userPreference.getUserLevel());
                    Log.d(TAG, "Changes detected");
                }
            }
        });
    }
}
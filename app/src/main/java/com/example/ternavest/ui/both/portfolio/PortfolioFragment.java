package com.example.ternavest.ui.both.portfolio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ternavest.R;
import com.example.ternavest.adapter.recycler.PortfolioAdapter;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.viewmodel.PortfolioViewModel;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;

public class PortfolioFragment extends Fragment {
    private static final String TAG = PortfolioFragment.class.getSimpleName();

    private PortfolioAdapter adapter;
    private PortfolioViewModel portfolioViewModel;
    private UserPreference userPreference;

    private LinearLayout layoutEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmpty;

    public PortfolioFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);

        userPreference = new UserPreference(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.rv_portfolio_portfolio);
        layoutEmpty = view.findViewById(R.id.layout_empty_portfolio);
        tvEmpty = view.findViewById(R.id.tv_empty_portfolio);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new PortfolioAdapter();
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) toolbar.setTitle("Portofolio");
        else if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) toolbar.setTitle("Peminat");

        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
        portfolioViewModel.loadData(userPreference.getUserLevel());
        portfolioViewModel.getData().observe(this, portfolioList -> {
            adapter.setData(portfolioList);
            if (portfolioList.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) tvEmpty.setText("Kamu belum pernah investasi");
                else if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) tvEmpty.setText("Belum ada peminat");
            }
            else layoutEmpty.setVisibility(View.INVISIBLE);
        });
        portfolioViewModel.getReference().addSnapshotListener((value, error) -> {
            if (error != null) Log.w(TAG, "Listen failed", error);
            else if (value != null){
                portfolioViewModel.loadData(userPreference.getUserLevel());
                Log.d(TAG, "Changes detected");
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            portfolioViewModel.loadData(userPreference.getUserLevel());
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}
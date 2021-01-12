package com.example.ternavest.ui.both.portfolio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ternavest.R;
import com.example.ternavest.adaper.PortfolioAdapter;
import com.example.ternavest.model.Portfolio;
import com.example.ternavest.preference.UserPreference;
import com.example.ternavest.viewmodel.PortfolioViewModel;

import java.util.ArrayList;

import static com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR;
import static com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK;

public class PortfolioFragment extends Fragment {
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

        TextView tvTitle = view.findViewById(R.id.tv_title_portfolio);
        if (userPreference.getUserLevel().equals(LEVEL_INVESTOR)) tvTitle.setText("Portofolio");
        else if (userPreference.getUserLevel().equals(LEVEL_PETERNAK)) tvTitle.setText("Peminat");

        portfolioViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(PortfolioViewModel.class);
        portfolioViewModel.loadData(userPreference.getUserLevel());
        portfolioViewModel.getData().observe(this, new Observer<ArrayList<Portfolio>>() {
            @Override
            public void onChanged(ArrayList<Portfolio> portfolioList) {
                adapter.setData(portfolioList);
            }
        });
    }
}
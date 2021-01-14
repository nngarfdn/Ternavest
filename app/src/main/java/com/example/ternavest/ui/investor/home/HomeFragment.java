package com.example.ternavest.ui.investor.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.ternavest.R;
import com.example.ternavest.adaper.recycler.ProyekInvestorAdaper;
import com.example.ternavest.viewmodel.ProyekViewModel;
import com.example.ternavest.viewmodel.SearchViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProyekViewModel projectViewModel;
    private ProyekInvestorAdaper adapter;
    private ShimmerFrameLayout shimmerKelola;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchViewModel searchViewModel;
    SearchView searchView;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        projectViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProyekViewModel.class);
        searchViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(SearchViewModel.class);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_investor);
        shimmerKelola = view.findViewById(R.id.shimmerKelola);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        searchView = view.findViewById(R.id.search_proyek);

        CharSequence query = searchView.getQuery();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewModel.loadData(query);
                shimmerKelola.startShimmerAnimation();
                searchViewModel.getData().observe(getViewLifecycleOwner(), result -> {
                    shimmerKelola.setVisibility(View.INVISIBLE);
                    shimmerKelola.stopShimmerAnimation();
                    adapter = new ProyekInvestorAdaper(result);
                    recyclerView.setAdapter(adapter);
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
            }
        });

        projectViewModel.loadResult();
        shimmerKelola.startShimmerAnimation();
        getData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void getData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        projectViewModel.getResult().observe(this, proyeks -> {
            shimmerKelola.setVisibility(View.INVISIBLE);
            shimmerKelola.stopShimmerAnimation();
            adapter = new ProyekInvestorAdaper(proyeks);
            recyclerView.setAdapter(adapter);
        });
    }
}
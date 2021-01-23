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
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.ternavest.R;
import com.example.ternavest.adapter.recycler.ProyekInvestorAdapter;
import com.example.ternavest.viewmodel.ProyekViewModel;
import com.example.ternavest.viewmodel.SearchViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProyekViewModel projectViewModel;
    private ProyekInvestorAdapter adapter;
    private ShimmerFrameLayout shimmerKelola;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchViewModel searchViewModel;
    private SearchView searchView;

    public HomeFragment() {}

    public static HomeFragment newInstance(String param1, String param2) {
        return new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    adapter = new ProyekInvestorAdapter(result);
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

        ImageButton ibSearchFilter = view.findViewById(R.id.ib_search_filter);
        ibSearchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                //bundle.putParcelable(EXTRA_FILTER, filter);
                SearchFilterFragment bottomSheet = new SearchFilterFragment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
    }

    private void getData() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        projectViewModel.getResult().observe(this, proyeks -> {
            shimmerKelola.setVisibility(View.INVISIBLE);
            shimmerKelola.stopShimmerAnimation();
            adapter = new ProyekInvestorAdapter(proyeks);
            recyclerView.setAdapter(adapter);
        });
    }
}
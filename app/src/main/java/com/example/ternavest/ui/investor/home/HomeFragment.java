package com.example.ternavest.ui.investor.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.ternavest.R;
import com.example.ternavest.adapter.recycler.ProyekInvestorAdapter;
import com.example.ternavest.model.Filter;
import com.example.ternavest.viewmodel.SearchViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import static com.example.ternavest.ui.investor.home.SearchFilterFragment.EXTRA_FILTER;
import static com.example.ternavest.utils.AppUtils.showToast;

public class HomeFragment extends Fragment implements SearchFilterFragment.SearchFilterListener {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private ProyekInvestorAdapter adapter;
    private Filter filter;
    private SearchViewModel searchViewModel;

    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerKelola;
    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_investor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        shimmerKelola = view.findViewById(R.id.shimmerKelola);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        SearchView searchView = view.findViewById(R.id.search_proyek);

        searchViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(SearchViewModel.class);
        searchViewModel.getData().observe(getViewLifecycleOwner(), result -> {
            shimmerKelola.setVisibility(View.INVISIBLE);
            shimmerKelola.stopShimmerAnimation();
            adapter = new ProyekInvestorAdapter(result);
            recyclerView.setAdapter(adapter);

            if (result.isEmpty()) showToast(getContext(), "Proyek yang kamu cari tidak ada.");
        });

        filter = new Filter();
        getData(filter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter.setKataKunci(query);
                getData(filter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) onQueryTextSubmit("");
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData(filter);
            swipeRefreshLayout.setRefreshing(false);
        });

        ImageButton ibSearchFilter = view.findViewById(R.id.ib_search_filter);
        ibSearchFilter.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_FILTER, filter);
            SearchFilterFragment bottomSheet = new SearchFilterFragment();
            bottomSheet.setArguments(bundle);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
        });
    }

    private void getData(Filter filter) {
        shimmerKelola.startShimmerAnimation();
        searchViewModel.loadData(filter);
    }

    @Override
    public void receiveData(Filter result) {
        filter = result;
        getData(filter);
        Log.d(TAG, filter.getNamaProvinsi());
    }
}
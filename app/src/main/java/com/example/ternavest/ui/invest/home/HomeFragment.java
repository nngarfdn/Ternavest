package com.example.ternavest.ui.invest.home;

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

import com.example.ternavest.R;
import com.example.ternavest.adaper.ProyekAdaper;
import com.example.ternavest.adaper.ProyekInvestorAdaper;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.viewmodel.ProyekViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProyekViewModel projectViewModel;
    private ProyekInvestorAdaper adapter;
    ShimmerFrameLayout shimmerKelola;

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
        shimmerKelola.startShimmerAnimation();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        projectViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProyekViewModel.class);
        projectViewModel.loadResult();
        projectViewModel.getResult().observe(this, new Observer<List<Proyek>>() {
            @Override
            public void onChanged(List<Proyek> proyeks) {
                shimmerKelola.setVisibility(View.INVISIBLE);
                shimmerKelola.stopShimmerAnimation();
                adapter = new ProyekInvestorAdaper(proyeks);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
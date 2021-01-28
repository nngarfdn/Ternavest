package com.example.ternavest.ui.peternak.kelola.proyek;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ternavest.R;
import com.example.ternavest.adapter.recycler.ProyekAdapter;
import com.example.ternavest.viewmodel.ProyekViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class KelolaFragment extends Fragment {


    ImageView imgProyekKosong;
    TextView txtProyekKosong;
    Button btnTambahProyek;
    ProyekViewModel proyekViewModel;
    FirebaseUser firebaseUser;
    RecyclerView rvKelolaProyek;
    FloatingActionButton floatingActionButton;
    ShimmerFrameLayout shimmerKelola;

    public KelolaFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proyekViewModel = ViewModelProviders.of(this).get(ProyekViewModel.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        proyekViewModel.loadResultByUUID(firebaseUser.getUid());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kelola, container, false);
        proyekViewModel.loadResultByUUID(firebaseUser.getUid());

        btnTambahProyek = view.findViewById(R.id.btnTambahProyek);
        txtProyekKosong = view.findViewById(R.id.txtProyekKosong);
        imgProyekKosong = view.findViewById(R.id.imgProyekKosong);
        rvKelolaProyek = view.findViewById(R.id.rv_kelola_proyek);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        shimmerKelola = view.findViewById(R.id.shimmerKelola);

        shimmerKelola.startShimmerAnimation();
        txtProyekKosong.setVisibility(View.INVISIBLE);
        imgProyekKosong.setVisibility(View.INVISIBLE);
        btnTambahProyek.setVisibility(View.INVISIBLE);

        floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getContext(), TambahProyekActivity.class)));

        btnTambahProyek.setOnClickListener(v -> startActivity(new Intent(getContext(), TambahProyekActivity.class)));

        proyekViewModel.getResultByUUID().observe(getViewLifecycleOwner(), result -> {

            if (result.isEmpty()) {
                txtProyekKosong.setVisibility(View.VISIBLE);
                imgProyekKosong.setVisibility(View.VISIBLE);
                btnTambahProyek.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.INVISIBLE);
                shimmerKelola.setVisibility(View.INVISIBLE);
                shimmerKelola.stopShimmerAnimation();
            } else {
                txtProyekKosong.setVisibility(View.INVISIBLE);
                imgProyekKosong.setVisibility(View.INVISIBLE);
                btnTambahProyek.setVisibility(View.INVISIBLE);
                shimmerKelola.setVisibility(View.INVISIBLE);
                shimmerKelola.stopShimmerAnimation();
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                rvKelolaProyek.setLayoutManager(layoutManager);
                ProyekAdapter adapter = new ProyekAdapter(result);
                rvKelolaProyek.setAdapter(adapter);
            }

        });
        return view;
    }

    @Override
    public void onResume() {
        proyekViewModel.loadResultByUUID(firebaseUser.getUid());
        super.onResume();
    }

    @Override
    public void onStart() {
        proyekViewModel.loadResultByUUID(firebaseUser.getUid());
        super.onStart();
    }
}
package com.example.ternavest.ui.peternak.kelola;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.ternavest.R;
import com.example.ternavest.adaper.ProyekAdaper;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.viewmodel.ProyekViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class KelolaFragment extends Fragment {

    private static final String TAG = "KelolaFragment";
    ImageView imgProyekKosong;
    TextView txtProyekKosong;
    Button btnTambahProyek;
    ProyekViewModel proyekViewModel;
    FirebaseUser firebaseUser;
    RecyclerView  rvKelolaProyek;
    FloatingActionButton floatingActionButton;

    public KelolaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proyekViewModel   = ViewModelProviders.of(this).get(ProyekViewModel.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kelola, container, false);

        btnTambahProyek = view.findViewById(R.id.btnTambahProyek);
        txtProyekKosong= view.findViewById(R.id.txtProyekKosong);
        imgProyekKosong = view.findViewById(R.id.imgProyekKosong);
        rvKelolaProyek = view.findViewById(R.id.rv_kelola_proyek);
        floatingActionButton= view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(v->{
            startActivity(new Intent(getContext(), TambahProyekActivity.class));
        });

        btnTambahProyek.setOnClickListener(v-> {
            startActivity(new Intent(getContext(), TambahProyekActivity.class));
        });

        proyekViewModel.getResultByUUID().observe(getViewLifecycleOwner(), result ->{
            txtProyekKosong.setVisibility(View.INVISIBLE);
            imgProyekKosong.setVisibility(View.INVISIBLE);
            btnTambahProyek.setVisibility(View.INVISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rvKelolaProyek.setLayoutManager(layoutManager);
            ProyekAdaper adapter = new ProyekAdaper(result);
            rvKelolaProyek.setAdapter(adapter);
        });
        return view;
    }

    @Override
    public void onResume() {
        proyekViewModel.loadResultByUUID(firebaseUser.getUid());
        super.onResume();
    }
}
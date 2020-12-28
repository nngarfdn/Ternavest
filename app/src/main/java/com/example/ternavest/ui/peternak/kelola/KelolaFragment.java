package com.example.ternavest.ui.peternak.kelola;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.ternavest.R;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.viewmodel.ProyekViewModel;

public class KelolaFragment extends Fragment {

    ImageView imgProyekKosong;
    TextView txtProyekKosong;
    Button btnTambahProyek;

    public KelolaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kelola, container, false);

        btnTambahProyek = view.findViewById(R.id.btnTambahProyek);
        txtProyekKosong= view.findViewById(R.id.txtProyekKosong);
        imgProyekKosong = view.findViewById(R.id.imgProyekKosong);

        btnTambahProyek.setOnClickListener(v-> {
            startActivity(new Intent(getContext(), TambahProyekActivity.class));
        });

        return view;
    }

}
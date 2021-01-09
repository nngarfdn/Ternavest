package com.example.ternavest.testing.portfolio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.ternavest.R;
import com.example.ternavest.adaper.ProyekAdaper;
import com.example.ternavest.model.Proyek;
import com.example.ternavest.viewmodel.ProyekViewModel;

import java.util.List;

public class DashboardInvestorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProyekViewModel projectViewModel;
    private ProyekAdaper adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_investor);

        recyclerView = findViewById(R.id.rv_investor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ProyekViewModel.class);
        projectViewModel.loadResult();
        projectViewModel.getResult().observe(this, new Observer<List<Proyek>>() {
            @Override
            public void onChanged(List<Proyek> proyeks) {
                adapter = new ProyekAdaper(proyeks);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
package com.example.ternavest.testing;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ternavest.R;
import com.example.ternavest.model.Location;
import com.example.ternavest.response.Attributes;
import com.example.ternavest.response.Districts;
import com.example.ternavest.response.Provinces;
import com.example.ternavest.response.Regencies;
import com.example.ternavest.viewmodel.LocationViewModel;
import java.util.ArrayList;
import java.util.List;

public class WilayahTest extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CheckBox.OnCheckedChangeListener {
    private final String TAG = getClass().getSimpleName();
    private LocationViewModel lvm;

    private ArrayList<Location> listProvinces, listRegencies, listDistricts;
    private int idProvince, idRegency, idDistrict = 0;
    private Spinner spinProvinces, spinRegencies, spinDistricts;
    private CheckBox cbProvinces, cbRegencies, cbDistricts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wilayah);

        cbProvinces = findViewById(R.id.cb_provinces);
        cbRegencies = findViewById(R.id.cb_regencies);
        cbDistricts = findViewById(R.id.cb_districts);
        spinProvinces = findViewById(R.id.spin_provinces);
        spinRegencies = findViewById(R.id.spin_regencies);
        spinDistricts = findViewById(R.id.spin_districts);

        cbProvinces.setEnabled(true);
        cbRegencies.setEnabled(false);
        cbDistricts.setEnabled(false);
        spinProvinces.setEnabled(false);
        spinRegencies.setEnabled(false);
        spinDistricts.setEnabled(false);

        cbProvinces.setOnCheckedChangeListener(this);
        cbRegencies.setOnCheckedChangeListener(this);
        cbDistricts.setOnCheckedChangeListener(this);
        spinProvinces.setOnItemSelectedListener(this);
        spinRegencies.setOnItemSelectedListener(this);
        spinDistricts.setOnItemSelectedListener(this);

        lvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(LocationViewModel.class);
        loadProvinces();
    }

    private void loadProvinces(){
        lvm.loadProvinces();
        lvm.getProvinces().observe(this, new Observer<Provinces>() {
            @Override
            public void onChanged(Provinces provinces) {
                if (provinces != null){
                    listProvinces = new ArrayList<>();
                    List<String> itemList = new ArrayList<>();
                    for (Attributes attributes : provinces.getProvinces()){ // Fix nama provinsi
                        if (attributes.getId() == 31) listProvinces.add(new Location(attributes.getId(), "DKI Jakarta"));
                        else if (attributes.getId() == 34) listProvinces.add(new Location(attributes.getId(), "DI Yogyakarta"));
                        else listProvinces.add(new Location(attributes.getId(), attributes.getName()));
                    }
                    for (Location location : listProvinces) itemList.add(location.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, itemList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinProvinces.setAdapter(adapter);
                }
            }
        });
    }

    private void loadRegencies(int idProvince){
        lvm.loadRegencies(idProvince);
        lvm.getRegencies().observe(this, new Observer<Regencies>() {
            @Override
            public void onChanged(Regencies regencies) {
                if (regencies != null){
                    listRegencies = new ArrayList<>();
                    List<String> itemList = new ArrayList<>();
                    for (Attributes attributes : regencies.getRegencies()) listRegencies.add(new Location(attributes.getId(), attributes.getName()));
                    for (Location location : listRegencies) itemList.add(location.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, itemList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinRegencies.setAdapter(adapter);
                }
            }
        });
    }

    private void loadDistricts(int idRegency){
        lvm.loadDistricts(idRegency);
        lvm.getDistricts().observe(this, new Observer<Districts>() {
            @Override
            public void onChanged(Districts districts) {
                if (districts != null){
                    listDistricts = new ArrayList<>();
                    List<String> itemList = new ArrayList<>();
                    for (Attributes attributes : districts.getDistricts()) listDistricts.add(new Location(attributes.getId(), attributes.getName()));
                    for (Location location : listDistricts) itemList.add(location.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, itemList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinDistricts.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.spin_provinces:
                idProvince = listProvinces.get(i).getId();
                loadRegencies(idProvince);
                break;

            case R.id.spin_regencies:
                idRegency = listRegencies.get(i).getId();
                loadDistricts(idRegency);
                break;

            case R.id.spin_districts:
                idDistrict = listDistricts.get(i).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.cb_provinces:
                spinProvinces.setEnabled(b);
                cbRegencies.setEnabled(b);
                if (!b){
                    cbRegencies.setChecked(false);
                    cbDistricts.setChecked(false);
                }
                break;

            case R.id.cb_regencies:
                spinRegencies.setEnabled(b);
                cbDistricts.setEnabled(b);
                if (!b) cbDistricts.setChecked(false);
                break;

            case R.id.cb_districts:
                spinDistricts.setEnabled(b);
                break;
        }
    }
}
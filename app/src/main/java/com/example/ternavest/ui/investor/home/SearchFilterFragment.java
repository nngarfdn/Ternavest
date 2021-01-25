package com.example.ternavest.ui.investor.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.ternavest.R;
import com.example.ternavest.model.Filter;
import com.example.ternavest.model.Location;
import com.example.ternavest.response.Attributes;
import com.example.ternavest.viewmodel.LocationViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.ternavest.utils.EditTextUtils.isNull;

public class SearchFilterFragment extends BottomSheetDialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, CheckBox.OnCheckedChangeListener {
    public final static String EXTRA_FILTER = "extra_filter";

    private Filter filter;
    private SearchFilterListener listener;
    private LocationViewModel locationViewModel;

    private Button btnFilter;
    private CheckBox cbProvince, cbRegency, cbDistrict;
    private Spinner spProvince, spRegency, spDistrict;

    private ArrayList<Location> listProvinces, listRegencies, listDistricts;

    public SearchFilterFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cbProvince = view.findViewById(R.id.cb_provinces_sf);
        cbRegency = view.findViewById(R.id.cb_regencies_sf);
        cbDistrict = view.findViewById(R.id.cb_districts_sf);
        spProvince = view.findViewById(R.id.sp_provinces_sf);
        spRegency = view.findViewById(R.id.sp_regencies_sf);
        spDistrict = view.findViewById(R.id.sp_districts_sf);

        cbProvince.setEnabled(true);
        cbRegency.setEnabled(false);
        cbDistrict.setEnabled(false);
        spProvince.setEnabled(false);
        spRegency.setEnabled(false);
        spDistrict.setEnabled(false);

        cbProvince.setOnCheckedChangeListener(this);
        cbRegency.setOnCheckedChangeListener(this);
        cbDistrict.setOnCheckedChangeListener(this);
        spProvince.setOnItemSelectedListener(this);
        spRegency.setOnItemSelectedListener(this);
        spDistrict.setOnItemSelectedListener(this);

        TextView tvReset = view.findViewById(R.id.tv_reset_sf);
        btnFilter = view.findViewById(R.id.btn_filter_sf);
        tvReset.setOnClickListener(this);
        btnFilter.setOnClickListener(this);

        btnFilter.setEnabled(false);

        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()){
            filter = bundle.getParcelable(EXTRA_FILTER);
            setView(filter);

            locationViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(LocationViewModel.class);
            locationViewModel.loadProvinces();
        }

        setLocationViewModelGetData();
    }

    private void setView(Filter filter){
        cbProvince.setChecked(filter.isProvinsi());
        cbRegency.setChecked(filter.isKabupaten());
        cbDistrict.setChecked(filter.isKecamatan());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_reset_sf:
                filter = new Filter();
                setView(filter);
                break;

            case R.id.btn_filter_sf:
                filter.setNamaProvinsi(spProvince.getSelectedItem().toString());
                filter.setNamaKabupaten(spRegency.getSelectedItem().toString());
                filter.setNamaKecamatan(spDistrict.getSelectedItem().toString());
                filter.setProvinsi(cbProvince.isChecked());
                filter.setKabupaten(cbRegency.isChecked());
                filter.setKecamatan(cbDistrict.isChecked());

                listener.receiveData(filter);
                dismiss();
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.sp_provinces_sf:
                btnFilter.setEnabled(false);
                int idProvince = listProvinces.get(i).getId();
                locationViewModel.loadRegencies(idProvince);
                break;

            case R.id.sp_regencies_sf:
                int idRegency = listRegencies.get(i).getId();
                locationViewModel.loadDistricts(idRegency);
                break;

            case R.id.sp_districts_sf:
                //int idDistrict = listDistricts.get(i).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.cb_provinces_sf:
                spProvince.setEnabled(isChecked);
                cbRegency.setEnabled(isChecked);
                if (!isChecked){
                    cbRegency.setChecked(false);
                    cbDistrict.setChecked(false);
                }
                break;

            case R.id.cb_regencies_sf:
                spRegency.setEnabled(isChecked);
                cbDistrict.setEnabled(isChecked);
                if (!isChecked) cbDistrict.setChecked(false);
                break;

            case R.id.cb_districts_sf:
                spDistrict.setEnabled(isChecked);
                break;
        }
    }

    private void setLocationViewModelGetData() {
        locationViewModel.getProvinces().observe(this, provinces -> {
            if (provinces != null){
                listProvinces = new ArrayList<>();
                List<String> itemList = new ArrayList<>();
                for (Attributes attributes : provinces.getProvinces()){ // Fix nama provinsi
                    if (attributes.getId() == 31) listProvinces.add(new Location(attributes.getId(), "DKI Jakarta"));
                    else if (attributes.getId() == 34) listProvinces.add(new Location(attributes.getId(), "DI Yogyakarta"));
                    else listProvinces.add(new Location(attributes.getId(), attributes.getName()));
                }
                for (Location location : listProvinces) itemList.add(location.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, itemList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spProvince.setAdapter(adapter);
                if (!isNull(filter.getNamaProvinsi())) spProvince.setSelection(adapter.getPosition(filter.getNamaProvinsi()));
            }
        });

        locationViewModel.getRegencies().observe(this, regencies -> {
            if (regencies != null){
                listRegencies = new ArrayList<>();
                List<String> itemList = new ArrayList<>();
                for (Attributes attributes : regencies.getRegencies()) listRegencies.add(new Location(attributes.getId(), attributes.getName()));
                for (Location location : listRegencies) itemList.add(location.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, itemList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spRegency.setAdapter(adapter);
                if (!isNull(filter.getNamaKabupaten())) spRegency.setSelection(adapter.getPosition(filter.getNamaKabupaten()));
            }
        });

        locationViewModel.getDistricts().observe(this, districts -> {
            if (districts != null){
                listDistricts = new ArrayList<>();
                List<String> itemList = new ArrayList<>();
                for (Attributes attributes : districts.getDistricts()) listDistricts.add(new Location(attributes.getId(), attributes.getName()));
                for (Location location : listDistricts) itemList.add(location.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, itemList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spDistrict.setAdapter(adapter);
                if (!isNull(filter.getNamaKecamatan())) spDistrict.setSelection(adapter.getPosition(filter.getNamaKecamatan()));
                btnFilter.setEnabled(true);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SearchFilterFragment.SearchFilterListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(HomeFragment.class.getSimpleName() +
                    " must implement " + SearchFilterFragment.SearchFilterListener.class.getSimpleName());
        }
    }

    public interface SearchFilterListener{
        void receiveData(Filter filter);
    }
}
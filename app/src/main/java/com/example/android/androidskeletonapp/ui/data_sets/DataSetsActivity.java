package com.example.android.androidskeletonapp.ui.data_sets;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.github.florent37.expansionpanel.ExpansionLayout;

import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import java.util.ArrayList;
import java.util.List;

public class DataSetsActivity extends ListActivity {

    ExpansionLayout expansionLayout;
    private Spinner spinner1, spinner2;
    OrganisationUnit filterOrgUnit = null;

    DataSetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_sets, R.id.dataSetsToolbar, R.id.dataSetsRecyclerView);
        adapter = new DataSetsAdapter();
        recyclerView.setAdapter(adapter);
        expansionLayout = (ExpansionLayout) findViewById(R.id.expansionLayout);
        toolBarListiner();
        setUpSpinerProv();

        observeDataSets();
    }

    private void observeDataSets() {
        LiveData<PagedList<OrganisationUnit>> liveData = Sdk.d2().organisationUnitModule().organisationUnits
                .byLevel().eq(4)
                .getPaged(20);

        liveData.observe(this, organisationUnits -> {
            adapter.submitList(organisationUnits);
            findViewById(R.id.dataSetsNotificator).setVisibility(
                    organisationUnits.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void observeDataSetsFilter() {
        LiveData<PagedList<OrganisationUnit>> liveData = Sdk.d2().organisationUnitModule().organisationUnits
                .byParentUid().eq(filterOrgUnit.uid())
                .getPaged(20);

        liveData.observe(this, organisationUnits -> {
            adapter.submitList(organisationUnits);
            findViewById(R.id.dataSetsNotificator).setVisibility(
                    organisationUnits.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    //toolbar click
    public void toolBarListiner() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String title = (String) menuItem.getTitle();
//                Toast.makeText(getApplicationContext(),title,Toast.LENGTH_SHORT).show();

                switch (menuItem.getItemId()) {
                    case R.id.filter:
                        expansionLayout.toggle(true);
                        Toast.makeText(getApplicationContext(),"Clicado",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    public void setUpSpinerProv(){
        spinner1 = (Spinner) findViewById(R.id.orgUnitFilterProv);
        List<OrganisationUnit> organisationUnits = Sdk.d2().organisationUnitModule().organisationUnits
                .byLevel().eq(2)
                .get();

        List<String> list = new ArrayList<String>();
        for(int i=0; i<organisationUnits.size();i++){
            list.add(organisationUnits.get(i).displayName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),"Index "+position,Toast.LENGTH_LONG).show();
                OrganisationUnit organisationUnit = organisationUnits.get(position);
                setUpSpinerDistrict(organisationUnit.uid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setUpSpinerDistrict(String orgUnitUid){
        spinner2 = (Spinner) findViewById(R.id.orgUnitFilterDis);
        List<OrganisationUnit> organisationUnits = Sdk.d2().organisationUnitModule().organisationUnits
                .byParentUid().eq(orgUnitUid)
                .get();

        List<String> list = new ArrayList<String>();
        for(int i=0; i<organisationUnits.size();i++){
            list.add(organisationUnits.get(i).displayName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),"Index "+position,Toast.LENGTH_LONG).show();
                filterOrgUnit = organisationUnits.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setVisibility(View.VISIBLE);
    }

    public void applyFilter(View view){
        Toast.makeText(getApplicationContext(),"Applying Filters",Toast.LENGTH_LONG).show();
        observeDataSetsFilter();
    }

    public void clearFilter(View view){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_list_menu, menu);
        return true;
    }
}

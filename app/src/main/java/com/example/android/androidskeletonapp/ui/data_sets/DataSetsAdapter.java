package com.example.android.androidskeletonapp.ui.data_sets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.androidskeletonapp.FakeMap;
import com.example.android.androidskeletonapp.Map;
import com.example.android.androidskeletonapp.MapsActivity;
import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.StyleBinderHelper;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithStyleHolder;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;

import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedListAdapter;

public class DataSetsAdapter extends PagedListAdapter<OrganisationUnit, ListItemWithStyleHolder> {

    DataSetsAdapter() {
        super(new DiffByIdItemCallback<>());
    }

    @NonNull
    @Override
    public ListItemWithStyleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_with_style, parent, false);
        itemView.setOnClickListener(v -> {
//            Toast.makeText(parent.getContext(), "Hi", Toast.LENGTH_LONG).show();
            ActivityStarter.startActivity((AppCompatActivity) parent.getContext(), FakeMap.class,false);
        });
        return new ListItemWithStyleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithStyleHolder holder, int position) {
        OrganisationUnit organisationUnit = getItem(position);
        holder.title.setText(organisationUnit.displayName());
        holder.subtitle1.setText(organisationUnit.code());
    }
}

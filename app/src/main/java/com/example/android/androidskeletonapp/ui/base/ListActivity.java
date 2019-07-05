package com.example.android.androidskeletonapp.ui.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;

public abstract class ListActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected Toolbar toolbar;

    protected void setUp(int contentViewId, int toolbarId, int recyclerViewId) {
        setContentView(contentViewId);
        toolbar = findViewById(toolbarId);

        toolbar.inflateMenu(R.menu.toolbar_list_menu);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

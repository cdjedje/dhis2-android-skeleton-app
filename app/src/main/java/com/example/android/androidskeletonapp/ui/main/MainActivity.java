package com.example.android.androidskeletonapp.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.SyncStatusHelper;
import com.example.android.androidskeletonapp.ui.d2_errors.D2ErrorActivity;
import com.example.android.androidskeletonapp.ui.data_sets.DataSetsActivity;
import com.example.android.androidskeletonapp.ui.data_sets.reports.DataSetReportsActivity;
import com.example.android.androidskeletonapp.ui.foreign_key_violations.ForeignKeyViolationsActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.search.TrackedEntityInstanceSearchActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;

import java.text.MessageFormat;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.androidskeletonapp.data.service.LogOutService.logOut;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CompositeDisposable compositeDisposable;

    private FloatingActionButton syncMetadataButton;
    private FloatingActionButton syncDataButton;
    private FloatingActionButton uploadDataButton;

    private TextView syncStatusText;
    private ProgressBar progressBar;

    private boolean isSyncing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        compositeDisposable = new CompositeDisposable();

        User user = getUser();
        TextView greeting = findViewById(R.id.greeting);
        greeting.setText(String.format("Hi %s!", user.displayName()));

        createNavigationView(user);
    }

    public void orgUnitList(View view) {
//        int orgUnitNumber = Sdk.d2().organisationUnitModule().organisationUnits
//                .byLevel().eq(4)
//                .count();
//        Toast.makeText(getApplicationContext(), "# Org Unit " + orgUnitNumber, Toast.LENGTH_LONG).show();
        ActivityStarter.startActivity(this, DataSetsActivity.class, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private User getUser() {
        return Sdk.d2().userModule().user.getWithoutChildren();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }


    private void setSyncing() {
        isSyncing = true;
        progressBar.setVisibility(View.VISIBLE);
        syncStatusText.setVisibility(View.VISIBLE);

    }

    private void setSyncingFinished() {
//        isSyncing = false;
//        progressBar.setVisibility(View.GONE);
//        syncStatusText.setVisibility(View.GONE);
    }


    private void createNavigationView(User user) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView firstName = headerView.findViewById(R.id.firstName);
        TextView email = headerView.findViewById(R.id.email);
        firstName.setText(user.firstName());
        email.setText(user.email());
    }

    private void syncMetadata() {
        compositeDisposable.add(Sdk.d2().syncMetaData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(Throwable::printStackTrace)
                .doOnComplete(() -> {
                    setSyncingFinished();
                    Toast.makeText(getApplicationContext(), "Data Synced", Toast.LENGTH_LONG).show();
                })
                .subscribe());
    }

    private void downloadData() {
        compositeDisposable.add(
                Observable.merge(
                        Sdk.d2().trackedEntityModule().downloadTrackedEntityInstances(10, false, false),
                        Sdk.d2().aggregatedModule().data().download()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            setSyncingFinished();
                            ActivityStarter.startActivity(this, TrackedEntityInstancesActivity.class, false);
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private void uploadData() {
        compositeDisposable.add(
                Single.merge(
                        Single.fromCallable(Sdk.d2().trackedEntityModule().trackedEntityInstances.upload()),
                        Single.fromCallable(Sdk.d2().dataValueModule().dataValues.upload()),
                        Single.fromCallable(Sdk.d2().eventModule().events.upload())
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(this::setSyncingFinished)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private void wipeData() {
        compositeDisposable.add(
                Observable.fromCallable(() -> Sdk.d2().wipeModule().wipeData())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(Throwable::printStackTrace)
                        .doOnComplete(this::setSyncingFinished)
                        .subscribe());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navExit) {
            compositeDisposable.add(logOut(this));
        } else if (id == R.id.navSync) {
            Toast.makeText(getApplicationContext(), "Sync", Toast.LENGTH_LONG).show();
            syncMetadata();
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

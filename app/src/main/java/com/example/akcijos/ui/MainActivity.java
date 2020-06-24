package com.example.akcijos.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.akcijos.R;
import com.example.akcijos.viewmodels.MainActivityViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inflate the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create an instance of the tab layout from the view.
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        // Set the text for each tab.
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all_offers_tab_label));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.user_cart_tab_label));
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Use PagerAdapter to manage page views in fragments.
        // Each page is represented by its own fragment.
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        // Setting a listener for tab changes.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        updateLastRefreshTextView();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainActivityViewModel.class);
        viewModel.getAllOffers().observe(this, offers -> updateLastRefreshTextView());

    }

    // Update the last refreshed text view to show when was the last offer refresh
    private void updateLastRefreshTextView() {
        TextView lastRefreshedTextView = findViewById(R.id.last_refreshed);

        // Get stored time of last refresh
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        Long lastRefreshedDate = sharedPref.getLong(getString(R.string.last_refresh_key_id), 0);

        // Update the TextView based on the returned value
        if (lastRefreshedDate != 0) {
            Long currentTime = Calendar.getInstance().getTimeInMillis();
            long timeDifference = currentTime - lastRefreshedDate;
            int days = (int) (timeDifference / (1000 * 60 * 60 * 24));

            if (days == 0) {
                lastRefreshedTextView.setText(getString(R.string.last_refresh_today));
            } else if (days == 1) {
                lastRefreshedTextView.setText(getString(R.string.last_refreshed_yesterday));
            } else {
                lastRefreshedTextView.setText(getString(R.string.last_refreshed_multi_days, days));
            }

        } else {
            lastRefreshedTextView.setText(getString(R.string.last_refresh_never));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.refresh_offers) {// User chose the "Refresh Offers" item, running the scraping

            // Save the offers refresh date
            long date = Calendar.getInstance().getTimeInMillis();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(getString(R.string.last_refresh_key_id), date);
            editor.apply();

            refreshDatabase();
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    private void refreshDatabase() {
        viewModel.refreshDatabase();
        findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

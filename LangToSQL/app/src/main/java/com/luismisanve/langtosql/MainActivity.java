package com.luismisanve.langtosql;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.*;
import androidx.core.graphics.Insets;
import androidx.core.view.*;
import androidx.navigation.*;
import androidx.navigation.ui.*;
import com.luismisanve.langtosql.databinding.ActivityMainBinding;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    // Variables
    private ActivityMainBinding binding;

    // Initializer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_run, R.id.navigation_maps, R.id.navigation_config)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Load app theme
        FileManager fileManager = new FileManager(this);
        File theme = new File(getFilesDir(), "themesettings.cfg");

        if (theme.exists()){
            String[] themeConfig = fileManager.readFromFile("themesettings.cfg").split(";");
            if (themeConfig.length > 0 && Boolean.parseBoolean(themeConfig[0]))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            else if (themeConfig.length > 1 && Boolean.parseBoolean(themeConfig[1]))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            else if (themeConfig.length > 2 && Boolean.parseBoolean(themeConfig[2]))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }



        // Set Insets
        View root = findViewById(android.R.id.content);

        // Events
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            // System bar
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    v.getPaddingLeft(),
                    bars.top,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );

            return insets;
        });
    }
}
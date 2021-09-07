package com.example.anewhue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SettingsActivity extends AppCompatActivity {
    private Button mButton;
    private Button mPermissionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mButton = findViewById(R.id.customfilterbutton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openCustomFilter();
            }
        });

        //Granting permission button
        mPermissionButton = findViewById(R.id.permissionbutton);
        mPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Permission Needed")
                            .setMessage("This permission is needed so that the ANewHue filter can be displayed on your device.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create().show();
                }
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.settingsbtn);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.testbtn:
                        startActivity(new Intent(getApplicationContext(), TestActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.filterbtn:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settingsbtn:
                        return true;
                }
                return false;
            }
        });
    }
    public void openCustomFilter() {
        Intent intent = new Intent(this, CustomFilterActivity.class);
        startActivity(intent);
    }
}
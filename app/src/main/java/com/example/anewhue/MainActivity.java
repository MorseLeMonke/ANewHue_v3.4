package com.example.anewhue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SharedMemory mSharedMemory;
    private ToggleButton mToggleButton;
    private CountDownTimer mCountDownTimer;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //START-UP PERMISSION MESSAGE
        if(!Settings.canDrawOverlays(MainActivity.this)){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission Needed")
                    .setMessage("For ANewHue to work as intended, you need to grant the 'Draw over other apps permission'. " +
                            "This is so we can display the colour filter on your device.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Permission Needed")
                                    .setMessage("After clicking 'Go to Permissions', just find and press 'ANewHue', and" +
                                                    " then set the slider to 'Allowed'. You can change this later in ANewHue's settings menu.")
                                    .setPositiveButton("Go to Permissions", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                                        }
                                    }).create().show();
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
        //START-UP PERMISSION MESSAGE END

        //IDENTIFYING ITEMS
        mToggleButton = findViewById(R.id.startButton);
        mSharedMemory = new SharedMemory(this);
        SeekBar alpha = findViewById(R.id.seek_alpha);
        SeekBar red = findViewById(R.id.seek_red);
        SeekBar green = findViewById(R.id.seek_green);
        SeekBar blue = findViewById(R.id.seek_blue);

        //SPINNER
        mSpinner = findViewById(R.id.filterspinner);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.filters, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
        //SPINNER END

        //ANEWHUE FILTER (SEEKBAR)
        SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSharedMemory.setAlpha(alpha.getProgress());
                mSharedMemory.setRed(red.getProgress());
                mSharedMemory.setGreen(green.getProgress());
                mSharedMemory.setBlue(blue.getProgress());

                if (FilterService.STATE == FilterService.STATE_ACTIVE) {
                    Intent intent =new Intent(MainActivity.this, FilterService.class);
                    startService(intent);
                }
                mToggleButton.setChecked(FilterService.STATE == FilterService.STATE_ACTIVE);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        alpha.setOnSeekBarChangeListener(changeListener);
        red.setOnSeekBarChangeListener(changeListener);
        green.setOnSeekBarChangeListener(changeListener);
        blue.setOnSeekBarChangeListener(changeListener);

        //ENABLING FILTER
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Settings.canDrawOverlays(MainActivity.this)){
                    Intent i =new Intent(MainActivity.this, FilterService.class);
                    if (FilterService.STATE == FilterService.STATE_ACTIVE) {
                        stopService(i);
                    } else {
                        startService(i);
                    }
                    refresh();
                }
                else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Permission Needed")
                            .setMessage("For ANewHue to work as intended, you need to grant the 'Draw over other apps permission'. " +
                                    "This is so we can display the colour filter on your device.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Permission Needed")
                                            .setMessage("After clicking 'Go to Permissions', just find and press 'ANewHue', and" +
                                                    " then set the slider to 'Allowed'. You can change this later in ANewHue's settings menu.")
                                            .setPositiveButton("Go to Permissions", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                                                }
                                            }).create().show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }
            }
        });
        //ANEWHUE FILTER END



        //NAVIGATION BAR
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.filterbtn);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.testbtn:
                        startActivity(new Intent(getApplicationContext(),TestActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.filterbtn:
                        return true;
                    case R.id.settingsbtn:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
        //NAVIGATION BAR END
    }


    //SEEK BAR UPDATE
    private void refresh() {
        if(mCountDownTimer != null)
            mCountDownTimer.cancel();

        mCountDownTimer = new CountDownTimer(100, 100) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                mToggleButton.setChecked(FilterService.STATE == FilterService.STATE_ACTIVE);
            }
        };

        mCountDownTimer.start();
    }
    //SEEK BAR UPDATE END


    //SPINNER ITEM SELECT AND FILTER CHANGE
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        SeekBar alphabar = findViewById(R.id.seek_alpha);
        SeekBar redbar = findViewById(R.id.seek_red);
        SeekBar greenbar = findViewById(R.id.seek_green);
        SeekBar bluebar = findViewById(R.id.seek_blue);

        String choice = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(), choice+" Filter Selected", Toast.LENGTH_LONG).show();

        if(adapterView.getItemAtPosition(i).equals("Protonomaly (Red-Weak)")){
            alphabar.setProgress(105);
            redbar.setProgress(220);
            greenbar.setProgress(0);
            bluebar.setProgress(200);
        }

        if(adapterView.getItemAtPosition(i).equals("Deutronomaly (Green-Weak)")){
            alphabar.setProgress(80);
            redbar.setProgress(0);
            greenbar.setProgress(255);
            bluebar.setProgress(255);
        }

        if(adapterView.getItemAtPosition(i).equals("Tritanomaly (Blue-Weak)")){
            alphabar.setProgress(250);
            redbar.setProgress(0);
            greenbar.setProgress(0);
            bluebar.setProgress(255);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //SPINNER ITEM SELECT END
}
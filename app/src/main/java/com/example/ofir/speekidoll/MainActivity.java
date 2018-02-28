package com.example.ofir.speekidoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



    }
    public void menuButtonClicked(View v)
    {
        Toast.makeText(this, v.getTag().toString(), Toast.LENGTH_LONG).show();
        Intent myIntent = null;
        switch(v.getId()){
            case R.id.manage_programs_button: myIntent = new Intent(MainActivity.this, ManageProgramsActivity.class);
                break;
            case R.id.manage_stickers_button: myIntent = new Intent(MainActivity.this, ManageStickersActivity.class);
                break;
            case R.id.settings_button: myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.statistics_button: myIntent = new Intent(MainActivity.this, StatisticsActivity.class);
                break;
            case R.id.sync_doll_button: myIntent = new Intent(MainActivity.this, SyncDollActivity.class);
                break;
            default:
                Log.d("No match for id", "button is: " + v.getTag().toString());
        }
        if(myIntent != null){
            MainActivity.this.startActivity(myIntent);
        }
    }
}

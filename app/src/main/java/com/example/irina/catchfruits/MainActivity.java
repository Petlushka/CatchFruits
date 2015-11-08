package com.example.irina.catchfruits;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener {

    ImageButton btnPlay, btnRules, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        btnRules = (ImageButton)findViewById(R.id.btnRules);
        btnExit = (ImageButton)findViewById(R.id.btnExit);

        btnPlay.setOnClickListener(this);
        btnRules.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnPlay:
                intent = new Intent(this, GamePlay.class);
                startActivity(intent);
                break;
            case R.id.btnRules:
                intent = new Intent(this, Rules.class);
                startActivity(intent);
                break;
            case R.id.btnExit:
                finish();
                break;
        }
    }
}

package com.example.irina.catchfruits;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class GamePlay extends Activity {

    private CatchFruitsView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_play);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.relativeLayout);
        view = new CatchFruitsView(this, getPreferences(Context.MODE_PRIVATE), layout);
        layout.addView(view);
    }

    @Override
    public void onPause(){
        super.onPause();
        view.pause();
    }

    @Override
    public void onResume(){
        super.onResume();
        view.resume(this);
    }


}

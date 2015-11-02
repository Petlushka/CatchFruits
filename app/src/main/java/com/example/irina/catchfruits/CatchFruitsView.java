package com.example.irina.catchfruits;

/**
 * Created by Irina on 27.10.2015.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CatchFruitsView extends View {

    private static final String HIGH_SCORE = "HIGH_SCORE";
    private SharedPreferences preferences;
    private int fruitsTouched;
    private int score;
    private int level;
    private int viewWidth;
    private int viewHeight;
    private long animationTime;
    private boolean gameOver;
    private boolean gamePaused;
    private boolean dialogDisplayed;
    private int highScore;
    private final Queue<ImageView> fruits = new ConcurrentLinkedQueue<ImageView>();
    private final Queue<Animator> animators = new ConcurrentLinkedQueue<Animator>();
    private TextView highScoreTextView;
    private TextView currentScoreTextView;
    private TextView levelTextView;
    private LinearLayout livesLinearLayout;
    private RelativeLayout relativeLayout;
    private Resources resources;
    private LayoutInflater layoutInflater;
    private static final int INITIAL_ANIMATION_DURATION = 6000;
    private static final Random random = new Random();
    private static final int FRUIT_DIAMETER = 100;
    private static final float SCALE_X = 0.25f;
    private static final float SCALE_Y = 0.25f;
    private static final int INITIAL_FRUITS = 5;
    private static final int FRUITS_DELAY = 500;
    private static final int LIVES = 3;
    private static final int MAX_LIVES = 7;
    private static final int NEW_LEVEL = 10;
    private Handler fruitsHandler;
    private static final int HIT_SOUND_ID = 1;
    private static final int MISS_SOUND_ID = 2;
    private static final int DISAPPEAR_SOUND_ID = 3;
    private static final int SOUND_PRIORITY = 1;
    private static final int SOUND_QUALITY = 100;
    private static final int MAX_STREAMS = 4;
    private SoundPool soundPool;
    private int volume;
    private Map<Integer, Integer> soundMap;


    public CatchFruitsView(Context context, SharedPreferences sharedPreferences, RelativeLayout parentLayout) {
        super(context);
        preferences = sharedPreferences;
        highScore = sharedPreferences.getInt(HIGH_SCORE, 0);
        resources = context.getResources();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        relativeLayout = parentLayout;
        livesLinearLayout = (LinearLayout) relativeLayout.findViewById(R.id.lifeLinearLayout);
        highScoreTextView = (TextView) relativeLayout.findViewById(R.id.highScoreTextView);
        currentScoreTextView = (TextView) relativeLayout.findViewById(R.id.scoreTextView);
        levelTextView = (TextView) relativeLayout.findViewById(R.id.levelTextView);
        fruitsHandler = new Handler();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        viewWidth = width;
        viewHeight = height;
    }
    public void pause(){
        gamePaused = true;
        soundPool.release();
        soundPool = null;
        cancelAnimations();
    }

    private void cancelAnimations(){
        for(Animator animator: animators){
            animator.cancel();
        }
        for(ImageView view: fruits) {
            relativeLayout.removeView(view);
        }
            fruitsHandler.removeCallbacks(addFruitsRunnable);
            animators.clear();
            fruits.clear();
    }

    public void resume(Context context){
        gamePaused = false;
        initializeSoundEffects(context);
        if(!dialogDisplayed){
            resetGame();
        }
    }

    public void resetGame(){
        fruits.clear();
        animators.clear();
        livesLinearLayout.removeAllViews();
        animationTime = INITIAL_ANIMATION_DURATION;
        fruitsTouched = 0;
        score = 0;
        level = 1;
        gameOver = false;
        displayScores();
        for(int i = 0; i < LIVES; ++i){
            livesLinearLayout.addView((ImageView)layoutInflater.inflate(R.layout.life, null));
        }
        for(int i = 1; i <= INITIAL_FRUITS; ++i){
            fruitsHandler.postDelayed(addFruitsRunnable, i * FRUITS_DELAY);
        }
    }

    private void initializeSoundEffects(Context context){
        soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, SOUND_QUALITY);
        AudioManager manager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        volume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);

        soundMap = new HashMap<>();
        soundMap.put(HIT_SOUND_ID, soundPool.load(context, R.raw.hit, SOUND_PRIORITY));
        soundMap.put(HIT_SOUND_ID, soundPool.load(context, R.raw.miss, SOUND_PRIORITY));
        soundMap.put(HIT_SOUND_ID, soundPool.load(context, R.raw.disappear, SOUND_PRIORITY));
    }

    private void displayScores(){
        highScoreTextView.setText(resources.getString(R.string.high_score) + " " + highScore);
        currentScoreTextView.setText(resources.getString(R.string.score) + " " + score);
        levelTextView.setText(resources.getString(R.string.level) + " " + level);

    }

    private Runnable addFruitsRunnable = new Runnable() {
        @Override
        public void run() {
            addNewFruit();
        }
    };

    public void addNewFruit(){
        int x = random.nextInt(viewWidth - FRUIT_DIAMETER);
        int y = random.nextInt(viewHeight - FRUIT_DIAMETER);
        int x2 = random.nextInt(viewWidth - FRUIT_DIAMETER);
        int y2 = random.nextInt(viewHeight - FRUIT_DIAMETER);

        final ImageView fruit = (ImageView)layoutInflater.inflate(R.layout.untouched, null);
        fruits.add(fruit);
        fruit.setLayoutParams(new RelativeLayout.LayoutParams(FRUIT_DIAMETER, FRUIT_DIAMETER));
        int fruit_rand = random.nextInt(4);
        switch (fruit_rand){
            case 0:
                fruit.setImageResource(R.drawable.apple);
                break;
            case 1:
                fruit.setImageResource(R.drawable.lemon);
                break;
            case 2:
                fruit.setImageResource(R.drawable.pear);
                break;
            case 3:
                fruit.setImageResource(R.drawable.strawberry);
                break;
        }
        fruit.setX(x);
        fruit.setY(y);
        fruit.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        touchedFruit(fruit);
                    }
                }
        );
        relativeLayout.addView(fruit);
        fruit.animate().x(x2).y(y2).scaleX(SCALE_X).scaleY(SCALE_Y).setDuration(animationTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animators.add(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animators.remove();
                if(!gamePaused && fruits.contains(fruit)){
                    missedFruit(fruit);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(soundPool != null){
            soundPool.play(MISS_SOUND_ID, volume, volume, SOUND_PRIORITY, 0, 1f);
        }
        score -= 15 * level;
        score = Math.max(score, 0);
        displayScores();
        return true;
    }

    private void touchedFruit(ImageView fruit){
        relativeLayout.removeView(fruit);
        fruits.remove(fruit);
        ++fruitsTouched;
        score += 10 * level;
        if(soundPool != null){
            soundPool.play(HIT_SOUND_ID, volume,volume, SOUND_PRIORITY, 0, 1f);
        }
        if(fruitsTouched % 10 == 0){
            ++level;
            animationTime *= 0.95;
            if(livesLinearLayout.getChildCount() < MAX_LIVES){
                ImageView life = (ImageView)layoutInflater.inflate(R.layout.life, null);
                livesLinearLayout.addView(life);
            }
        }
        displayScores();
        if(!gameOver)
            addNewFruit();

    }

    public void missedFruit(ImageView fruit){
        fruits.remove(fruit);
        relativeLayout.removeView(fruit);
        if(gameOver)
            return;
        if(soundPool != null)
            soundPool.play(DISAPPEAR_SOUND_ID, volume, volume, SOUND_PRIORITY, 0, 1f);
        if(livesLinearLayout.getChildCount() == 0){
            gameOver = true;
            if(score > highScore){
                SharedPreferences.Editor editor =  preferences.edit();
                editor.putInt(HIGH_SCORE, score);
                editor.commit();
                highScore = score;
            }
            cancelAnimations();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle(R.string.game_over);
            dialogBuilder.setMessage(resources.getString(R.string.score) + " " + score);
            dialogBuilder.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    displayScores();
                    dialogDisplayed = false;
                    resetGame();
                }
            });
            dialogDisplayed = true;
            dialogBuilder.show();
        } else {
            livesLinearLayout.removeViewAt(livesLinearLayout.getChildCount() -1);
            addNewFruit();
        }
    }

}

package com.bgulai_gcu.earthquaketracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bgulai_gcu.earthquaketracker.R;

/**
 * Created by Bereketab Gulai | s1827985
 */
public class FullscreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 2500;

    ImageView imageView;
    TextView textViewTitle;
    Animation top;
    Animation bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        imageView = findViewById(R.id.imageView);
        textViewTitle = findViewById(R.id.textViewTitle);

        top = AnimationUtils.loadAnimation(this, R.anim.top);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);

        imageView.setAnimation(top);
        textViewTitle.setAnimation(bottom);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(FullscreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}
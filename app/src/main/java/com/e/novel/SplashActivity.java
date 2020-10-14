package com.e.novel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.e.novel.User.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    boolean login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            login = true;
            Handler hd = new Handler();
            hd.postDelayed(new splashhandler(), 1000);
        } else {
            login = false;
            Handler hd = new Handler();
            hd.postDelayed(new splashhandler(), 1000);
        }
    }

    private class splashhandler implements Runnable {
        public void run() {
            if (login) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(intent,activityOptions.toBundle());
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(intent,activityOptions.toBundle());
                finish();
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}

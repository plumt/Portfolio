package com.e.plan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.e.plan.alert.SendMailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {
    boolean login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
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
                Intent intent = new Intent(SplashActivity.this, PlanActivity.class);
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

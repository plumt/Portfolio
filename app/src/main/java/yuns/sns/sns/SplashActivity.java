package yuns.sns.sns;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import yuns.sns.R;

public class SplashActivity extends AppCompatActivity {
    boolean login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
                Intent intent = new Intent(SplashActivity.this, SnsActivity.class);
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
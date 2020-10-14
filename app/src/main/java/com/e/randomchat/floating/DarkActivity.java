package com.e.randomchat.floating;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.randomchat.R;
import com.e.randomchat.profile.MyProfileAlertActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DarkActivity extends AppCompatActivity {
    Animation fab_open, fab_close;
    FloatingActionButton floatingActionButton, floatingActionButton1, floatingActionButton2, floatingActionButton3;
    TextView textView1, textView2, textView3;
    Boolean openFlag = false, rotate = false;
    RelativeLayout dark_relative;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dark);
        dark_relative = (RelativeLayout) findViewById(R.id.dark_relative);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingbutton);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floatingbutton1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floatingbutton2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.floatingbutton3);
        textView1 = (TextView) findViewById(R.id.talk_text);
        textView2 = (TextView) findViewById(R.id.delete_room_text);
        textView3 = (TextView) findViewById(R.id.logout_text);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        floatingActionButton1.startAnimation(fab_close);
        floatingActionButton2.startAnimation(fab_close);
        floatingActionButton3.startAnimation(fab_close);
        textView1.startAnimation(fab_close);
        textView2.startAnimation(fab_close);
        textView3.startAnimation(fab_close);
        floatingActionButton1.setClickable(false);
        floatingActionButton2.setClickable(false);
        floatingActionButton3.setClickable(false);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dark_relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                anim();
                Rotation(45, 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }, 250);
                return false;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                if (rotate) {
                    Rotation(45, 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    }, 250);

                } else {
                    Rotation(0, 45);
                }
                rotate = !rotate;
            }
        });

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plan = new Intent(DarkActivity.this, BancedActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
                startActivity(plan, activityOptions.toBundle());
                finish();
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myprofile = new Intent(DarkActivity.this, MyProfileAlertActivity.class);
                startActivity(myprofile);
                finish();
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("pushToken").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
        floatingActionButton.performClick();
    }

    public void Rotation(int s, int e) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                s,
                e,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(250);
        rotateAnimation.setFillAfter(true);
        floatingActionButton.startAnimation(rotateAnimation);
    }

    public void anim() {
        if (openFlag) {
            floatingActionButton1.startAnimation(fab_close);
            floatingActionButton2.startAnimation(fab_close);
            floatingActionButton3.startAnimation(fab_close);
            textView1.startAnimation(fab_close);
            textView2.startAnimation(fab_close);
            textView3.startAnimation(fab_close);
            floatingActionButton1.setClickable(false);
            floatingActionButton2.setClickable(false);
            floatingActionButton3.setClickable(false);
            openFlag = false;
        } else {
            floatingActionButton1.startAnimation(fab_open);
            floatingActionButton2.startAnimation(fab_open);
            floatingActionButton3.startAnimation(fab_open);
            textView1.startAnimation(fab_open);
            textView2.startAnimation(fab_open);
            textView3.startAnimation(fab_open);
            floatingActionButton1.setClickable(true);
            floatingActionButton2.setClickable(true);
            floatingActionButton3.setClickable(true);
            openFlag = true;
        }
    }

    @Override
    public void onBackPressed() {
        anim();
        Rotation(45, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        }, 250);
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}

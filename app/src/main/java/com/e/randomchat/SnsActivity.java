package com.e.randomchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.e.randomchat.fragment.ChatFragment;
import com.e.randomchat.fragment.ProfileFragment;
import com.e.randomchat.fragment.RewardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class SnsActivity extends AppCompatActivity {
    private int finished = 0;
    private int history = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(this, LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            finish();
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.sns_frame, new ChatFragment()).commit();
        try {
            BottomNavigationView bottomNavigationView = findViewById(R.id.sns_bottomview);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    switch (menuItem.getItemId()) {
                        case R.id.action_chat:
                            if (history != 1) {
                                if (history == 2 || history == 3) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromleft2, R.anim.toright2, R.anim.fromright2, R.anim.toleft2);
                                }
                                fragmentTransaction.replace(R.id.sns_frame, new ChatFragment()).commit();
                                finished = 0;
                                history = 1;
                            }
                            return true;

                        case R.id.action_profile:
                            if (history != 2) {
                                if (history == 1) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromright2, R.anim.toleft2, R.anim.fromleft2, R.anim.toright2);
                                } else if (history == 3) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromleft2, R.anim.toright2, R.anim.fromright2, R.anim.toleft2);
                                }
                                fragmentTransaction.replace(R.id.sns_frame, new ProfileFragment()).commit();
                                finished = 0;
                                history = 2;
                            }
                            return true;

                        case R.id.action_reward:
                            if (history != 3) {
                                if (history == 1 || history == 2) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromright2, R.anim.toleft2, R.anim.fromleft2, R.anim.toright2);
                                }
                                fragmentTransaction.replace(R.id.sns_frame, new RewardFragment()).commit();
                                finished = 0;
                                history = 3;
                            }
                            return true;

                    }
                    return false;
                }
            });

            passPushTokenToServer();
            pushToken();
        } catch (Exception e) {

        }
    }

    void passPushTokenToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("pushToken", token);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }

    void pushToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult().getToken();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (finished == 0) {
            Toast.makeText(getApplicationContext(), "한 번 더 입력하시면 종료됩니다", Toast.LENGTH_SHORT).show();
            finished = 1;
        } else {
            finish();
        }
    }

    protected void onResume() {
        super.onResume();
        finished = 0;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(SnsActivity.this, LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fromright, R.anim.toleft);
            startActivity(intent, activityOptions.toBundle());
            finish();
        }
    }
}

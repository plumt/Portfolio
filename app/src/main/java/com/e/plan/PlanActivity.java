package com.e.plan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.e.plan.fragment.CalenderFragment;
import com.e.plan.fragment.MemoFragment;
import com.e.plan.fragment.ProfileFragment;
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

public class PlanActivity extends AppCompatActivity {
    private int finished = 0;
    private int history = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(this, LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            finish();
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.plan_frame, new CalenderFragment()).commit();
        try {
            BottomNavigationView bottomNavigationView = findViewById(R.id.plan_bottomview);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    switch (menuItem.getItemId()) {
                        case R.id.action_calendar:
                            if (history != 1) {
                                if (history == 2 || history == 3) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromleft2, R.anim.toright2, R.anim.fromright2, R.anim.toleft2);
                                }
                                fragmentTransaction.replace(R.id.plan_frame, new CalenderFragment()).commit();
                                finished = 0;
                                history = 1;
                            }
                            return true;

                        case R.id.action_memo:
                            if (history != 2) {
                                if (history == 1) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromright2, R.anim.toleft2, R.anim.fromleft2, R.anim.toright2);
                                } else if (history == 3) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromleft2, R.anim.toright2, R.anim.fromright2, R.anim.toleft2);
                                }
                                fragmentTransaction.replace(R.id.plan_frame, new MemoFragment()).commit();
                                finished = 0;
                                history = 2;
                            }
                            return true;

                        case R.id.action_profile:
                            if (history != 3) {
                                if (history == 1 || history == 2) {
                                    fragmentTransaction.setCustomAnimations(R.anim.fromright2, R.anim.toleft2, R.anim.fromleft2, R.anim.toright2);
                                }
                                fragmentTransaction.replace(R.id.plan_frame, new ProfileFragment()).commit();
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

    @Override
    protected void onResume() {
        super.onResume();
        finished = 0;
    }
}

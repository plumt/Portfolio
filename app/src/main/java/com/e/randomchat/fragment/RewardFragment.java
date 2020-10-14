package com.e.randomchat.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.e.randomchat.R;
import com.e.randomchat.model.UserModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RewardFragment extends Fragment {
    Button show_btn;
    private static final String AD_UNIT_ID = "ca-app-pub-7669498576619816/3257280275";
    //             ca-app-pub-3940256099942544/5224354917   테스트
//           ca-app-pub-7669498576619816/3257280275    진짜
    Calendar calendar;
    private RewardedAd rewardedAd;
    private String TAG = "Google";
    boolean isLoading, pass = false;
    TextView name_txt, count_txt, name_txt2, count_txt2;
    LinearLayout liner1, liner2, liner3;
    Integer y, m, d;
    boolean pass1 = true, pass2 = true, pass3 = true, stop = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reward, container, false);

        show_btn = (Button) view.findViewById(R.id.show_btn);
        name_txt = (TextView) view.findViewById(R.id.name_txt);
        name_txt2 = (TextView) view.findViewById(R.id.name_txt2);
        count_txt = (TextView) view.findViewById(R.id.count_txt);
        count_txt2 = (TextView) view.findViewById(R.id.count_txt2);
        liner1 = (LinearLayout) view.findViewById(R.id.liner1);
        liner2 = (LinearLayout) view.findViewById(R.id.liner2);
        liner3 = (LinearLayout) view.findViewById(R.id.liner3);
        calendar = Calendar.getInstance();
        setting();
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        loadRewardedAd();
        startrewardAD();
        recycle();


        show_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass) {
                    show_btn.setEnabled(false);
                    showRewardedVideo();
                } else {
                    Toast.makeText(getActivity(), "잠시 후 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;

    }

    void recycle() {
        if (stop) {
            return;
        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!stop && pass1) {
                    pass1 = false;
                    liner1.setBackgroundResource(R.drawable.reward_button);
                    recycle();
                } else if (!stop && pass2) {
                    pass2 = false;
                    liner2.setBackgroundResource(R.drawable.reward_button);
                    recycle();
                } else if (!stop && pass3) {
                    pass3 = false;
                    liner3.setBackgroundResource(R.drawable.reward_button);
                    recycle();
                } else {
                    pass1 = pass2 = pass3 = true;
                    liner1.setBackgroundResource(0);
                    liner2.setBackgroundResource(0);
                    liner3.setBackgroundResource(0);
                    if (!stop) {
                        recycle();
                    }
                }
            }
        }, 1000);

    }

    void setting() {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                name_txt.setText(userModel.userName);
                count_txt.setText(userModel.reward);
                count_txt2.setVisibility(View.VISIBLE);
                name_txt2.setVisibility(View.VISIBLE);

                y = calendar.get(Calendar.YEAR);
                m = calendar.get(Calendar.MONTH) + 1;
                d = calendar.get(Calendar.DATE);
                if (!userModel.date.equals(y.toString() + "," + m.toString() + "," + d.toString())) {
                    Map<String, Object> stringObjectMap = new HashMap<>();
                    stringObjectMap.put("date", y.toString() + "," + m.toString() + "," + d.toString());
                    stringObjectMap.put("reward", "5");
                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            count_txt.setText("5");
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadRewardedAd() {
        if (rewardedAd == null || !rewardedAd.isLoaded()) {
            rewardedAd = new RewardedAd(getActivity(), AD_UNIT_ID);
            isLoading = true;
            rewardedAd.loadAd(
                    new AdRequest.Builder().build(),
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() {
                            // Ad successfully loaded.
                            RewardFragment.this.isLoading = false;
                            pass = true;
                            setting();
//                            Toast.makeText(getActivity(), "리워드광고성공", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdFailedToLoad(int errorCode) {
                            // Ad failed to load.
                            RewardFragment.this.isLoading = false;
                            pass = true;
                            setting();
//                            Toast.makeText(getActivity(), "리워드광고 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void startrewardAD() {
        // Hide the retry button, load the ad, and start the timer.
        if (!rewardedAd.isLoaded() && !isLoading) {
            loadRewardedAd();
        }
    }


    private void showRewardedVideo() {
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback =
                    new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
//                            Toast.makeText(getActivity(), "광고 켜짐", Toast.LENGTH_SHORT).show(); // 광고 켜짐
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
//                            Toast.makeText(getActivity(), "광고 닫음", Toast.LENGTH_SHORT).show(); // 광고 닫음
                            // Preload the next video ad.
                            RewardFragment.this.loadRewardedAd();
                            show_btn.setEnabled(true);
                        }

                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            // User earned reward. // 광고 다 봄
//                            Toast.makeText(getActivity(),"광고 켜짐",Toast.LENGTH_SHORT).show();
                            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                    Map<String, Object> stringObjectMap = new HashMap<>();
                                    stringObjectMap.put("reward", "5");
                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getActivity(), "충전되었습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
//                            Toast.makeText(getActivity(), "광고 안봄(실패)", Toast.LENGTH_SHORT).show(); // 광고 다 안봄
                        }
                    };
            rewardedAd.show(getActivity(), adCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stop = true;
    }
}
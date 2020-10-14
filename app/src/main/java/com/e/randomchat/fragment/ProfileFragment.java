package com.e.randomchat.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.randomchat.LoginActivity;
import com.e.randomchat.R;
import com.e.randomchat.floating.DarkActivity;
import com.e.randomchat.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    Button btn1, btn2, btn3, btn4;
    ImageView profile_image;
    TextView profile_name, my_gender, my_comment, right_comment, left_comment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.profile_floatingbutton);


        my_gender = (TextView) view.findViewById(R.id.my_gender);
        profile_name = (TextView) view.findViewById(R.id.profile_name);
        my_comment = (TextView) view.findViewById(R.id.my_comment);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);
        right_comment = (TextView) view.findViewById(R.id.right_comment);
        left_comment = (TextView) view.findViewById(R.id.left_comment);
        btn1 = (Button) view.findViewById(R.id.btn1);
        btn2 = (Button) view.findViewById(R.id.btn2);
        btn3 = (Button) view.findViewById(R.id.btn3);
        btn4 = (Button) view.findViewById(R.id.btn4);
        setting();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setBackgroundResource(R.drawable.profile_btn2);
                btn2.setBackgroundResource(R.drawable.profile_btn);
                btn3.setBackgroundResource(R.drawable.profile_btn);
                btn4.setBackgroundResource(R.drawable.profile_btn);
                who("남성");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setBackgroundResource(R.drawable.profile_btn);
                btn2.setBackgroundResource(R.drawable.profile_btn2);
                btn3.setBackgroundResource(R.drawable.profile_btn);
                btn4.setBackgroundResource(R.drawable.profile_btn);
                who("여성");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setBackgroundResource(R.drawable.profile_btn);
                btn2.setBackgroundResource(R.drawable.profile_btn);
                btn3.setBackgroundResource(R.drawable.profile_btn2);
                btn4.setBackgroundResource(R.drawable.profile_btn);
                who("모두");
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setBackgroundResource(R.drawable.profile_btn);
                btn2.setBackgroundResource(R.drawable.profile_btn);
                btn3.setBackgroundResource(R.drawable.profile_btn);
                btn4.setBackgroundResource(R.drawable.profile_btn2);
                who("차단");
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.setEnabled(false);
                Intent dark = new Intent(getActivity(), DarkActivity.class);
                dark.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(dark, 1);
                floatingActionButton.setEnabled(true);
            }
        });
        return view;
    }

    void setting() {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.uid.equals(myUid)) {
                        switch (userModel.who) {
                            case "남성":
                                btn1.performClick();
                                break;
                            case "여성":
                                btn2.performClick();
                                break;
                            case "모두":
                                btn3.performClick();
                                break;
                            case "차단":
                                btn4.performClick();
                                break;
                        }
                        my_gender.setText(userModel.gender);
                        my_comment.setText(userModel.comment);
                        if(userModel.comment == null || userModel.comment.equals("")){
                            right_comment.setVisibility(View.INVISIBLE);
                            left_comment.setVisibility(View.INVISIBLE);
                        } else{
                            right_comment.setVisibility(View.VISIBLE);
                            left_comment.setVisibility(View.VISIBLE);
                        }
                        profile_name.setText(userModel.userName);
                        try {
                            Glide.with(getActivity()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                        } catch (Exception e) {

                        }
                        btn1.setClickable(true);
                        btn2.setClickable(true);
                        btn3.setClickable(true);
                        btn4.setClickable(true);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void who(final String s) { // 채팅할 상대방 성별 지정

        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> stringObjectMap = new HashMap<>();
                stringObjectMap.put("who", s);
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(getActivity(), LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            getActivity().finish();
            return;
        }
        setting();
    }
}

package com.e.plan.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.plan.LoginActivity;
import com.e.plan.Model.RoomModel;
import com.e.plan.Model.UserModel;
import com.e.plan.R;
import com.e.plan.alert.AddRoomActivity;
import com.e.plan.floating.DarkActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    ImageView my_image;
    TextView title, my_name;
    Button room1, room2, room3;
    String select = "0", tag, myUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.profile_floatingbutton);
        my_image = (ImageView) view.findViewById(R.id.profile_image);
        title = (TextView) view.findViewById(R.id.titlebar);
        my_name = (TextView) view.findViewById(R.id.profile_name);
        room1 = (Button) view.findViewById(R.id.room1);
        room2 = (Button) view.findViewById(R.id.room2);
        room3 = (Button) view.findViewById(R.id.room3);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.setEnabled(false);
                Intent dark = new Intent(getActivity(), DarkActivity.class);
                startActivity(dark);
                floatingActionButton.setEnabled(true);
            }
        });

        room1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rooms1 = new Intent(getActivity(), AddRoomActivity.class);
                rooms1.putExtra("number", "1");
                rooms1.putExtra("select", select);
                rooms1.putExtra("NEW", room1.getText().toString());
                rooms1.putExtra("tag", room1.getTag().toString());
                startActivity(rooms1);
            }
        });

        room2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rooms2 = new Intent(getActivity(), AddRoomActivity.class);
                rooms2.putExtra("number", "2");
                rooms2.putExtra("select", select);
                rooms2.putExtra("NEW", room2.getText().toString());
                rooms2.putExtra("tag", room2.getTag().toString());
                startActivity(rooms2);
            }
        });

        room3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rooms3 = new Intent(getActivity(), AddRoomActivity.class);
                rooms3.putExtra("number", "3");
                rooms3.putExtra("select", select);
                rooms3.putExtra("NEW", room3.getText().toString());
                rooms3.putExtra("tag", room3.getTag().toString());
                startActivity(rooms3);
            }
        });
        myProfile();
        return view;
    }

    void myProfile() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    my_name.setText(userModel.userName);
                    Glide.with(getActivity()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(my_image);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void rooms() {
        room1.setEnabled(false);
        room2.setEnabled(false);
        room3.setEnabled(false);
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        room1.setBackgroundResource(R.drawable.room_button);
                        room2.setBackgroundResource(R.drawable.room_button);
                        room3.setBackgroundResource(R.drawable.room_button);
                        if (userModel.select != null) {
                            if (userModel.select.equals("1")) {
                                select = "1";
                                tag = userModel.room1;
                                room1.setBackgroundResource(R.drawable.room_button2);
                            } else if (userModel.select.equals("2")) {
                                select = "2";
                                tag = userModel.room2;
                                room2.setBackgroundResource(R.drawable.room_button2);
                            } else if (userModel.select.equals("3")) {
                                select = "3";
                                tag = userModel.room3;
                                room3.setBackgroundResource(R.drawable.room_button2);
                            }
                        } else {
                            select = "0";
                            tag = null;
                            title.setText("");
                        }
                        room1.setText("NEW");
                        room2.setText("NEW");
                        room3.setText("NEW");
                        if (userModel.room1 != null) {
                            room1.setText(userModel.room1_name);
                            room1.setTag(userModel.room1);
                        }
                        if (userModel.room2 != null) {
                            room2.setText(userModel.room2_name);
                            room2.setTag(userModel.room2);
                        }
                        if (userModel.room3 != null) {
                            room3.setText(userModel.room3_name);
                            room3.setTag(userModel.room3);
                        }
                        FirebaseDatabase.getInstance().getReference().child("room").orderByChild("users/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    RoomModel roomModel = snapshot.getValue(RoomModel.class);
                                    if (roomModel.roomUid.equals(tag)) {
                                        title.setText(roomModel.roomdName + "(#" + roomModel.roomUid + ")");
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        room1.setEnabled(true);
                        room2.setEnabled(true);
                        room3.setEnabled(true);
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
        rooms();
        myProfile();
    }
}

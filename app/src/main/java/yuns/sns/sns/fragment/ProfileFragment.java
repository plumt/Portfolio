package yuns.sns.sns.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import yuns.sns.R;
import yuns.sns.sns.LoginActivity;
import yuns.sns.sns.floating.DarkActivity;
import yuns.sns.sns.model.UserModel;

public class ProfileFragment extends Fragment {

    ImageView profile_image;
    TextView profile_name, my_gender, my_comment, right_comment, left_comment, my_friend, my_banced, add_me_friend, my_tag;
    Integer friend, banced, add_me;
    UserModel myModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.profile_btn);
        my_gender = (TextView) view.findViewById(R.id.my_gender);
        profile_name = (TextView) view.findViewById(R.id.profile_name);
        my_comment = (TextView) view.findViewById(R.id.my_comment);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);
        right_comment = (TextView) view.findViewById(R.id.right_comment);
        left_comment = (TextView) view.findViewById(R.id.left_comment);
        my_friend = (TextView) view.findViewById(R.id.my_friend);
        my_banced = (TextView) view.findViewById(R.id.my_banced);
        add_me_friend = (TextView) view.findViewById(R.id.add_me_friend);
        my_tag = (TextView) view.findViewById(R.id.my_tag);

        final Context contextTheme = new ContextThemeWrapper(getActivity(), R.style.AppTheme2);
        LayoutInflater localInflater = getActivity().getLayoutInflater().cloneInContext(contextTheme);


        setting();


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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    void setting() {
        friend = banced = add_me = 0;
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    myModel = dataSnapshot.getValue(UserModel.class);
                    my_gender.setText(myModel.gender);
                    profile_name.setText(myModel.userName);
                    my_tag.setText("#" + myModel.tag);
                    if (myModel.comment == null || myModel.comment.equals("")) {
                        my_comment.setText("");
                        right_comment.setVisibility(View.INVISIBLE);
                        left_comment.setVisibility(View.INVISIBLE);
                    } else {
                        my_comment.setText(myModel.comment);
                        right_comment.setVisibility(View.VISIBLE);
                        left_comment.setVisibility(View.VISIBLE);
                    }

                    Glide.with(getActivity()).load(myModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(profile_image);
                } catch (Exception e) {
                }

                FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friend = banced = add_me = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel.uid.equals(myUid)) {
                                continue;
                            } else if (userModel.friend.containsKey(myUid) && myModel.friend.containsKey(userModel.uid)) {
                                friend++;
                            } else if (userModel.banced.containsKey(myUid)) {
                                banced++;
                            } else if (!userModel.friend.containsKey(myUid) && myModel.friend.containsKey(userModel.uid)) {
                                add_me++;
                            }
                        }
                        my_friend.setText(friend.toString() + "명");
                        my_banced.setText(banced.toString() + "명");
                        add_me_friend.setText(add_me.toString() + "명");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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



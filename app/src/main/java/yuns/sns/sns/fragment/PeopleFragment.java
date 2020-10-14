package yuns.sns.sns.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yuns.sns.R;
import yuns.sns.sns.LoginActivity;
import yuns.sns.sns.floating.DarkActivity;
import yuns.sns.sns.model.UserModel;
import yuns.sns.sns.profile.FriendProfileActivity;
import yuns.sns.sns.profile.MyProfileActivity;

public class PeopleFragment extends Fragment {
    LinearLayout myprofile;
    TextView mytext, my_comment, friend_count;
    ImageView myimage;
    public RecyclerView recyclerView;
    private AdView mAdView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_people, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.people_recycle);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.people_floatingbutton);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());
        mytext = (TextView) view.findViewById(R.id.my_text);
        my_comment = (TextView) view.findViewById(R.id.my_comment);
        friend_count = (TextView) view.findViewById(R.id.friend_count);
        myimage = (ImageView) view.findViewById(R.id.my_image);
        myprofile = (LinearLayout) view.findViewById(R.id.myprofile);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(getActivity(), LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            getActivity().finish();
        }

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myprofile = new Intent(getActivity(), MyProfileActivity.class);
                startActivityForResult(myprofile, 2);
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


    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<UserModel> userModels;

        public PeopleFragmentRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModels.clear();
                    Integer count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel.uid.equals(myUid)) {
                            try {
                                mytext.setText(userModel.userName);
                                if (userModel.comment != null && !userModel.comment.equals("")) {
                                    my_comment.setText(userModel.comment);
                                    my_comment.setVisibility(View.VISIBLE);
                                } else {
                                    my_comment.setText("");
                                    my_comment.setVisibility(View.INVISIBLE);
                                }
                                Glide.with(getActivity()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(myimage);
                            } catch (Exception e) {

                            }
                        } else if (userModel.friend.containsKey(myUid)) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                final UserModel user = snap.getValue(UserModel.class);
                                if (user.uid.equals(myUid)) {
                                    if (user.friend.containsKey(userModel.uid)) {
                                        count++;
                                        userModels.add(snapshot.getValue(UserModel.class));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    Collections.sort(userModels, comparators);
                    friend_count.setText(count.toString() + "명");
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
            return new CustomViewHolder(view);
        }

        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Glide.with
                                (holder.itemView.getContext())
                                .load(userModels.get(position).profileImageUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(((CustomViewHolder) holder).imageView);


                        ((CustomViewHolder) holder).textView.setText(userModels.get(position).userName);

                        if (userModels.get(position).comment != null && !userModels.get(position).comment.equals("")) {
                            ((CustomViewHolder) holder).friend_comment.setVisibility(View.VISIBLE);
                            ((CustomViewHolder) holder).friend_comment.setText(userModels.get(position).comment);
                        } else {
                            ((CustomViewHolder) holder).friend_comment.setVisibility(View.INVISIBLE);
                            ((CustomViewHolder) holder).friend_comment.setText("");
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent friendprofile = new Intent(getActivity(), FriendProfileActivity.class);
                    friendprofile.putExtra("uid", userModels.get(position).uid);
                    startActivity(friendprofile);
                }
            });


        }

        public int getItemCount() {
            return userModels.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView friend_comment;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.friend_image);
                textView = (TextView) view.findViewById(R.id.friend_text);
                friend_comment = (TextView) view.findViewById(R.id.friend_comment);
            }

        }
    }

    Comparator<UserModel> comparators = new Comparator<UserModel>() {
        @Override
        public int compare(UserModel item1, UserModel item2) {
            return item1.userName.compareTo(item2.userName);
        }
    };

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
    }
}

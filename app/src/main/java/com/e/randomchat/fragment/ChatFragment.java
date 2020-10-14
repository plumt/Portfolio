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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.randomchat.LoginActivity;
import com.e.randomchat.R;
import com.e.randomchat.alert.OutRoomActivity;
import com.e.randomchat.chat.RandomMessageActivity;
import com.e.randomchat.model.ChatModel;
import com.e.randomchat.model.UserModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatFragment extends Fragment {
    public RecyclerView recyclerView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private int count = 0;
    boolean check_chat = true, check_gender = false, no_reward = false, banced = false;
    private AdView mAdView;
    Integer y, m, d;
    Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.random_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.getRecycledViewPool().clear();
        recyclerView.setAdapter(new RandomRecyclerViewAdapter());
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.send_btn);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        calendar = Calendar.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent logout = new Intent(getActivity(), LoginActivity.class);
            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fromright, R.anim.toleft);
            startActivity(logout, activityOptions.toBundle());
            getActivity().finish();
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        if(userModel.who.equals("차단")){
                            Toast.makeText(getActivity(),"매칭 상대 설정이 '차단' 상태입니다",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        RandomSend();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

    void RandomSend() {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final List<UserModel> userModels = new ArrayList<>();
        final List<UserModel> myModels = new ArrayList<>();

        count = 0;
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myModels.clear();
                userModels.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    no_reward = check_gender = banced = false;
                    check_chat = true;
                    final UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.uid.equals(myUid)) {
                        y = calendar.get(Calendar.YEAR);
                        m = calendar.get(Calendar.MONTH) + 1;
                        d = calendar.get(Calendar.DATE);
                        if (!userModel.date.equals(y.toString() + "," + m.toString() + "," + d.toString())) {
                            Map<String, Object> stringObjectMap = new HashMap<>();
                            stringObjectMap.put("date", y.toString() + "," + m.toString() + "," + d.toString());
                            stringObjectMap.put("reward", "5");
                            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap);
                        } else if (userModel.reward.equals("0")) {
                            no_reward = true;
                            break;
                        }

                        myModels.add(snapshot.getValue(UserModel.class));
                        continue;
                    } else {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            UserModel my = item.getValue(UserModel.class);
                            if (my.uid.equals(myUid)) {
                                if (!my.who.equals(userModel.gender) && !my.who.equals("모두")) {
                                    check_gender = true;
                                    break;
                                } else if (!userModel.who.equals(my.gender) && !userModel.who.equals("모두")) {
                                    check_gender = true;
                                    break;
                                } else if (userModel.banced.containsKey(myUid) || my.banced.containsKey(userModel.uid)) {
                                    banced = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (check_gender) {
                        continue;
                    } else if (banced || userModel.banced.get(myUid) != null) {
                        continue;
                    } else if (userModel.random.get(myUid) != null) {
                        continue;
                    } else {
                        count++;
                        userModels.add(snapshot.getValue(UserModel.class));
                    }
                }

                if (no_reward) {
                    Toast.makeText(getActivity(), "하루 5회 이용 가능합니다", Toast.LENGTH_SHORT).show();
                } else if (count > 0) {
                    final int select = (int) ((Math.random()) * (count));
                    userModels.get(select).random.put(myUid, "random");
                    myModels.get(0).random.put(userModels.get(select).uid, "random");
                    FirebaseDatabase.getInstance().getReference().child("users").child(userModels.get(select).uid)
                            .setValue(userModels.get(select)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(myModels.get(0).uid)
                                    .setValue(myModels.get(0)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Integer reward;
                                    reward = Integer.parseInt(myModels.get(0).reward);
                                    reward--;
                                    Map<String, Object> stringObjectMap = new HashMap<>();
                                    stringObjectMap.put("reward", reward.toString());
                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).updateChildren(stringObjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent chat = new Intent(getContext(), RandomMessageActivity.class);
                                            chat.putExtra("destinationUid", userModels.get(select).uid);
                                            chat.putExtra("first", true);
                                            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getContext(), R.anim.fromleft, R.anim.toright);
                                            startActivity(chat, activityOptions.toBundle());

                                        }
                                    });
                                }
                            });

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "채팅할 상대가 없습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    class RandomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ChatModel> chatModels = new ArrayList<>();
        private ArrayList<String> destinationUsers = new ArrayList<>();
        private String uid;

        public RandomRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("randoms").orderByChild("users/" + uid).equalTo(true).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    destinationUsers.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        chatModels.add(item.getValue(ChatModel.class));
                    }

                    Collections.sort(chatModels, comparators);
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
            return new CustomViewHolder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            String destinationUid = null;

            for (String user : chatModels.get(position).users.keySet()) {
                if (!user.equals(uid)) {
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userModel.profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);
                    customViewHolder.textView_title.setText(userModel.userName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            if (commentMap.keySet().toArray().length > 0) {
                if (chatModels.get(position).user != null && !chatModels.get(position).user.equals(uid)) {
                    customViewHolder.circle.setVisibility(View.VISIBLE);
                } else {
                    customViewHolder.circle.setVisibility(View.INVISIBLE);
                }
                String lastMessageKey = (String) commentMap.keySet().toArray()[0];
                customViewHolder.textView_last.setText(chatModels.get(position).comments.get(lastMessageKey).message);
                long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
                Date date = new Date(unixTime);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                customViewHolder.textView_time.setText(simpleDateFormat.format(date));
            } else {
                chatModels.remove(position);
            }

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), RandomMessageActivity.class);
                    intent.putExtra("destinationUid", destinationUsers.get(chatModels.size() - 1 - position));
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromleft, R.anim.toright);
                    startActivity(intent, activityOptions.toBundle());
                }
            });

            customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent out = new Intent(getActivity(), OutRoomActivity.class);
                    out.putExtra("uid",destinationUsers.get(chatModels.size() - 1 - position));
                    startActivity(out);
                    return false;
                }
            });
        }


        public int getItemCount() {
            return chatModels.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last;
            public TextView textView_time;
            public TextView circle;


            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.chatitem_image);
                textView_last = (TextView) view.findViewById(R.id.chatitem_lasttext);
                textView_title = (TextView) view.findViewById(R.id.chatitem_titletext);
                textView_time = (TextView) view.findViewById(R.id.chatitem_timetext);
                circle = (TextView) view.findViewById(R.id.circle);
            }
        }
    }


    Comparator<ChatModel> comparators = new Comparator<ChatModel>() {
        @Override
        public int compare(ChatModel item1, ChatModel item2) {
            return String.valueOf(item1.date).compareTo(String.valueOf(item2.date));
        }
    };

}

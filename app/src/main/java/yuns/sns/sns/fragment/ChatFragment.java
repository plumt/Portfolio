package yuns.sns.sns.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import yuns.sns.R;
import yuns.sns.sns.alert.RoomOutWarningAlertActivity;
import yuns.sns.sns.chat.MessageActivity;
import yuns.sns.sns.model.ChatModel;
import yuns.sns.sns.model.UserModel;

public class ChatFragment extends Fragment {
    public RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private AdView mAdView;
    Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chatfragment_recycle);
        recyclerView.setAdapter(new ChatRecycleViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        calendar = Calendar.getInstance();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);


        return view;
    }

    class ChatRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();

        public ChatRecycleViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    destinationUsers.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        ChatModel chatModel = item.getValue(ChatModel.class);
                        if (chatModel.comments.size() > 0) {
                            chatModels.add(item.getValue(ChatModel.class));
                        }
                    }
                    Collections.sort(chatModels, comparators);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
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
                    try {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        Glide.with(customViewHolder.itemView.getContext())
                                .load(userModel.profileImageUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(customViewHolder.imageView);

                        customViewHolder.textView_title.setText(userModel.userName);
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
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
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", destinationUsers.get(chatModels.size() - 1 - position));
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromleft, R.anim.toright);
                    startActivity(intent, activityOptions.toBundle());
                }
            });

            customViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(v.getContext(), RoomOutWarningAlertActivity.class);
                    intent.putExtra("destinationUid", destinationUsers.get(chatModels.size() - 1 - position));
                    startActivity(intent);
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
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

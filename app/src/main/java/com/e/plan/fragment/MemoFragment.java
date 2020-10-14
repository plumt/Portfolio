package com.e.plan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.plan.Model.RoomModel;
import com.e.plan.Model.UserModel;
import com.e.plan.R;
import com.e.plan.alert.MemoAlertActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MemoFragment extends Fragment {
    public RecyclerView recyclerView;
    Button btn;
    private AdView mAdView;
    String room;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memo, container, false);
        recyclerView = view.findViewById(R.id.memo_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new MemoFragmentRecyclerViewAdapter());
        btn = (Button) view.findViewById(R.id.add_memo);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent memo = new Intent(getActivity(), MemoAlertActivity.class);
                memo.putExtra("btn", true);
                startActivityForResult(memo, 1);
            }
        });

        return view;
    }

    class MemoFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<RoomModel.Memo> roomModels;

        public MemoFragmentRecyclerViewAdapter() {
            roomModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel.select != null) {
                        switch (userModel.select) {
                            case "1":
                                room = userModel.room1;
                                break;
                            case "2":
                                room = userModel.room2;
                                break;
                            case "3":
                                room = userModel.room3;
                        }
                    }
                    FirebaseDatabase.getInstance().getReference().child("room").orderByChild("users/" + myUid).equalTo(true).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                final RoomModel roomModel = item.getValue(RoomModel.class);
                                if (roomModel.roomUid.equals(room)) {
                                    String roomkey = item.getKey();
                                    FirebaseDatabase.getInstance().getReference().child("room").child(roomkey).child("memo").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            roomModels.clear();
                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                RoomModel.Memo memo = snapshot1.getValue(RoomModel.Memo.class);
                                                roomModels.add(memo);
                                            }
                                            if (roomModels.size() == 0) {
                                                return;
                                            }
                                            notifyDataSetChanged();
                                            recyclerView.scrollToPosition(0);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                }
                            }
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

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            CustomViewHolder customViewHolder = ((CustomViewHolder) holder);
            customViewHolder.write_name.setText(roomModels.get(roomModels.size() - 1 - position).name);
            customViewHolder.write_memo.setText(roomModels.get(roomModels.size() - 1 - position).title);
            customViewHolder.write_date.setText(roomModels.get(roomModels.size() - 1 - position).date);


            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent memo = new Intent(v.getContext(), MemoAlertActivity.class);
                        memo.putExtra("uid", roomModels.get(roomModels.size() - 1 - position).uid);
                        memo.putExtra("name", roomModels.get(roomModels.size() - 1 - position).name);
                        memo.putExtra("title", roomModels.get(roomModels.size() - 1 - position).title);
                        memo.putExtra("memo", roomModels.get(roomModels.size() - 1 - position).memo);
                        memo.putExtra("date", roomModels.get(roomModels.size() - 1 - position).date);
                        startActivityForResult(memo, 1);
                    } catch (Exception e) {
                        recyclerView.setAdapter(new MemoFragmentRecyclerViewAdapter());
                        Toast.makeText(getActivity(), "존재하지 않는 메모입니다", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return roomModels.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView write_name, write_memo, write_date;

            public CustomViewHolder(View view) {
                super(view);
                write_name = (TextView) view.findViewById(R.id.write_name);
                write_memo = (TextView) view.findViewById(R.id.write_memo);
                write_date = (TextView) view.findViewById(R.id.write_date);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean del = data.getBooleanExtra("del", false);
            if (del) {
                recyclerView.setAdapter(new MemoFragmentRecyclerViewAdapter());
            }
        }
    }
}


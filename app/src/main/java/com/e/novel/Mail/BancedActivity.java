package com.e.novel.Mail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.novel.Model.MailModel;
import com.e.novel.Model.UserModel;
import com.e.novel.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BancedActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    TextView banced_count;
    private AdView mAdView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banced);
        banced_count = (TextView) findViewById(R.id.banced_count);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(BancedActivity.this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<String> uids = new ArrayList<>();
        private List<MailModel> mailModels = new ArrayList<>();


        public RecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    uids.clear();
                    mailModels.clear();
                    MailModel mailModel = dataSnapshot.getValue(MailModel.class);
                    mailModels.add(mailModel);
                    if (mailModel != null) {
                        for (String user : mailModels.get(0).banced.keySet()) {
                            uids.add(user);
                        }
                    }
                    notifyDataSetChanged();
                    if (uids.size() == 0) {
                        banced_count.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banced, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final RecyclerViewAdapter.CustomViewHolder customViewHolder = (RecyclerViewAdapter.CustomViewHolder) holder;

            banced_count.setText(String.valueOf(uids.size()));

            FirebaseDatabase.getInstance().getReference().child("users").child(uids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    try {
                        customViewHolder.name.setText(userModel.userName);
                        Glide.with(customViewHolder.itemView.getContext())
                                .load(userModel.profileImageUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(customViewHolder.image);

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            customViewHolder.nobanced.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noBanced(uids.get(position));
                }
            });

        }

        @Override
        public int getItemCount() {
            return uids.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView image;
            public Button nobanced;

            public CustomViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                image = (ImageView) view.findViewById(R.id.image);
                nobanced = (Button) view.findViewById(R.id.nobanced);
            }
        }
    }

    void noBanced(String uid) {
        FirebaseDatabase.getInstance().getReference().child("mails").child(myUid).child("banced").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                recyclerView.setAdapter(new RecyclerViewAdapter());
                Toast.makeText(getApplicationContext(), "차단을 해제하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }
}

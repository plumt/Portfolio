package yuns.sns.sns.floating;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yuns.sns.R;
import yuns.sns.sns.alert.BancedAlertActivity;
import yuns.sns.sns.model.UserModel;

public class BancedActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView banced_count;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banced);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recyclerView = (RecyclerView) findViewById(R.id.banced_recycle);
        banced_count = (TextView) findViewById(R.id.banced_count);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new BancedActivityRecyclerViewAdapter());
    }

    class BancedActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<UserModel> userModels;

        public BancedActivityRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModels.clear();
                    Integer count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel.banced.containsKey(myUid)) {
                            userModels.add(snapshot.getValue(UserModel.class));
                            count++;
                        }
                    }
                    banced_count.setText(count.toString() + "명");
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add, parent, false);
            return new CustomViewHolder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            try {
                Glide.with
                        (holder.itemView.getContext())
                        .load(userModels.get(position).profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(((CustomViewHolder) holder).imageView);

                ((CustomViewHolder) holder).textView.setText(userModels.get(position).userName);
            } catch (Exception e) {

            }
            ((CustomViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BancedActivity.this, BancedAlertActivity.class);
                    intent.putExtra("friend_uid", userModels.get(position).uid);
                    startActivity(intent);
                }
            });
        }

        public int getItemCount() {
            return userModels.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public Button button;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.friend_image);
                textView = (TextView) view.findViewById(R.id.friend_text);
                button = (Button) view.findViewById(R.id.friend_add);
                button.setText("해제");
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fromright, R.anim.toleft);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setAdapter(new BancedActivityRecyclerViewAdapter());
    }
}

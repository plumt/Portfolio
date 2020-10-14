package com.e.novel;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.novel.Alert.MyProfileActivity;
import com.e.novel.Book.SelectNovelActivity;
import com.e.novel.Category.CategoryActivty;
import com.e.novel.Model.NovelModel;
import com.e.novel.Model.UserModel;
import com.e.novel.Navigation.MyLibraryActivity;
import com.e.novel.Navigation.MyMailActivity;
import com.e.novel.Navigation.MyPageActivity;
import com.e.novel.SerchNovels.MoreNovelActivity;
import com.e.novel.SerchNovels.SerchNovelActivity;
import com.e.novel.User.LoginActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    private DrawerLayout drawerLayout;
    Context context;
    LinearLayout liner1, liner3, liner5;
    TextView new_more, like_more, view_more, profile_name, profile_email;
    EditText serch_edit;
    boolean serch_bol = true, bye = false;
    ImageView x, imageView;
    Integer[] numBtnIds = {R.id.fantasy, R.id.sf, R.id.game, R.id.drama, R.id.detective, R.id.mystery, R.id.heroism, R.id.romance, R.id.comic};
    Button[] numButtons = new Button[9];
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        new_more = (TextView) findViewById(R.id.new_more);
        like_more = (TextView) findViewById(R.id.like_more);
        view_more = (TextView) findViewById(R.id.view_more);
        serch_edit = (EditText) findViewById(R.id.serch_edit);
        x = (ImageView) findViewById(R.id.x);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        liner1 = (LinearLayout) findViewById(R.id.liner1);
        liner3 = (LinearLayout) findViewById(R.id.liner3);
        liner5 = (LinearLayout) findViewById(R.id.liner5);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_header_view = navigationView.getHeaderView(0);
        profile_name = (TextView) nav_header_view.findViewById(R.id.profile_name);
        profile_email = (TextView) nav_header_view.findViewById(R.id.profile_email);
        imageView = (ImageView) nav_header_view.findViewById(R.id.image);

        for (int i = 0; i < numBtnIds.length; i++) {
            numButtons[i] = (Button) findViewById(numBtnIds[i]);
        }
        for (int i = 0; i < numBtnIds.length; i++) {
            final int index;
            index = i;
            numButtons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noSerch();
                    Intent category = new Intent(getApplicationContext(), CategoryActivty.class);
                    category.putExtra("category", numButtons[index].getText().toString().trim());
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                    startActivity(category, activityOptions.toBundle());
                }
            });
        }
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serch_edit.setText("");
                noSerch();
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                noSerch();
                int id = menuItem.getItemId();
//                String title = menuItem.getTitle().toString();
//                menuItem.setIcon(R.drawable.serch);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                switch (id) {
                    case R.id.profile:
                        Intent myprofile = new Intent(getApplicationContext(), MyProfileActivity.class);
                        ActivityOptions activityOptions2 = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fab_open, R.anim.fab_close);
                        startActivityForResult(myprofile, 1, activityOptions2.toBundle());
                        break;
                    case R.id.library:
                        Intent library = new Intent(getApplicationContext(), MyLibraryActivity.class);
                        startActivity(library, activityOptions.toBundle());
                        break;
                    case R.id.writer:
                        Intent writer = new Intent(getApplicationContext(), MyPageActivity.class);
                        startActivity(writer, activityOptions.toBundle());
                        break;
                    case R.id.memo:
                        Intent mail = new Intent(getApplicationContext(), MyMailActivity.class);
                        startActivity(mail, activityOptions.toBundle());
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                        Toast.makeText(getApplicationContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show();
                        ActivityOptions activityOptions3 = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(logout, activityOptions3.toBundle());
                        finish();
                        break;
                }

                return true;
            }
        });

        view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noSerch();
                Intent mostview = new Intent(getApplicationContext(), MoreNovelActivity.class);
                mostview.putExtra("more", "mostview");
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(mostview, activityOptions.toBundle());
            }
        });
        like_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noSerch();
                Intent mostlike = new Intent(getApplicationContext(), MoreNovelActivity.class);
                mostlike.putExtra("more", "mostlike");
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(mostlike, activityOptions.toBundle());
            }
        });
        new_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noSerch();
                Intent recently = new Intent(getApplicationContext(), MoreNovelActivity.class);
                recently.putExtra("more", "recently");
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(recently, activityOptions.toBundle());
            }
        });

        viewNovel();
        setting();
    }

    void viewNovel() {
        final List<NovelModel> recently = new ArrayList<>();
        final List<NovelModel> mostlike = new ArrayList<>();
        final List<NovelModel> mostview = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("novels").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recently.clear();
                mostlike.clear();
                mostview.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NovelModel novelModel = snapshot.getValue(NovelModel.class);
                    if (novelModel.open) {
                        recently.add(snapshot.getValue(NovelModel.class));
                        mostlike.add(snapshot.getValue(NovelModel.class));
                        mostview.add(snapshot.getValue(NovelModel.class));
                    }
                }
                Collections.sort(recently, comparators1);
                Collections.sort(mostlike, comparators2);
                Collections.sort(mostview, comparators3);

                for (int i = 0; i < 10; i++) {
                    if (i == recently.size()) {
                        break;
                    }
                    createbtn(1, recently.get(i).title, recently.get(i).category_str, recently.get(i).key, recently.get(i).comment, recently.get(i).myuid);
                }
                for (int i = 0; i < 10; i++) {
                    if (i == recently.size()) {
                        break;
                    }
                    createbtn(2, mostlike.get(i).title, mostlike.get(i).category_str, mostlike.get(i).key, mostlike.get(i).comment, mostlike.get(i).myuid);
                }
                for (int i = 0; i < 10; i++) {
                    if (i == recently.size()) {
                        break;
                    }
                    createbtn(3, mostview.get(i).title, mostview.get(i).category_str, mostview.get(i).key, mostview.get(i).comment, mostview.get(i).myuid);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void createbtn(int choice, String name, String category, String key, String comment, String uid) {
        int bookColor = 0;
        switch (category) {
            case "fantasy":
                bookColor = 1;
                break;
            case "sf":
                bookColor = 7;
                break;
            case "game":
                bookColor = 3;
                break;
            case "drama":
                bookColor = 4;
                break;
            case "detective":
                bookColor = 5;
                break;
            case "mystery":
                bookColor = 6;
                break;
            case "romance":
                bookColor = 2;
                break;
            case "heroism":
                bookColor = 8;
                break;
            case "comic":
                bookColor = 9;
                break;
        }
        createButton(choice, name, bookColor, key, comment, category, uid);
    }

    Comparator<NovelModel> comparators1 = new Comparator<NovelModel>() {
        @Override
        public int compare(NovelModel item1, NovelModel item2) {
            return String.valueOf(item2.date).compareTo(String.valueOf(item1.date));
        }
    };

    Comparator<NovelModel> comparators2 = new Comparator<NovelModel>() {
        @Override
        public int compare(NovelModel item1, NovelModel item2) {
            if (Integer.parseInt(item1.like) > Integer.parseInt(item2.like)) {
                return -1;
            } else if (Integer.parseInt(item1.like) < Integer.parseInt(item2.like)) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    Comparator<NovelModel> comparators3 = new Comparator<NovelModel>() {
        @Override
        public int compare(NovelModel item1, NovelModel item2) {
            if (Integer.parseInt(item1.view) > Integer.parseInt(item2.view)) {
                return -1;
            } else if (Integer.parseInt(item1.view) < Integer.parseInt(item2.view)) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    void setting() {
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    profile_name.setText(userModel.userName);
                    profile_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    Glide.with(getApplicationContext()).load(userModel.profileImageUrl).apply(new RequestOptions().circleCrop()).into(imageView);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void createButton(int choice, final String name, int bookColor, final String key, final String comment, final String category, final String uid) {
        final Button btn = new Button(context);
        final TextView txt = new TextView(context);
        LinearLayout.LayoutParams texts = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        texts.width = 300;
        texts.height = 50;
        txt.setLayoutParams(texts);
        txt.setTextSize(12);

        final LinearLayout liners = new LinearLayout(context);
        LinearLayout.LayoutParams linerss = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linerss.width = 300;
        linerss.height = 400;
        liners.setLayoutParams(linerss);
        liners.setOrientation(LinearLayout.VERTICAL);


        btn.setPadding(50, 50, 50, 50);

        txt.setText(name);
        txt.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams topButton = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        topButton.width = 300;
        topButton.height = 350;
        btn.setLayoutParams(topButton);
        liners.addView(btn);
        liners.addView(txt);

        switch (bookColor) {
            case 1:
                btn.setBackgroundResource(R.drawable.books1);
                break;
            case 2:
                btn.setBackgroundResource(R.drawable.books2);
                break;
            case 3:
                btn.setBackgroundResource(R.drawable.books3);
                break;
            case 4:
                btn.setBackgroundResource(R.drawable.books4);
                break;
            case 5:
                btn.setBackgroundResource(R.drawable.books5);
                break;
            case 6:
                btn.setBackgroundResource(R.drawable.books6);
                break;
            case 7:
                btn.setBackgroundResource(R.drawable.books7);
                break;
            case 8:
                btn.setBackgroundResource(R.drawable.books8);
                break;
            case 9:
                btn.setBackgroundResource(R.drawable.books9);
                break;
            default:
                btn.setBackgroundResource(R.drawable.books1);
        }
        if (choice == 1) {
            liner1.addView(liners);
        } else if (choice == 2) {
            liner3.addView(liners);
        } else if (choice == 3) {
            liner5.addView(liners);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noSerch();
                Intent select = new Intent(getApplicationContext(), SelectNovelActivity.class);
                select.putExtra("key", key);
                select.putExtra("title", name);
                select.putExtra("comment", comment);
                select.putExtra("category", category);
                select.putExtra("uid", uid);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivityForResult(select, 1, activityOptions.toBundle());
            }
        });
    }

    void noSerch(){
        serch_edit.setText("");
        serch_bol = true;
        serch_edit.setVisibility(View.INVISIBLE);
        x.setVisibility(View.INVISIBLE);
        imm.hideSoftInputFromWindow(serch_edit.getWindowToken(), 0);
        bye = false;
    }

    void serch() {
        bye = false;
        if (serch_bol) {
            serch_bol = false;
            serch_edit.post(new Runnable() {
                @Override
                public void run() {
                    serch_edit.setVisibility(View.VISIBLE);
                    x.setVisibility(View.VISIBLE);
                    serch_edit.setText("");
                    serch_edit.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            });
        } else {
            noSerch();
            if (serch_edit.getText().toString().trim().length() > 0) {
                Intent Serch_novel = new Intent(getApplicationContext(), SerchNovelActivity.class);
                Serch_novel.putExtra("serch", serch_edit.getText().toString().trim());
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                startActivity(Serch_novel, activityOptions.toBundle());
            }
            serch_edit.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.memu_top_, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // 왼쪽 상단 버튼 눌렀을 때
                noSerch();
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.serch:
                serch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setting();
        }
    }

    @Override
    public void onBackPressed() {
        if(!bye){
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            bye = true;
        } else{
            finish();
        }
    }
}

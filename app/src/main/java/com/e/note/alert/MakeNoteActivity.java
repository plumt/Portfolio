package com.e.note.alert;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.note.MemoActivity;
import com.e.note.R;
import com.e.note.model.MemoModel;
import com.e.note.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MakeNoteActivity extends AppCompatActivity {
    EditText notename, password;
    Button result, cancle;
    TextView namewarning, password_warning;
    CheckBox checkbox;
    int choice = 1;
    Button[] numButtons = new Button[7];
    ImageView[] numImages = new ImageView[7];
    Integer[] numBtnIds = {R.id.red, R.id.orange, R.id.yellow, R.id.green, R.id.sky, R.id.blue, R.id.purple};
    Integer[] numImgIds = {R.id.arrow_red, R.id.arrow_orange, R.id.arrow_yellow, R.id.arrow_green, R.id.arrow_sky, R.id.arrow_blue, R.id.arrow_purple};
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_note);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        notename = (EditText) findViewById(R.id.notename);
        password = (EditText) findViewById(R.id.password);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        namewarning = (TextView) findViewById(R.id.name_warning);
        password_warning = (TextView) findViewById(R.id.password_warning);
        for (int i = 0; i < numBtnIds.length; i++) {
            numButtons[i] = (Button) findViewById(numBtnIds[i]);
            numImages[i] = (ImageView) findViewById(numImgIds[i]);
        }

        for (int i = 0; i < numBtnIds.length; i++) {
            final int index;
            index = i;
            numButtons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(notename.getWindowToken(), 0);
                    for (int j = 0; j < numBtnIds.length; j++) {
                        numImages[j].setVisibility(View.INVISIBLE);
                    }
                    numImages[index].setVisibility(View.VISIBLE);
                    choice = index + 1;
                }
            });
        }

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(notename.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);

            }
        });
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setEnabled(false);
                imm.hideSoftInputFromWindow(notename.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                if (notename.getText().toString().trim().length() < 1) {
                    namewarning.setVisibility(View.VISIBLE);
                    namewarning.setText("폴더 이름은 한 글자 이상 입력해 주세요");
                    notename.setBackgroundResource(R.drawable.edittext_red);
                    result.setEnabled(true);
                } else if (checkbox.isChecked() && password.getText().toString().trim().length() < 1) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                    result.setEnabled(true);
                } else {
                    makeNote();
                }
            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(notename.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                if (checkbox.isChecked()) {
                    password.setEnabled(true);
                } else {
                    password.setText("");
                    password.setEnabled(false);
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.edittext_login);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password.getText().toString().trim().length() < 1) {
                    password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                } else {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        notename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (notename.getText().toString().trim().length() < 1) {
                    namewarning.setVisibility(View.VISIBLE);
                    namewarning.setText("폴더 이름은 한 글자 이상 입력해 주세요");
                    notename.setBackgroundResource(R.drawable.edittext_red);
                } else {
                    namewarning.setVisibility(View.INVISIBLE);
                    notename.setBackgroundResource(R.drawable.edittext_login);
                }
            }
        });
    }

    void makeNote() {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel.notes.containsKey(notename.getText().toString())) {
                    namewarning.setText("이미 같은 이름의 폴더가 있습니다");
                    namewarning.setVisibility(View.VISIBLE);
                    notename.setBackgroundResource(R.drawable.edittext_red);
                    result.setEnabled(true);
                    return;
                } else {
                    final MemoModel memoModel = new MemoModel();
                    memoModel.user.put(myUid, true);
                    memoModel.date = ServerValue.TIMESTAMP;
                    memoModel.color = String.valueOf(choice);
                    memoModel.name = notename.getText().toString().trim();
                    if (checkbox.isChecked()) {
                        memoModel.password = password.getText().toString();
                    }
                    FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).push().setValue(memoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        String key;

                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).orderByChild("user/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        MemoModel memoModels = snapshot.getValue(MemoModel.class);
                                        if (notename.getText().toString().trim().equals(memoModels.name)) {
                                            key = snapshot.getKey();
                                            Map<String, Object> addkey = new HashMap<>();
                                            addkey.put("key", key);
                                            FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).updateChildren(addkey).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Map<String, Object> notes = new HashMap<>();
                                                    notes.put(notename.getText().toString(), key);
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("notes").updateChildren(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getApplicationContext(), "새로운 폴더를 만들었습니다", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
                                                            intent.putExtra("name", notename.getText().toString().trim());
                                                            intent.putExtra("key", key);
                                                            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fromleft, R.anim.toright);
                                                            startActivity(intent, activityOptions.toBundle());
                                                            finish();
                                                            return;
                                                        }
                                                    });

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
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}

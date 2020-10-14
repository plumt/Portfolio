package com.e.note.alert;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.note.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateNoteActivity extends AppCompatActivity {
    EditText notename, password, input_password;
    FrameLayout frame;
    LinearLayout liner;
    Button result, cancle, delete, result2;
    TextView namewarning, title, password_warning2, password_warning;
    View view, view2, view3;
    int choice, choice2;
    Button[] numButtons = new Button[7];
    ImageView[] numImages = new ImageView[7];
    Integer[] numBtnIds = {R.id.red, R.id.orange, R.id.yellow, R.id.green, R.id.sky, R.id.blue, R.id.purple};
    Integer[] numImgIds = {R.id.arrow_red, R.id.arrow_orange, R.id.arrow_yellow, R.id.arrow_green, R.id.arrow_sky, R.id.arrow_blue, R.id.arrow_purple};
    CheckBox checkbox;
    Intent intent;
    String key, name, pass;
    InputMethodManager imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_note);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        notename = (EditText) findViewById(R.id.notename);
        password = (EditText) findViewById(R.id.password);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        frame = (FrameLayout) findViewById(R.id.frame);
        liner = (LinearLayout) findViewById(R.id.liner2);
        view = (View) findViewById(R.id.view);
        view2 = (View) findViewById(R.id.view2);
        view3 = (View) findViewById(R.id.view3);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        delete = (Button) findViewById(R.id.delete);
        result2 = (Button) findViewById(R.id.result2);
        input_password = (EditText) findViewById(R.id.input_password);
        namewarning = (TextView) findViewById(R.id.name_warning);
        password_warning2 = (TextView) findViewById(R.id.password_warning2);
        password_warning = (TextView) findViewById(R.id.password_warning);
        title = (TextView) findViewById(R.id.title);
        title.setText("폴더 수정");
        cancle.setVisibility(View.GONE);
        delete.setVisibility(View.VISIBLE);
        intent = getIntent();
        name = intent.getStringExtra("name");
        notename.setText(name);
        choice = choice2 = intent.getIntExtra("choice", 0);
        key = intent.getStringExtra("key");
        pass = intent.getStringExtra("pass");

        if (pass != null) {
            inputPassword();
            checkbox.setChecked(true);
            password.setEnabled(true);
            password.setText(pass);
        }

        for (int i = 0; i < numBtnIds.length; i++) {
            numButtons[i] = (Button) findViewById(numBtnIds[i]);
            numImages[i] = (ImageView) findViewById(numImgIds[i]);
        }
        numImages[0].setVisibility(View.INVISIBLE);
        numImages[choice2 - 1].setVisibility(View.VISIBLE);

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                if (checkbox.isChecked()) {
                    password.setEnabled(true);
                } else {
                    password.setText("");
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password.setEnabled(false);
                }
            }
        });

        for (int i = 0; i < numBtnIds.length; i++) {
            final int index;
            index = i;
            numButtons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    keyboardDown();
                    for (int j = 0; j < numBtnIds.length; j++) {
                        numImages[j].setVisibility(View.INVISIBLE);
                    }
                    numImages[index].setVisibility(View.VISIBLE);
                    choice = index + 1;
                }
            });
        }

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setEnabled(false);
                keyboardDown();
                if (notename.getText().toString().trim().length() < 1) {
                    namewarning.setVisibility(View.VISIBLE);
                    notename.setBackgroundResource(R.drawable.edittext_red);
                    namewarning.setText("폴더 이름은 한 글자 이상 입력해 주세요");
                    result.setEnabled(true);
                    return;
                } else if (checkbox.isChecked() && password.getText().toString().trim().length() < 1) {
                    password_warning.setVisibility(View.VISIBLE);
                    password.setBackgroundResource(R.drawable.edittext_red);
                    namewarning.setText("패스워드는 한 글자 이상 입력해 주세요");
                    result.setEnabled(true);
                } else if (choice == choice2 && name.equals(notename.getText().toString()) && (pass == null && !checkbox.isChecked() || checkbox.isChecked() && pass != null && password.getText().toString().equals(pass))) {
                    namewarning.setVisibility(View.INVISIBLE);
                    notename.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.edittext_login);
                    finish();
                    overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                } else {
                    namewarning.setVisibility(View.INVISIBLE);
                    notename.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                    password.setBackgroundResource(R.drawable.edittext_login);
                    updateNote();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("notes").child(notename.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "폴더를 삭제했습니다", Toast.LENGTH_SHORT).show();
                                intent.putExtra("recycle", true);
                                setResult(RESULT_OK, intent);
                                finish();
                                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                            }
                        });
                    }
                });
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
                    notename.setBackgroundResource(R.drawable.edittext_red);
                    namewarning.setText("폴더 이름은 한 글자 이상 입력해 주세요");
                } else {
                    namewarning.setVisibility(View.INVISIBLE);
                    notename.setBackgroundResource(R.drawable.edittext_login);
                }
            }
        });

        result2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                if (input_password.getText().toString().equals(pass)) {
                    input_password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning2.setVisibility(View.INVISIBLE);
                    resultPassword();
                } else {
                    input_password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning2.setVisibility(View.VISIBLE);
                    password_warning2.setText("올바른 패스워드를 입력해 주세요");
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
                    password_warning.setText("패스워드는 한 글자 이상 입력해 주세요");
                } else {
                    password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        input_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (input_password.getText().toString().trim().length() < 1) {
                    input_password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning2.setVisibility(View.VISIBLE);
                    password_warning2.setText("패스워드는 한 글자 이상 입력해 주세요");
                } else {
                    input_password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void keyboardDown(){
        imm.hideSoftInputFromWindow(notename.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(input_password.getWindowToken(), 0);
    }

    void inputPassword() {
        notename.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        frame.setVisibility(View.GONE);
        liner.setVisibility(View.GONE);
        namewarning.setVisibility(View.GONE);
        checkbox.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.VISIBLE);
        input_password.setVisibility(View.VISIBLE);
        result2.setVisibility(View.VISIBLE);
    }

    void resultPassword() {
        notename.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        frame.setVisibility(View.VISIBLE);
        liner.setVisibility(View.VISIBLE);
        namewarning.setVisibility(View.INVISIBLE);
        checkbox.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
        view3.setVisibility(View.GONE);
        input_password.setVisibility(View.GONE);
        result2.setVisibility(View.GONE);
    }

    void updateNote() {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> update = new HashMap<>();
        update.put("name", notename.getText().toString());
        update.put("color", String.valueOf(choice));
        if (checkbox.isChecked()) {
            update.put("password", password.getText().toString());
        } else {
            update.put("password", null);
        }
        FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("notes").child(name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> notes = new HashMap<>();
                        notes.put(notename.getText().toString(), key);
                        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("notes").updateChildren(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "폴더를 수정했습니다", Toast.LENGTH_SHORT).show();
                                intent.putExtra("recycle", true);
                                setResult(RESULT_OK, intent);
                                finish();
                                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                return;
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}

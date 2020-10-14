package com.e.note.alert;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.e.note.MemoActivity;
import com.e.note.R;
import com.e.note.model.MemoModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteMemoActivity extends AppCompatActivity {

    TextView txt, password_warning;
    Button delete, cancle, result;
    LinearLayout liner;
    EditText input_password;
    View view, view2;
    String key, name, date, pass, myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_memo);
        delete = (Button) findViewById(R.id.delete);
        cancle = (Button) findViewById(R.id.cancle);
        result = (Button) findViewById(R.id.result);
        input_password = (EditText) findViewById(R.id.input_password);
        liner = (LinearLayout) findViewById(R.id.liner);
        view = (View) findViewById(R.id.view);
        view2 = (View) findViewById(R.id.view2);
        txt = (TextView) findViewById(R.id.txt);
        password_warning = (TextView) findViewById(R.id.password_warning);
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        intent = getIntent();
        key = intent.getStringExtra("key");
        name = intent.getStringExtra("name");
        date = intent.getStringExtra("date");
        pass = intent.getStringExtra("pass");

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_password.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_password.getWindowToken(), 0);
                deleteMemo();
            }
        });

        if (pass != null) {
            inputPassword();
        }
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_password.getWindowToken(), 0);
                if (pass.equals(input_password.getText().toString())) {
                    input_password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                    resultPassword();
                } else {
                    input_password.setBackgroundResource(R.drawable.edittext_red);
                    password_warning.setVisibility(View.VISIBLE);
                    password_warning.setText("패스워드가 일치하지 않습니다");
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
                    password_warning.setVisibility(View.VISIBLE);
                    password_warning.setText("패스워드는 한 글자 이상 입력해 주세요");
                } else {
                    input_password.setBackgroundResource(R.drawable.edittext_login);
                    password_warning.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void inputPassword() {
        liner.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        txt.setVisibility(View.GONE);
        password_warning.setVisibility(View.INVISIBLE);
        result.setVisibility(View.VISIBLE);
        input_password.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
    }

    void resultPassword() {
        liner.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        txt.setVisibility(View.VISIBLE);
        password_warning.setVisibility(View.GONE);
        result.setVisibility(View.GONE);
        input_password.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
    }

    void deleteMemo() {
        FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).child("memos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MemoModel.Memo memo = snapshot.getValue(MemoModel.Memo.class);
                    if (String.valueOf(memo.date).equals(date)) {
                        String memokey = snapshot.getKey();
                        FirebaseDatabase.getInstance().getReference().child("notes").child(myUid).child(key).child("memos").child(memokey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                intent.putExtra("del", true);
                                setResult(RESULT_OK, intent);
                                Toast.makeText(getApplicationContext(), "메모를 삭제했습니다", Toast.LENGTH_SHORT).show();
                                Intent del = new Intent(getApplicationContext(), MemoActivity.class);
                                del.putExtra("name", name);
                                del.putExtra("key", key);
                                startActivity(del);
                                finish();
                                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                return;
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
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}

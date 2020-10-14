package com.e.note.alert;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.note.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SendMailActivity extends AppCompatActivity {
    TextView title, comment;
    Button serch_btn, cansle_btn;
    EditText input_email;
    TextView email_warning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_password);
        serch_btn = (Button) findViewById(R.id.serch_btn);
        cansle_btn = (Button) findViewById(R.id.cansle_btn);
        input_email = (EditText) findViewById(R.id.input_email);
        email_warning = (TextView) findViewById(R.id.email_warning);
        title = (TextView) findViewById(R.id.title);
        comment = (TextView) findViewById(R.id.comment);
        title.setText("메일 인증");
        serch_btn.setText("전송");
        comment.setVisibility(View.VISIBLE);
        input_email.setVisibility(View.GONE);
        email_warning.setVisibility(View.GONE);
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        serch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);

                final FirebaseUser user_check = FirebaseAuth.getInstance().getCurrentUser();
                user_check.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "인증 메일을 전송했습니다", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                        }
                    }
                });
            }
        });
        cansle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
                FirebaseAuth.getInstance().signOut();
                finish();
                overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
    }
}


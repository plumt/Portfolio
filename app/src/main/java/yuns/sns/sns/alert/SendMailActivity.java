package yuns.sns.sns.alert;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import yuns.sns.R;

public class SendMailActivity extends Activity {
    TextView title, comment;
    Button cansle_btn, serch_btn;
    EditText input_email;
    TextView text_warning;
    InputMethodManager imm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_password);
        cansle_btn = (Button) findViewById(R.id.cansle_btn);
        serch_btn = (Button) findViewById(R.id.serch_btn);
        input_email = (EditText) findViewById(R.id.input_email);
        title = (TextView) findViewById(R.id.title);
        text_warning = (TextView) findViewById(R.id.email_warning);
        comment = (TextView) findViewById(R.id.comment);
        title.setText("메일 인증");
        serch_btn.setText("전송");
        comment.setVisibility(View.VISIBLE);
        input_email.setVisibility(View.GONE);
        text_warning.setVisibility(View.GONE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        serch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
                final FirebaseUser user_check = FirebaseAuth.getInstance().getCurrentUser();
                user_check.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SendMailActivity.this, "메일을 전송하였습니다", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                            return;
                        }
                    }
                });
            }
        });
        cansle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

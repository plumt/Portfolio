package yuns.sns.sns.alert;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import yuns.sns.R;

public class SerchPasswordActivity extends Activity {

    Button cansle_btn, serch_btn;
    EditText input_email;
    TextView text_warning;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_password);
        cansle_btn = (Button) findViewById(R.id.cansle_btn);
        serch_btn = (Button) findViewById(R.id.serch_btn);
        input_email = (EditText) findViewById(R.id.input_email);
        text_warning = (TextView) findViewById(R.id.email_warning);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        input_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (input_email.getText().toString().equals("")) {
                    input_email.setBackgroundResource(R.drawable.edittext_login);
                    text_warning.setVisibility(View.INVISIBLE);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    input_email.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setText("이메일 형식으로 입력해 주세요");
                    text_warning.setVisibility(View.VISIBLE);
                } else if (input_email.getText().toString().length() < 10 || input_email.getText().toString().trim().length() < 10) {
                    input_email.setBackgroundResource(R.drawable.edittext_red);
                    text_warning.setVisibility(View.VISIBLE);
                } else {
                    input_email.setBackgroundResource(R.drawable.edittext_login);
                    text_warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        serch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
                final AutoCompleteTextView email = new AutoCompleteTextView(SerchPasswordActivity.this);
                email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                String emailAdress = input_email.getText().toString();
                if (!emailAdress.equals("")) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(emailAdress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "메일을 전송하였습니다", Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(R.anim.fab_open, R.anim.fab_close);
                                        return;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "올바른 메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "메일을 입력해 주세요", Toast.LENGTH_SHORT).show();

                }
            }
        });

        cansle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(input_email.getWindowToken(), 0);
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
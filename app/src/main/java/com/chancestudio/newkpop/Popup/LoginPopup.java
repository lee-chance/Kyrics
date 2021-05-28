package com.chancestudio.newkpop.Popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chancestudio.newkpop.AccountActivity;
import com.chancestudio.newkpop.R;
import com.chancestudio.newkpop.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPopup extends Activity {

    private FirebaseAuth mAuth;

    private EditText et_email, et_password;
//    private TextView tv_findPassword;
    Button btn_register;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_login);

        mAuth = FirebaseAuth.getInstance();
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.logging));

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

/*
        tv_findPassword = findViewById(R.id.tv_findPassword);
        tv_findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();

                if((email.length() == 0) && (!Patterns.EMAIL_ADDRESS.matcher(email).matches())){
                    et_email.setError("Invalid email");
                }

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginPopup.this, R.string.check_email, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(LoginPopup.this, R.string.failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginPopup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
*/

    }


    //확인 버튼 클릭
    public void mLogin(View v) {

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (email.length() > 0 || password.length() > 0) {
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginPopup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                                finish();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.failure_login,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginPopup.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), R.string.input_login, Toast.LENGTH_SHORT).show();
        }
    }

}

package com.chancestudio.newkpop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chancestudio.newkpop.Popup.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    EditText et_title, et_content;
    Button btn_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        btn_post = findViewById(R.id.btn_post);

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_content.getText().toString().length() != 0){
                    newFirebaseDB();
                }else{
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void newFirebaseDB() {

        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        String id = mStore.collection("noticeList").document().getId();

        String title = et_title.getText().toString();
        String content = et_content.getText().toString();

        Map<String, Object> post = new HashMap<>();
        post.put("id", id);
        post.put("title", title);
        post.put("content", content);

        mStore.collection("noticeList").document(id).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), LoadingDialog.class));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

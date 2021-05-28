package com.chancestudio.newkpop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    ImageButton back;
    Button btn_report;
    EditText et_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        back = findViewById(R.id.back);
        btn_report = findViewById(R.id.btn_report);
        et_report = findViewById(R.id.et_report);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String report = et_report.getText().toString();
                if(report.length() != 0){
                    FirebaseFirestore mStore = FirebaseFirestore.getInstance();

                    //데이터 전달하기
                    Map<String, Object> data = new HashMap<>();
                    data.put("reportMsg", report);
                    data.put("timestamp", FieldValue.serverTimestamp());

                    String id = mStore.collection("errorReports").document().getId();

                    mStore.collection("errorReports").document(id).set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                    Snackbar.make(v, "Message sent successfully", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(v, "Please write a Message down", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }
}

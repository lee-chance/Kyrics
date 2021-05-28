package com.chancestudio.newkpop.Popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chancestudio.newkpop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RequestPopup extends Activity {

    String title, singer, others;
    EditText et_title, et_singer, et_others;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_request);

        et_title = findViewById(R.id.et_title);
        et_singer = findViewById(R.id.et_singer);
        et_others = findViewById(R.id.et_others);

        RadioGroup radioGroup_request = findViewById(R.id.radioGroup_request);

        final LinearLayout radio_translate_request = findViewById(R.id.radio_translate_request);
        final LinearLayout radio_others_request = findViewById(R.id.radio_others_request);

        radioGroup_request.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.translate_request){
                    radio_translate_request.setVisibility(View.VISIBLE);
                    radio_others_request.setVisibility(View.GONE);
                } else {
                    radio_translate_request.setVisibility(View.GONE);
                    radio_others_request.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //확인 버튼 클릭
    public void sendEmail(View v) {

        title = et_title.getText().toString().trim();
        singer = et_singer.getText().toString().trim();
        others = et_others.getText().toString().trim();

        FirebaseFirestore mStore = FirebaseFirestore.getInstance();

        //데이터 전달하기
        Map<String, Object> data = new HashMap<>();
        data.put("requestNickname", user.getDisplayName());
        data.put("requestEmail", user.getEmail());
        data.put("requestTitle", title);
        data.put("requestSinger", singer);
        data.put("requestOthers", others);
        data.put("timestamp", FieldValue.serverTimestamp());

        String id = mStore.collection("requests").document().getId();

        mStore.collection("requests").document(id).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RequestPopup.this, "success", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RequestPopup.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });

        //액티비티(팝업) 닫기
        finish();
    }

}

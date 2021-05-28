package com.example.newkpop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InputActivity extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private EditText input_title, input_singer, input_youtube_address, input_lyrics, input_pronounce, input_translate;
    private Button input_button;
    private String id, title, singer, youtube_address, lyrics, pronounce, translate;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        input_title = findViewById(R.id.input_title);
        input_singer = findViewById(R.id.input_singer);
        input_youtube_address = findViewById(R.id.input_youtube_address);
        input_lyrics = findViewById(R.id.input_lyrics);
        input_pronounce = findViewById(R.id.input_pronounce);
        input_translate = findViewById(R.id.input_translate);
        input_button = findViewById(R.id.input_button);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);

        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = mStore.collection("songList").document().getId();

                Map<String, Object> post = new HashMap<>();
                post.put("id",id);
                post.put("title",input_title.getText().toString());
                post.put("singer",input_singer.getText().toString());
                post.put("youtube",input_youtube_address.getText().toString());
                post.put("lyrics",input_lyrics.getText().toString());
                post.put("pronounce",input_pronounce.getText().toString());
                post.put("translate",input_translate.getText().toString());

                mStore.collection("songList").document(id).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(InputActivity.this, "success",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InputActivity.this, "failure",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private class pagerAdapter extends FragmentStatePagerAdapter{

        public pagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new FragmentView1();
                case 1:
                    return new FragmentView2();
                case 2:
                    return new FragmentView3();
                case 3:
                    return new FragmentView4();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}

package com.chancestudio.newkpop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chancestudio.newkpop.Popup.LoginPopup;
import com.chancestudio.newkpop.Popup.RequestPopup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class ContentsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    kr.co.prnd.YouTubePlayerView showYoutube;
    kr.co.prnd.YouTubePlayerView pinYoutube;

    private String VideoId = "";

    ImageButton btn_favorite;
    SharedPreferences sharedPreferences;

    String title, singer, youtube_address;
    String lyrics, pronounce, translate;
    String[] arrayLyrics, arrayPronounce, arrayTranslate;

    TextView tv_title, tv_singer;
    TextView[] tv_lyrics, tv_pronounce, tv_translate;
    LinearLayout ll_mainContent, linearLayout;

    LinearLayout fab_ll_pronounce, fab_ll_translate, fab_ll_youtube, fab_ll_size;
    TextView fab_tv_pronounce, fab_tv_translate, fab_tv_youtube, fab_tv_size;
    FloatingActionButton fab_main, fab_pronounce, fab_translate, fab_youtube, fab_size;
    Animation fab_open, fab_close, fab_main_open, fab_main_close;

    boolean isFabOpen = false;
    boolean isPronounce = true;
    boolean isTranslate = true;
    boolean isSize = true;

    String stateYoutube = "show";

    Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        //네비게이션 드로어
        navigationDrawer();

        {
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_singer = (TextView) findViewById(R.id.tv_singer);
            ll_mainContent = (LinearLayout) findViewById(R.id.ll_mainContent);
            linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

            showYoutube = (kr.co.prnd.YouTubePlayerView) findViewById(R.id.showYoutube);

            btn_favorite = findViewById(R.id.Button_favorite);

            fab_ll_pronounce = (LinearLayout) findViewById(R.id.fab_ll_pronounce);
            fab_ll_translate = (LinearLayout) findViewById(R.id.fab_ll_translate);
            fab_ll_youtube = (LinearLayout) findViewById(R.id.fab_ll_youtube);
            fab_ll_size = (LinearLayout) findViewById(R.id.fab_ll_size);
            fab_tv_pronounce = (TextView) findViewById(R.id.fab_tv_pronounce);
            fab_tv_translate = (TextView) findViewById(R.id.fab_tv_translate);
            fab_tv_youtube = (TextView) findViewById(R.id.fab_tv_youtube);
            fab_tv_size = (TextView) findViewById(R.id.fab_tv_size);
            fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
            fab_pronounce = (FloatingActionButton) findViewById(R.id.fab_pronounce);
            fab_translate = (FloatingActionButton) findViewById(R.id.fab_translate);
            fab_youtube = (FloatingActionButton) findViewById(R.id.fab_youtube);
            fab_size = (FloatingActionButton) findViewById(R.id.fab_size);

            fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
            fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
            fab_main_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_open);
            fab_main_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_close);
        }



        //Ad mob
        MobileAds.initialize(this);
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);




        Intent intent = getIntent();
        String document_id = intent.getStringExtra("document_id");

        setup(document_id);
        clickFavorite(document_id);

    }


    public void setup(String id) {

        DocumentReference documentReference = mStore.collection("songList").document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        title = (String) document.getData().get("title");
                        singer = (String) document.getData().get("singer");
                        youtube_address = (String) document.getData().get("youtube_address");
                        lyrics = (String) document.getData().get("lyrics");
                        pronounce = (String) document.getData().get("pronounce");
                        translate = (String) document.getData().get("translate");

                        VideoId = youtube_address;

                        setText();

                    } else {
                        Log.d("Chance", "No such document");
                    }
                } else {
                    Log.d("Chance", "get failed with ", task.getException());
                }
            }
        });

        sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        isFavorite = sharedPreferences.getBoolean(id, false);

        if (isFavorite) {
            btn_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            btn_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

    }

    public void setText() {

        showYoutube.play(VideoId, null);

        tv_title.setText(title);
        tv_singer.setText(singer);

        arrayLyrics = lyrics.split("/ ");
        arrayPronounce = pronounce.split("/ ");
        arrayTranslate = translate.split("/ ");

        tv_lyrics = new TextView[arrayLyrics.length];
        tv_pronounce = new TextView[arrayPronounce.length];
        tv_translate = new TextView[arrayTranslate.length];

        for (int i = 0; i < arrayLyrics.length; i++) {

            tv_lyrics[i] = new TextView(getApplicationContext());
            LinearLayout.LayoutParams lyricsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lyricsParams.setMargins(40, 80, 40, 0);
            tv_lyrics[i].setLayoutParams(lyricsParams);
            tv_lyrics[i].setText(arrayLyrics[i]);
            tv_lyrics[i].setTextSize(16);
            tv_lyrics[i].setTypeface(null, Typeface.BOLD);
            ll_mainContent.addView(tv_lyrics[i]);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(40, 0, 40, 0);

            if(arrayPronounce[i].length() != 0){
                tv_pronounce[i] = new TextView(getApplicationContext());
                tv_pronounce[i].setLayoutParams(params);
                tv_pronounce[i].setText(arrayPronounce[i]);
                tv_pronounce[i].setTextSize(16);
                ll_mainContent.addView(tv_pronounce[i]);
            }

            if(arrayTranslate[i].length() != 0){
                tv_translate[i] = new TextView(getApplicationContext());
                tv_translate[i].setLayoutParams(params);
                tv_translate[i].setText(arrayTranslate[i]);
                tv_translate[i].setTextSize(16);
                ll_mainContent.addView(tv_translate[i]);
            }
        }

    }

    public void fabClick(View v) {
        switch (v.getId()) {
            case R.id.fab_main:
                toggleFab();
                break;
            case R.id.fab_size:
                toggleSize();
                break;
            case R.id.fab_pronounce:
                togglePronounce();
                break;
            case R.id.fab_translate:
                toggleTranslate();
                break;
            case R.id.fab_youtube:
                toggleYoutube();
                break;
        }
    }

    private void toggleFab() {
        if (isFabOpen) {
            fab_main.startAnimation(fab_main_close);

            fab_size.startAnimation(fab_close);
            fab_tv_size.startAnimation(fab_close);
            fab_size.setClickable(false);

            fab_pronounce.startAnimation(fab_close);
            fab_tv_pronounce.startAnimation(fab_close);
            fab_pronounce.setClickable(false);

            fab_translate.startAnimation(fab_close);
            fab_tv_translate.startAnimation(fab_close);
            fab_translate.setClickable(false);

            fab_youtube.startAnimation(fab_close);
            fab_tv_youtube.startAnimation(fab_close);
            fab_youtube.setClickable(false);

            isFabOpen = false;

        } else {
            fab_main.startAnimation(fab_main_open);

            fab_size.startAnimation(fab_open);
            fab_tv_size.startAnimation(fab_open);
            fab_size.setClickable(true);

            fab_pronounce.startAnimation(fab_open);
            fab_tv_pronounce.startAnimation(fab_open);
            fab_pronounce.setClickable(true);

            fab_translate.startAnimation(fab_open);
            fab_tv_translate.startAnimation(fab_open);
            fab_translate.setClickable(true);

            fab_youtube.startAnimation(fab_open);
            fab_tv_youtube.startAnimation(fab_open);
            fab_youtube.setClickable(true);

            isFabOpen = true;
        }
    }

    private void toggleSize() {
        if (isSize) {
            for (int i = 0; i < arrayLyrics.length; i++) {
                try {
                    tv_lyrics[i].setTextSize(22);
                    tv_pronounce[i].setTextSize(22);
                    tv_translate[i].setTextSize(22);
                } catch (Exception e){

                }
            }
            fab_tv_size.setText(R.string.smaller);
            isSize = false;
        } else {
            for (int i = 0; i < arrayLyrics.length; i++) {
                try {
                    tv_lyrics[i].setTextSize(16);
                    tv_pronounce[i].setTextSize(16);
                    tv_translate[i].setTextSize(16);
                } catch (Exception e){

                }
            }
            fab_tv_size.setText(R.string.bigger);
            isSize = true;
        }
    }

    private void togglePronounce() {
        if (isPronounce) {
            for (int i = 0; i < arrayPronounce.length; i++) {
                try {
                    tv_pronounce[i].setVisibility(View.INVISIBLE);
                } catch (Exception e){

                }
            }
            fab_pronounce.setImageResource(R.drawable.eye_off);
            isPronounce = false;
        } else {
            for (int i = 0; i < arrayPronounce.length; i++) {
                try {
                    tv_pronounce[i].setVisibility(View.VISIBLE);
                } catch (Exception e){

                }
            }
            fab_pronounce.setImageResource(R.drawable.eye_on);
            isPronounce = true;
        }
    }

    private void toggleTranslate() {
        if (isTranslate) {
            for (int i = 0; i < arrayTranslate.length; i++) {
                try {
                    tv_translate[i].setVisibility(View.INVISIBLE);
                } catch (Exception e){

                }
            }
            fab_translate.setImageResource(R.drawable.eye_off);
            isTranslate = false;
        } else {
            for (int i = 0; i < arrayTranslate.length; i++) {
                try {
                    tv_translate[i].setVisibility(View.VISIBLE);
                } catch (Exception e){

                }
            }
            fab_translate.setImageResource(R.drawable.eye_on);
            isTranslate = true;
        }
    }

    private void toggleYoutube() {

        if (stateYoutube.equals("show")) {

            ll_mainContent.removeView(showYoutube);
            pinYoutube = new kr.co.prnd.YouTubePlayerView(ContentsActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 10);
            pinYoutube.setLayoutParams(params);
            linearLayout.addView(pinYoutube, 0);
            pinYoutube.play(youtube_address, null);

            fab_tv_youtube.setText(R.string.pinYoutube);
            fab_youtube.setImageResource(R.drawable.pin_youtube);
            stateYoutube = "pin";

        } else if (stateYoutube.equals("pin")) {

            linearLayout.removeView(pinYoutube);

            fab_tv_youtube.setText(R.string.hideYoutube);
            fab_youtube.setImageResource(R.drawable.hide_youtube);
            stateYoutube = "hide";

        } else {

            showYoutube = new kr.co.prnd.YouTubePlayerView(ContentsActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 10);
            showYoutube.setLayoutParams(params);
            ll_mainContent.addView(showYoutube, 0);
            showYoutube.play(youtube_address, null);

            fab_tv_youtube.setText(R.string.showYoutube);
            fab_youtube.setImageResource(R.drawable.show_youtube);
            stateYoutube = "show";
        }

    }

    public void clickFavorite(final String document_id) {

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(document_id, false);
                    editor.apply();
                    btn_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Toast.makeText(getApplicationContext(), R.string.like_it_cancel, Toast.LENGTH_SHORT).show();

                    isFavorite = false;

                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(document_id, true);
                    editor.apply();
                    btn_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    Toast.makeText(getApplicationContext(), R.string.like_it, Toast.LENGTH_SHORT).show();

                    isFavorite = true;

                }

            }
        });
    }

    // DrawerLayout, Header (without MainActivity)
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void btnHeader(View view) {

        switch (view.getId()) {
            case R.id.Button_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.Button_search:
                Intent search = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(search);
                break;

            case R.id.Button_home:
                finish();
                break;

            case R.id.Button_favorite_storage:
                Intent favorite = new Intent(getApplicationContext(), FavoriteActivity.class);
                startActivity(favorite);
                break;

            default:
                break;
        }
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void navigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerView = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View nav_header = navigationView.getHeaderView(0);
        ImageView nav_header_img = nav_header.findViewById(R.id.nav_img);
        TextView nav_header_email = nav_header.findViewById(R.id.nav_email);
        TextView nav_header_nickname = nav_header.findViewById(R.id.nav_nickname);

        nav_header_img.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            nav_header_img.setClipToOutline(true);
        }

        if (user != null) {
            drawImage(nav_header_img);
            nav_header_email.setText(user.getEmail());
            nav_header_nickname.setText(user.getDisplayName());
            nav_header_nickname.setVisibility(View.VISIBLE);
        } else {
            nav_header_nickname.setVisibility(View.GONE);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.home:
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        finish();
                        break;

                    case R.id.account:
                        if (user == null) {
                            Intent login = new Intent(getApplicationContext(), LoginPopup.class);
                            startActivityForResult(login, 0);
                        } else {
                            Intent account = new Intent(getApplicationContext(), AccountActivity.class);
                            startActivity(account);
                            finish();
                        }
                        break;

                    case R.id.favorite:
                        Intent favorite = new Intent(getApplicationContext(), FavoriteActivity.class);
                        startActivity(favorite);
                        finish();
                        break;

                    case R.id.search:
                        Intent search = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(search);
                        finish();
                        break;

                    case R.id.notice:
                        Intent notice = new Intent(getApplicationContext(), NoticeActivity.class);
                        startActivity(notice);
                        finish();
                        break;


                    case R.id.request:
                        if (user == null) {
                            Intent login = new Intent(getApplicationContext(), LoginPopup.class);
                            startActivityForResult(login, 0);
                        } else {
                            Intent request = new Intent(getApplicationContext(), RequestPopup.class);
                            startActivity(request);
                        }
                        break;

                    case R.id.starred:
                        final String appPackageName = getPackageName();
                        try{
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;

                    case R.id.report:
                        Intent report = new Intent(getApplicationContext(), ReportActivity.class);
                        startActivity(report);
                        finish();
                        break;


                    case R.id.setting:
                        Intent setting = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(setting);
                        finish();
                        break;

                    case R.id.manager:
                        Intent manager = new Intent(getApplicationContext(), ManagerActivity.class);
                        startActivity(manager);
                        finish();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void drawImage(final ImageView img){
        String email = user.getEmail();
        String uid = user.getUid();
        String emailName = email.substring(0, email.lastIndexOf("@"));

        String fileName = uid + "_" + emailName + ".png";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("profile/" + fileName);

        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(img);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}

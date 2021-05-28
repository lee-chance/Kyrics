package com.chancestudio.newkpop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chancestudio.newkpop.Fragment.DeactivateRequestFragment;
import com.chancestudio.newkpop.Fragment.ErrorReportFragment;
import com.chancestudio.newkpop.Fragment.OthersRequestFragment;
import com.chancestudio.newkpop.Fragment.SongRequestFragment;
import com.chancestudio.newkpop.Popup.LoginPopup;
import com.chancestudio.newkpop.Popup.RequestPopup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ManagerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerView = findViewById(R.id.drawer);

        //네비게이션 드로어
        navigationDrawer();

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        SongRequestFragment songRequestFragment = new SongRequestFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.container, songRequestFragment);
        ft1.commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.nav_request_song:
                            SongRequestFragment songRequestFragment = new SongRequestFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.container, songRequestFragment);
                            ft1.commit();
                            return true;
                        case R.id.nav_request_other:
                            OthersRequestFragment othersRequestFragment = new OthersRequestFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.container, othersRequestFragment);
                            ft2.commit();
                            return true;
                        case R.id.nav_error:
                            ErrorReportFragment errorReportFragment = new ErrorReportFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.container, errorReportFragment);
                            ft3.commit();
                            return true;
                        case R.id.nav_delete_account:
                            DeactivateRequestFragment deactivateRequestFragment = new DeactivateRequestFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.container, deactivateRequestFragment);
                            ft4.commit();
                            return true;
                    }

                    return false;
                }
            };


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

    public void drawImage(final ImageView img) {
        String email = user.getEmail();
        String uid = user.getUid();
        String emailName = email.substring(0, email.lastIndexOf("@"));

        String fileName = uid + "_" + emailName + ".png";
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

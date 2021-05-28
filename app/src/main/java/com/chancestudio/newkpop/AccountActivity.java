package com.chancestudio.newkpop;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.chancestudio.newkpop.Popup.LoginPopup;
import com.chancestudio.newkpop.Popup.NicknamePopup;
import com.chancestudio.newkpop.Popup.RequestPopup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView iv_profile;
    private Uri filePath;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //네비게이션 드로어
        navigationDrawer();

        Intent register = getIntent();
        boolean registerOk = register.getBooleanExtra("registerOk",false);
        if(registerOk){
            finish();
            startActivity(register);
        }


        TextView tv_nickname = findViewById(R.id.tv_nickname);
        TextView tv_email = findViewById(R.id.tv_email);
        iv_profile = findViewById(R.id.iv_profile);

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });

        if (user == null) {

            findViewById(R.id.ll_logout).setVisibility(View.GONE);
            findViewById(R.id.btn_login).setVisibility(View.VISIBLE);

            Intent intent = new Intent(getApplicationContext(), LoginPopup.class);
            startActivityForResult(intent, 0);

        } else {

            findViewById(R.id.btn_login).setVisibility(View.GONE);
            findViewById(R.id.ll_logout).setVisibility(View.VISIBLE);

            // Name, email address, and profile photo Url
            String nickname = user.getDisplayName();
            String email = user.getEmail();

            if(user.getDisplayName()!=null){
                tv_nickname.setText(nickname);
            }
            tv_email.setText(email);

            drawImage(iv_profile);

        }

    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                startActivity(new Intent(getApplicationContext(), LoginPopup.class));

                break;

            case R.id.btn_edit:
                Intent nickname = new Intent(getApplicationContext(), NicknamePopup.class);
                startActivityForResult(nickname, 1);
                break;

            case R.id.btn_logout:
                mAuth.signOut();
                Intent logout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(logout);
                finish();
                break;

            case R.id.btn_leave:
                leave_dialog();
                break;
        }
    }

    void leave_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.leave_title);
        builder.setMessage(R.string.leave_main);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Map<String, Object> post = new HashMap<>();
                        post.put("leaveEmail", user.getEmail());
                        post.put("leaveUid", user.getUid());
                        post.put("timestamp", FieldValue.serverTimestamp());

                        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                        mStore.collection("leaveList").document(user.getEmail()).set(post)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), R.string.leave_msg, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                            }
                        });

                        finish();
                    }
                });
        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean isChanged = data.getBooleanExtra("isChanged",false);
            if(isChanged){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {

            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv_profile.setImageBitmap(bitmap);

                updateProfileImage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfileImage() {
        if (filePath != null) {
            progressDialog = new ProgressDialog(this);

            progressDialog.setTitle(R.string.uploading_title);
            progressDialog.show();

            String email = user.getEmail();
            String uid = user.getUid();
            String emailName = email.substring(0, email.lastIndexOf("@"));
            String fileName = uid + "_" + emailName + ".png";

            StorageReference storageReference = storage.getReferenceFromUrl("gs://newkpop-84c7f.appspot.com").child("profile/" + fileName);

            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int) progress) + "% " + getString(R.string.uploading_msg));
                }
            });
        }
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

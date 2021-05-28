package com.chancestudio.newkpop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chancestudio.newkpop.Model.ThumbnailList;
import com.chancestudio.newkpop.Popup.LoginPopup;
import com.chancestudio.newkpop.Popup.RequestPopup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FavoriteActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DrawerLayout drawerLayout;
    private View drawerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView rv_favorite;
    TextView tv_favorite;

    private FavoriteAdapter favoriteAdapter;
    List<ThumbnailList> favoriteList, list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //네비게이션 드로어
        navigationDrawer();

        //새로고침
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorSignature, R.color.colorSignature_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = getIntent();
                startActivity(intent);
                finish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rv_favorite = findViewById(R.id.rv_favorite);
        tv_favorite = findViewById(R.id.tv_favorite);

        list = new ArrayList<>();
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        mStore.collection("songList")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                    String id = (String) dc.getDocument().getData().get("id");
                    String title = (String) dc.getDocument().getData().get("title");
                    String singer = (String) dc.getDocument().getData().get("singer");
                    ThumbnailList data = new ThumbnailList(id, title, singer);

                    list.add(0, data);
                }

                favoriteList = new ArrayList<>();

                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                for(int i=0; i<list.size(); i++){
                    String document_id = list.get(i).getTitle() + "_" + list.get(i).getSinger();
                    boolean isFavorite = sharedPreferences.getBoolean(document_id, false);
                    if(isFavorite){
                        favoriteList.add(list.get(i));
                    }
                }

                if(favoriteList.size() == 0){
                    tv_favorite.setVisibility(View.VISIBLE);
                    rv_favorite.setVisibility(View.GONE);
                }else {
                    tv_favorite.setVisibility(View.GONE);
                    rv_favorite.setVisibility(View.VISIBLE);
                    favoriteAdapter = new FavoriteAdapter(favoriteList);
                    rv_favorite.setAdapter(favoriteAdapter);
                }

            }
        });
    }

    private class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

        private List<ThumbnailList> thumbnailLists;

        private FavoriteAdapter(List<ThumbnailList> thumbnailLists) {
            this.thumbnailLists = thumbnailLists;
        }


        @NonNull
        @Override
        public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FavoriteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final FavoriteViewHolder holder, int position) {
            ThumbnailList data = thumbnailLists.get(position);
            holder.item_title.setText(data.getTitle());
            holder.item_singer.setText(data.getSinger());

            String fileName = data.getTitle() + "_" + data.getSinger() + ".jpg";

            final FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("images/"+fileName);

            storageReference.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(holder.item_album_img);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return thumbnailLists.size();
        }

        class FavoriteViewHolder extends RecyclerView.ViewHolder{

            private ImageView item_album_img;
            private TextView item_title, item_singer;

            public FavoriteViewHolder(@NonNull View itemView) {
                super(itemView);

                item_title = itemView.findViewById(R.id.item_title);
                item_singer = itemView.findViewById(R.id.item_singer);
                item_album_img = itemView.findViewById(R.id.item_album_img);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        ThumbnailList data = thumbnailLists.get(position);

                        Intent intent = new Intent(getApplicationContext(), ContentsActivity.class);
                        intent.putExtra("document_id", data.getTitle() + "_" + data.getSinger());

                        startActivity(intent);
                    }
                });
            }
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

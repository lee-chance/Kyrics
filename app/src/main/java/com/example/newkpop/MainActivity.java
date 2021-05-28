package com.example.newkpop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    RecyclerView main_recyclerView;
    FloatingActionButton main_add_FAB;

    private MainAdapter mainAdapter;
    private List<SongList> mSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_recyclerView = findViewById(R.id.main_recyclerView);
        main_add_FAB = findViewById(R.id.main_add_FAB);

        main_add_FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                startActivity(intent);
            }
        });

        mSongList = new ArrayList<>();

        mStore.collection("songList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                    String id = (String) dc.getDocument().getData().get("id");
                    String title = (String) dc.getDocument().getData().get("title");
                    String singer = (String) dc.getDocument().getData().get("singer");
                    String youtube_address = (String) dc.getDocument().getData().get("youtube_address");
                    String lyrics = (String) dc.getDocument().getData().get("lyrics");
                    String pronounce = (String) dc.getDocument().getData().get("pronounce");
                    String translate = (String) dc.getDocument().getData().get("translate");
                    SongList data = new SongList(id, title, singer, youtube_address, lyrics, pronounce, translate);

                    mSongList.add(data);
                }

                mainAdapter = new MainAdapter(mSongList);
                main_recyclerView.setAdapter(mainAdapter);
            }
        });

    }

    //나중에 따롴 클래스
    private class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

        private List<SongList> mSongList;

        public MainAdapter(List<SongList> mSongList){
            this.mSongList = mSongList;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            SongList data = mSongList.get(position);
            holder.item_title.setText(data.getTitle());
            holder.item_singer.setText(data.getSinger());
        }

        @Override
        public int getItemCount() {
            return mSongList.size();
        }

        class MainViewHolder extends RecyclerView.ViewHolder{

            private TextView item_title, item_singer;
            private ImageView item_album_img;

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);

                item_title = itemView.findViewById(R.id.item_title);
                item_singer = itemView.findViewById(R.id.item_singer);
                item_album_img = itemView.findViewById(R.id.item_album_img);
            }
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration{

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}

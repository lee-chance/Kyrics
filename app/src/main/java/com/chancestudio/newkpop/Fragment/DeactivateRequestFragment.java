package com.chancestudio.newkpop.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chancestudio.newkpop.Adapter.ManagerAdapter;
import com.chancestudio.newkpop.Model.ManagerModelList;
import com.chancestudio.newkpop.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeactivateRequestFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv_deactivateRequest;
    ManagerAdapter managerAdapter;
    List<ManagerModelList> managerModelListList;


    public DeactivateRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deactivate_request, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                managerModelListList.clear();
                getdata();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rv_deactivateRequest = view.findViewById(R.id.rv_deactivateRequest);

        rv_deactivateRequest.setHasFixedSize(true);
        rv_deactivateRequest.setLayoutManager(new LinearLayoutManager(getActivity()));

        managerModelListList = new ArrayList<>();

        getdata();

        return view;
    }

    private void getdata() {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();

        mStore.collection("leaveList").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                            String email = (String) dc.getDocument().getData().get("leaveEmail");
                            String uid = (String) dc.getDocument().getData().get("leaveUid");

                            ManagerModelList data = new ManagerModelList(email, uid);

                            managerModelListList.add(data);


                        }

                        managerAdapter = new ManagerAdapter(getActivity(), managerModelListList);
                        rv_deactivateRequest.setAdapter(managerAdapter);
                    }
                });
    }

}

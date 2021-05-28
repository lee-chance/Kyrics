package com.chancestudio.newkpop.Fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class ErrorReportFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv_errorReport;
    ManagerAdapter managerAdapter;
    List<ManagerModelList> managerModelListList;


    public ErrorReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_error_report, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                managerModelListList.clear();
                getdata();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rv_errorReport = view.findViewById(R.id.rv_errorReport);

        rv_errorReport.setHasFixedSize(true);
        rv_errorReport.setLayoutManager(new LinearLayoutManager(getActivity()));

        managerModelListList = new ArrayList<>();

        getdata();

        return view;
    }

    private void getdata() {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();

        mStore.collection("errorReports").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {


                            String nickname = "Anonymous";
                            String msg = (String) dc.getDocument().getData().get("reportMsg");

                            ManagerModelList data = new ManagerModelList(nickname, msg);

                            managerModelListList.add(data);


                        }

                        managerAdapter = new ManagerAdapter(getActivity(), managerModelListList);
                        rv_errorReport.setAdapter(managerAdapter);
                    }
                });
    }

}

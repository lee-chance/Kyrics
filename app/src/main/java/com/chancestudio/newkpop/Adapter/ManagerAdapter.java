package com.chancestudio.newkpop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chancestudio.newkpop.Model.ManagerModelList;
import com.chancestudio.newkpop.R;

import java.util.List;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.MyHolder> {

    Context context;
    List<ManagerModelList> managerModelListList;

    public ManagerAdapter(Context context, List<ManagerModelList> managerModelListList) {
        this.context = context;
        this.managerModelListList = managerModelListList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manager, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String content = managerModelListList.get(position).getContent();
        String nickname = managerModelListList.get(position).getNickname();

        holder.tv_nickname.setText(nickname);
        holder.tv_content.setText(content);


    }

    @Override
    public int getItemCount() {
        return managerModelListList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tv_nickname, tv_content;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}

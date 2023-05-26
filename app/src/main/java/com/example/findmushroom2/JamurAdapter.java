package com.example.findmushroom2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class JamurAdapter extends RecyclerView.Adapter<JamurAdapter.MyViewHolder> {

    private Context context ;
    private List<Jamur> list = new ArrayList<>();
    private RecycleKlik lister;
    private LayoutInflater inflater;
    private Activity activity;

    public JamurAdapter(Context context, Activity activity, List<Jamur> list){
        this.context = context;
        this.activity = activity;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Jamur> items){
        list.clear();
        list.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Jamur data = list.get(position);

        holder.nama.setText(data.getNama());
        holder.namailmiah.setText(data.getIlmiah());
        holder.statusJamur.setText(data.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    interface RecycleKlik{
        void onKlikItem(View view, int posisi);
    }

    void setKlikListener(RecycleKlik recycleKlik){
        this.lister = recycleKlik;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama;
        TextView namailmiah;
        TextView statusJamur;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.subject);
            namailmiah = itemView.findViewById(R.id.namailmiah);
            statusJamur = itemView.findViewById(R.id.statusJamur);

            itemView.setTag(itemView);

            itemView.setOnClickListener(view -> {
                if(lister != null) lister.onKlikItem(view, getAdapterPosition());
            });
        }
    }

}
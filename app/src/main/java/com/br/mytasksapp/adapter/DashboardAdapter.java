package com.br.mytasksapp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.model.Task;
import com.br.mytasksapp.util.Util;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.RecyclerViewHolder> {

    private ArrayList<Task> tasks;

    private Context context;

    public DashboardAdapter(Context context, ArrayList<Task> tasks){
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_dash, viewGroup, false);
        return new DashboardAdapter.RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, @SuppressLint("RecyclerView") final int i) {
        final Task task = tasks.get(i);

        recyclerViewHolder.name.setText(Util.limitString(task.getName(), 7, "..."));
        recyclerViewHolder.date.setText(task.getDate());

        recyclerViewHolder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, task.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert(i, task.getName());
            }
        });
    }

    private void alert(final int position, String name){
        final AlertDialog alertDialog =  new AlertDialog.Builder(context)
                .setTitle("Atenção!")
                .setMessage("Deseja realmente excluir a tarefa " + name + "?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        removeAt(position);
                    }
                }).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray_app));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });

        alertDialog.show();
    }


    private void removeAt(int position){
        tasks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private GridLayout info;
        private AppCompatButton delete;

        private RecyclerViewHolder(View itemView) {
            super(itemView);

            info = itemView.findViewById(R.id.info);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            delete = itemView.findViewById(R.id.remove);
        }
    }
}
package com.br.mytasksapp.ui.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.rest.TaskHttp;
import com.br.mytasksapp.model.Setting;
import com.br.mytasksapp.model.Task;
import com.br.mytasksapp.util.Util;

import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.RecyclerViewHolder> implements Filterable {

    private ArrayList<Setting> settings;
    private ArrayList<Setting> settingsFiltered;
    private Context context;
    private TaskHttp taskHttp;

    public SettingsAdapter(Context context, ArrayList<Setting> settings){
        this.context = context;
        this.settings = settings;
        this.settingsFiltered = settings;
        taskHttp = new TaskHttp(context);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_settings, viewGroup, false);
        return new SettingsAdapter.RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        final Setting setting = settingsFiltered.get(i);

        recyclerViewHolder.action.setText(setting.getAction());

        recyclerViewHolder.toggle_action.setChecked(setting.isActive());

        recyclerViewHolder.toggle_action.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!setting.getAction().equalsIgnoreCase("notificacoes")){
                    if(isChecked){
                        taskHttp.setFCMToken(true);

                    }else{
                        taskHttp.setFCMToken(false);
                    }
                }
            }
        });

        if(getItemCount() - 1 == i && i > 1){
            recyclerViewHolder.divider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return settingsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = String.valueOf(constraint);
                if(search.isEmpty()){
                    settingsFiltered = settings;
                }else{
                    ArrayList<Setting> settingsSearch = new ArrayList<>();
                    for(Setting row: settingsFiltered){
                        if(row.getAction().toLowerCase().contains(search.toLowerCase())){
                            settingsSearch.add(row);
                        }
                    }
                    settingsFiltered = settingsSearch;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = settingsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                settingsFiltered = (ArrayList<Setting>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView action;
        private ToggleButton toggle_action;
        private View divider;

        private RecyclerViewHolder(View itemView) {
            super(itemView);

            action = itemView.findViewById(R.id.action);
            toggle_action = itemView.findViewById(R.id.toggle_action);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}

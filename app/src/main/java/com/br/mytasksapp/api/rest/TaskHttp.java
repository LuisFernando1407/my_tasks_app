package com.br.mytasksapp.api.rest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.br.mytasksapp.Constants;
import com.br.mytasksapp.api.BaseJsonHandler;
import com.br.mytasksapp.api.http.AuthenticatedHttp;
import com.br.mytasksapp.api.interfaces.OnTaskCompleted;
import com.br.mytasksapp.model.Task;
import com.br.mytasksapp.ui.activity.HomeActivity;
import com.br.mytasksapp.ui.activity.TaskActivity;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TaskHttp extends AuthenticatedHttp {
    private Context context;
    private OnTaskCompleted listener;

    public TaskHttp(Context context, OnTaskCompleted listener){
        this.context = context;
        this.listener = listener;

        setupClient();
    }

    public TaskHttp(Context context){
        this.context = context;

        setupClient();
    }

    public void getMyTasks(final SwipeRefreshLayout swipeRefreshLayout, boolean isAnimation) {
        client.get(Constants.API.TASKS, new BaseJsonHandler(context, isAnimation) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(swipeRefreshLayout != null){
                    swipeRefreshLayout.setRefreshing(false);
                }
                listener.taskCompleted(response);
            }
        });

    }

    public void registerTask(JSONObject params){
        StringEntity entity = null;

        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, Constants.API.TASKS, entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(context) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context, "Tarefa cadastrada com sucesso", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, HomeActivity.class));
                ((TaskActivity) context).finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(statusCode == 400){
                    try {
                        Util.alert(context, "Atenção!", errorResponse.getString("error"), null, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updateTask(String uid, JSONObject params){

        StringEntity entity = null;

        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.put(context, Constants.API.TASKS + "/" + uid, entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(context) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context, "Tarefa atualizada com sucesso", Toast.LENGTH_LONG).show();
                listener.taskCompleted(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(statusCode == 400){
                    try {
                        Util.alert(context, "Atenção!", errorResponse.getString("error"), null, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getTaskById(String uid) {
        client.get(Constants.API.TASKS + "/" + uid, new BaseJsonHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.taskCompleted(response);
            }
        });

    }

    public void getFCMTokenStatus(){
        client.get(Constants.API.TASKS + "/notification/status", new BaseJsonHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.taskCompleted(response);
            }
        });
    }

    public void setFCMToken(boolean isAccepted){
        JSONObject params = new JSONObject();
        StringEntity entity = null;

        try {
            params.put("fcm_token", Util.getApiFCMToken());
            params.put("is_accepted", isAccepted);
            Util.putPref("is_accepted", String.valueOf(isAccepted));

            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, Constants.API.TASKS + "/notification", entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Util.putPref("first_access", "no");
            }
        });
    }
}
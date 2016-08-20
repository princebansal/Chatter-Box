/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Entity.Fragments;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.princebansal.instavoice.Boundary.API.ConnectAPI;
import com.princebansal.instavoice.Boundary.Managers.DataHandler;
import com.princebansal.instavoice.Control.DatabaseContract;
import com.princebansal.instavoice.Entity.Actors.Insight;
import com.princebansal.instavoice.Entity.Actors.Message;
import com.princebansal.instavoice.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowFragment extends Fragment implements ConnectAPI.ServerAuthenticateListener, View.OnClickListener {


    private static final String TAG = FollowFragment.class.getSimpleName();

    public static FollowFragment newInstance() {

        Bundle args = new Bundle();

        FollowFragment fragment = new FollowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ConnectAPI connectAPI;
    private DataHandler dataHandler;

    private InsightsAdapter adapter;

    private RecyclerView recyclerView;
    private TextView noContentView;
    private ProgressBar progressBar;
    private View layout;
    private EditText blogIdEditText;
    private Button follow,unfollow;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setInit();
        setData();
    }

    private void init(View view) {
        layout = view;
        connectAPI = new ConnectAPI();
        dataHandler = DataHandler.getInstance(getActivity());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noContentView = (TextView) view.findViewById(R.id.no_content_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        follow=(Button)view.findViewById(R.id.follow);
        unfollow=(Button)view.findViewById(R.id.unfollow);
        blogIdEditText=(EditText)view.findViewById(R.id.blogger_id);
    }

    private void setInit() {
        follow.setOnClickListener(this);
        unfollow.setOnClickListener(this);
        connectAPI.setServerAuthenticateListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                connectAPI.refresh();
            }
        });

        getActivity().getContentResolver().registerContentObserver(DatabaseContract.CONTENT_URI_MESSAGE, false,
                new ContentObserver(new Handler()) {
                    @Override
                    public boolean deliverSelfNotifications() {
                        return super.deliverSelfNotifications();
                    }

                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        Log.i(TAG, "onChange: ");
                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        super.onChange(selfChange, uri);
                        Log.i(TAG, "onChange: "+uri.toString());
                        setData();
                    }
                });
    }

    private void setData() {
        if(dataHandler.isDatabaseBuild()){
            List<Insight> list = new ArrayList<>();
            adapter = new InsightsAdapter(getActivity(), list);
            recyclerView.setAdapter(adapter);
        }else{
            //connectAPI.refresh();
        }
    }

    @Override
    public void onRequestInitiated(int code) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestCompleted(int code) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        noContentView.setVisibility(View.GONE);
        if (dataHandler.isDatabaseBuild()) {
            List<Insight> list =new ArrayList<>();
            adapter = new InsightsAdapter(getActivity(), list);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestError(int code, String message) {

        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        noContentView.setVisibility(View.VISIBLE);
        showMessage(message);
    }

    @Override
    public void onRequestCompleted(int coversationFetchCode, List<Message> list) {

    }

    private void showMessage(String message) {
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.follow:
                if(!TextUtils.isEmpty(blogIdEditText.getText().toString())){
                    connectAPI.follow(getActivity(),blogIdEditText.getText().toString());
                }
                break;
            case R.id.unfollow:
                if(!TextUtils.isEmpty(blogIdEditText.getText().toString())){
                    connectAPI.unfollow(getActivity(),blogIdEditText.getText().toString());
                }
        }
    }


    public class InsightsAdapter extends RecyclerView.Adapter<InsightsAdapter.InsightViewHolder> {


        private Context context;
        private LayoutInflater inflater;
        private List contentList;


        public InsightsAdapter(Context context, List contentList) {
            this.context = context;
            this.contentList = contentList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public InsightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.fragment_insights_recycler_row, parent, false);
            InsightViewHolder holder = new InsightViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(InsightViewHolder holder, int position) {

            Insight insight = (Insight) contentList.get(position);
            holder.username.setText(insight.getUserName());
            holder.name.setText(insight.getName());
            holder.total.setText("Total: " + insight.getTotal());
            holder.favourite.setText(String.valueOf(insight.getFavourites()));
            Glide.with(FollowFragment.this).load(insight.getImageUrl())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.ic_tag_faces_black_24dp)
                    .into(holder.profileImage);
        }

        @Override
        public int getItemCount() {
            return contentList.size();
        }

        public class InsightViewHolder extends RecyclerView.ViewHolder {

            private TextView name, total, username, favourite;
            private CircleImageView profileImage;

            public InsightViewHolder(View itemView) {
                super(itemView);
                username = (TextView) itemView.findViewById(R.id.username);
                name = (TextView) itemView.findViewById(R.id.name);
                total = (TextView) itemView.findViewById(R.id.total);
                favourite = (TextView) itemView.findViewById(R.id.favourites);
                profileImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
            }
        }

    }



}

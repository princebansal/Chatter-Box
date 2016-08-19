/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Entity.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.princebansal.instavoice.Boundary.API.ConnectAPI;
import com.princebansal.instavoice.Boundary.Managers.DataHandler;
import com.princebansal.instavoice.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment implements ConnectAPI.ServerAuthenticateListener {

    private static final String TAG = ChatFragment.class.getSimpleName();

    public static ChatFragment newInstance() {

        Bundle args = new Bundle();

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ConnectAPI connectAPI;

    private DataHandler dataHandler;
    private ChatAdapter adapter;

    private Menu mMenu;

    private RecyclerView recyclerView;
    private TextView noContentView;
    private ProgressBar progressBar;
    private View layout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int favouriteposition = -1;
    private List newList = new ArrayList();
    private ArrayList<Integer> favMessageQueue;


    public boolean messageSelectedForFavourite = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
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
        favMessageQueue = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noContentView = (TextView) view.findViewById(R.id.no_content_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
    }

    private void setInit() {
        connectAPI.setServerAuthenticateListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                connectAPI.refresh();
            }

        });
    }

    private void setData() {
        if (dataHandler.isDatabaseBuild()) {
            updateData();
        } else {
            connectAPI.refresh();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.i(TAG, "onCreateOptionsMenu: fragment");
        menu.clear();
        inflater.inflate(R.menu.favourite_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.favourite_buttton).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.favourite_buttton) {
            if (!favMessageQueue.isEmpty()) {
                for (int i = 0; i < favMessageQueue.size(); i++) {
                    int position = favMessageQueue.get(i);
                    adapter.setAsFavourite(position,true);
                    ((Message) newList.get(position)).setFavourite(true);
                    dataHandler.markAsFavourite(newList.get(position),true);
                    favMessageQueue.remove(i);
                    i--;
                }
            }
        }else if(item.getItemId()==R.id.favourite_clear_buttton){
            if (!favMessageQueue.isEmpty()) {
                for (int i = 0; i < favMessageQueue.size(); i++) {
                    int position = favMessageQueue.get(i);
                    adapter.setAsFavourite(position,false);
                    ((Message) newList.get(position)).setFavourite(false);
                    dataHandler.markAsFavourite(newList.get(position),false);
                    favMessageQueue.remove(i);
                    i--;
                }
            }
        }

        if(mMenu!=null){
            mMenu.findItem(R.id.favourite_buttton).setVisible(false);
            mMenu.findItem(R.id.favourite_clear_buttton).setVisible(false);
        }
        return false;
    }

    @Override
    public void onRequestInitiated(int code) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestCompleted(int code) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        noContentView.setVisibility(View.GONE);
        if (dataHandler.isDatabaseBuild()) {
            updateData();
        }
    }

    @Override
    public void onRequestError(int code, String message) {

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        noContentView.setVisibility(View.VISIBLE);
        showMessage(message);
    }

    private void updateData() {
        List<Message> list = dataHandler.getMessagesList();


        ArrayList<Integer> type = new ArrayList<>();
        String datePrevious = "";
        for (int i = 0; i < list.size(); i++) {
            String date = Message.formatDateForView(getActivity(), list.get(i).getTime());
            if (i == 0) {
                newList.add(date);
                newList.add(list.get(i));
                type.add(ChatAdapter.DATE_TYPE);
                type.add(ChatAdapter.MESSAGE_TYPE);
            } else {
                if (date.equals(datePrevious)) {
                    newList.add(list.get(i));
                    type.add(ChatAdapter.MESSAGE_TYPE);
                } else {
                    newList.add(date);
                    newList.add(list.get(i));
                    type.add(ChatAdapter.DATE_TYPE);
                    type.add(ChatAdapter.MESSAGE_TYPE);
                }

            }
            datePrevious = date;
        }
        adapter = new ChatAdapter(getActivity(), type, newList);
        recyclerView.setAdapter(adapter);
    }

    private void showMessage(String message) {
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int MESSAGE_TYPE = 0;
        private static final int DATE_TYPE = 1;

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<Integer> itemType;
        private List contentList;


        public ChatAdapter(Context context, ArrayList<Integer> itemType, List contentList) {
            this.context = context;
            this.itemType = itemType;
            this.contentList = contentList;
            inflater = LayoutInflater.from(context);

        }

        public void setAsFavourite(int position,boolean isFavourite) {
            ((Message) contentList.get(position)).setFavourite(isFavourite);
            ((Message) contentList.get(position)).setSelected(false);
            notifyItemChanged(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view;
            RecyclerView.ViewHolder holder = null;
            if (viewType == ChatAdapter.MESSAGE_TYPE) {
                view = inflater.inflate(R.layout.fragment_chats_recycler_message, parent, false);
                holder = new MessageViewHolder(view);
            } else if (viewType == ChatAdapter.DATE_TYPE) {
                view = inflater.inflate(R.layout.fragment_chats_recycler_date, parent, false);
                holder = new DateViewHolder(view);
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


            if (getItemViewType(position) == ChatAdapter.MESSAGE_TYPE) {
                Message message = (Message) contentList.get(position);
                MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
                messageViewHolder.time.setText(Message.formatTimeForView(getActivity(), message.getTime()));
                messageViewHolder.messageBody.setText(message.getBody());
                if (message.isFavourite()) {
                    messageViewHolder.favIcon.setVisibility(View.VISIBLE);
                } else {
                    messageViewHolder.favIcon.setVisibility(View.GONE);
                }
                if (message.isSelected()) {
                    messageViewHolder.layout.setActivated(true);
                } else {
                    messageViewHolder.layout.setActivated(false);
                }
                if (position - 1 >= 0 && getItemViewType(position - 1) == MESSAGE_TYPE &&
                        ((Message) contentList.get(position - 1)).getUsername().equals(message.getUsername())) {
                    messageViewHolder.header.setVisibility(View.GONE);
                } else {
                    messageViewHolder.header.setVisibility(View.VISIBLE);
                    messageViewHolder.username.setText(message.getUsername());
                    Glide.with(ChatFragment.this).load(message.getImageUrl())
                            .asBitmap()
                            .centerCrop()
                            .placeholder(R.drawable.ic_tag_faces_black_24dp)
                            .into(messageViewHolder.profileImage);
                }
            } else if (getItemViewType(position) == ChatAdapter.DATE_TYPE) {
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                String date = (String) contentList.get(position);
                dateViewHolder.date.setText(date);
            }
        }

        @Override
        public int getItemCount() {
            return contentList.size();
        }

        @Override
        public int getItemViewType(int position) {

            return itemType.get(position);
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

            private TextView messageBody, time, username;
            private CircleImageView profileImage;
            private LinearLayout header, layout;
            private ImageView favIcon;
            private GestureDetector gestureDetector;

            public MessageViewHolder(View itemView) {
                super(itemView);
                messageBody = (TextView) itemView.findViewById(R.id.message);
                time = (TextView) itemView.findViewById(R.id.time);
                username = (TextView) itemView.findViewById(R.id.name);
                profileImage = (CircleImageView) itemView.findViewById(R.id.image);
                header = (LinearLayout) itemView.findViewById(R.id.header);
                layout = (LinearLayout) itemView.findViewById(R.id.layout);
                favIcon = (ImageView) itemView.findViewById(R.id.fav_icon);
                layout.setOnTouchListener(this);
                gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                        Log.i(TAG, "onLongPress: ");
                        if(!layout.isActivated()&&!favMessageQueue.contains(getAdapterPosition())) {
                            layout.setActivated(true);
                            favouriteposition = getAdapterPosition();
                            mMenu.findItem(R.id.favourite_buttton).setVisible(true);
                            mMenu.findItem(R.id.favourite_clear_buttton).setVisible(true);
                            favMessageQueue.add(getAdapterPosition());
                            ((Message) contentList.get(getAdapterPosition())).setSelected(true);
                        }
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        Log.i(TAG, "onSingleTapUp: ");
                        if (!layout.isActivated()) {
                            if (favMessageQueue.size() > 0) {
                                layout.setActivated(true);
                                favouriteposition = getAdapterPosition();
                                mMenu.findItem(R.id.favourite_buttton).setVisible(true);
                                mMenu.findItem(R.id.favourite_clear_buttton).setVisible(true);
                                favMessageQueue.add(getAdapterPosition());
                                ((Message) contentList.get(getAdapterPosition())).setSelected(true);
                            }
                        } else {
                            if (favMessageQueue.size() > 1) {
                                layout.setActivated(false);
                                if (favMessageQueue.contains(getAdapterPosition()))
                                    favMessageQueue.remove(new Integer(getAdapterPosition()));
                                ((Message) contentList.get(getAdapterPosition())).setSelected(false);
                            } else {
                                layout.setActivated(false);
                                mMenu.findItem(R.id.favourite_buttton).setVisible(false);
                                mMenu.findItem(R.id.favourite_clear_buttton).setVisible(false);
                                if (favMessageQueue.contains(getAdapterPosition()))
                                    favMessageQueue.remove(new Integer(getAdapterPosition()));
                                ((Message) contentList.get(getAdapterPosition())).setSelected(false);
                            }
                        }

                        return true;
                    }
                });
            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return gestureDetector.onTouchEvent(motionEvent);
            }
        }

        public class DateViewHolder extends RecyclerView.ViewHolder {

            private TextView date;

            public DateViewHolder(View itemView) {
                super(itemView);
                date = (TextView) itemView.findViewById(R.id.date);
            }
        }
    }

}

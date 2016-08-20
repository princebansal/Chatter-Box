/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Entity.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.bumptech.glide.Glide;
import com.princebansal.instavoice.API.UploadAudio;
import com.princebansal.instavoice.Boundary.API.ConnectAPI;
import com.princebansal.instavoice.Boundary.Managers.DataHandler;
import com.princebansal.instavoice.Entity.Activities.MainActivity;
import com.princebansal.instavoice.Entity.Actors.Message;
import com.princebansal.instavoice.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment implements ConnectAPI.ServerAuthenticateListener, View.OnClickListener {

    private static final String TAG = ChatFragment.class.getSimpleName();
    private boolean isRecording=false;
    private long timeSpent=0;
    private String outputFile="";

    public static ChatFragment newInstance() {

        Bundle args = new Bundle();

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ConnectAPI connectAPI;

    private DataHandler dataHandler;
    private ChatAdapter adapter;

    private MediaRecorder mediaRecorder;

    private Menu mMenu;

    private RecyclerView recyclerView;
    private TextView noContentView,timer;
    private ProgressBar progressBar;
    private View layout;
    private ImageView cancelButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton recordButton;

    private int favouriteposition = -1;
    private List newList = new ArrayList();
    private ArrayList<Integer> favMessageQueue;



    public boolean messageSelectedForFavourite = false;
    private ArrayList<Integer> type = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
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
        timer = (TextView) view.findViewById(R.id.timer);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recordButton=(FloatingActionButton)view.findViewById(R.id.record_fab);
        cancelButton=(ImageView)view.findViewById(R.id.cancel);

        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
    }

    private void setInit() {
        connectAPI.setServerAuthenticateListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                connectAPI.fetchMessages();
            }

        });
        recordButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void setData() {
        if (dataHandler.isDatabaseBuild()) {
            updateData();
        } else {
            connectAPI.fetchMessages();
        }
    }

    /*@Override
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
*/
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

    @Override
    public void onRequestCompleted(int coversationFetchCode, List<Message> list) {
        newList=list;
        type=new ArrayList<>();
        updateData();
    }

    private void updateData() {
        List<Message> list = dataHandler.getMessagesList();


        String datePrevious = "";
        for (int i = 0; i < list.size(); i++) {
            String date = Message.formatDateForView(getActivity(), list.get(i).getMessageDate());
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.record_fab:
                if(isRecording){
                    timer.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    stopRecording();
                    addToList();
                }else{
                    timer.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                    timer.setText("00:00");
                    startRecording();
                }
                break;
            case R.id.cancel:
                timer.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                stopRecording();
        }
    }

    private void addToList() {
        Message message=new Message();
        message.setAnnotation("Recording");
        message.setBloggerDisplayName("prince");
        message.setDuration(timeSpent*1000);
        message.setFromBloggerId(123456789);
        message.setMediaFormat(".wav");
        message.setMessageBase64(false);
        message.setMessageContentType("a");
        message.setMessageDate(System.currentTimeMillis());
        message.setOutputFile(outputFile);
        newList.add(message);
        type.add(ChatAdapter.MESSAGE_TYPE);
        adapter.setContentList(newList);
        adapter.setItemType(type);
        adapter.notifyItemChanged(newList.size()-1);
        UploadAudio uploadAudio=new UploadAudio(getActivity(),message.getOutputFile(),getActivity());
        uploadAudio.execute();

    }

    private void startRecording() {
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Chatter");
        file.mkdirs();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chatter/" + "recording+"+System.currentTimeMillis()+".wav";;
        isRecording=true;
        recordButton.setImageResource(R.drawable.ic_stop_black_24dp);
        Thread stopWatch=new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while(isRecording){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    i=i+1;
                    timeSpent=i;
                    final int c=i;
                    final int sec=c%60;
                    final int min=c/60;
                    ChatFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.setText((min<10?"0"+min:min)+":"+(sec<10?"0"+sec:sec));
                        }
                    });
                }
            }
        });
        mediaRecorder.setOutputFile(outputFile);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            stopWatch.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        isRecording=false;
        recordButton.setImageResource(R.drawable.ic_mic_black_24dp);
        mediaRecorder.stop();
        mediaRecorder.release();
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

        public void setItemType(ArrayList<Integer> itemType) {
            this.itemType = itemType;
        }

        public void setContentList(List contentList) {
            this.contentList = contentList;
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
                messageViewHolder.time.setText(Message.formatTimeForView(getActivity(), message.getMessageDate()));
                messageViewHolder.messageBody.setText(message.getAnnotation());
                if (position - 1 >= 0 && getItemViewType(position - 1) == MESSAGE_TYPE &&
                        ((Message) contentList.get(position - 1)).getFromBloggerId()==message.getFromBloggerId()) {
                    messageViewHolder.header.setVisibility(View.GONE);
                } else {
                    messageViewHolder.header.setVisibility(View.VISIBLE);
                    messageViewHolder.username.setText(message.getBloggerDisplayName());
                    Glide.with(ChatFragment.this).load("")
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

        public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView messageBody, time, username;
            private CircleImageView profileImage;
            private LinearLayout header, layout;
            private ImageView playIcon;
            private GestureDetector gestureDetector;

            public MessageViewHolder(View itemView) {
                super(itemView);
                messageBody = (TextView) itemView.findViewById(R.id.message);
                time = (TextView) itemView.findViewById(R.id.time);
                username = (TextView) itemView.findViewById(R.id.name);
                profileImage = (CircleImageView) itemView.findViewById(R.id.image);
                header = (LinearLayout) itemView.findViewById(R.id.header);
                layout = (LinearLayout) itemView.findViewById(R.id.layout);
                playIcon = (ImageView) itemView.findViewById(R.id.fav_icon);
                playIcon.setOnClickListener(this);
                //layout.setOnTouchListener(this);
                /*gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
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
                });*/
            }

            @Override
            public void onClick(View view) {
                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(((Message)contentList.get(getAdapterPosition())).getOutputFile());
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
            }

            /*@Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return gestureDetector.onTouchEvent(motionEvent);
            }*/
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

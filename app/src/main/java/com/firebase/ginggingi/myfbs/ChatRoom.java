package com.firebase.ginggingi.myfbs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ginggingi.myfbs.Adapters.ChatListAdapter;
import com.firebase.ginggingi.myfbs.model.ChatData;
import com.firebase.ginggingi.myfbs.model.ChatRoomUserData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *채팅방을 이루는 코드
 */

public class ChatRoom extends AppCompatActivity implements OnClickListener {

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference chatRef;

    private InputMethodManager imm;

    private RecyclerView crv;
    private ChatListAdapter adapter;
    private RecyclerView.LayoutManager lm;

    private ChatRoomUserData nowUsers;

    private String uid;
    private String photoRoute;
    private String ChatRoomCode;

    private LinearLayout bg,listlayout;

    private TextView nowPeople;
    private EditText chatedit;
    private ImageView sendBtn;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        init();

        GetChats();
    }

    private void init(){
        intent = getIntent();
        ChatRoomCode = intent.getStringExtra("ChatCode");
        uid = intent.getStringExtra("Userid");
        photoRoute = intent.getStringExtra("UserPhoto");

        chatRef = mRef.child("Chat").child(ChatRoomCode);

        listlayout = (LinearLayout) findViewById(R.id.list_Linear);
        bg = (LinearLayout) findViewById(R.id.bg);

        nowPeople = (TextView) findViewById(R.id.nowPeople);
        chatedit = (EditText) findViewById(R.id.GetTexts);
        sendBtn = (ImageButton) findViewById(R.id.sendBtn);

        crv = (RecyclerView) findViewById(R.id.ChatList);

        lm = new LinearLayoutManager(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        listlayout.setOnClickListener(this);
        crv.setOnClickListener(this);
        bg.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

/*        ShowChatRoomPeople();
        addMeToChatRoom();*/
    }

    /*현재 접속자들 받아오는코드(아직 미구현)*/
//    private void addMeToChatRoom() {
//        mRef.child("nowRoom").child(ChatRoomCode).push().setValue(uid.toString());
//
//    }

//    private void ShowChatRoomPeople() {
//        try {
//            mRef.child("nowRoom").child(ChatRoomCode).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    int i = 0;
//                    for (DataSnapshot snapshot ){
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.e("Logined", "user " + uid + " 가 널값임");
//                    Toast.makeText(ChatRoom.this, "에러: 채팅로그를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            });
//            Thread.sleep(1000);
//            nowPeople.setVisibility(View.GONE);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.list_Linear:
            case R.id.ChatList:
            case R.id.bg :
                imm.hideSoftInputFromWindow(chatedit.getWindowToken(),0);
                break;
            case R.id.sendBtn :
                final String chatmsg = chatedit.getText().toString();
                chatedit.setText(null);
                sendData(chatmsg);
                break;
        }
    }

    private void sendData(String chatmsg) {

        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("hh:mm");

        String now = sdf.format(date);

        if (chatmsg != null && !chatmsg.isEmpty()) {
            ChatData CD = new ChatData(uid, chatmsg, photoRoute, now,ChatData.TYPE_TEXT_OTHER);
            mRef.child("Chat").child(ChatRoomCode).push().setValue(CD);
        }
//        Toast.makeText(this, chatmsg, Toast.LENGTH_SHORT).show();
    }

    private void GetChats(){
        adapter = new ChatListAdapter();

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData CData = dataSnapshot.getValue(ChatData.class);

                CData.setType(ChkIsMe(CData.getUserName(), uid));

                adapter.addItem(CData.getUserName(),CData.getMessages(),CData.getPictureURI(),CData.getNowTime(),CData.getType());
                adapter.notifyDataSetChanged();

                if (adapter.getItemCount() > 0)
                    crv.scrollToPosition(adapter.getItemCount() -1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatData CData = dataSnapshot.getValue(ChatData.class);

                int IsMe = ChkIsMe(CData.getUserName(), uid);

                adapter.addItem(CData.getUserName(),CData.getMessages(),CData.getPictureURI(),CData.getNowTime(),IsMe);
                adapter.notifyDataSetChanged();

                if (adapter.getItemCount() > 0)
                    crv.scrollToPosition(adapter.getItemCount() -1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //좀있다구현ㄱㄱㄱㄱ
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Logined", "user " + uid + " 가 널값임");
                Toast.makeText(ChatRoom.this, "에러: 채팅로그를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();

            }

        });
//        SnapHelper sHelper = new LinearSnapHelper();
        crv.setLayoutManager(lm);
        crv.setAdapter(adapter);
        /*예전 채팅받아오는코드*/
        //        sHelper.attachToRecyclerView(crv);

        //        chatRef.addValueEventListener(new ValueEventListener() {
        //
        //            @Override
        //            public void onDataChange(DataSnapshot dataSnapshot) {
        //                adapter = new ChatListAdapter();
        //                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
        //
        //                    ChatData CData = snapshot.getValue(ChatData.class);
        //
        //                    boolean IsMe = ChkIsMe(CData.getUserName(), uid);
        ////                    if (ChkIsMe(CData.getUserName(), uid)){
        ////                        Toast.makeText(ChatRoom.this,
        ////                                "CData.getUserName() is equal\n" +
        ////                                        CData.getMessages(), Toast.LENGTH_SHORT).show();
        ////                    }
        //                    adapter.addItem(CData.getUserName(),CData.getMessages(),CData.getPictureURI(),CData.getNowTime(),IsMe);
        //                }
        //
        //                adapter.notifyDataSetChanged();
        //                Log.d("Rec", adapter.getItemCount()+"");
        //                crv.setLayoutManager(lm);
        //                crv.setAdapter(adapter);
        //                if (adapter.getItemCount() > 0)
        //                crv.scrollToPosition(adapter.getItemCount() -1);
        ////                Toast.makeText(ChatRoom.this, adapter.getItemCount()+"", Toast.LENGTH_SHORT).show();
        //            }
        //
        //            @Override
        //            public void onCancelled(DatabaseError databaseError) {
        //                Log.e("Logined", "user " + uid + " 가 널값임");
        //                Toast.makeText(ChatRoom.this, "에러: 채팅로그를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
        //            }
        //        });
    }

    private int ChkIsMe(String userName, String uid) {
        if (userName.equals(uid)){
            return ChatData.TYPE_TEXT_ME;
        }else {
            return ChatData.TYPE_TEXT_OTHER;
        }
    }
}


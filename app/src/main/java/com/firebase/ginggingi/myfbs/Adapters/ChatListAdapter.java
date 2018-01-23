package com.firebase.ginggingi.myfbs.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ginggingi.myfbs.R;
import com.firebase.ginggingi.myfbs.model.ChatData;
import com.firebase.ginggingi.myfbs.model.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 *채팅방들을 가져와서 Recyclerview에 연결해주는 adapter
 */

public class ChatListAdapter extends RecyclerView.Adapter {

    private ArrayList<ChatData> listViewItemList = new ArrayList<>();

//    private ChatData Cdata = new ChatData();
//    private ChatViewHolder prehoder;

    final long ONE_MEAGBYTE = (1024*1024);
    final private String ImgPath = "data/data/com.firebase.ginggingi.myfbs/files/";

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private StorageReference sRef = FirebaseStorage.getInstance().getReference();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case ChatData.TYPE_TEXT_ME:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_list_me, parent, false);
                return new TextTypeViewHolder_Me(view);
            case ChatData.TYPE_TEXT_OTHER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_list_other, parent, false);
                return new TexTTypeViewHolder_Other(view);
        }
        return null;
    }
    //아이템 뷰타입 가져오는코드
    @Override
    public int getItemViewType(int position){
        switch (listViewItemList.get(position).getType()){
            case 0:
                return ChatData.TYPE_TEXT_ME;
            case 1:
                return ChatData.TYPE_TEXT_OTHER;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ChatData CData = listViewItemList.get(position);

        if (CData != null) {
            switch (CData.getType()) {
                case ChatData.TYPE_TEXT_ME:
                    ((TextTypeViewHolder_Me) holder).Message.setText(listViewItemList.get(position).getMessages());
                    ((TextTypeViewHolder_Me) holder).Time.setText(listViewItemList.get(position).getNowTime());

                    break;
                case ChatData.TYPE_TEXT_OTHER:

                    final StorageReference getRef = sRef.child("images/"+listViewItemList.get(position).getPictureURI());

                    ((TexTTypeViewHolder_Other) holder).Message.setText(listViewItemList.get(position).getMessages());
                    ((TexTTypeViewHolder_Other) holder).Time.setText(listViewItemList.get(position).getNowTime());

                    getRef.getBytes(ONE_MEAGBYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

//                       bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                            ((TexTTypeViewHolder_Other) holder).Photo.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("DownloadUnSuccessFul :", e.getMessage());
                        }
                    });

                    //holder.UserName.setText↓
                    mRef.child("users").child(listViewItemList.get(position).getUserName()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserData user = dataSnapshot.getValue(UserData.class);

                            if (user == null) {
                                Log.e("Logined", "user 가 널값임");
                            } else {
                                ((TexTTypeViewHolder_Other) holder).Name.setText(user.getUsername());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Logind", "getUser:onCanceled", databaseError.toException());
                        }
                    });

            break;

            }
        }
    }

    /*
    예전 뷰바인드 코드
    */

//    @Override
//    public void onBindViewHolder(final ChatViewHolder holder, int position) {
//
//        final StorageReference getRef = FirebaseStorage.getInstance().getReference().child("images/"+listViewItemList.get(position).getPictureURI());
//        if (IsMe.get(position)){
//            holder.other.setVisibility(View.GONE);
//            holder.IsMe.setVisibility(View.VISIBLE);
//            holder.myChat.setText(listViewItemList.get(position).getMessages());
//            holder.myTime.setText(listViewItemList.get(position).getNowTime());
//        }else{
//            holder.IsMe.setVisibility(View.GONE);
//            holder.other.setVisibility(View.VISIBLE);
//            holder.Chat.setText(listViewItemList.get(position).getMessages());
//            holder.Time.setText(listViewItemList.get(position).getNowTime());

            //holder.UserPhoto.setImage↓
//                getRef.getBytes(ONE_MEAGBYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
////                       bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//
//                        holder.UserPhoto.setImageBitmap(bitmap);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("DownloadUnSuccessFul :", e.getMessage());
//                    }
//                });
//            }
//
//            //holder.UserName.setText↓
//            mRef.child("users").child(listViewItemList.get(position).getUserName()).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    UserData user = dataSnapshot.getValue(UserData.class);
//
//                    if (user == null) {
//                        Log.e("Logined", "user 가 널값임");
//                    }else{
//                        holder.UserName.setText(user.getUsername());
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.e("Logind", "getUser:onCanceled", databaseError.toException());
//                }
//            });

//        }

    @Override
    public int getItemCount() {
        return (listViewItemList != null) ? listViewItemList.size() : 0;
    }

    public void addItem (String UserName, String Messages,  String PictureURI, String nowTime, int Type){
        ChatData item = new ChatData(UserName, Messages, PictureURI, nowTime, Type);
        listViewItemList.add(item);
    }

    public class TextTypeViewHolder_Me extends RecyclerView.ViewHolder{

        TextView Message;
        TextView Time;

        public TextTypeViewHolder_Me(View itemView) {
            super(itemView);

            this.Message = (TextView) itemView.findViewById(R.id.mychat);
            this.Time = (TextView) itemView.findViewById(R.id.myTime);
        }
    }

    public class TexTTypeViewHolder_Other extends RecyclerView.ViewHolder{

        ImageView Photo;
        TextView Name;
        TextView Message;
        TextView Time;

        public TexTTypeViewHolder_Other(View itemView) {
            super(itemView);
            Photo = (ImageView) itemView.findViewById(R.id.UserPhoto);
            Name = (TextView) itemView.findViewById(R.id.UserName);
            Message = (TextView) itemView.findViewById(R.id.chat);
            Time = (TextView) itemView.findViewById(R.id.Time);
        }
    }
}



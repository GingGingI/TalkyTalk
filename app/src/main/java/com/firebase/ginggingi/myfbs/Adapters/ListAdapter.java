package com.firebase.ginggingi.myfbs.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ginggingi.myfbs.Logined;
import com.firebase.ginggingi.myfbs.R;
import com.firebase.ginggingi.myfbs.model.ChatList;

import java.util.ArrayList;

/**
 *채팅들을 가져와서 Recyclerview에 연결해주는 adapter
 *나중에 개편예정
 */

public class ListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<ChatList> listViewItemList = new ArrayList<>();
    private Context mContext;
    private Logined log;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        log = (Logined) mContext;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.Title.setText(listViewItemList.get(position).getRoomname());
        holder.Submatt.setText(listViewItemList.get(position).getsub_matt());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i("Recycler", "Clicked : "+position + " & "+ listViewItemList.get(position).getCid());
                log.toChatActivity(listViewItemList.get(position).getCid());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("채팅방지우기");
                builder.setMessage("채팅방을 지우시겠습니까?");
                builder.setPositiveButton("지우기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            log.DelChatRoom(listViewItemList.get(position).getCid(), listViewItemList.get(position).getUid());
                    } // onClick
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(mContext, "ㅇ...ㅇㅋ;;;", Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "취소", Toast.LENGTH_SHORT).show();
                    } // onClick
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (listViewItemList != null) ? listViewItemList.size() : 0;
    }

    public void addItem (String Roomname, String sub_matt, String cid, String uid){
        ChatList item = new ChatList(Roomname, sub_matt, cid, uid);

        listViewItemList.add(item);
    }
}
class ViewHolder extends RecyclerView.ViewHolder{

    public TextView Title;
    public TextView Submatt;

    public ViewHolder(View itemView) {
        super(itemView);
        Title = (TextView)itemView.findViewById(R.id.Title);
        Submatt = (TextView)itemView.findViewById(R.id.Submatt);
    }
}
//TokyTalk
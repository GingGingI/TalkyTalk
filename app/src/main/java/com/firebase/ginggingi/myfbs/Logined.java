package com.firebase.ginggingi.myfbs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ginggingi.myfbs.Adapters.ListAdapter;
import com.firebase.ginggingi.myfbs.model.ChatData;
import com.firebase.ginggingi.myfbs.model.ChatList;
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
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListenerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *로그인 되었을시[사실상 메인]
 */

public class Logined extends LoginBase implements View.OnClickListener{

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference chatRef;
    private StorageReference sRef = FirebaseStorage.getInstance().getReference();
    private ImageView profilephoto;
    private FloatingActionButton fab;
    private InputMethodManager imm;

    private BoomMenuButton bmb;

    private TextView testView;

    private EditText DialRoomname,Sub_matters;

    private RecyclerView rv;
    private ListAdapter adapter;
    private RecyclerView.LayoutManager lm;

    private String uid;
    private String ImgPath;
    private String photoRoute;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logined_activity);

        init();
        bmbinit();

        showProgressDialog();
        changeProgressMessage("닉네임을가져오는중...");
        setUsername();
        changeProgressMessage("채팅방들을 긁어오는중...");
        GetChats();
    }

    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImgPath = "data/data/com.firebase.ginggingi.myfbs/files/";
        
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(true);

        testView = (TextView) findViewById(R.id.testText);
        profilephoto = (ImageView) findViewById(R.id.ProfileImage);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        fab.setOnClickListener(this);

        lm = new LinearLayoutManager(this);
        rv = (RecyclerView) findViewById(R.id.ChatList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            rv.setHasFixedSize(true);
        }

        chatRef = mRef.child("ChatList");
    }

    private void bmbinit() {
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.addBuilder(getHamButtonBuilder("정보").normalColor(Color.rgb(110,222,110)).pieceColor(Color.rgb(222,222,222)));
        bmb.addBuilder(getHamButtonBuilder("로그아웃").normalColor(Color.rgb(222,110,110)).pieceColor(Color.rgb(222,222,222)));
        bmb.addBuilder(getHamButtonBuilder("취소").normalColor(Color.rgb(150,150,150)).pieceColor(Color.rgb(222,222,222)));
        bmb.setOnBoomListener(new OnBoomListenerAdapter() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                super.onClicked(index, boomButton);
                BoomBtnClickEvent(index);
            }
        });
    }

    private void setUsername(){
        final String uid = getIntent().getExtras().getString("user_id");
        this.uid = uid;
        mRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               UserData user = dataSnapshot.getValue(UserData.class);

                if (user == null) {
                    Log.e("Logined", "user " + uid + " 가 널값임");
                    Toast.makeText(Logined.this,
                            "에러: 사용자를 가져오지 못했습니다."
                            , Toast.LENGTH_SHORT).show();
                    testView.setText("죄송합니다. 닉네임을 가져오지 못했습니다.");
                }else{
                    testView.setText("안녕하세요 "+user.username+"님");
                    changeProgressMessage("프로필사진 가져오는중");
                    setphoto(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Login", "getUser:onCanceled", databaseError.toException());
            }
        });

    }

    private void setphoto(UserData user) {
        photoRoute = user.photofilename;

        File f = new File(ImgPath + photoRoute + ".png");

        if (f.exists()){
            profilephoto.setImageURI(Uri.fromFile(f));
        }else{
            saveImage(photoRoute);
        }

        hideProgressDialog();
    }

    private void toLoginActivity(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("채팅방만들기");
        builder.setMessage("로그아웃을 하고 로그인창으로 가시겠습니까?");
        builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                initDB(Logined.this);
                if (DBHelper.ChkDB())
                    DBHelper.DelId();

                intent = new Intent(Logined.this, MainActivity.class);
                startActivity(intent);
                finish();
            }// onClick
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(Logined.this, "ㅇ...ㅇㅋ;;;", Toast.LENGTH_SHORT).show();
            } // onClick
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void toInfoActivity(){
        intent = new Intent(Logined.this, infoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.background:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    imm.hideSoftInputFromWindow(DialRoomname.getWindowToken(),0);
                }
                break;
            case R.id.fab:
                LayoutInflater inflater = getLayoutInflater();

                final View dialogView = inflater.inflate(R.layout.make_chat,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("채팅방만들기");
                builder.setView(dialogView);
                builder.setPositiveButton("맹글기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DialRoomname = (EditText) dialogView.findViewById(R.id.Roomname);
                        Sub_matters = (EditText) dialogView.findViewById(R.id.Submatt);

                        String Roomname = DialRoomname.getText().toString();
                        String sub_matters = Sub_matters.getText().toString();
                        if (Roomname != "") {
                            makeNewRoom(Roomname,sub_matters);
                        } // if
                    } // onClick
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(Logined.this, "ㅇ...ㅇㅋ;;;", Toast.LENGTH_SHORT).show();
                    } // onClick
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
        } // Switch
    } // OnClick

    private void BoomBtnClickEvent(int i) {
        switch (i){
            case 0:
                toInfoActivity();
                break;
            case 1:
                toLoginActivity();
                break;
            case 2:
                Toast.makeText(this, "ㅇㅋ", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void saveImage(final String userPhotoRoute) {

        final StorageReference getRef = sRef.child("images/"+userPhotoRoute);

        final long ONE_MEAGBYTE = (1024*1024);
        getRef.getBytes(ONE_MEAGBYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                try{
                    FileOutputStream fos = openFileOutput(userPhotoRoute + ".png", 0);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                profilephoto.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Logined.this, "파일을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
                Log.e("DownloadUnSuccessFul :", e.getMessage());
            }
        });
    }

    private void makeNewRoom(String roomname, String sub_matt) {

        String cid = mRef.child("Chat").push().getKey().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("KK:mm");
        Date date = Calendar.getInstance().getTime();
        String now = formatter.format(date);
        ChatData CD = new ChatData("ChatMakeBot","방이 생성되었습니다","20172702_2700.png",now,0);
        mRef.child("Chat").child(cid).push().setValue(CD);
        ChatList chatList = new ChatList(roomname,sub_matt,cid,uid);
        mRef.child("ChatList").child(cid).setValue(chatList);
    }

    private void GetChats(){
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter = new ListAdapter();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatList Clist = snapshot.getValue(ChatList.class);

//                    Toast.makeText(Logined.this, Clist.getRoomname()+",\n"+Clist.getsub_matt()+",\n"+Clist.getCid(), Toast.LENGTH_SHORT).show();
                    adapter.addItem(Clist.getRoomname(), Clist.getsub_matt(), Clist.getCid(), Clist.getUid());
                }
                adapter.notifyDataSetChanged();
                rv.setLayoutManager(lm);
                rv.setAdapter(adapter);
//                Toast.makeText(Logined.this, adapter.getItemCount()+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Logined", "user " + uid + " 가 널값임");
                Toast.makeText(Logined.this,
                        "에러: 채팅로그를 가져오지 못했습니다."
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void DelChatRoom(String ChatRoomCode, String uid){
        if (this.uid.equals(uid)){
            mRef.child("ChatList").child(ChatRoomCode).removeValue();
            mRef.child("Chat").child(ChatRoomCode).removeValue();
        }else {
            Toast.makeText(this, "방을 생성한 유저만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void toChatActivity(String ChatRoomCode){
        if (uid != null && photoRoute != null && ChatRoomCode != null) {
            intent = new Intent(this, ChatRoom.class);
            intent.putExtra("ChatCode", ChatRoomCode);
            intent.putExtra("Userid", uid);
            intent.putExtra("UserPhoto", photoRoute);
            startActivity(intent);
        }else{
            Toast.makeText(this, "잠시만 더 기다려주세요~", Toast.LENGTH_SHORT).show();
        }
    }

    private HamButton.Builder getHamButtonBuilder(String Text) {
        return new HamButton.Builder()
                .normalText(Text);
    }
}
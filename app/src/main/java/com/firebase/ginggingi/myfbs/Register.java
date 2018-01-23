package com.firebase.ginggingi.myfbs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ginggingi.myfbs.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 회원가입할때 사용되는 코드
 */

public class Register extends LoginBase implements View.OnClickListener{

    private static final String TAG = "EmailPassword";

    private final int GET_PICTURE = 0;
    private final int CROP_FROM_IMAGE = 1;

    private Uri mImageUri;

    private FirebaseAuth auth;
    private EditText email,passwd,name;
    private String getid,getname,getemail,filename;
    private ImageButton image;
    private LinearLayout lay1,lay2;

    private InputMethodManager imm;
    private Animation aniin,aniout;

    private boolean chk_layout = false;

    private DatabaseReference mRef;
    private StorageReference sRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        init();
    }

    private void init(){
        auth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.email);
        passwd = (EditText) findViewById(R.id.pw);
        name = (EditText) findViewById(R.id.name);
        image = (ImageButton) findViewById(R.id.ProfileImage);

        lay1 = (LinearLayout) findViewById(R.id.register1);
        lay2 = (LinearLayout) findViewById(R.id.register2);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        aniin = AnimationUtils.loadAnimation(this, R.anim.next_in);
        aniout = AnimationUtils.loadAnimation(this, R.anim.next_out);

        findViewById(R.id.background).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.Next).setOnClickListener(this);

        image.setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference();
        sRef = FirebaseStorage.getInstance().getReference();
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            },0);
        }
    }

    private void SignUp(String email, String pw, final String name){

        if (!ValiDataForm()){return;}

        showProgressDialog();

        auth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //정규식을 뚫고 성공했을시
                            Log.d(TAG, "createUserWithEmail:success");
                            getname = name;
                            Registered();
                        } else {
                            //실패 ㅠㅠ
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Register.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean ValiDataForm(){
        boolean valid = true;

        String getEmail = email.getText().toString();
        String getPw = passwd.getText().toString();

        if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPw)){valid = false;
            Toast.makeText(this, "비어있음 ㅎㅎ", Toast.LENGTH_SHORT).show();
            onBackPressed();}
        else {
            if (!ChkEmail(getEmail)) {
                valid = false;
                Toast.makeText(this, "이메일이 아님 ㅇㅇ", Toast.LENGTH_SHORT).show();
            }
            if (!ChkPw(getPw)) {
                valid = false;
                Toast.makeText(this, "비밀번호는 4자부터 16자까지", Toast.LENGTH_SHORT).show();
            }
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        if (chk_layout){
            lay1.startAnimation(aniin);
            lay2.startAnimation(aniout);
            lay1.setVisibility(View.VISIBLE);
            lay2.setVisibility(View.GONE);
            chk_layout = !chk_layout;
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.background:
                hidekey();
                break;
            case R.id.Next:
                lay1.startAnimation(aniout);
                lay2.startAnimation(aniin);
                lay1.setVisibility(View.GONE);
                lay2.setVisibility(View.VISIBLE);
                chk_layout = true;
                break;
            case R.id.register:
                SignUp(email.getText().toString(),
                        passwd.getText().toString(),
                         name.getText().toString());
                break;
            case R.id.ProfileImage:
                TakePicture();
        }
    }

    private void Registered(){

        getid = auth.getCurrentUser().getUid();
        getemail = auth.getCurrentUser().getEmail();

        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();

        putPhoto();

        writeNewUser(getid,getname,getemail,filename);

        Toast.makeText(this, "계정생성\nemail:"+getemail+"\ngetname:"+getname, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void putPhoto() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyymmhh_mmss");
        Date now  = new Date();
        filename = formatter.format(now) + ".png";

        StorageReference PutinPhoto = sRef.child("images/"+filename);

        Bitmap profilePhoto = image.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profilePhoto.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = PutinPhoto.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "사진이 없거나 실패함 ㅠㅠ", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    private void writeNewUser(String userid,String username,String useremail,String photofilename){

        UserData user = new UserData(useremail,username,photofilename);

        mRef.child("users").child(userid).setValue(user);
    }

    private void TakePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GET_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode){
            case GET_PICTURE:
                mImageUri = data.getData();
                Log.d(TAG, mImageUri.getPath().toString());
                ImgCrop();
                break;
            case CROP_FROM_IMAGE:
                if (resultCode != RESULT_OK)
                    return;

                final Bundle extras = data.getExtras();

                String FilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        +"/Cropimages/"+System.currentTimeMillis()+".png";

                if (extras != null){
                    Bitmap photo = extras.getParcelable("data");

                    photo = makeCircle(photo);

                    image.setImageBitmap(photo);
                }
                break;
        }
    }

    private void ImgCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageUri, "image/*");

        intent.putExtra("outputX", 1000); // CROP 한 이미지의 X축 크기
        intent.putExtra("outputY", 1000); // CROP 한 이미지의 Y축 크기
        intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
        intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_FROM_IMAGE);
    }

    private Bitmap makeCircle(Bitmap photo){
        Bitmap circleBitmap = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(circleBitmap);

        final int color = Color.GRAY;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0,
                photo.getWidth(), photo.getHeight());
        final RectF rectf = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(color);
        canvas.drawOval(rectf,paint);

        paint.setXfermode(new
                PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(photo, rect, rect, paint);

        photo.recycle();

        return circleBitmap ;
    }

    private void hidekey(){
        imm.hideSoftInputFromWindow(email.getWindowToken(),0);
        imm.hideSoftInputFromWindow(passwd.getWindowToken(),0);
        imm.hideSoftInputFromWindow(name.getWindowToken(),0);
    }
}

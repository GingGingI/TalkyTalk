package com.firebase.ginggingi.myfbs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ginggingi.myfbs.model.UserIdPw;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;

/**
 *처음 로그인창
 */

public class MainActivity extends LoginBase
        implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText mIdField;
    private EditText mPwField;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        //Auth 초기화

        initDB(MainActivity.this);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.background).setOnClickListener(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mIdField = (EditText) findViewById(R.id.id);
        mPwField = (EditText) findViewById(R.id.pw);

        if (DBHelper.ChkDB()){
            UserIdPw idPw = DBHelper.GetId();
            SignIn(idPw.getID(), idPw.getPW(), true);
        }
    }

    protected void onStart(){
        super.onStart();
        //구글이했던 클릭리스너 지정방식 (이방식 처음암 ㄹㅇ 신기)
        findViewById(R.id.Login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.findIDPW).setOnClickListener(this);
    }

    private void SignIn(final String email, final String pw, final boolean AutoLog){
        //로딩창 켜짐
        showProgressDialog();
        if (AutoLog){
        changeProgressMessage("잠시만기다려봐\n" +
                "[자동로그인]");
        }
        mAuth.signInWithEmailAndPassword(email,pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //로그인되면 로그인된 인텐트로 넘어가고 로그인된 유저의 정보를 보여줄꺼임
                            Toast.makeText(MainActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                            if (!DBHelper.ChkDB()){
                                SaveID(email, pw);
                                Log.i("Login", "수동로그인댐");
                            }else{
                                Log.i("Login", "자동로그인댐");
                            }

                            LoginSuccess();

                            }else{
                            //실패하면 화면은 그대로고 실패했다는 토스트만 띄움
                            Toast.makeText(MainActivity.this, "로그인 실패 ㅠㅠ\n" +
                                    "이메일이나 비밀번호가 맞는지 확인해주세용", Toast.LENGTH_SHORT).show();
                        }
                        //로딩창 꺼짐
                        hideProgressDialog();
                    }
                });
        //SignIn 끝남
    }

    private void SaveID(final String email, final String pw) {
        UserIdPw IDPW = new UserIdPw();
        IDPW.setID(email);
        IDPW.setPW(pw);

        try{
            DBHelper.AddId(IDPW);
        }catch (DatabaseException e){
            e.printStackTrace();
        }

    }

    private void LoginSuccess(){

        mIdField.setText(null);
        mPwField.setText(null);

        Intent intent = new Intent(this, Logined.class);
        String uid = mAuth.getCurrentUser().getUid();
        intent.putExtra("user_id",uid);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.background:
                hidekey();
                break;
            case R.id.Login :
                    String email = mIdField.getText().toString();
                    String pw = mPwField.getText().toString();
                if (ChkEmail(email) && ChkPw(pw)) {SignIn(email, pw, false);}
                break;
            case R.id.register :
                Intent intent = new Intent(this, Register.class);
                startActivity(intent);
                finish();
                break;
            case R.id.findIDPW :
                Toast.makeText(this, "추후 추가될 예정입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void hidekey(){
        imm.hideSoftInputFromWindow(mIdField.getWindowToken(),0);
        imm.hideSoftInputFromWindow(mPwField.getWindowToken(),0);
    }
}

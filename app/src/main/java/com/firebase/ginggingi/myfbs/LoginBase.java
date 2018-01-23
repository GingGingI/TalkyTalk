package com.firebase.ginggingi.myfbs;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ginggingi.myfbs.Database.AutoLoginDBHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MainActivity 와 Logined 의 공통되는역할을 맡은 코드
 */

public class LoginBase extends AppCompatActivity{

    long pressedTime = 0;
    public ProgressDialog mProgress;
    protected AutoLoginDBHelper DBHelper;

    public void showProgressDialog(){
        if (mProgress == null){
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("잠시만 기다려봐");
            mProgress.setIndeterminate(true);
        }
        mProgress.show();
    }

    public void changeProgressMessage(String Msg){
        mProgress.setMessage(Msg);
    }

    public void hideProgressDialog(){
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    public void onStop(){
        super.onStop();
        hideProgressDialog();
    }

    protected boolean ChkPw(String pw){
        String Regular = "^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$";
        Pattern ptn = Pattern.compile(Regular);
        Matcher mat = ptn.matcher(pw);

        return mat.matches();
    }

    protected boolean ChkEmail(String email){
        String Regular = "([_a-zA-Z0-9-\\\\.]+@[\\\\.a-zA-Z0-9-]+\\.[a-zA-Z])\\w+";
        Pattern ptn = Pattern.compile(Regular);
        Matcher mat = ptn.matcher(email);

        return mat.matches();
    }

    protected void initDB(Context mContext){
        if (DBHelper == null){
            DBHelper = new AutoLoginDBHelper(mContext, "TalkyTalk_Database",null, 1);
        }
    }

    @Override
    public void onBackPressed() {

        if(pressedTime == 0){
            Toast.makeText(this, "한번 더 누르면 앱꺼짐 ㅇㅇ.", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        } else{
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if (seconds > 2000) {
                pressedTime = 0;
            } else {
                super.onBackPressed();
                finish();
                //app 종료
            }
        }
    }
}

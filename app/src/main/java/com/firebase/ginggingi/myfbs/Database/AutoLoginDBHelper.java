package com.firebase.ginggingi.myfbs.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.firebase.ginggingi.myfbs.model.UserIdPw;

/**
 *자동로그인을 하기위해 필요한 Database 연결을 돕는 Helper코드
 */

public class AutoLoginDBHelper extends SQLiteOpenHelper {

    private final String TAG = "Database";

    private Context mContext;

    public AutoLoginDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE IDPW_TABLE (");
        sb.append("ID TEXT,");
        sb.append("PW TEXT)");

        db.execSQL(sb.toString());
//        Log.i(TAG, "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.i(TAG,"Version up from: "+oldVersion+", to : "+newVersion);
    }

//    idpw 추가
    public void AddId(UserIdPw IdPw){
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO IDPW_TABLE");
        sb.append("(ID,PW)");
        sb.append("VALUES(?,?)");

        db.execSQL(sb.toString(),
                new Object[]{
                        IdPw.getID(),
                        IdPw.getPW()
                });
    }
//    모든컬럼삭제
    public void DelId(){
        SQLiteDatabase db = getWritableDatabase();

        db.delete("IDPW_TABLE",null,null);
    }

//    idpw 가져오기
    public UserIdPw GetId(){
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ID, PW ");
        sb.append("FROM IDPW_TABLE");

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(sb.toString(), null);
        UserIdPw IDPW = null;
//        Log.i(TAG,"Data row founds : "+cursor.getCount());
        while (cursor.moveToNext()){
            IDPW = new UserIdPw();

            IDPW.setID(cursor.getString(0));
            IDPW.setPW(cursor.getString(1));
        }
        return IDPW;
    }
//    db가 존재하는지 확인
    public boolean ChkDB(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM IDPW_TABLE", null);
        Boolean rowExists;

        if (cursor.moveToFirst())
            rowExists = true;
        else
            rowExists = false;

        return rowExists;
    }
//    쓸모는없지만 한두번 작동하는지 써봄
    public void DBTest(){
        SQLiteDatabase db = getReadableDatabase();
    }
}

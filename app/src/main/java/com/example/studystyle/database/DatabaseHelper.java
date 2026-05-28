package com.example.studystyle.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.studystyle.database.ResultContract.ResultEntry;
import com.example.studystyle.database.ResultContract.UserEntry;
import com.example.studystyle.models.Result;
import com.example.studystyle.models.User;
import com.example.studystyle.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserEntry.SQL_CREATE_TABLE);
        db.execSQL(ResultEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserEntry.SQL_DROP_TABLE);
        db.execSQL(ResultEntry.SQL_DROP_TABLE);
        onCreate(db);
    }

    // ========== USER OPERATIONS ==========

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME,     user.getName());
        values.put(UserEntry.COLUMN_EMAIL,    user.getEmail());
        values.put(UserEntry.COLUMN_PASSWORD, user.getPassword());
        values.put(UserEntry.COLUMN_JURUSAN,  user.getJurusan());
        long id = db.insert(UserEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                UserEntry.TABLE_NAME, null,
                UserEntry.COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry._ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_PASSWORD)));
            user.setJurusan(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_JURUSAN)));
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                UserEntry.TABLE_NAME, new String[]{UserEntry._ID},
                UserEntry.COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                UserEntry.TABLE_NAME, new String[]{UserEntry._ID},
                UserEntry.COLUMN_EMAIL + "=? AND " + UserEntry.COLUMN_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        boolean valid = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        db.close();
        return valid;
    }

    public boolean updateUser(int userId, String name, String jurusan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME,    name);
        values.put(UserEntry.COLUMN_JURUSAN, jurusan);
        int rows = db.update(UserEntry.TABLE_NAME, values,
                UserEntry._ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    // ========== RESULT OPERATIONS ==========

    public long insertResult(Result result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ResultEntry.COLUMN_USER_ID,    result.getUserId());
        values.put(ResultEntry.COLUMN_VISUAL,     result.getVisualScore());
        values.put(ResultEntry.COLUMN_AUDITORY,   result.getAuditoryScore());
        values.put(ResultEntry.COLUMN_KINESTETIK, result.getKinestetikScore());
        values.put(ResultEntry.COLUMN_RESULT_TYPE,result.getResultType());
        values.put(ResultEntry.COLUMN_DATE,       result.getDate());
        long id = db.insert(ResultEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Result> getResultsByUserId(int userId) {
        List<Result> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                ResultEntry.TABLE_NAME, null,
                ResultEntry.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, ResultEntry.COLUMN_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Result result = new Result();
                result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry._ID)));
                result.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_USER_ID)));
                result.setVisualScore(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_VISUAL)));
                result.setAuditoryScore(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_AUDITORY)));
                result.setKinestetikScore(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_KINESTETIK)));
                result.setResultType(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_RESULT_TYPE)));
                result.setDate(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_DATE)));
                results.add(result);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return results;
    }

    public Result getLatestResult(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                ResultEntry.TABLE_NAME, null,
                ResultEntry.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, ResultEntry.COLUMN_DATE + " DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            Result result = new Result();
            result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry._ID)));
            result.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_USER_ID)));
            result.setVisualScore(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_VISUAL)));
            result.setAuditoryScore(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_AUDITORY)));
            result.setKinestetikScore(cursor.getInt(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_KINESTETIK)));
            result.setResultType(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_RESULT_TYPE)));
            result.setDate(cursor.getString(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_DATE)));
            cursor.close();
            db.close();
            return result;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }
}

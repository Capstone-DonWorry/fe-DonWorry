package com.example.capstone_donworry;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.capstone_donworry.fragment.calendar.AmountItem;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "donworry.db";

    // Table
    private static final String item_TABLE_NAME = "EXPENSE";
    private static final String UID = "UID";
    private static final String COL_CONTENT = "CONTENT";
    private static final String COL_DATE = "DATE";
    private static final String COL_CARD = "CARD";
    private static final String COL_BANK = "BANK";
    private static final String COL_CATEGORY = "CATEGORY";
    private static final String COL_AMOUNT = "AMOUNT";
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + item_TABLE_NAME + " (" +
                UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CONTENT + " CHAR(20), " +
                COL_DATE + " DATE, " +
                COL_CARD + " CHAR(20), " +
                COL_BANK + " CHAR(20), " +
                COL_CATEGORY + " CHAR(20), " +
                COL_AMOUNT + " INT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + item_TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    // db에 저장되어 있는 같은 달 항목 정보
    @SuppressLint("Range")
    public List<AmountItem> getMonthItems(String year, String month) {
        Toast.makeText(this.context, "Month 아이템 호출", Toast.LENGTH_SHORT).show();
        List<AmountItem> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + item_TABLE_NAME +
                " WHERE strftime('%Y-%m', " + COL_DATE + ") = ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{year+'-'+month});

        if (cursor.moveToFirst()) {
            do {
                AmountItem amountItem = new AmountItem();
                amountItem.setUid(cursor.getInt(cursor.getColumnIndex(UID)));
                amountItem.setContent(cursor.getString(cursor.getColumnIndex(COL_CONTENT)));
                amountItem.setDate(cursor.getString(cursor.getColumnIndex(COL_DATE)));
                amountItem.setCard(cursor.getString(cursor.getColumnIndex(COL_CARD)));
                amountItem.setBank(cursor.getString(cursor.getColumnIndex(COL_BANK)));
                amountItem.setCategory(cursor.getString(cursor.getColumnIndex(COL_CATEGORY)));
                amountItem.setAmount(cursor.getString(cursor.getColumnIndex(COL_AMOUNT)));
                items.add(amountItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }
    // TODO: 월 별 합계 구하기


    // db에 저장되어 있는 같은 날짜 항목 정보
    @SuppressLint("Range")
    public List<AmountItem> getDateItems(String date) {
        Toast.makeText(this.context, "Date 아이템 호출", Toast.LENGTH_SHORT).show();
        List<AmountItem> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + item_TABLE_NAME + " WHERE " + COL_DATE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                AmountItem amountItem = new AmountItem();
                amountItem.setUid(cursor.getInt(cursor.getColumnIndex(UID)));
                amountItem.setContent(cursor.getString(cursor.getColumnIndex(COL_CONTENT)));
                amountItem.setDate(cursor.getString(cursor.getColumnIndex(COL_DATE)));
                amountItem.setCard(cursor.getString(cursor.getColumnIndex(COL_CARD)));
                amountItem.setBank(cursor.getString(cursor.getColumnIndex(COL_BANK)));
                amountItem.setCategory(cursor.getString(cursor.getColumnIndex(COL_CATEGORY)));
                amountItem.setAmount(cursor.getString(cursor.getColumnIndex(COL_AMOUNT)));
                items.add(amountItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }
    // TODO: 일별 합계 구하기

    // db에 저장되어 있는 특정 항목 정보
    @SuppressLint("Range")
    public AmountItem getItem(AmountItem amountItem) {
        Toast.makeText(this.context, "getItem 호출", Toast.LENGTH_SHORT).show();
        AmountItem item = new AmountItem();
        String selectQuery = "SELECT * FROM " + item_TABLE_NAME + " WHERE " + COL_CONTENT + " = ? AND " + COL_DATE + " = ? AND " + UID + " = ?";
        String[] selectionArgs = {amountItem.getContent(), amountItem.getDate(), String.valueOf(amountItem.getUid())};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, selectionArgs);

        if (cursor.moveToFirst()) {
            amountItem.setUid(cursor.getInt(cursor.getColumnIndex(UID)));
            item.setContent(cursor.getString(cursor.getColumnIndex(COL_CONTENT)));
            item.setDate(cursor.getString(cursor.getColumnIndex(COL_DATE)));
            item.setCard(cursor.getString(cursor.getColumnIndex(COL_CARD)));
            item.setBank(cursor.getString(cursor.getColumnIndex(COL_BANK)));
            item.setCategory(cursor.getString(cursor.getColumnIndex(COL_CATEGORY)));
            item.setAmount(cursor.getString(cursor.getColumnIndex(COL_AMOUNT)));
        }
        cursor.close();
        db.close();
        return item;
    }

    // 새로운 item 추가
    public long addItem(AmountItem amountItem) {
        SQLiteDatabase db = null;
        long uid = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_CONTENT, amountItem.getContent());
            values.put(COL_DATE, amountItem.getDate());
            values.put(COL_CARD, amountItem.getCard());
            values.put(COL_BANK, amountItem.getBank());
            values.put(COL_CATEGORY, amountItem.getCategory());
            values.put(COL_AMOUNT, amountItem.getAmount());

            // 삽입 후 생성된 UID 값 반환
            uid = db.insert(item_TABLE_NAME, null, values);

            if (uid != -1) {
                // UID 값은 삽입된 항목의 고유 ID
                amountItem.setUid((int) uid);
                Toast.makeText(this.context, "아이템 등록" + amountItem.getUid(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.context, "아이템 등록 실패", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            Toast.makeText(this.context, "아이템 등록 오류", Toast.LENGTH_SHORT).show();
        } finally {
            if (db!= null) {
                db.close();
            }
        }
        return uid;
    }

    // item 정보 업데이트
    public int updateItem(AmountItem amountItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, amountItem.getContent());
        values.put(COL_DATE, amountItem.getDate());
        values.put(COL_CARD, amountItem.getCard());
        values.put(COL_BANK, amountItem.getBank());
        values.put(COL_CATEGORY, amountItem.getCategory());
        values.put(COL_AMOUNT, amountItem.getAmount());

        String whereClause =  COL_DATE + "=? AND " + COL_CONTENT + "=? AND " + UID + "=?";
        String[] whereArgs = {amountItem.getDate(), amountItem.getContent(), String.valueOf(amountItem.getUid())};
        return db.update(item_TABLE_NAME, values, whereClause, whereArgs);
    }

    // item 삭제
    public void deleteItem(AmountItem amountItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(item_TABLE_NAME, COL_CONTENT + "=? AND " + UID + "=?" , new String[]{amountItem.getContent(), String.valueOf(amountItem.getUid())});
        db.close();
    }
}

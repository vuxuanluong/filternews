package com.t3h.filternews.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.t3h.filternews.model.News;

import java.util.ArrayList;

public class DAO extends SQLiteOpenHelper {
    public static final String DB_NAME = "qltt";
    public static final String TB_NAME = "filte_news";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESC = "description";
    public static final String DATE = "date";
    public static final String IMAGE = "image";
    public static final String FAVORITE = "favorite";
    public static final String LINK = "link";
    public static final String PATHFILE = "pathfile";


    public DAO(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = " create table " + TB_NAME + "(" +
                ID + " integer primary key autoincrement," +
                TITLE + " text," +
                DESC + " text," +
                DATE + " text," +
                IMAGE + " text," +
                LINK + " text," +
                PATHFILE + " text," +
                FAVORITE + " integer" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<News> getData() {
        ArrayList<News> arr = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TB_NAME, null, null, null,
                null, null, null);
        cursor.moveToFirst();
        int indexId = cursor.getColumnIndex(ID);
        int indexTitle = cursor.getColumnIndex(TITLE);
        int indexDesc = cursor.getColumnIndex(DESC);
        int indexDate = cursor.getColumnIndex(DATE);
        int indexImage = cursor.getColumnIndex(IMAGE);
        int indexLink = cursor.getColumnIndex(LINK);
        int indexPath = cursor.getColumnIndex(PATHFILE);
        while (cursor.isAfterLast() == false) {
            int id = cursor.getInt(indexId);
            String title = cursor.getString(indexTitle);
            String desc = cursor.getString(indexDesc);
            String date = cursor.getString(indexDate);
            String image = cursor.getString(indexImage);
            String link = cursor.getString(indexLink);
            String pathfile = cursor.getString(indexPath);
            News news = new News();
            news.setId(id);
            news.setTitle(title);
            news.setDesc(desc);
            news.setImg(image);
            news.setPubDate(date);
            news.setLink(link);
            news.setPathFile(pathfile);
            arr.add(news);
            cursor.moveToNext();
        }
        database.close();
        return arr;
    }

    public ArrayList<News> getDataFavorite() {
        ArrayList<News> arr = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
//        Cursor cursor = database.query(TB_NAME, null, null, null,
//                null  ,null, null, null);
//        cursor.moveToFirst();
        String sql = " select * from " + TB_NAME + " where" + "(" + FAVORITE + " == " + 1 + ")";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        int indexId = cursor.getColumnIndex(ID);
        int indexTitle = cursor.getColumnIndex(TITLE);
        int indexDesc = cursor.getColumnIndex(DESC);
        int indexDate = cursor.getColumnIndex(DATE);
        int indexImage = cursor.getColumnIndex(IMAGE);
        int indexLink = cursor.getColumnIndex(LINK);
        int indexFavorite = cursor.getColumnIndex(FAVORITE);
        int indexPath = cursor.getColumnIndex(PATHFILE);
        while (cursor.isAfterLast() == false) {
            int id = cursor.getInt(indexId);
            String title = cursor.getString(indexTitle);
            String desc = cursor.getString(indexDesc);
            String date = cursor.getString(indexDate);
            String image = cursor.getString(indexImage);
            String link = cursor.getString(indexLink);
            String pathfile = cursor.getString(indexPath);
            boolean favorite = cursor.getInt(indexFavorite) > 0;
            News news = new News();
            news.setId(id);
            news.setTitle(title);
            news.setDesc(desc);
            news.setImg(image);
            news.setPubDate(date);
            news.setLink(link);
            news.setFavorite(favorite);
            news.setPathFile(pathfile);
            arr.add(news);
            cursor.moveToNext();
        }
        database.close();
        return arr;
    }

    public long insert(News news) {
        ContentValues values = new ContentValues();
        values.put(TITLE, news.getTitle());
        values.put(DESC, news.getDesc());
        values.put(DATE, news.getPubDate());
        values.put(IMAGE, news.getImg());
        values.put(LINK, news.getLink());
        values.put(FAVORITE, 0);
        values.put(PATHFILE, news.getPathFile());
        SQLiteDatabase database = getWritableDatabase();
        long id = database.insert(TB_NAME, null, values);
        database.close();
        return id;
    }

    public int delete(int id) {
        SQLiteDatabase database = getWritableDatabase();
        String selection = ID + " = ?";
        String[] selectionAgrs = {String.valueOf(id)};
        int rows = database.delete(TB_NAME, selection, selectionAgrs);
        database.close();
        return rows;
    }

    public long updateFavorite(News news) {
        ContentValues values = new ContentValues();
        values.put(FAVORITE, news.isFavorite() ? 1 : 0);
        SQLiteDatabase database = getWritableDatabase();
        String selection = ID + " = ?";
        String[] selectionAgrs = {String.valueOf(news.getId())};
        int rows = database.update(TB_NAME, values, selection, selectionAgrs);
        database.close();
        return rows;
    }
}

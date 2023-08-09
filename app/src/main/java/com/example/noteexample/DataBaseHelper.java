package com.example.noteexample;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final  String DATABASE_NAME = "notes.db";

    public DataBaseHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Note.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // add note
    public long insertNote (String title, String note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // add value in column
        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_TITLE,title);
        values.put(Note.COLUMN_NOTE,note);
        // inset in database, long id (return the id of newly inserted row, if has an error return -1)
        long id = db.insert(Note.TABLE_NAME,null,values);
        db.close();
        return id;
    }

    // get a note
    @SuppressLint("Range")
    public Note getNote(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID,Note.COLUMN_TITLE,Note.COLUMN_NOTE,Note.COLUMN_TIME_STAMP},
                Note.COLUMN_ID + "=?",
                new String []{String.valueOf(id)},null,null,null,null);

        if(c!=null) c.moveToFirst();

        Note note = new Note (c.getInt(c.getColumnIndex(Note.COLUMN_ID)),
                c.getString(c.getColumnIndex(Note.COLUMN_TITLE)),
                c.getString(c.getColumnIndex(Note.COLUMN_NOTE)),
                c.getString(c.getColumnIndex(Note.COLUMN_TIME_STAMP)));

        c.close();
        return note;
    }

    // getAllNotes()
    @SuppressLint("Range")
    public List<Note> getAllNotes()
    {
        List<Note> notes = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Note.TABLE_NAME + " ORDER BY " + Note.COLUMN_TIME_STAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);


        if(c.moveToFirst())
        {
            do{
                Note note = new Note();
                note.setId(c.getInt(c.getColumnIndex(Note.COLUMN_ID)));
                note.setTitle(c.getString(c.getColumnIndex(Note.COLUMN_TITLE)));
                note.setNote(c.getString(c.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(c.getString(c.getColumnIndex(Note.COLUMN_TIME_STAMP)));

                notes.add(note);
            }while(c.moveToNext());
        }
        db.close();
        return notes;
    }

    // updateNote
    public int updateNotes(Note note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Note.COLUMN_NOTE,note.getNote());
        v.put(Note.COLUMN_TITLE,note.getTitle());
        return db.update(Note.TABLE_NAME,v,Note.COLUMN_ID + "=?",new String[]{String.valueOf(note.getId())});
    }

    //Delete note
    public void deleteNote(Note note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME,Note.COLUMN_ID + "=?",new String[]{String.valueOf(note.getId())});
        db.close();
    }

}

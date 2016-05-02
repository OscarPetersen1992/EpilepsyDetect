package portablehealthtech.epilepsydetect;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final  int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "seizures.db";
    public static final String TABLE_SEIZURES = "seizures";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_TRUEPOSITIVE = "truepositive";


    public DBHandler(Context context){ //,String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_SEIZURES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " + COLUMN_DURATION + " TEXT, " + COLUMN_DATA + " TEXT, " + COLUMN_TRUEPOSITIVE + " INTEGER DEFAULT 1" +  ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_SEIZURES);
        onCreate(db);
    }

    // Add a new row containing seizure

    public void addSeizure(Seizure seizure){

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE,seizure.getSeizure_date());
        values.put(COLUMN_DURATION, seizure.getSeizure_duration());
        values.put(COLUMN_DATA, seizure.getSeizure_data());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SEIZURES, null, values);

        db.close();
    }


    public Cursor getSeizure(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SEIZURES + " WHERE " +
                COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return cursor;
    }

    public Cursor getStats(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DATE + " FROM " + TABLE_SEIZURES + " WHERE " +
                COLUMN_DATE + " LIKE '" + date +"%'", null);
        return cursor;
    }

    public Cursor getAllSeizures() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SEIZURES + " ORDER BY " + COLUMN_DATE + " DESC", null);
        return cursor;
    }

    public int rejectSeizure(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TRUEPOSITIVE, 0);

        // updating row
        return db.update(TABLE_SEIZURES, values, COLUMN_ID + " = ?",
                new String[]{Integer.toString(id)});
    }


    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);

    }

}

package portablehealthtech.epilepsydetect;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final  int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "seizures.db";
    public static final String TABLE_SEIZURES = "seizures";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DURATION = "duration";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_SEIZURES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " + COLUMN_DURATION + " TEXT" + ");";

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

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SEIZURES, null, values);

        db.close();
    }


    // Getting All Contacts
    public List<Seizure> getAllSeizures() {
        List<Seizure> seizureList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SEIZURES;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Seizure seizure = new Seizure();
                seizure.setSeizure_id(Integer.parseInt(cursor.getString(0)));
                seizure.setSeizure_date(cursor.getString(1));
                seizure.setSeizure_duration(cursor.getDouble(2));

                String name =  "Date: " + cursor.getString(1) + ", Duration: " + cursor.getString(2) + " sec";
                SeizureList.ArrayofName.add(name);
                // Adding seizure to list
                seizureList.add(seizure);
            } while (cursor.moveToNext());
        }

        if (db != null) {
            db.execSQL("DELETE FROM " + TABLE_SEIZURES);
            db.close();
        }

        // return seizure list
        return seizureList;
    }


    // Remove all data from database.
    public void removeAll()
    {
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_SEIZURES, null, null);
        db.close();
    }




    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SEIZURES + "WHERE 1";

        // Cursor point to location in results
        /*
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        if (!(cursor.getString(cursor.getColumnIndex("id")) != null)){
            dbString += cursor.getString(cursor.getColumnIndex("id"));
            dbString += "\n";
        }
        */

        db.close();
        return dbString;
    }

}

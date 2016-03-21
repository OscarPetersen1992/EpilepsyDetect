package portablehealthtech.epilepsydetect;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class DBHandler extends SQLiteOpenHelper {

    private static final  int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "seizures.db";
    public static final String TABLE_SEIZURES = "seizures";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DURATION = "duration";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_SEIZURES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_DATE + " TEXT " + COLUMN_DURATION + " TEXT " + ");";

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
        values.put(COLUMN_ID,seizure.getSeizure_id());
        values.put(COLUMN_DATE,seizure.getSeizure_date());
        values.put(COLUMN_DURATION, seizure.getSeizure_duration());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SEIZURES, null, values);
        db.close();
    }
}

package uk.ac.abertay.firedroidpager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;
import java.sql.Date;

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    // Define database fields
    public static final String DATABASE_NAME = "d1ebu1g124.db";
    public static final String TABLE_NAME = "mydroid_smsmsg";
    public static final String COL1 = "_id";
    public static final String COL2 = "MSG";
    public static final String COL3 = "TIMESTAMP";
    public static final String DBVER = "1";
    String datastring = "";

    public SQLDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }
    // Create Database Function - Override
    @Override
    public void onCreate(SQLiteDatabase db) {
    String createTable = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "MSG TEXT, TIMESTAMP TEXT)";
        db.execSQL(createTable);
    }

    // Upgrade Database Function - Override
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
    }

    // Insert Database Function - Override
    public boolean insertDataDB(final String sql1) {
        // Get date/time
        Date currentDate = new Date(System.currentTimeMillis());
        CharSequence s = DateFormat.format("dd-MM-yyyy hh:mm:ss", currentDate.getTime());
        // Store date - String
        final String date = s.toString();
        // Set/define writable database
        final SQLiteDatabase db = this.getWritableDatabase();
        // Multi Threading - Insert into SQL (Worker Thread).
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Set/define contentvalues
                ContentValues contentVal = new ContentValues();
                // Put database values into COL2/3
                contentVal.put(COL2,sql1);
                contentVal.put(COL3,date);
                // Insert data (To Table)
                long results = db.insert(TABLE_NAME, null, contentVal);

                // Close DB After Insertion
                db.close();
            }
        }).start();
        return true;
    }

    // Get all tables from DB
    public String getDataDB() {
        // Define DB Helper
        final SQLiteDatabase db = this.getWritableDatabase();

                Cursor data = db.rawQuery("SELECT _id,MSG,TIMESTAMP FROM " + TABLE_NAME, null);

                if (data.getCount() == 0) {
                    Log.i("SMS", "SQL: No Data To Read??");
                } else {
                    StringBuffer buffer = new StringBuffer();
                    while (data.moveToNext()) {
                        buffer.append("SMS: " + data.getString(1) + "\n");
                        buffer.append("Alarm Time: " + data.getString(2) + "\n\n");
                        datastring = buffer.toString();
                    }
                    Log.i("SMS", "SQL: DATA" + datastring);
                }
                // Close DB Cursor
                data.close();
                return datastring;
    }
}

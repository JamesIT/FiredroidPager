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
                db.insert(TABLE_NAME, null, contentVal);
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
        // Get SMS Alerts from DB into Cursor(Data). Sort by latest alerts, desc and limit to five entries.
        Cursor data = db.rawQuery("SELECT _id,MSG,TIMESTAMP FROM " + TABLE_NAME + " ORDER BY _id DESC LIMIT 3", null);

        try {
            // If no data, log error.
            if (data.getCount() == 0) {
                Log.i("SMS", "SQL: No Data To Read??.");
            } else {
                StringBuffer buffer = new StringBuffer();
                while (data.moveToNext()) {
                    // Chained Buffer Append Calls.
                    buffer.append("SMS: ").append(data.getString(1)).append("\n");
                    buffer.append("Alarm Time: ").append(data.getString(2)).append("\n\n");
                    datastring = buffer.toString();
                }
                Log.i("SMS", "SQL: DATA" + datastring);
                    }
        } catch (Exception e) {
            // Debugging - Exception Handling
            Log.d("Exception caught", e.getMessage());
        }
        // Close DB Cursor
        data.close();
        // Close Database
        db.close();
        return datastring;
    }

    public void removeDataDB() {
        // Multi-threading. Remove Data from DB in worker thread.
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Define SQLite DB & Get writable database.
                SQLiteDatabase db = getWritableDatabase();
                // Initialize cursor & Execute raw query (Delete data).
                Cursor data = db.rawQuery("DELETE FROM " + TABLE_NAME, null);
                // If no data or is data log message
                if (data.getCount() == 0) {
                    Log.i("SMS", "SQL: No Data To Delete.");
                } else {
                    Log.i("SMS", "SQL: Data Deleted.");
                }
                // Close Cursor.
                data.close();
                // Close Database
                db.close();
            }
        }).start();
    }

}

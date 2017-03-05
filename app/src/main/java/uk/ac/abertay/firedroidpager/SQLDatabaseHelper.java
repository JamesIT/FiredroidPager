package uk.ac.abertay.firedroidpager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;

import java.sql.Date;

class SQLDatabaseHelper extends SQLiteOpenHelper {

    public static final String COL1 = "_id";
    // Define database fields
    private static final String DATABASE_NAME = "d1ebu1g124.db";
    private static final String TABLE_NAME = "mydroid_smsmsg";
    private static final String COL2 = "MSG";
    private static final String COL3 = "TIMESTAMP";
    private static final Integer DBVER = 1;
    private final String ETAG = "SQLDatabaseHelper: ";
    // Define SQLiteDB object, get writable DB.
    private final SQLiteDatabase sdb = this.getWritableDatabase();
    // Define/initialize data string.
    private String datastring = "";

    // Set public object and context.
    public SQLDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    // Android Lifecycle - onCreate.
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB();
    }

    // Check if DB open and close.
    private void closeDB() {
        if (!sdb.isOpen()) {
            // Close Database
            sdb.close();
        }
    }

    // Check if cursor open and close.
    private void closeCursor(Cursor data) {
        // Check if cursor is closed.
        if (data != null && !data.isClosed()) {
            // Close Cursor.
            data.close();
        }
    }

    // Create SQLite DB.
    private void createDB() {
        //Threading - Use worker thread.
        new Thread(new Runnable() {

            @Override
            public void run() {
                String createTable = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "MSG TEXT, TIMESTAMP TEXT)";
                sdb.execSQL(createTable);
                closeDB();
            }
        }).start();
    }

    // Upgrade Database Function - Override
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not used, if version more than 1. DROP TABLE.
        if (DBVER > 1) {
    db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        }
    }

    // Insert Database Function
    public void insertDataDB(final String sql1) {
        // Get date/time
        Date currentDate = new Date(System.currentTimeMillis());
        CharSequence s = DateFormat.format("dd-MM-yyyy hh:mm:ss", currentDate.getTime());
        // Store date - String
        final String date = s.toString();
        // Set/define writable database
        //final SQLiteDatabase db = this.getWritableDatabase();
        // Threading - Insert into SQL (Worker Thread).
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Set/define contentvalues
                ContentValues contentVal = new ContentValues();
                // Put database values into COL2/3
                contentVal.put(COL2,sql1);
                contentVal.put(COL3,date);
                // Insert data (To Table)
                sdb.insert(TABLE_NAME, null, contentVal);
                // Close DB After Insertion
                closeDB();
            }
        }).start();
    }

    // TODO: Implement threading.
    // Get all tables from DB
    public String getDataDB() {
        // Initialize Cursor, set to null for now.
        Cursor data = null;

        try {
            // Get SMS Alerts from DB into Cursor(Data). Sort by latest alerts, desc and limit to five entries.
            data = sdb.rawQuery("SELECT _id,MSG,TIMESTAMP FROM " + TABLE_NAME + " ORDER BY _id DESC LIMIT 3", null);

            // If no data, log error.
            if (data.getCount() == 0) {
                Log.i("SMS", "SQL: No Data To Read??.");
            } else {
                // Initialize + define String Builder
                StringBuilder buffer = new StringBuilder();
                // Move Cursor to next row, and append data to datastring.
                while (data.moveToNext()) {
                    // Chained Buffer Append Calls.
                    buffer.append("SMS: ").append(data.getString(1)).append("\n");
                    buffer.append("Alarm Time: ~[").append(data.getString(2)).append("]~").append("\n\n");
                    // Add data from buffer to string.
                    datastring = buffer.toString();
                }
                // Debug Message
                Log.i(ETAG, "SQL: DATA" + datastring);
                    }
        } catch (Exception e) {
            //TODO: http://stackoverflow.com/questions/21619618/sqlexception-error-handling-in-android
            // Debugging - Exception Handling
            Log.e(ETAG, "Exception caught " + e.getMessage());
            // Set datastring message, incase of error. Prevent further errors. (Due to null message).
            datastring = "Error! Exception " + e;
        } finally {
            closeCursor(data);
            closeDB();
        }

        // Return data
        return datastring;
    }

    public void removeDataDB() {
        //Threading. Remove Data from DB in worker thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Initialize Cursor - Set to null for now.
                Cursor data = null;
                try {
                    // Initialize cursor & Execute raw query (Delete data).
                    data = sdb.rawQuery("DELETE FROM " + TABLE_NAME, null);
                    // If no data or is data log message
                    if (data.getCount() == 0) {
                        Log.i("SMS", "SQL: No Data To Delete.");
                    } else {
                        Log.i("SMS", "SQL: Data Deleted.");
                    }
                } catch (Exception e) {
                    // Debugging - Exception Handling
                    Log.e(ETAG, "Exception caught " + e.getMessage());
                } finally {
                    closeCursor(data);
                    closeDB();
                }
            }
        }).start();
    }

}

package frankowicz.damian.projektinz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.List;

import frankowicz.damian.projektinz.Model.User;

public class SqLiteDb {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "TestDB.db";
    private static final String DB_TABLE = "users";
    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static final String DB_CREATE_TODO_TABLE =
            "CREATE TABLE " + DB_TABLE + "(id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT);";


    public SqLiteDb(Context context) {
        this.context = context;
    }

    public SqLiteDb open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void addUser(User user){
        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        db.insertOrThrow(DB_TABLE, null, values);
    }

    public Cursor getAll() {
        String[] columns = {"id", "first_name", "last_name"};
        return db.query(DB_TABLE, columns, null, null, null, null, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        List<User> testUsers = Arrays.asList(
                new User("Adam", "Joe"),
                new User("Joe", "Smith"),
                new User("Greg", "Novak"),
                new User("Adam", "Joe"),
                new User("Joe", "Smith"),
                new User("Greg", "Novak"),
                new User("Adam", "Joe"),
                new User("Joe", "Smith"),
                new User("Greg", "Novak"),
                new User("Adam", "Joe"));

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TODO_TABLE);

            System.out.print("Database creating...");
            System.out.print("Table " + DB_TABLE + " ver." + DB_VERSION + " created");

            for (User user : testUsers) {
                ContentValues values = new ContentValues();
                values.put("first_name", user.getFirstName());
                values.put("last_name", user.getLastName());
                db.insertOrThrow(DB_TABLE, null, values);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

            System.out.print("Database updating...");
            System.out.print("Table " + DB_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            System.out.print("All data is lost.");

            onCreate(db);
        }
    }
}


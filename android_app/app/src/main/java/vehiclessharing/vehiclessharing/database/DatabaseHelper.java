package vehiclessharing.vehiclessharing.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import vehiclessharing.vehiclessharing.model.BirthDay;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.model.UserAddress;
import vehiclessharing.vehiclessharing.model.UserInfo;


/**
 * Created by Tuan on 16/04/2017.
 */

/**
 * This class create to using SQLite
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // private static final String TAG = "DatabaseHelper";
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "vehicles_sharing.db";
    private static final int DATABASE_VERSION = 1;

    //TABLE users
    private static final String TABLE_USER = "users";
    private static final String USER_ID = "user_id";
    private static final String FULL_NAME_COLUMN = "full_name";
    private static final String PHONE_NUMBER_COLUMN = "phone_number";
    private static final String EMAIL_COLUMN = "email";
    private static final String THUMB_LINK_COLUMN = "thumb_link";
    private static final String GENDER_COLUMN = "gender";
    private static final String ADDRESS_COLUMN = "address";
    private static final String BIRTHDAY_COLUMN = "birthday";

    private static final String CREATE_USER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" +
            USER_ID + " TEXT NOT NULL PRIMARY KEY," +
            FULL_NAME_COLUMN + " TEXT NOT NULL," +
            PHONE_NUMBER_COLUMN + " TEXT NOT NULL," +
            EMAIL_COLUMN + " TEXT," +
            THUMB_LINK_COLUMN + " TEXT," +
            GENDER_COLUMN + " TEXT NOT NULL," +
            ADDRESS_COLUMN + " TEXT," +
            BIRTHDAY_COLUMN + " TEXT" +
            ")";

    private static DatabaseHelper sInstance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e(TAG, "DatabaseHelper: ");
    }

    @Override

    /**
     * Thực thi các câu lệnh tạo bảng
     * Sử dụng db.execSQL(sql); chạy câu lênh sql tạo bảng
     */
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e(TAG, "onCreate: ");
        sqLiteDatabase.execSQL(CREATE_USER_TABLE_SQL);

    }

    /**
     * Gọi khi bạn thay đổi DATABASE_VERSION
     * Thường sử dụng để thay đổi cấu trúc bảng (ALTER, DROP, ADD CONSTRAIN...)
     *
     * @param sqLiteDatabase instance SQLite databse
     * @param i              old version
     * @param i1             new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.e(TAG, "onUpgrade: ");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BIRTHDAY);
//        onCreate(sqLiteDatabase);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Check user exists or not exists on device
     *
     * @param userId
     * @return true - exists | false - not exists
     */
    public boolean isUserExists(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;
        /*Cursor cursor = db.query(TABLE_USER, new String[]{EMAIL_COLUMN,
                        IMAGE_COLUMN,
                        FULL_NAME_COLUMN,
                        PHONE_NUMBER_COLUMN,
                        SEX_COLUMN}, USER_ID + " = ?",
                new String[]{userId}, null, null, null);
        if (cursor != null && cursor.moveToFirst())
            exists = true;
        cursor.close();*/
        db.close();
        return exists;
    }


    /**
     * Insert data into SQLite
     *
     * @param user   object to storage
     * @param
     * @return true if insert success
     */
    public boolean insertUser(UserInfo user) {
        boolean insertResult = false;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();//Sử dụng đối tượng ContentValues để put các giá trị ứng với tên colum và sử dụng phương thức insert của SQLiteDatabase để tiến hành ghi xuống database.
            values.put(USER_ID, user.getId());
            values.put(FULL_NAME_COLUMN, user.getName());
            values.put(PHONE_NUMBER_COLUMN, user.getPhone());
            values.put(GENDER_COLUMN, user.getGender());
            if (user.getAddress() != null && !user.getAddress().equals("")) {
                values.put(ADDRESS_COLUMN, user.getAddress());
            }
            if (user.getBirthday() != null && !user.getBirthday().equals("")) {
                values.put(BIRTHDAY_COLUMN, user.getBirthday());

            }
            if (user.getEmail() != null && !user.getEmail().equals("")) {
                values.put(EMAIL_COLUMN, user.getEmail());
            }
            if (user.getAvatarLink() != null && !user.getAvatarLink().equals("")) {
                values.put(THUMB_LINK_COLUMN, user.getAvatarLink());
            }
            long rowId = db.insert(TABLE_USER, null, values);
            insertResult = true;
            db.close();
        } catch (Exception e) {
            Log.d(TAG, "DBException");
            //insertResult=false;
        }
        //success
        return insertResult;
    }

    /**
     * Select data from SQLite
     *
     * @param userId
     * @return object User
     */
    public UserInfo getUser(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        UserInfo user = null;
        Cursor cursor = db.query(TABLE_USER, new String[]{
                        USER_ID,
                        FULL_NAME_COLUMN,
                        PHONE_NUMBER_COLUMN,
                        GENDER_COLUMN,
                        ADDRESS_COLUMN,
                        BIRTHDAY_COLUMN,
                        EMAIL_COLUMN,
                        THUMB_LINK_COLUMN}, USER_ID + " = ?",
                new String[]{userId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user.setName(cursor.getString(cursor.getColumnIndex(USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(FULL_NAME_COLUMN)));
            user.setGender(cursor.getInt(cursor.getColumnIndex(GENDER_COLUMN)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER_COLUMN)));
            if(cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMN))!=null)
            {
                user.setAddress(cursor.getColumnName(cursor.getColumnIndex(ADDRESS_COLUMN)));
            }
            if (cursor.getString(cursor.getColumnIndex(BIRTHDAY_COLUMN))!=null)
            {
                user.setBirthday(cursor.getColumnName(cursor.getColumnIndex(BIRTHDAY_COLUMN)));
            }
            if(cursor.getString(cursor.getColumnIndex(THUMB_LINK_COLUMN))!=null)
            {
                user.setAvatarLink(THUMB_LINK_COLUMN);
            }
           // user = new UserInfo(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), null, null);
            //Get Addresscursor.close();
        }
        db.close();
        return user;
    }

    /**
     * update fullname's user
     *
     * @param userId
     * @param value  new value of fullname
     * @return
     */
    public boolean updateFullName(String userId, String value) {
        if (value == null || value.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(FULL_NAME_COLUMN, value);
        int ret = db.update(TABLE_USER, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update phonenumber's user
     *
     * @param userId
     * @param value  new value of phonenumber
     * @return
     */
    public boolean updatePhoneNumber(String userId, String value) {
        if (value == null || value.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHONE_NUMBER_COLUMN, value);
        int ret = db.update(TABLE_USER, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }

    /**
     * update sex's user
     *
     * @param userId
     * @param value  new value of sex
     * @return
     */
    /*public boolean updateSex(String userId, String value) {
        if (value == null || value.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(SEX_COLUMN, value);
        int ret = db.update(TABLE_USER, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }*/

    /**
     * update country's user
     *
     * @param userId
     * @param value  new value of country
     * @return
     */
    /*public boolean updateCountry(String userId, String value) {
        if (value == null) value = "";
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COUNTRY_COLUMN, value);
        int ret = db.update(TABLE_ADDRESS, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }
*/
    /**
     * update province's user
     *
     * @param userId
     * @param value  new value of province
     * @return
     */
  /*  public boolean updateProvince(String userId, String value) {
        if (value == null) value = "";
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROVINCE_COLUMN, value);
        int ret = db.update(TABLE_ADDRESS, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }
*/
    /**
     * update district's user
     *
     * @param userId
     * @param value  new value of district
     * @return
     */
  /*  public boolean updateDistrict(String userId, String value) {
        if (value == null) value = "";
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DISTRICT_COLUMN, value);
        int ret = db.update(TABLE_ADDRESS, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }
*/
    /**
     * update day's birthday user
     *
     * @param userId
     * @param value  new value of day
     * @return
     */
  /*  public boolean updateDay(String userId, int value) {
        if (value <= 0) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DAY_COLUMN, value);
        int ret = db.update(TABLE_BIRTHDAY, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }

    *//**
     * update month's birthday user
     *
     * @param userId
     * @param value  new value of month
     * @return
     *//*
    public boolean updateMonth(String userId, int value) {
        if (value <= 0) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MONTH_COLUMN, value);
        int ret = db.update(TABLE_BIRTHDAY, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }

    *//**
     * update year's birthday user
     *
     * @param userId
     * @param value  new value of year
     * @return
     *//*
    public boolean updateYear(String userId, int value) {
        if (value <= 0) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(YEAR_COLUMN, value);
        int ret = db.update(TABLE_BIRTHDAY, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }

    *//**
     * update url avatar user
     *
     * @param userId
     * @param url
     * @return
     *//*
    public boolean uploadURLImage(String userId, String url) {
        if (url.isEmpty()) return false;
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_COLUMN, url);
        int ret = db.update(TABLE_USER, values, USER_ID + "=?", new String[]{userId});
        if (ret != 0) return true;//success
        return false;//failed
    }*/
}

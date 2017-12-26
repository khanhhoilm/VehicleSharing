package vehiclessharing.vehiclessharing.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import vehiclessharing.vehiclessharing.controller.activity.MainActivity;
import vehiclessharing.vehiclessharing.model.LatLngLocation;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.utils.Helper;

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
    private static final String THUMB_BLOB_COLUMN = "thumb_blob";

    //Table request
    private static final String TABLE_REQUEST = "requests_sharing";
    private static final String SOURCE_LOCATION = "source_location";
    private static final String DESTINATION_LOCATION = "destination_location";
    private static final String TIME_START = "time_start";
    private static final String VEHICLE_TYPE = "vehicle_type";


    private static final String CREATE_USER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" +
            USER_ID + " INTEGER NOT NULL PRIMARY KEY," +
            FULL_NAME_COLUMN + " TEXT NOT NULL," +
            PHONE_NUMBER_COLUMN + " TEXT NOT NULL," +
            EMAIL_COLUMN + " TEXT," +
            THUMB_LINK_COLUMN + " TEXT," +
            THUMB_BLOB_COLUMN + " BLOB," +
            GENDER_COLUMN + " TEXT NOT NULL," +
            ADDRESS_COLUMN + " TEXT," +
            BIRTHDAY_COLUMN + " TEXT" +
            ")";
    private static final String CREATE_REQUEST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUEST + " (" +
            USER_ID + " INTEGER NOT NULL PRIMARY KEY," +
            SOURCE_LOCATION + " TEXT NOT NULL," +
            DESTINATION_LOCATION + " TEXT NOT NULL," +
            TIME_START + " TEXT," +
            VEHICLE_TYPE + " INTEGER" +
            ")";


    private static DatabaseHelper sInstance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e(TAG, "DatabaseHelper: ");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e(TAG, "onCreate: ");
        sqLiteDatabase.execSQL(CREATE_USER_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_REQUEST_TABLE);

    }

    /**
     * @param sqLiteDatabase instance SQLite databse
     * @param i              old version
     * @param i1             new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.e(TAG, "onUpgrade: ");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST);

        onCreate(sqLiteDatabase);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static String GET_CURRENT_USER(int userId) {
        String query = "";
        query = "SELECT * FROM " +
                TABLE_USER + " WHERE " + USER_ID + " = " + userId;
        // rs = GET_ALL_ARTICLE_SAVED;
        return query;
    }

    public static String GET_REQUEST_INFO(int userId) {
        String query = "SELECT * FROM " + TABLE_REQUEST + " WHERE " + USER_ID + " = " + userId;
        return query;
    }

    public static String GET_REQUEST_INFO_NOT_ME(int userId) {
        String query = "SELECT * FROM " + TABLE_REQUEST + " WHERE " + USER_ID + " <> " + userId;
        return query;
    }

    /**
     * Check user exists or not exists on device
     *
     * @param userId
     * @return true - exists | false - not exists
     */
    public boolean isUserExists(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;
        Cursor cursor = db.rawQuery(GET_CURRENT_USER(userId), null);
        if (cursor.getCount() > 0) {
            exists = true;
        }
        //db.close();
        return exists;
    }

    public boolean isRequestExists(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exists = false;
        Cursor cursor = db.rawQuery(GET_REQUEST_INFO(userId), null);
        if (cursor.getCount() > 0) {
            exists = true;
        }
        //db.close();
        return exists;
    }


    public int sumRecordInUserDB() {
        int numberRow = 0;
        SQLiteDatabase db = getReadableDatabase();
        String checkRow = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(checkRow, null);
        if (cursor.getCount() > 0) {
            numberRow = cursor.getCount();
        }
        return numberRow;
    }

    public int sumRecordInRequestDB() {
        int numberRow = 0;
        SQLiteDatabase db = getReadableDatabase();
        String checkRow = "SELECT * FROM " + TABLE_REQUEST;
        Cursor cursor = db.rawQuery(checkRow, null);
        if (cursor.getCount() > 0) {
            numberRow = cursor.getCount();
        }
        return numberRow;
    }

    /**
     * Insert data into SQLite
     *
     * @param user object to storage
     * @param
     * @return true if insert success
     */
    public boolean insertUser(User user) {
        boolean insertResult = false;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            if (sumRecordInUserDB() > 0) {
                String deleteQuery = "DELETE FROM " + TABLE_USER;
                db.execSQL(deleteQuery);
            }
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
            if (user.getPicture() != null && !user.getPicture().equals("")) {
                values.put(THUMB_BLOB_COLUMN, Helper.getPictureByteOfArray(user.getPicture()));
            }
            long rowId = db.insert(TABLE_USER, null, values);
            insertResult = true;
            //      this.close();
            //      db.close();
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
    public User getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = new User();
        Cursor cursor = db.rawQuery(GET_CURRENT_USER(userId), null);
        try {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                user.setId(cursor.getInt(cursor.getColumnIndex(USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(FULL_NAME_COLUMN)));
                user.setGender(cursor.getInt(cursor.getColumnIndex(GENDER_COLUMN)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER_COLUMN)));
                if (cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMN)) != null) {
                    user.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMN)));
                }
                if (cursor.getString(cursor.getColumnIndex(BIRTHDAY_COLUMN)) != null) {
                    user.setBirthday(cursor.getString(cursor.getColumnIndex(BIRTHDAY_COLUMN)));
                }
                if (cursor.getString(cursor.getColumnIndex(THUMB_LINK_COLUMN)) != null) {
                    user.setAvatarLink(cursor.getString(cursor.getColumnIndex(THUMB_LINK_COLUMN)));
                }
                if (cursor.getBlob(cursor.getColumnIndex(THUMB_BLOB_COLUMN)) != null) {
                    user.setPicture(Helper.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(THUMB_BLOB_COLUMN))));
                }
                // user = new UserInfo(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), null, null);
                //Get Addresscursor.close();
            }
        } catch (Exception e) {
            Log.d("Exception", "DB error");
        }
        // this.close();
        //    db.close();
        return user;
    }

    public boolean updateInfoUser(User userInfo) {
        boolean updateSuccess = false;
        SQLiteDatabase database = this.getWritableDatabase();
        //synchronized (database) {

        ContentValues contentValues = new ContentValues();
        try {
            if (isUserExists(userInfo.getId())) {
                contentValues.put(FULL_NAME_COLUMN, userInfo.getName());
                //          contentValues.put(PHONE_NUMBER_COLUMN, userInfo.getPhone());
                contentValues.put(GENDER_COLUMN, userInfo.getGender());
                if (userInfo.getBirthday() != null && !userInfo.getBirthday().equals("")) {
                    contentValues.put(BIRTHDAY_COLUMN, userInfo.getBirthday());
                }
                if (userInfo.getAddress() != null && !userInfo.getAddress().equals("")) {
                    contentValues.put(ADDRESS_COLUMN, userInfo.getAddress());
                }
                if (userInfo.getPicture() != null && !userInfo.getPicture().equals("")) {
                    contentValues.put(THUMB_BLOB_COLUMN, Helper.getPictureByteOfArray(userInfo.getPicture()));
                }
                String selection = USER_ID + " = " + userInfo.getId();
                database.update(TABLE_USER, contentValues, selection, null);
                updateSuccess = true;
            }
        } catch (Exception e) {
            Log.d("UpdateUserInfo", "Exception: ");
        }
        //}
        return updateSuccess;
    }


    public boolean insertRequest(RequestInfo requestInfo, int userId) {
        boolean insertResult = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            if (isRequestExists(userId)) {
                deleteRequest(userId);
            }
                if (!isRequestExists(userId)) {
                ContentValues values = new ContentValues();
                values.put(USER_ID, userId);
                values.put(SOURCE_LOCATION, requestInfo.getSourceLocation().convertLatLngToStringToDatabase());
                values.put(DESTINATION_LOCATION, requestInfo.getDestLocation().convertLatLngToStringToDatabase());
                values.put(TIME_START, requestInfo.getTimeStart());
                values.put(VEHICLE_TYPE, requestInfo.getVehicleType());
                long rowId = db.insert(TABLE_REQUEST, null, values);
                insertResult = true;
            }
        } catch (Exception e) {
            Log.d(TAG, "DBException");
        }
        return insertResult;
    }

    public boolean insertRequestNotMe(RequestInfo requestInfo,int userId){
        boolean rs=false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(GET_REQUEST_INFO_NOT_ME(MainActivity.userId), null);
            if(cursor.getCount()>0){
                deleteAllRequestExceptMe(MainActivity.userId);
            }
            if (!isRequestExists(userId)) {
                ContentValues values = new ContentValues();
                values.put(USER_ID, userId);
                values.put(SOURCE_LOCATION, requestInfo.getSourceLocation().convertLatLngToStringToDatabase());
                values.put(DESTINATION_LOCATION, requestInfo.getDestLocation().convertLatLngToStringToDatabase());
                values.put(TIME_START, requestInfo.getTimeStart());
                values.put(VEHICLE_TYPE, requestInfo.getVehicleType());
                long rowId = db.insert(TABLE_REQUEST, null, values);
            rs=true;
            }
        } catch (Exception e) {
            Log.d(TAG, "DBException");
        }
        return rs;
    }

    public boolean updateRequest(RequestInfo requestInfo, int userId) {
        boolean updateSuccess = false;
        SQLiteDatabase database = this.getWritableDatabase();
        //synchronized (database) {

        ContentValues contentValues = new ContentValues();
        try {
            if (isRequestExists(userId)) {
                contentValues.put(SOURCE_LOCATION, requestInfo.getSourceLocation().convertLatLngToStringToDatabase());
                contentValues.put(DESTINATION_LOCATION, requestInfo.getDestLocation().convertLatLngToStringToDatabase());
                contentValues.put(TIME_START, requestInfo.getTimeStart());
                contentValues.put(VEHICLE_TYPE, requestInfo.getVehicleType());
                String selection = USER_ID + " = " + userId;
                database.update(TABLE_USER, contentValues, selection, null);
                updateSuccess = true;
            }
        } catch (Exception e) {
            Log.d("UpdateUserInfo", "Exception: ");
        }
        return updateSuccess;
    }

    public RequestInfo getRequestInfo(int userId) {
        RequestInfo requestInfo = new RequestInfo();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(GET_REQUEST_INFO(userId), null);
        try {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                requestInfo.setUserId(cursor.getInt(cursor.getColumnIndex(USER_ID)));
                requestInfo.setSourceLocation(LatLngLocation.convertStringFromDatabaseToLatLng(cursor.getString(cursor.getColumnIndex(SOURCE_LOCATION))));
                requestInfo.setDestLocation(LatLngLocation.convertStringFromDatabaseToLatLng(cursor.getString(cursor.getColumnIndex(DESTINATION_LOCATION))));
                requestInfo.setTimeStart(cursor.getString(cursor.getColumnIndex(TIME_START)));
                requestInfo.setVehicleType(cursor.getInt(cursor.getColumnIndex(VEHICLE_TYPE)));
            }
        } catch (Exception e) {
            Log.d("Exception", "DB error");
        }
        return requestInfo;
    }

    public RequestInfo getRequestInfoNotMe(int userId) {
        RequestInfo requestInfo = new RequestInfo();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(GET_REQUEST_INFO_NOT_ME(userId), null);
        try {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                requestInfo.setUserId(cursor.getInt(cursor.getColumnIndex(USER_ID)));
                requestInfo.setSourceLocation(LatLngLocation.convertStringFromDatabaseToLatLng(cursor.getString(cursor.getColumnIndex(SOURCE_LOCATION))));
                requestInfo.setDestLocation(LatLngLocation.convertStringFromDatabaseToLatLng(cursor.getString(cursor.getColumnIndex(DESTINATION_LOCATION))));
                requestInfo.setTimeStart(cursor.getString(cursor.getColumnIndex(TIME_START)));
                requestInfo.setVehicleType(cursor.getInt(cursor.getColumnIndex(VEHICLE_TYPE)));

            }
        } catch (Exception e) {
            Log.d("Exception", "DB error");
        }
        return requestInfo;
    }

    public boolean deleteRequest(int userId) {
        boolean result = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            if (isRequestExists(userId)) {
                String query = "DELETE FROM " + TABLE_REQUEST + " WHERE " + USER_ID + " = " + userId;
                sqLiteDatabase.execSQL(query);
                result = true;
            }
        } catch (Exception e) {

        }
        return result;
    }
    public boolean deleteAllRequestExceptMe(int userId) {
        boolean result = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            if (isRequestExists(userId)) {
                String query = "DELETE FROM " + TABLE_REQUEST + " WHERE " + USER_ID + " <> " + userId;
                sqLiteDatabase.execSQL(query);
                result = true;
            }
        } catch (Exception e) {

        }
        return result;
    }
}

/*
package vehiclessharing.vehiclessharing.database;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import vehiclessharing.vehiclessharing.model.UserOnDevice;

public class RealmDatabase {
    private static Realm realm = Realm.getDefaultInstance();


    public RealmDatabase() {
    }

    */
/**
     * Luu data tren device
     *//*

    public static void storageOnDiviceRealm(UserOnDevice userOnDevice) {
        realm.beginTransaction();
        UserOnDevice temp = realm.copyToRealm(userOnDevice);
        realm.commitTransaction();
        Log.d("INSERT_OFFLINE", "aaaa");
    }

    public static RealmResults<UserOnDevice> getListData() {
        RealmQuery<UserOnDevice> query = realm.where(UserOnDevice.class);
        RealmResults<UserOnDevice> result1 = query.findAll();
        return result1;
    }

    public static UserOnDevice getCurrentUser(int userId) {
        RealmResults<UserOnDevice> result1 = realm.where(UserOnDevice.class).equalTo("userId", userId).findAll();
        if (result1.size() == 0) return null;
        return result1.get(0);
    }

    */
/**
     * Delete data in Realm
     *
     * @param target
     *//*

    public void deteleFirst(int target) {
        RealmResults<UserOnDevice> results = realm.where(UserOnDevice.class).findAll();
        realm.beginTransaction();
        // remove a single object
        UserOnDevice a = results.get(target);
        a.deleteFromRealm();
        realm.commitTransaction();
    }

    public void deteleAll() {
        RealmResults<UserOnDevice> results = realm.where(UserOnDevice.class).findAll();
        realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }


    */
/**
     * Check user is exists on device
     *
     * @param userId
     * @return true if exists
     *//*

    public boolean isUserExists(String userId) {
        // Build the query looking at all users:
        RealmQuery<UserOnDevice> query = realm.where(UserOnDevice.class);

        // Add query conditions:
        query.equalTo("userId", userId);

        // Execute the query:
        RealmResults<UserOnDevice> result1 = query.findAll();
        if (result1.size() > 0) return true;
        return false;
    }

    */
/**
     * Update url image's user
     *
     * @param url
     *//*

    public void updateImageUser(String url) {
        realm.beginTransaction();

        realm.commitTransaction();
    }

}
*/

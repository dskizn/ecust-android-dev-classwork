package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LoginApp.db";
    private static final int DATABASE_VERSION = 2; // 版本号升级

    // 用户表
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_IS_ADMIN = "is_admin"; // 新增管理员标识字段

    // 好友表
    private static final String TABLE_FRIENDS = "friends";
    private static final String COLUMN_FRIEND_ID = "friend_id";
    private static final String COLUMN_FRIEND_NAME = "name";
    private static final String COLUMN_FRIEND_STATUS = "status";
    private static final String COLUMN_FRIEND_AVATAR = "avatar";
    private static final String COLUMN_IS_ONLINE = "is_online";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表（新增is_admin字段）
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_AVATAR + " INTEGER,"
                + COLUMN_IS_ADMIN + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // 创建好友表
        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + "("
                + COLUMN_FRIEND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FRIEND_NAME + " TEXT,"
                + COLUMN_FRIEND_STATUS + " TEXT,"
                + COLUMN_FRIEND_AVATAR + " INTEGER,"
                + COLUMN_IS_ONLINE + " INTEGER" + ")";
        db.execSQL(CREATE_FRIENDS_TABLE);

        // 插入初始数据
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 升级数据库，添加is_admin字段
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_IS_ADMIN + " INTEGER DEFAULT 0");
            // 重新插入管理员账号
            insertAdminUser(db);
        }
    }

    private void insertInitialData(SQLiteDatabase db) {
        // 插入管理员账号
        insertAdminUser(db);

        // 插入示例普通用户
        insertUser(db, "user1", "password123", R.drawable.avatar2, 0);
        insertUser(db, "test", "test123", R.drawable.avatar3, 0);

        // 插入好友数据
        ContentValues values = new ContentValues();
        String[] friendNames = {"user1", "user2", "user3", "user4", "user5"};
        String[] statuses = {"休息一下", "学习中...", "忙碌中，请勿打扰", "在线", "刚刚更新了动态"};
        int[] avatars = {R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8};
        boolean[] onlineStatus = {true, true, false, true, false};

        for (int i = 0; i < friendNames.length; i++) {
            values.clear();
            values.put(COLUMN_FRIEND_NAME, friendNames[i]);
            values.put(COLUMN_FRIEND_STATUS, statuses[i]);
            values.put(COLUMN_FRIEND_AVATAR, avatars[i]);
            values.put(COLUMN_IS_ONLINE, onlineStatus[i] ? 1 : 0);
            db.insert(TABLE_FRIENDS, null, values);
        }
    }

    private void insertAdminUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "管理员");
        values.put(COLUMN_PASSWORD, "123456");
        values.put(COLUMN_AVATAR, R.drawable.avatar1);
        values.put(COLUMN_IS_ADMIN, 1); // 设置为管理员
        db.insert(TABLE_USERS, null, values);
    }

    private void insertUser(SQLiteDatabase db, String username, String password, int avatarResId, int isAdmin) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_AVATAR, avatarResId);
        values.put(COLUMN_IS_ADMIN, isAdmin);
        db.insert(TABLE_USERS, null, values);
    }

    // 用户操作
    public long insertUser(String username, String password, int avatarResId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_AVATAR, avatarResId);
        values.put(COLUMN_IS_ADMIN, 0); // 普通用户
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean isAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_IS_ADMIN};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean isAdmin = false;
        if (cursor.moveToFirst()) {
            isAdmin = cursor.getInt(0) == 1;
        }
        cursor.close();
        return isAdmin;
    }

    public int getUserAvatar(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_AVATAR};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int avatar = cursor.getInt(0);
            cursor.close();
            return avatar;
        }
        cursor.close();
        return R.drawable.avatar1;
    }

    // 更新用户头像
    public boolean updateUserAvatar(String username, int avatarResId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AVATAR, avatarResId);

        int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        return result > 0;
    }

    // 根据用户名获取用户信息
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME, COLUMN_AVATAR, COLUMN_IS_ADMIN};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(0), // username
                    cursor.getInt(1),    // avatar
                    cursor.getInt(2) == 1 // isAdmin
            );
        }
        cursor.close();
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME, COLUMN_AVATAR, COLUMN_IS_ADMIN};

        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getString(0), // username
                        cursor.getInt(1),    // avatar
                        cursor.getInt(2) == 1 // isAdmin
                );
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public int deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COLUMN_USERNAME + " = ?", new String[]{username});
    }

    // 好友操作
    public List<Friend> getAllFriends() {
        List<Friend> friends = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_FRIEND_ID, COLUMN_FRIEND_NAME, COLUMN_FRIEND_STATUS,
                COLUMN_FRIEND_AVATAR, COLUMN_IS_ONLINE};

        Cursor cursor = db.query(TABLE_FRIENDS, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Friend friend = new Friend(
                        cursor.getString(1), // name
                        cursor.getString(2), // status
                        cursor.getInt(3),    // avatar
                        cursor.getInt(4) == 1 // isOnline
                );
                friends.add(friend);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friends;
    }

    // 修改用户名
    public boolean updateUsername(String oldUsername, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 检查新用户名是否已存在
        if (isUsernameExists(newUsername)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);

        int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{oldUsername});
        return result > 0;
    }

    // 检查用户名是否存在
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 获取用户总数
    public int getTotalUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // 获取管理员数量
    public int getAdminUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COLUMN_IS_ADMIN + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}
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
    private static final int DATABASE_VERSION = 3; // 版本号升级

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
    // 聊天信息表
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_RECEIVER = "receiver";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_IS_READ = "is_read";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
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

        // 创建消息表
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SENDER + " TEXT,"
                + COLUMN_RECEIVER + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_IS_READ + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);

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
        insertUser(db, "jack", "jack123", R.drawable.avatar4, 0);
        insertUser(db, "lily", "lily123", R.drawable.avatar5, 0);
        insertUser(db, "tom", "tom123", R.drawable.avatar6, 0);

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


    // 添加获取所有用户作为好友的方法（包括当前用户）
    public List<Friend> getAllUsersAsFriends() {
        List<Friend> friends = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_USERNAME, COLUMN_AVATAR, COLUMN_IS_ADMIN};

        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                int avatarResId = cursor.getInt(1);
                boolean isAdmin = cursor.getInt(2) == 1;

                String[] statusOptions = {
                        "在线学习中", "休息一下", "忙碌中", "刚刚上线",
                        "离开中", "正在会议", "专注工作", "放松一下"
                };
                String randomStatus = statusOptions[(int)(Math.random() * statusOptions.length)];
                // 直接使用用户名
                String displayName = username;

                Friend friend = new Friend(
                        displayName,        // 显示名称
                        randomStatus,       // 随机状态
                        avatarResId,        // 用户头像
                        false               // 设置为离线状态
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

    // 保存消息到数据库
    public long saveMessage(String sender, String receiver, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_RECEIVER, receiver);
        values.put(COLUMN_CONTENT, content);

        long result = db.insert(TABLE_MESSAGES, null, values);
        db.close();
        return result;
    }

    // 获取两个用户之间的聊天记录
    public List<Message> getChatMessages(String user1, String user2) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_MESSAGES
                + " WHERE (" + COLUMN_SENDER + " = ? AND " + COLUMN_RECEIVER + " = ?)"
                + " OR (" + COLUMN_SENDER + " = ? AND " + COLUMN_RECEIVER + " = ?)"
                + " ORDER BY " + COLUMN_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{user1, user2, user2, user1});

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setMessageId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)));
                message.setSender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER)));
                message.setReceiver(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER)));
                message.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                message.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                message.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1);

                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    // 获取用户的所有未读消息数量
    public int getUnreadMessageCount(String currentUsername, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_MESSAGES
                + " WHERE " + COLUMN_RECEIVER + " = ? AND " + COLUMN_IS_READ + " = 0";

        Cursor cursor = db.rawQuery(query, new String[]{username});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // 标记消息为已读
    public void markMessagesAsRead(String sender, String receiver) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);

        db.update(TABLE_MESSAGES, values,
                COLUMN_SENDER + " = ? AND " + COLUMN_RECEIVER + " = ? AND " + COLUMN_IS_READ + " = 0",
                new String[]{sender, receiver});
        db.close();
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
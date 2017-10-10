package com.jc.sb_pos.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "sb-api-db";

	// User table name (used on Login)
	private static final String TABLE_USER = "user";

	// User cart table name (used to shopping)
	private static final String TABLE_USER_CART = "user_cart";

	// User Table Columns names (used on Login)
	private static final String KEY_ID = "id";
	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_USER_NAME = "user_name";
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_ADDRESS = "address";
	private static final String KEY_CONTACT_NUMBER = "contact_number";
	private static final String KEY_BALANCE = "balance";
	private static final String KEY_API_KEY = "api_key";
	private static final String KEY_STATUS = "status";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_UPDATED_AT = "updated_at";

	// User Cart Table Columns names (used to shopping)
	private static final String KEY_CART_ID = "id";
	private static final String KEY_CART_CODE = "cart_code";
	private static final String KEY_CART_USER_ID = "user_id";
	private static final String KEY_CART_PRODUCT_ID = "product_id";
	private static final String KEY_CART_PRODUCT_SINGLE_PRICE = "product_single_price";
	private static final String KEY_CART_PRODUCT_QUANTITY = "product_quantity";
	private static final String KEY_CART_PRODUCT_TOTAL_PRICE = "product_total_price";
	private static final String KEY_CART_CREATED_AT = "created_at";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Sql Queries

		// Table User
		String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_USER_ID + " TEXT,"
				+ KEY_USER_NAME + " TEXT,"
				+ KEY_FIRST_NAME + " TEXT,"
				+ KEY_LAST_NAME + " TEXT,"
				+ KEY_EMAIL + " TEXT UNIQUE,"
				+ KEY_ADDRESS + " TEXT,"
				+ KEY_CONTACT_NUMBER + " TEXT,"
				+ KEY_BALANCE + " TEXT,"
				+ KEY_API_KEY + " TEXT,"
				+ KEY_STATUS + " TEXT,"
				+ KEY_CREATED_AT + " TEXT,"
				+ KEY_UPDATED_AT + " TEXT" + ")";

		// Table Cart
		String CREATE_USER_CART = "CREATE TABLE " + TABLE_USER_CART + "("
				+ KEY_CART_ID + " INTEGER PRIMARY KEY,"
				+ KEY_CART_CODE + " TEXT,"
				+ KEY_CART_USER_ID + " TEXT,"
				+ KEY_CART_PRODUCT_ID + " TEXT,"
				+ KEY_CART_PRODUCT_SINGLE_PRICE + " TEXT,"
				+ KEY_CART_PRODUCT_QUANTITY + " TEXT,"
				+ KEY_CART_PRODUCT_TOTAL_PRICE + " TEXT,"
				+ KEY_CART_CREATED_AT + " TEXT" + ")";

		// Running Sql Queries
		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_USER_CART);

		Log.d(TAG, "Database tables created.");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older tables if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_CART);

		// Create tables again
		onCreate(db);

		Log.d(TAG, "Database tables upgraded.");
	}

	/**
	 * This will insert new row on the user table
	 * */

	public void addUser(String user_id, String user_name, String first_name, String last_name, String email, String address, String contact_number, String balance, String api_key, String status, String created_at, String updated_at) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_USER_ID, user_id); // User's User ID (ID from db)
		values.put(KEY_USER_NAME, user_name); // User's User Name
		values.put(KEY_FIRST_NAME, first_name); // User's First Name
		values.put(KEY_LAST_NAME, last_name); // User's Last Name
		values.put(KEY_EMAIL, email); // User's Email
		values.put(KEY_ADDRESS, address); // User's Address
		values.put(KEY_CONTACT_NUMBER, contact_number); // User's Contact Number
		values.put(KEY_BALANCE, balance); // User's Balance
		values.put(KEY_API_KEY, api_key); // User's Api Key
		values.put(KEY_STATUS, status); // User's Status
		values.put(KEY_CREATED_AT, created_at); // User Created At
		values.put(KEY_UPDATED_AT, updated_at); // User Updated At

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		// Closing database connection
		db.close();

		Log.d(TAG, "New user inserted into SQLite db: " + id);
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// Move to first row
		cursor.moveToFirst();

		if (cursor.getCount() > 0) {

			user.put("user_id", cursor.getString(1));
			user.put("user_name", cursor.getString(2));
			user.put("first_name", cursor.getString(3));
			user.put("last_name", cursor.getString(4));
			user.put("email", cursor.getString(5));
			user.put("address", cursor.getString(6));
			user.put("contact_number", cursor.getString(7));
			user.put("balance", cursor.getString(8));
			user.put("api_key", cursor.getString(9));
			user.put("status", cursor.getString(10));
			user.put("created_at", cursor.getString(11));
			user.put("updated_at", cursor.getString(12));
		}
		// Closing cursor
		cursor.close();
		// Closing database connection
		db.close();

		// return user
		Log.d(TAG, "Getting user from SQLite db: " + user.toString());

		return user;
	}

	/**
	 * Re-create database. Delete user table and create it again
	 * */
	public void deleteUser() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from SQLite db.");
	}

	/**
	 * This will insert new row on the cart table
	 * */

	public void addProduct(String cart_code, String user_id, String product_id, String product_single_price, String product_quantity, String product_total_price, String created_at) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_CART_CODE, cart_code); // Cart Randomly Generated Code
		values.put(KEY_CART_USER_ID , user_id); // User ID
		values.put(KEY_CART_PRODUCT_ID, product_id); // Product ID
		values.put(KEY_CART_PRODUCT_SINGLE_PRICE, product_single_price); // Product (Single) Price
		values.put(KEY_CART_PRODUCT_QUANTITY, product_quantity); // Product Quantity
		values.put(KEY_CART_PRODUCT_TOTAL_PRICE, product_total_price); // Product Total Price (Product (Single) Price x Product Quantity)
		values.put(KEY_CART_CREATED_AT, created_at); // Date/Time

		// Inserting Row
		long id = db.insert(TABLE_USER_CART, null, values);
		// Closing database connection
		db.close();

		Log.d(TAG, "New product inserted into SQLite db: " + id);
	}

	/**
	 * Re-create database. Delete cart table and create it again
	 * */
	public void deleteCart() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER_CART, null, null);
		db.close();

		Log.d(TAG, "Deleted all cart info from SQLite db.");
	}

}

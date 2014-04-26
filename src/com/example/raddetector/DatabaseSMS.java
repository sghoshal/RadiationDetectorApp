package com.example.raddetector;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.em;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSMS extends SQLiteOpenHelper{
	private static DatabaseSMS instance;

	public static synchronized DatabaseSMS getInstance(Context context) {
		if (instance == null)
			instance = new DatabaseSMS(context);
		return instance;
	}
	// Database Version
	private static final int DATABASE_VERSION = 1;
	private Cursor cursor;
	// Database Name
	private static final String DATABASE_NAME = "SMSDB";
	// Contacts table name
	private static final String TABLE_CONTACT = "SMSContacts";

	// Contacts Table Columns names
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String PHONE_NUMBER = "phone";
	private static final String EMAIL = "email";

	private static String CREATE_CONTACTS_TABLE = 
			"CREATE TABLE " + TABLE_CONTACT + 
			"(" + FIRST_NAME + " TEXT, " + LAST_NAME + " TEXT, " + 
			EMAIL + " TEXT, " + PHONE_NUMBER + " TEXT, " + 
			"CONSTRAINT user_grp_id PRIMARY KEY " +
			"(" + PHONE_NUMBER + "))";

	public SQLiteDatabase database;

	/**
	 * Constructor
	 * @param context
	 */
	public DatabaseSMS(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("Create Table Query: \n" + CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE);
		System.out.println("Created Contacts Table");

	}

	/**
	 * Called if the database version is increased. 
	 * Drops the existing database and recreate it via the onCreate() method.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_CONTACT);
		onCreate(db);
	}
	
	/**
	 * Opens the Database Connection
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = instance.getWritableDatabase();
	}

	/**
	 * Closes the Database Connection
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		instance.close();
	}

	/**
	 * Adding a particular Person's Details for a Group in Database
	 * @param person
	 */
	public long addContact(String firstName, String lastName, 
					String phoneNumber, String email) {

		System.out.println(String.format("Adding Contact: %s %s %s %s", 
				firstName, lastName, phoneNumber, email));
		
		ContentValues values = new ContentValues();

		values.put(PHONE_NUMBER, phoneNumber); 
		values.put(FIRST_NAME, firstName); 
		values.put(LAST_NAME, lastName); 
		values.put(EMAIL, email); 

		System.out.println("VALUES: \n" + values.get(PHONE_NUMBER) + 
				values.get(FIRST_NAME) + values.get(LAST_NAME)+ values.get(EMAIL));
		// Inserting Row
		long newRowId = -100;
		if (database != null)
			newRowId = database.insert(TABLE_CONTACT, null, values);
		else
			System.out.println("Database has not been opened.");
		System.out.println("INSERTED CONTACT");
		return newRowId;
	}
	
	/**
	 * Return all the VALID phone numbers from a particular group
	 * @return
	 */
	public List<String> getAllPhoneNumbers() {
		List<String> phonelist = new ArrayList<String>();
		try {
			SQLiteDatabase db = instance.getReadableDatabase();
			String query = String.format(
						   "SELECT %s FROM %s ORDER BY %s", 
						   PHONE_NUMBER, TABLE_CONTACT, FIRST_NAME);
					
			cursor = db.rawQuery(query, null);
			
			// If move to the first element is possible
			if (cursor.moveToFirst()) {

				String pNumber = "";
				do {
					pNumber = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
					if (pNumber != null && (!pNumber.equals(""))) {
						phonelist.add(pNumber);
					}
				} while (cursor.moveToNext());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			phonelist.add("Phone numbers Exception Occured.");
		}
		return phonelist;
	}
	
	/**
	 * Return all the VALID email IDs from a particular group
	 * @return
	 */
	public List<String> getAllEmails() {
		List<String> emaillist = new ArrayList<String>();
		try {
			SQLiteDatabase db = instance.getReadableDatabase();
			String query = String.format(
						   "SELECT %s FROM %s ORDER BY %s", 
						   EMAIL, TABLE_CONTACT, FIRST_NAME);
					
			cursor = db.rawQuery(query, null);
			
			// If move to the first element is possible
			if (cursor.moveToFirst()) {
				String email = "";
				do {
					email = cursor.getString(cursor.getColumnIndex(EMAIL));
					if (email != null && (!email.equals("")))
						emaillist.add(email);
				} while (cursor.moveToNext());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			emaillist.add("Email Exception Occured.");
		}
		
		return emaillist;
	}
	
	/**
	 * Delete entire Group
	 * @param group
	 * @return
	 */
	public boolean removeContact (String phoneNumber) {
		try {
			SQLiteDatabase db = instance.getReadableDatabase();
			String whereClause = String.format("%s = '%s' ", PHONE_NUMBER, phoneNumber);
			int ret = db.delete(TABLE_CONTACT, whereClause, null);
			System.out.println("RET: " + ret);
			return (ret > 0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

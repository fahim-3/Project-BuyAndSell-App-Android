package com.example.fahim.ibuyandsell;


import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManagment {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "Session";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn",IS_Admin="IsAdmin";
	
	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";
	
	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email",KEY_PRODUCT_EMAIL="";
	
	// Constructor
	public SessionManagment(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String name, String email){
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		
		// Storing name in pref
		editor.putString(KEY_NAME, name);
		
		// Storing email in pref
		editor.putString(KEY_EMAIL, email);
		
		// commit changes
		editor.commit();
	}
	
	public void createAdminSession(Boolean value){
		// Storing login value as TRUE
		
		editor.putBoolean(IS_Admin, value);
		// commit changes
		editor.commit();
	}
	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
		}
		
	}
	
	// Check Admin
	public void checkAdmin(){
		// Check Admin status
		if(!this.isAdmin()){
			editor.putBoolean(IS_Admin, false);
			// commit changes
			editor.commit();
		}
		
	}
	
	
	/**
	 * Get stored session data
	 * */
	public String getUserEmail(){
		
		
		// user email id
		
		String user= pref.getString(KEY_EMAIL, null);
		
		// return user
		return user;
	}
	
	public void storeEmail(String email){
		editor.putString(KEY_EMAIL,email);
		editor.commit();
	}
	public String getProductUserEmail(){
		
		
		// user email id
		
		String user= pref.getString(KEY_PRODUCT_EMAIL, null);
		
		// return user
		return user;
	}
	public void storeProductUserEmail(String email){
		editor.putString(KEY_PRODUCT_EMAIL,email);
		editor.commit();
	}
	/**
	 * Clear session details
	 * */
	public void logoutUser(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, LoginActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
	
	// Get admin State
	public boolean isAdmin(){
		return pref.getBoolean(IS_Admin, false);
	}
}

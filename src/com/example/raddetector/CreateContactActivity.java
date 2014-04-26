package com.example.raddetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateContactActivity extends Activity {

	private DatabaseSMS db;
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText phoneNumberEditText;
	private EditText emailEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_contact);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		db = DatabaseSMS.getInstance(this);

		firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
		lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);

	}

	/**
	 * Method called when Add contact has been clicked
	 * Creates a new row in the Database storing the details entered
	 * @param v
	 */
	public void onAddContactClick(View v) {
		db.open();
		String firstName = firstNameEditText.getText().toString();
		String lastName = lastNameEditText.getText().toString();
		String phoneNumber = phoneNumberEditText.getText().toString();
		String email = emailEditText.getText().toString();

		long ret = -10;
		ret = db.addContact(firstName, lastName, phoneNumber, email);

		firstNameEditText.setText("");
		lastNameEditText.setText("");
		phoneNumberEditText.setText("");
		emailEditText.setText("");

		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", ret);
		setResult(RESULT_OK, returnIntent);     
		finish();
	}

	/**
	 * Method called when Cancel Button has been clicked
	 * @param v
	 */
	public void onCancelContactClick(View v) {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);        
		finish();
	}
}

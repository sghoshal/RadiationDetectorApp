package com.example.raddetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayContacts extends Activity {

	private static List<String> listOfContacts = null;
	private String contactSelected;
	private ArrayAdapter<String> adapter;
	private Button newContactButton;
	private EditText filterText;
	private ListView listViewContacts = null;

	private static int CREATE_CONTACT_REQ_CODE = 1;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_contacts);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		captureElements();
		setLongClickListener(listViewContacts);

		listOfContacts = DatabaseSMS.getInstance(this).getAllPhoneNumbers();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
				listOfContacts);
		listViewContacts.setAdapter(adapter);

		// Add a Listener to Filter Edit Text 
		final TextView search = (EditText) findViewById(R.id.filterResults);
		search.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				String query = search.getText().toString();
				filterResults(query);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	public void captureElements() {
		newContactButton = (Button) findViewById(R.id.createContactButton);
		filterText = (EditText) findViewById(R.id.filterResults);
		listViewContacts = (ListView) findViewById(R.id.listViewContacts);
	}

	public void setLongClickListener(ListView lvContacts) {
		listViewContacts.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				contactSelected = (String) parent.getItemAtPosition(position);
				System.out.println("GROUP SELECTED: " + contactSelected);

				AlertDialog.Builder builder = new AlertDialog.Builder(DisplayContacts.this);
				builder.setMessage(String.format("Do you want to delete the Contact: '%s'?",  
						contactSelected))
						.setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								boolean rowsDeleted = 
										DatabaseSMS.getInstance(DisplayContacts.this)
										.removeContact(contactSelected);
								
								if (rowsDeleted) {
									listOfContacts.remove(contactSelected);
									adapter.notifyDataSetChanged();
								}
							}
						})				
						.setNegativeButton("No", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

				AlertDialog dialog = builder.create();
				dialog.show();
				return true;				
			}
		});
	}

	/**
	 * Whenever a text is entered in the filter text box, the results are filtered
	 * @param query
	 */
	@SuppressWarnings("unchecked")
	public void filterResults(String query) {
		query = query.toLowerCase(Locale.US);
		DatabaseSMS gdb = new DatabaseSMS(getApplicationContext());
		List<String> storedPhNumbers = gdb.getAllPhoneNumbers();
		listOfContacts.clear();
		listOfContacts.addAll(storedPhNumbers);
		
		List<String> listItems = new ArrayList<String>();

		for (int i = 0; i < listOfContacts.size(); i++) {
			listItems.add(listOfContacts.get(i));
		}

		if (!query.equals("")) {
			for (int i = listItems.size() - 1; i >= 0; i--) {
				String s = listItems.get(i);
				if (s.toLowerCase(Locale.US).indexOf(query) >= 0) {
					System.out.println("Contact: " + s);
				} else {
					listItems.remove(i);
				}
			}
		}
		
		listOfContacts.clear();
		listOfContacts.addAll(listItems);
		adapter.notifyDataSetChanged();	
	}

	/**
	 * Callback method when a new contact has been created / cancelled
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CREATE_CONTACT_REQ_CODE) {
			if(resultCode == RESULT_OK){      
				long result = data.getLongExtra("result", 0);
				List<String> phListFromDB = DatabaseSMS.getInstance(this).getAllPhoneNumbers();
				listOfContacts.clear();
				listOfContacts.addAll(phListFromDB);
				adapter.notifyDataSetChanged();
			}

			// If the user pressed cancel while adding contact
			if (resultCode == RESULT_CANCELED) {
				
			}
		}
	}

	/**
	 * Method called when New Contact button has been clicked
	 * @param v
	 */
	public void onNewContactButtonClick(View v) {
		Intent toCreateNewContact = new Intent(DisplayContacts.this, 
				CreateContactActivity.class);
		startActivityForResult(toCreateNewContact, CREATE_CONTACT_REQ_CODE);
	}
}

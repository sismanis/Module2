package com.jmel.party_shuffle;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class VotingPageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voting_page);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voting_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
        case R.id.action_search:
            openSearch();
            return true;
        case R.id.action_settings:
            openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("MY_MESSAGE", "in onResume (DisplayMessageActivity)");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("MY_MESSAGE", "in onPause (DisplayMessageActivity)");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("MY_MESSAGE", "in onStop (DisplayMessageActivity)");
	}

	private void openSettings() {
		// TODO Auto-generated method stub
		Toast t = Toast.makeText(getApplicationContext(),"I would now call Settings",
				Toast.LENGTH_LONG);
		t.show();
	}

	private void openSearch() {
		// TODO Auto-generated method stub
		Toast t = Toast.makeText(getApplicationContext(),"I would now call Search",
				Toast.LENGTH_LONG);
		t.show();
	}

}

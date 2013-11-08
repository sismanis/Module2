package com.jmel.party_shuffle;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i("MY_MESSAGE", "in onResume (MainActivity)");
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.i("MY_MESSAGE", "in onPause (MainActivity)");
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	Log.i("MY_MESSAGE", "in onStop (MainActivity)");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    // Called when the user clicks the vote button */
    public void voteButton(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, VotingPageActivity.class);
    	//EditText editText = (EditText) findViewById(R.id.edit_message);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
    
    // Called when the user clicks the song list button */
    public void songlistButton(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, SongListActivity.class);
    	//EditText editText = (EditText) findViewById(R.id.edit_message);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
    
}

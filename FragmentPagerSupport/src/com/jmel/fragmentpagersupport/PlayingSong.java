package com.jmel.fragmentpagersupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class PlayingSong extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_playing_song);
		Intent intent = getIntent();
		String song = intent.getExtras().getString("songname");
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setText(song);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.playing_song, menu);
		return true;
	}
	boolean isPlay = true;
	public void playClick(View v){
		
		MyApplication app = (MyApplication) getApplication();
		if(!isPlay){
			v.setBackgroundResource(R.drawable.play);
			isPlay = true;
			app.sendMessage(255);
		}
		else{
			v.setBackgroundResource(R.drawable.pause);
			isPlay= false;
			app.sendMessage(254);
		}
			
	}
}

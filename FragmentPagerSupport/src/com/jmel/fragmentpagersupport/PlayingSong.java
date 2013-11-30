package com.jmel.fragmentpagersupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class PlayingSong extends Activity {
 public String[] songlist = new String[100];
 public int currentsong;
 public int songcount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getActionBar().setDisplayHomeAsUpEnabled(false);
		setContentView(R.layout.activity_playing_song);
		Intent intent = getIntent();
		String song = intent.getExtras().getString("songname");
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setText(song);
		songlist = intent.getExtras().getStringArray("songslist");
		currentsong = intent.getExtras().getInt("cursong");
		songcount = intent.getExtras().getInt("count");
	}

	protected void onResume(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getActionBar().setDisplayHomeAsUpEnabled(false);
		setContentView(R.layout.activity_playing_song);
		Intent intent = getIntent();
		String song = intent.getExtras().getString("songname");
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setText(song);
		songlist = intent.getExtras().getStringArray("songslist");
		currentsong = intent.getExtras().getInt("cursong");
		songcount = intent.getExtras().getInt("count");

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
			v.setBackgroundResource(R.drawable.pause);
			isPlay = true;
			app.sendMessage(255);
		}
		else{
			v.setBackgroundResource(R.drawable.play);
			isPlay= false;
			app.sendMessage(254);
		}
			
	}
	
public void stopClick(View v){
		
		MyApplication app = (MyApplication) getApplication();
		
			app.sendMessage(251);
			Intent i = new Intent(app, MainActivity.class);
			startActivity(i);
			
	}

public void forwardClick(View v){
	
	MyApplication app = (MyApplication) getApplication();
	
	app.sendMessage(253);
	if( currentsong == songcount)
		currentsong = 0;
	else
	currentsong = currentsong + 1;
	//Log.i("", songlist[currentsong]);
	TextView text = (TextView) findViewById(R.id.textView1);

	text.setText((String) songlist[currentsong]);
	
		
}

public void backClick(View v){
	
	MyApplication app = (MyApplication) getApplication();
	
	app.sendMessage(252);
	if( currentsong == 0 )
		currentsong = songcount-1;
	else
	currentsong = currentsong - 1;
	TextView text = (TextView) findViewById(R.id.textView1);
	text.setText(songlist[currentsong]);
}
}

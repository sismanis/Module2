package com.jmel.fragmentpagersupport;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import java.io.OutputStream;

public class MainActivity extends FragmentActivity {// implements ProgressBar{
	static final int NUM_ITEMS = 3;

	MyAdapter mAdapter;
	static String[] songs;
	static int AndroidId = 0;
	ViewPager mPager;
	static int songquantity= 0;
	static boolean[] votecheck;
	public static boolean mode = false;
	static String[] votesongs;
	int[] id= new int[4];
	int[] votecount = new int[4];
	int totalvotings= 0;
	//public List<String> voting_list = new ArrayList<String>();

	// private ProgressBar mProgress;
	// private int mProgressStatus = 0;
	// private Handler mHandler = new Handler();
	// ProgressBar progress_bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_pager);

		// mAdapter = new MyAdapter(getSupportFragmentManager(), this, mPager);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		mAdapter = new MyAdapter(getSupportFragmentManager(), this, mPager);
		mAdapter.addTab(bar.newTab().setText("Home"), HomePageFragment.class,
				null);
		mAdapter.addTab(bar.newTab().setText("Vote"), VotingListFragment.class,
				null);
		mAdapter.addTab(bar.newTab().setText("Songs"), SongListFragment.class,
				null);

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
		// This call will result in better error messages if you
		// try to do things in the wrong thread.

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		TCPReadTimerTask tcp_task = new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 3000, 500);


		// Set up a timer task. We will use the timer to check the
		// input queue every 500 ms
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the options menu from XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}




	public static class MyAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {

		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public MyAdapter(FragmentManager fm, Activity activity, ViewPager pager) {
			super(fm);
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
			

		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

	public static class HomePageFragment extends Fragment implements // ///////////////////////////////////////////////////////////////////////////////////////////
			ActionBar.TabListener {
		int mNum;
		String pagename;

		/**
		 * Create a new instance of CountingFragment, providing "num" as an
		 * argument.
		 */
		static HomePageFragment newInstance(int num) {
			HomePageFragment f = new HomePageFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			f.setArguments(args);

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;

		}

		/**
		 * The Fragment's UI is just a simple text view showing its instance
		 * number.
		 */

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.activity_main, container, false);
			// View tv = v.findViewById(R.id.text);
			// ((TextView)tv).setText( "HomePage" );
			return v;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}
	}

	public static class VotingListFragment extends ListFragment implements
			ActionBar.TabListener {
		int mNum;
		String pagename;
		long songid;

		/**
		 * Create a new instance of CountingFragment, providing "num" as an
		 * argument.
		 */
		static VotingListFragment newInstance(int num) {
			VotingListFragment f = new VotingListFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			f.setArguments(args);

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;

		}

		/**
		 * The Fragment's UI is just a simple text view showing its instance
		 * number.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.voting_pager_list, container,
					false);
			View tv = v.findViewById(R.id.text);
			((TextView) tv).setText("Voting Page");
			//VotingListFragment fm1 = (VotingListFragment) mAdapter.getItem(1);
			//setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, votesongs));
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			//final String[] votinglist = new String[4];
			super.onActivityCreated(savedInstanceState);
			/*votesongs = new String[4];
		
			for (int i = 0; i < 4; i++) {
				votesongs[i] = "votinglist " + Integer.toString(i);
			}
			
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_single_choice, votesongs));*/
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("VotingPageFragmentList", "Item clicked: " + id);

			this.songid = id;
			ListView listView = getListView();
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView.setSelection(0);
			listView.setSelected(true);
			MainActivity a = (MainActivity) getActivity();
			MyApplication app = (MyApplication) a.getApplication();
			//app.sendMessage((int) id);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}
	}

	public static class SongListFragment extends ListFragment implements
			ActionBar.TabListener {// , TextWatcher {
		int mNum;
		String pagename;
		// Fragment fm = (Fragment)
		// getFragmentManager().findFragmentById(R.id.SongListFragment);
		// FragmentManager.findFragmentById(R.id.songeditText);
		public EditText et;// = (EditText)

		// getListView().findViewById(R.id.songeditText);

		/**
		 * Create a new instance of CountingFragment, providing "num" as an
		 * argument.
		 */
		static SongListFragment newInstance(int num) {
			SongListFragment f = new SongListFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			f.setArguments(args);

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;

		}

		/**
		 * The Fragment's UI is just a simple text view showing its instance
		 * number.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_pager_list, container,
					false);
			int i;
			View tv = v.findViewById(R.id.text);
			((TextView) tv).setText("Song List");


			et = (EditText) v.findViewById(R.id.songeditText);

			et.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
					// Abstract Method of TextWatcher Interface.
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// Abstract Method of TextWatcher Interface.
				}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					int textlength = et.getText().length();
					// List<String> songs_sort = new ArrayList<String>();
					songs_sort.clear();
					for (int i = 0; i < songs.length; i++) {
						if (textlength <= songs[i].length()) {
							if (songs[i].toLowerCase().contains(
									et.getText().toString().toLowerCase())) {
								songs_sort.add(songs[i]);
							}
						}
					}

					setListAdapter(new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_list_item_1, songs_sort));
				}
			});

			return v;
		}

		public List<String> songs_sort = new ArrayList<String>();
	//	public final String[] songs = new String[100];

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, songs));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			if(!mode){
			Log.i("SongListFragmentList", "Item clicked: " + id);
			boolean forcheck = false;
			MainActivity a = (MainActivity) getActivity();
			MyApplication app = (MyApplication) a.getApplication();
			String s;
			songs_sort.clear();
			if (songs_sort.isEmpty()) {
				s = "empty";
			} else
				s = "full";
			Log.i("songs", s);

			// app.sendMessage((int) id);
			if (!songs_sort.isEmpty()) {
				for (int j = 0; j < songs.length && forcheck == false; j++) {
					if (songs[j].equals(songs_sort.get((int) id))) {
						id = j;
						forcheck = true;
						Log.i("songs", songs_sort.get((int) id));
						Log.i("songs", songs[j]);
					}
				
				}
			}

			app.sendMessage((int) id);
			Intent i = new Intent(app, PlayingSong.class);
			i.putExtra("songname", songs[(int) id]);
			i.putExtra("songslist", songs);
			i.putExtra("cursong", (int) id);
			i.putExtra("count", songquantity);
			startActivity(i);
			}
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

	}

	private static final int PROGRESS = 0x1;

	private ProgressBar mProgress;
	private int mProgressStatus = 0;

	private Handler mHandler = new Handler();

	protected synchronized void startprogressbar() {// Bundle icicle) {
		// super.onCreate(icicle);
		mProgressStatus = 0;
		// setContentView(R.layout.progressbar_activity);

		mProgress = (ProgressBar) findViewById(R.id.adprogress_progressBar);
		
		Executor exe = Executors.newSingleThreadExecutor();
		
		// Start lengthy operation in a background thread
		exe.execute(new Runnable() {
			public void run() {
				while (mProgressStatus < 100) {
					if(mProgressStatus >=95){
						resetandSend();
						mProgressStatus = 0;
					}
					if (mProgressStatus == 90) {
						try {
							Uri notification = RingtoneManager
									.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
							Ringtone r = RingtoneManager.getRingtone(
									getApplicationContext(), notification);
							r.play();
						} catch (Exception e) {
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mProgressStatus = mProgressStatus + 1;

					// Update the progress bar
					mHandler.post(new Runnable() {
						public void run() {
							mProgress.setProgress(mProgressStatus);
						}
					});
				}
			}
		});
	}
public synchronized void resetandSend(){
	int send=0;
	int i;
	for(i= 0; i<4; i++){
		if(votecount[i]>send)
			send = i;
	}
	send = id[send];
	for( i= 0; i<4; i++)
		votecount[i]=0;
	
	totalvotings++;
	if(totalvotings>=(songquantity/4)){
		for( i=0; i<songquantity; i++)
			votecheck[i]= false;
	}
	MyApplication app = (MyApplication) getApplication();
	app.sendMessage(send);
		
	int random0, random1, random2, random3;
	Random rand = new Random();
		random0 =rand.nextInt(songquantity-1);
		random1 =rand.nextInt(songquantity-1);
		random2 =rand.nextInt(songquantity-1);
		random3 =rand.nextInt(songquantity-1);
		Log.i("here","ere");
		String h = "" + songquantity;
		Log.i("songquantity", h);
		votecheck[random0]= true;
		while(votecheck[random1] == true){
			random1 = rand.nextInt(songquantity-1);
			Log.i("loop","loop");
		}
		votecheck[random1]= true;
		while(votecheck[random2]== true){
			random2 =rand.nextInt(songquantity-1);
		}
		votecheck[random2]= true;
		while(votecheck[random3]== true){
			random3 =rand.nextInt(songquantity-1);
		}
		votecheck[random3]= true;
		votesongs = new String[4];
		votesongs[0] = songs[random0];
		Log.i("Vote:", votesongs[0]);
		votesongs[1] = songs[random1];
		Log.i("Vote:", votesongs[1]);
		votesongs[2] = songs[random2];
		Log.i("Vote:", votesongs[2]);
		votesongs[3] = songs[random3];
		Log.i("Vote:", votesongs[3]);

		this.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Button but1 = (Button) findViewById(R.id.Song1);
				Button but2= (Button) findViewById(R.id.Song2);
				Button but3 = (Button) findViewById(R.id.Song3);
				Button but4 = (Button) findViewById(R.id.Song4);
				but1.setText(votesongs[0]);
				but2.setText(votesongs[1]);
				but3.setText(votesongs[2]);
				but4.setText(votesongs[3]);
			}
		});
		
		id[0]=random0;
		id[1]=random1;
		id[2]=random2;
		id[3]=random3;
		//startprogressbar();
}
public void sendVote1(View v){
	//MyApplication app = (MyApplication) getApplication();
	votecount[0]++;
}
public void sendVote2(View v){
	//MyApplication app = (MyApplication) getApplication();
	 votecount[1]++;

}
public void sendVote3(View v){
	//MyApplication app = (MyApplication) getApplication();
	votecount[2]++;

}
public void sendVote4(View v){
	//MyApplication app = (MyApplication) getApplication();
	 votecount[3]++;

}
public void partyMode(View view){
	MyApplication app = (MyApplication) getApplication();
	app.sendMessage(229);
	Button but = (Button) findViewById(R.id.party);
	if(mode == false ){
	but.setText("Remote Mode");
	mode = true;
	}
	else{
		but.setText("Party Mode");
		mode = false;
	}
//	VotingListFragment fm1 = (VotingListFragment) mAdapter.getItem(1);
//	fm1.setListAdapter(new ArrayAdapter<String>(this,
	//		android.R.layout.simple_list_item_1, voting_list));
}
	public void loadSongs(View view) {
		Log.i("running:", "running");

		int i;
		String[] songstemp = new String[100];
		final byte shake[] = { 0xF };
		byte buf[];
		byte bufstore[]; 
				bufstore = new byte[500];
		int bufcount = 0;
		int current = 0;
		boolean done = false;
		// public boolean transmitting = false;
		int songnumber = 0;
		
		// MainActivity a = (MainActivity) getActivity();
		MyApplication app = (MyApplication) getApplication();
		// app.sendMessage(101);
		app.sendMessage(230);
		if (app.sock != null && app.sock.isConnected() && !app.sock.isClosed()) {
			// transmitting = true;
			Log.i("socket", "socket");
			InputStream in;
			try {
				in = app.sock.getInputStream();
				buf = new byte[200];
				//app.sendMessage(231);

			in.read(buf);
			for (i = 0; buf[i]!=0x0 && done == false; i++) {
				
				bufstore[bufcount] = buf[i];
				bufcount++;
				if(buf[i]== 0xf)
					done = true;
			}
				
				while(!done){
					buf = new byte[130];
					app.sendMessage(231);

				in.read(buf);
				for (i = 0; buf[i]!=0 && done == false; i++) {
					
					bufstore[bufcount] = buf[i];
					bufcount++;
					if(buf[i]== 0xf)
						done = true;
				}
			}
				// See if any bytes are available from the Middleman

				Log.i("pastsocket", "pastsocet");
				//in.read(buf);
				Log.i("bufstore", bufstore.toString());
				bufcount = 0;
				int stringcount = 0;
				for (stringcount = 0; bufstore[stringcount] != 0xf; stringcount++) {
					// If so, read them in and create a sring
					 //bufcount = stringcount;
					if (bufstore[stringcount] == 0x1) {
						songstemp[songnumber] = new String(bufstore, bufcount,
								stringcount - bufcount, "US-ASCII");
						Log.i("song", songstemp[songnumber]);
						songnumber++;
						bufcount = stringcount + 1;
					}/* else if (buf[buf.length - 1] == 0x2) {

						app.sendMessage(0x0);

						in.read(buf);
					}*/
					// buf = new byte[20];
					// in.read(buf);
					// bufcount = 0;
				}

				songs = new String[songnumber];
				for (i = 0; i < songnumber; i++) {
					songs[i] = songstemp[i];
					Log.i("songs:", songs[i]);
				}
				songquantity = i;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	/*	SongListFragment fm = (SongListFragment) mAdapter.getItem(2);
		fm.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, songs));*/
		// Fragment fm = new SongListFragment();
		// fm.onCreateView();
		
		
		
		votecheck = new boolean[songquantity];
		for(i= 0; i<songquantity; i++){
			votecheck[i] = false;
		}
		int random0, random1, random2, random3;
		Random rand = new Random();
			random0 =rand.nextInt(songquantity-1);
			random1 =rand.nextInt(songquantity-1);
			random2 =rand.nextInt(songquantity-1);
			random3 =rand.nextInt(songquantity-1);
			Log.i("here","ere");
			String h = "" + songquantity;
			Log.i("songquantity", h);
			votecheck[random0]= true;
			while(votecheck[random1] == true){
				random1 = rand.nextInt(songquantity-1);
				Log.i("loop","loop");
			}
			votecheck[random1]= true;
			while(votecheck[random2]== true){
				random2 =rand.nextInt(songquantity-1);
			}
			votecheck[random2]= true;
			while(votecheck[random3]== true){
				random3 =rand.nextInt(songquantity-1);
			}
			votecheck[random3]= true;
			votesongs = new String[4];
			votesongs[0] = songs[random0];
			Log.i("Vote:", votesongs[0]);
			votesongs[1] = songs[random1];
			Log.i("Vote:", votesongs[1]);
			votesongs[2] = songs[random2];
			Log.i("Vote:", votesongs[2]);
			votesongs[3] = songs[random3];
			Log.i("Vote:", votesongs[3]);
			Button but1 = (Button) findViewById(R.id.Song1);
			Button but2= (Button) findViewById(R.id.Song2);
			Button but3 = (Button) findViewById(R.id.Song3);
			Button but4 = (Button) findViewById(R.id.Song4);
			but1.setText(votesongs[0]);
			but2.setText(votesongs[1]);
			but3.setText(votesongs[2]);
			but4.setText(votesongs[3]);
			id[0]=random0;
			id[1]=random1;
			id[2]=random2;
			id[3]=random3;
			
			startprogressbar();
		//	VotingListFragment fm1 = (VotingListFragment) mAdapter.getItem(1);
			//fm1.setListAdapter(new ArrayAdapter<String>(this,
				//	android.R.layout.simple_list_item_single_choice, voting_list));
		//MyAdapter fm1 =  mAdapter;
		//fm1.notifyDataSetChanged();
	}

	// Route called when the user presses "connect"

	public void openSocket(View view) {
		// MyApplication app = (MyApplication) getApplication();
		// TextView msgbox = (TextView) findViewById(R.id.error_message_box);

		// Make sure the socket is not already opened

		// if (app.sock != null && app.sock.isConnected() &&
		// !app.sock.isClosed()) {
		// msgbox.setText("Socket already open");
		// return;
		// }

		// open the socket. SocketConnect is a new subclass
		// (defined below). This creates an instance of the subclass
		// and executes the code in it.

		new SocketConnect().execute((Void) null);
		

	}

	// Called when the user closes a socket

	public void closeSocket(View view) {
		MyApplication app = (MyApplication) getApplication();
		Socket s = app.sock;
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Construct an IP address from the four boxes

	public String getConnectToIP() {
		String addr = "";
		EditText text_ip;
		text_ip = (EditText) findViewById(R.id.ip1);
		addr += text_ip.getText().toString();
		text_ip = (EditText) findViewById(R.id.ip2);
		addr += "." + text_ip.getText().toString();
		text_ip = (EditText) findViewById(R.id.ip3);
		addr += "." + text_ip.getText().toString();
		text_ip = (EditText) findViewById(R.id.ip4);
		addr += "." + text_ip.getText().toString();
		return addr;
	}

	// Gets the Port from the appropriate field.

	public Integer getConnectToPort() {
		Integer port;
		EditText text_port;

		text_port = (EditText) findViewById(R.id.port);
		port = Integer.parseInt(text_port.getText().toString());

		return port;
	}

	// This is the Socket Connect asynchronous thread. Opening a socket
	// has to be done in an Asynchronous thread in Android. Be sure you
	// have done the Asynchronous Tread tutorial before trying to understand
	// this code.

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {

		// The main parcel of work for this thread. Opens a socket
		// to connect to the specified IP.

		protected Socket doInBackground(Void... voids) {
			Socket s = null;
			String ip = getConnectToIP();
			Integer port = getConnectToPort();

			try {
				s = new Socket(ip, port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return s;
		}

		// After executing the doInBackground method, this is
		// automatically called, in the UI (main) thread to store
		// the socket in this app's persistent storage

		protected void onPostExecute(Socket s) {
			MyApplication myApp = (MyApplication) MainActivity.this
					.getApplication();
			myApp.sock = s;
		}
	}

	// This is a timer Task. Be sure to work through the tutorials
	// on Timer Tasks before trying to understand this code.

	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			MyApplication app = (MyApplication) getApplication();
			if (app.sock != null && app.sock.isConnected()
					&& !app.sock.isClosed()) {

				// try {
				// InputStream in = app.sock.getInputStream();

				// See if any bytes are available from the Middleman

				// int bytes_avail = in.available();
				// if (bytes_avail > 0) {

				// If so, read them in and create a sring

				// byte buf[] = new byte[bytes_avail];
				// in.read(buf);

				// final String s = new String(buf, 0, bytes_avail,
				// "US-ASCII");

				// As explained in the tutorials, the GUI can not be
				// updated in an asyncrhonous task. So, update the GUI
				// using the UI thread.

				// runOnUiThread(new Runnable() {
				// public void run() {
				// EditText et = (EditText)
				// findViewById(R.id.RecvdMessage);
				// et.setText(s);
				// }
				// });

				// }
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
			}
		}
	}

}

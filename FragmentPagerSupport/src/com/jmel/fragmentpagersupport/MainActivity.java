package com.jmel.fragmentpagersupport;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Response;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
//import java.io.OutputStream;


//import java.io.OutputStream;

public class MainActivity extends FragmentActivity{// implements{
		//SongListFragment.OnSongSelectedListener {

	static final int NUM_ITEMS = 3;

	MyAdapter mAdapter;
	static String[] songs;
	static String song;

	static int AndroidId = 0;
	ViewPager mPager;
	static int songquantity= 0;
	static boolean[] votecheck;
	public static boolean mode = false;
	static String[] votesongs;
	int[] id= new int[4];
	static int[] votecount = new int[4];
	int totalvotings= 0;
	static String s;
	//public List<String> voting_list = new ArrayList<String>();

	// FB
	//private HomePageFragment homePageFragment;



	MyAdapter getAdapter() {
		return mAdapter;
	}

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

		/*
		 * //FB if (savedInstanceState == null) { // Add the fragment on initial
		 * activity setup homePageFragment = new HomePageFragment();
		 * getSupportFragmentManager() .beginTransaction()
		 * .add(android.R.id.content, homePageFragment) .commit(); } else { //
		 * Or set the fragment from restored state info homePageFragment =
		 * (HomePageFragment) getSupportFragmentManager()
		 * .findFragmentById(android.R.id.content); }
		 */
	}

	/*public void OnSongSelected(String s) {
		// The user selected the headline of an article from the
		// HeadlinesFragment
		// Do something here to display that article

		//VotingListFragment votingFrag = (VotingListFragment) mAdapter
				//.getItem(1);
		MainActivity.song = s;


	}*/

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

		// FB
		private static final String TAG = "MainFragment";
		private UiLifecycleHelper uiHelper;
		private Button shareButton;
		private static final List<String> PERMISSIONS = Arrays
				.asList("publish_actions");
		private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
		private boolean pendingPublishReauthorization = false;

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

			// FB
			uiHelper = new UiLifecycleHelper(getActivity(), callback);
			uiHelper.onCreate(savedInstanceState);

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

			// FB
			LoginButton authButton = (LoginButton) v
					.findViewById(R.id.authButton);
			authButton.setFragment(this);
			// authButton.setReadPermissions(Arrays.asList("user_likes",
			// "user_status"));
			shareButton = (Button) v.findViewById(R.id.shareButton);
			shareButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					publishStory();
				}
			});
			if (savedInstanceState != null) {
				pendingPublishReauthorization = savedInstanceState.getBoolean(
						PENDING_PUBLISH_KEY, false);
			}


			return v;
		}

		// FB
		private void onSessionStateChange(com.facebook.Session session,
				com.facebook.SessionState state, Exception exception) {
			if (state.isOpened()) {
				Log.i(TAG, "Logged in...");
				shareButton.setVisibility(View.VISIBLE);
				if (pendingPublishReauthorization
						&& state.equals(com.facebook.SessionState.OPENED_TOKEN_UPDATED)) {
					pendingPublishReauthorization = false;
					publishStory();
				}
			} else if (state.isClosed()) {
				Log.i(TAG, "Logged out...");
				shareButton.setVisibility(View.INVISIBLE);
			}
		}

		private com.facebook.Session.StatusCallback callback = new com.facebook.Session.StatusCallback() {
			@Override
			public void call(com.facebook.Session session,
					com.facebook.SessionState state, Exception exception) {
				onSessionStateChange(session, state, exception);
			}
		};


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
		public void onResume() {
			super.onResume();
			// For scenarios where the main activity is launched and user
			// session is not null, the session state change notification
			// may not be triggered. Trigger it if it's open/closed.
			com.facebook.Session session = com.facebook.Session
					.getActiveSession();
			if (session != null && (session.isOpened() || session.isClosed())) {
				onSessionStateChange(session, session.getState(), null);
			}
			uiHelper.onResume();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}

		@Override
		public void onPause() {
			super.onPause();
			uiHelper.onPause();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			uiHelper.onDestroy();
		}


		
		private void publishStory() {
		    com.facebook.Session session = com.facebook.Session.getActiveSession();

		    if (session != null){

		        // Check for publish permissions    
		        List<String> permissions = session.getPermissions();
		        if (!isSubsetOf(PERMISSIONS, permissions)) {
		            pendingPublishReauthorization = true;
		            com.facebook.Session.NewPermissionsRequest newPermissionsRequest = new com.facebook.Session
		                    .NewPermissionsRequest(this, PERMISSIONS);
		        session.requestNewPublishPermissions(newPermissionsRequest);
		            return;
		        }

		        
		        //String s = song;
		        Bundle postParams = new Bundle();
		        postParams.putString("name", "Party Shuffle");
		        postParams.putString("caption", "I'm listening to  " + s + "\n" + "join in on the party and vote for the next song!");
		        postParams.putString("description", "Project by Jesse Melamed, Alex Sismanis, Andy Whitman, Justin Sui");
		        postParams.putString("link", "https://developers.facebook.com/android");
		        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		        com.facebook.Request.Callback callback= new com.facebook.Request.Callback() {
		            public void onCompleted(Response response) {
		                JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
		                String postId = null;
		                try {
		                    postId = graphResponse.getString("id");
		                } catch (JSONException e) {
		                    Log.i(TAG,
		                        "JSON error "+ e.getMessage());
		                }
		                FacebookRequestError error = response.getError();
		                if (error != null) {
		                    Toast.makeText(getActivity()
		                         .getApplicationContext(),
		                         error.getErrorMessage(),
		                         Toast.LENGTH_SHORT).show();
		                    } else {
		                        Toast.makeText(getActivity()
		                             .getApplicationContext(), 
		                             postId,
		                             Toast.LENGTH_LONG).show();
		                }
		            }
		        };

		        com.facebook.Request request = new com.facebook.Request(session, "me/feed", postParams, 
		                              HttpMethod.POST, callback);

		        com.facebook.RequestAsyncTask task = new com.facebook.RequestAsyncTask(request);
		        task.execute();
		    }


		}
		
		private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		    for (String string : subset) {
		        if (!superset.contains(string)) {
		            return false;
		        }
		    }
		    return true;
		}

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
		
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


		public static class VotingListFragment extends ListFragment implements
				ActionBar.TabListener {
			int mNum;
			String pagename;

			long songid;
			//String[] votinglist = new String[100];

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
				return v;
			}
		
			
			
			@Override
			public void onActivityCreated(Bundle savedInstanceState) {
				//String[] votinglist = new String[100];

				//MainActivity a = (MainActivity) getActivity();
				//SongListFragment fm = (SongListFragment) a.getAdapter().getItem(2);
				// votinglist = fm.songs;
				
				
				//for (int i = 0; i < 4; i++) { 
			//		votinglist[i] = "votinglist " +Integer.toString(i); 
				//	}
					
				
				super.onActivityCreated(savedInstanceState);
			//	setListAdapter(new ArrayAdapter<String>(getActivity(),
				//		android.R.layout.simple_list_item_single_choice, votinglist));
			}

			@Override
			public void onListItemClick(ListView l, View v, int position, long id) {
				Log.i("VotingPageFragmentList", "Item clicked: " + id);

				id = songid;
				ListView listView = getListView();
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				listView.setSelection(0);
				listView.setSelected(true);
				// MainActivity a = (MainActivity) getActivity();
				// MyApplication app = (MyApplication) a.getApplication();
				// app.sendMessage((int) id);
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
		SongListFragment fm = (SongListFragment) mAdapter.getItem(2);
		fm.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, songs));
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

			}
		}
	}

	

	public static class SongListFragment extends ListFragment implements
			ActionBar.TabListener {
		int mNum;
		String pagename;
		long songid;
		public EditText et;

		/*OnSongSelectedListener mCallback;

		// Container Activity must implement this interface
		public interface OnSongSelectedListener {
			public void OnSongSelected(String s);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

			// This makes sure that the container activity has implemented
			// the callback interface. If not, it throws an exception
			try {
				mCallback = (OnSongSelectedListener) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString()
						+ " must implement OnSongSelectedListener");
			}
		}*/

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
		 * The Fragment's UI is just a simple text view showing its instance number.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_pager_list, container,
					false);
			View tv = v.findViewById(R.id.text);
			((TextView) tv).setText("Song List");

			et = (EditText) v.findViewById(R.id.songeditText);

			et.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
					// Abstract Method of TextWatcher Interface.
				}

				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// Abstract Method of TextWatcher Interface.
				}

				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
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
		//public final String[] songs = new String[100];
		//public final String[] voting = new String[100];

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			//for (int i = 0; i < 100; i++) {
				//songs[i] = "song " + Integer.toString(i);
			//}
			

			/*Random gen = new Random();
			int vote1;
			int i = 0;
			while (i < 4) {
				vote1 = gen.nextInt();
				if (vote1 < 100) {
					voting[i] = songs[vote1];
					i++;
				}
			}*/

			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, songs));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("SongListFragmentList", "Item clicked: " + id);
			boolean forcheck = false;
			songid = id;
			Log.i("songs", Integer.toString((int) songid));
			MainActivity a = (MainActivity) getActivity();
			MyApplication app = (MyApplication) a.getApplication();

			

			 app.sendMessage((int) id);
				s = songs[(int)id];
			if (!songs_sort.isEmpty()) {
				for (int j = 0; j < songs.length && forcheck == false; j++) {
					if (songs[j].equals(songs_sort.get((int) id))) {
						id = j;
						forcheck = true;
					}
				}
			}
			

			//mCallback.OnSongSelected(s);

			// app.sendMessage((int) id);
			Intent i = new Intent(app, PlayingSong.class);
			i.putExtra("songname", songs[(int) id]);
			i.putExtra("songslist", songs);
			i.putExtra("cursong", (int) id);
			startActivity(i);

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

}

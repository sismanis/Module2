package com.jmel.fragmentpagersupport;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Response;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
//import java.io.OutputStream;

//import java.io.OutputStream;

public class MainActivity extends FragmentActivity implements
		SongListFragment.OnSongSelectedListener {

	static final int NUM_ITEMS = 3;

	MyAdapter mAdapter;
	static String[] songs;

	ViewPager mPager;

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

	public void OnSongSelected(String string[]) {
		// The user selected the headline of an article from the
		// HeadlinesFragment
		// Do something here to display that article

		VotingListFragment votingFrag = (VotingListFragment) mAdapter
				.getItem(1);
		votingFrag.votinglist = string;

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

		@Override
		public void onSaveInstanceState(Bundle outState) {

		    super.onSaveInstanceState(outState);
		    outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		    uiHelper.onSaveInstanceState(outState);
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

		        Bundle postParams = new Bundle();
		        postParams.putString("name", "Party Shuffle");
		        postParams.putString("caption", "I'm listening to song join in on the party and vote for the next song!");
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

	/*
	 * public void loadSongs(View view){ int i; String[] songstemp = new
	 * String[100]; final byte shake[] = {0xF}; byte buf[] = new byte[20]; int
	 * bufcount=0; boolean done = false; //public boolean transmitting = false;
	 * int songnumber = 0; // MainActivity a = (MainActivity) getActivity();
	 * MyApplication app = (MyApplication) getApplication(); if (app.sock !=
	 * null && app.sock.isConnected() && !app.sock.isClosed() ) { //transmitting
	 * = true; InputStream in; try { in = app.sock.getInputStream();
	 * 
	 * 
	 * // See if any bytes are available from the Middleman
	 * 
	 * 
	 * in.read(buf); while(buf[bufcount]!= 0x0){ // If so, read them in and
	 * create a sring //bufcount = 0; while(buf[bufcount]!=0x1){ in.read(buf);
	 * bufcount++; } songstemp[songnumber] = new String(buf, 0, bufcount-1,
	 * "US-ASCII"); Log.i("song", songstemp[songnumber]); songnumber++; buf =
	 * new byte[20]; in.read(buf); bufcount = 0; }
	 * 
	 * 
	 * songs = new String[songnumber]; for( i = 0; i<songnumber; i++){ songs[i]
	 * = songstemp[i]; } } catch (IOException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * SongListFragment fragment = (SongListFragment) mAdapter.getItem(2);
	 * fragment.setListAdapter(new ArrayAdapter<String>(this,
	 * android.R.layout.simple_list_item_1, songs)); }
	 */
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

}

package com.jmel.fragmentpagersupport;

import java.io.IOException;
import java.io.InputStream;
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
import android.app.DownloadManager.Request;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.service.textservice.SpellCheckerService.Session;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
//import java.io.OutputStream;

//import java.io.OutputStream;

public class MainActivity extends FragmentActivity {// implements ProgressBar{
	static final int NUM_ITEMS = 3;

	MyAdapter mAdapter;
	static String[] songs;

	ViewPager mPager;

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


		/*
		 * TextProgressBar textProgressBar = (TextProgressBar)
		 * findViewById(R.id.progress_bar);
		 * textProgressBar.setText("Loading 70%");
		 * textProgressBar.setProgress(70); textProgressBar.setTextSize(18);
		 */

		// progress_bar = new ProgressBar(this);
		// mProgress = (ProgressBar) findViewById(R.id.progress_bar);

		/*
		 * // Start lengthy operation in a background thread int time = 0;
		 * while(time != 6){ time++; new Thread(new Runnable() { public void
		 * run() { while (mProgressStatus < 100) { int i = 0; try {
		 * Thread.sleep(10000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } i += 10;
		 * mProgressStatus = i;
		 * 
		 * // Update the progress bar mHandler.post(new Runnable() { public void
		 * run() { mProgress.setProgress(mProgressStatus); } }); } } }).start();
		 * } Thread.currentThread().isInterrupted();
		 * 
		 * publishprogress(0);
		 */
		// Watch for button clicks.

		/*
		 * Button button = (Button) findViewById(R.id.submit_vote);
		 * button.setOnClickListener(new OnClickListener() {
		 */

		// });

		/*
		 * private void sendVote() { VotingListFragment votefragvar = new
		 * VotingListFragment(); long id = votefragvar.songid; //MainActivity a
		 * = (MainActivity) getActivity(); MyApplication app = (MyApplication)
		 * getApplication(); app.sendMessage((int) id);
		 * 
		 * }
		 */

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

	/*
	 * public class TextProgressBar extends ProgressBar {
	 * 
	 * private String text = ""; private int textColor = Color.BLACK; private
	 * float textSize = 15;
	 * 
	 * public TextProgressBar(Context context) { super(context); }
	 * 
	 * @Override protected synchronized void onDraw(Canvas canvas) {
	 * super.onDraw(canvas); // create an instance of class Paint, set color and
	 * font size Paint textPaint = new Paint(); textPaint.setAntiAlias(true);
	 * textPaint.setColor(textColor); textPaint.setTextSize(textSize); // In
	 * order to show text in a middle, we need to know its size Rect bounds =
	 * new Rect(); textPaint.getTextBounds(text, 0, text.length(), bounds); //
	 * Now we store font size in bounds variable and can calculate it's //
	 * position int x = getWidth() / 2 - bounds.centerX(); int y = getHeight() /
	 * 2 - bounds.centerY(); // drawing text with appropriate color and size in
	 * the center canvas.drawText(text, x, y, textPaint); }
	 * 
	 * public synchronized void setText(String text) { if (text != null) {
	 * this.text = text; } else { this.text = ""; } postInvalidate(); }
	 * 
	 * public synchronized void setTextSize(float textSize) { this.textSize =
	 * textSize; postInvalidate(); }
	 * 
	 * }
	 */

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
		
		//FB
		private UiLifecycleHelper uiHelper;
		private static final String TAG = "HomePageFragment";
		private Button shareButton;
		private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
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
			authButton.setReadPermissions(Arrays.asList("user_likes",
					"user_status"));
			shareButton = (Button) v.findViewById(R.id.shareButton);
			shareButton.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
			        publishStory();        
			    }
			});
			if (savedInstanceState != null) {
			    pendingPublishReauthorization = 
			        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
			}

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

		// FB Session
		private void onSessionStateChange(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				Log.i(TAG, "Logged in...");
			} else if (state.isClosed()) {
				Log.i(TAG, "Logged out...");
			}
			if (state.isOpened()) {
		        shareButton.setVisibility(View.VISIBLE);
		        if (pendingPublishReauthorization && 
		                state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
		            pendingPublishReauthorization = false;
		            publishStory();
		        }
		    } else if (state.isClosed()) {
		        shareButton.setVisibility(View.INVISIBLE);
		    }
		}

		private Session.StatusCallback callback = new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);
			}
		};

		@Override
		public void onResume() {
			super.onResume();
			Session session = Session.getActiveSession();
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
		    Session session = Session.getActiveSession();

		    if (session != null){

		        // Check for publish permissions    
		        List<String> permissions = session.getPermissions();
		        if (!isSubsetOf(PERMISSIONS, permissions)) {
		            pendingPublishReauthorization = true;
		            Session.NewPermissionsRequest newPermissionsRequest = new Session
		                    .NewPermissionsRequest(this, PERMISSIONS);
		        session.requestNewPublishPermissions(newPermissionsRequest);
		            return;
		        }

		        Bundle postParams = new Bundle();
		        postParams.putString("name", "Party Shuffle!");
		        postParams.putString("caption", "Listen to a song and vote on the next one with your friends!");
		        postParams.putString("description", "Project created by Jesse Melamed, Alex Sismanis, Justin Siu and Andrew Whitman.");
		        postParams.putString("link", "https://developers.facebook.com/android");
		        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		        Request.Callback callback= new Request.Callback() {
		            public void onCompleted(Response response) {
		                JSONObject graphResponse = response
		                                           .getGraphObject()
		                                           .getInnerJSONObject();
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

		        Request request = new Request(session, "me/feed", postParams, 
		                              HttpMethod.POST, callback);

		        RequestAsyncTask task = new RequestAsyncTask(request);
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
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			final String[] votinglist = new String[5];


			for (int i = 0; i < 4; i++) {
				votinglist[i] = "votinglist " + Integer.toString(i);
			}
			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_single_choice, votinglist));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("VotingPageFragmentList", "Item clicked: " + id);

			this.songid = id;
			ListView listView = getListView();
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView.setSelection(0);
			listView.setSelected(true);
			// MainActivity a = (MainActivity) getActivity();
			// MyApplication app = (MyApplication) a.getApplication();
			// app.sendMessage((int) id);
		}

		public void submitVote(View view) {
			// VotingListFragment votefragvar = new VotingListFragment();
			// long id = votefragvar.songid;
			// MainActivity a = (MainActivity) getActivity();
			// MyApplication app = (MyApplication) a.getApplication();
			// app.sendMessage((int) id);
			// Context context = a.getApplicationContext();
			// CharSequence text = "+id";
			// int duration = Toast.LENGTH_SHORT;

			// Toast toast = Toast.makeText(context, text, duration);
			// toast.show();

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
			View tv = v.findViewById(R.id.text);
			((TextView) tv).setText("Song List");

			// final ListView lv = (ListView) v.findViewById(R.id.1);
			// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// final ArrayList<String> ar = new ArrayList<String>();
			// et = (EditText) v.findViewById(R.id.songeditText);
			// final String[] words = list.TERM;
			// Populate list with our static array of titles.
			// lv.setAdapter(new ArrayAdapter(getActivity(),
			// android.R.layout.simple_list_item_activated_1, songs));
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_activated_1, songs));
			// setTextFilterEnabled(true);

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
		public final String[] songs = new String[100];


		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			
			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, songs));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("SongListFragmentList", "Item clicked: " + id);
			boolean forcheck = false;
			MainActivity a = (MainActivity) getActivity();
			MyApplication app = (MyApplication) a.getApplication();
			String s;
			songs_sort.clear();
			if (songs_sort.isEmpty()){
				s = "empty";
			}
			else
				s = "full";
			Log.i("songs", s);


			// app.sendMessage((int) id);
			if (!songs_sort.isEmpty()) {
				for (int j = 0; j < songs.length && forcheck == false; j++) { 
					if (songs[j].equals(songs_sort.get((int)id))){
						id = j;
						forcheck = true;
						Log.i("songs", songs_sort.get((int)id));
						Log.i("songs", songs[j]);
					}
					//.contains(et.getText().toString().toLowerCase())) {
						/*if (songs[j].contains(Integer.toString(1))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(2))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(3))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(4))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(5))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(6))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(7))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(8))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(Integer.toString(9))) {
							id = j;
							forcheck = true;
						} else if (songs[j].contains(et.getText().toString().toLowerCase())) {
							id = j;
							forcheck = true;
						}*/
					//}
				}
			}

			//app.sendMessage((int) id);
			Intent i = new Intent(app, PlayingSong.class);
			i.putExtra("songname", songs[(int)id]);
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

	public void loadSongs(View view){
		int i;
		String[] songstemp = new String[100];
		 final byte shake[] = {0xF};
		 byte buf[] = new byte[20];
		 int bufcount=0;
		 boolean done = false;
		//public boolean transmitting = false;
		int songnumber = 0;
	//	MainActivity a = (MainActivity) getActivity();
		MyApplication app = (MyApplication) getApplication();
		if (app.sock != null && app.sock.isConnected() && !app.sock.isClosed() ) {
			//transmitting = true;
			InputStream in;
			try {
				in = app.sock.getInputStream();
			
			
					// See if any bytes are available from the Middleman
					
					
						in.read(buf);
						while(buf[bufcount]!= 0x0){
						// If so, read them in and create a sring
						//bufcount = 0;
						while(buf[bufcount]!=0x1){
						in.read(buf);
						bufcount++;
						}
						songstemp[songnumber]  = new String(buf, 0, bufcount-1, "US-ASCII");
						Log.i("song", songstemp[songnumber]);
						songnumber++;
						buf = new byte[20];
						in.read(buf);
						bufcount = 0;
					}

					
						songs = new String[songnumber];
					for( i = 0; i<songnumber; i++){
						songs[i] = songstemp[i];
					}
					} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		//Fragment fm = new SongListFragment();
	//	fm.onCreateView();
		
		SongListFragment fragment = (SongListFragment) mAdapter.getItem(2);
				fragment.setListAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, songs));
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

				//try {
				//	InputStream in = app.sock.getInputStream();

					// See if any bytes are available from the Middleman

					//int bytes_avail = in.available();
				//	if (bytes_avail > 0) {

						// If so, read them in and create a sring

						//byte buf[] = new byte[bytes_avail];
						//in.read(buf);

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

				//	}
			//	} catch (IOException e) {
				//	e.printStackTrace();
				//}
			}
		}
	}

}

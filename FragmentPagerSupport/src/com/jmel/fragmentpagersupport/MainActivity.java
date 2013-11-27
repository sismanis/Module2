package com.jmel.fragmentpagersupport;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//import java.io.OutputStream;

public class MainActivity extends FragmentActivity{// implements ProgressBar{
	static final int NUM_ITEMS = 3;

	MyAdapter mAdapter;

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
		
        /*TextProgressBar textProgressBar = (TextProgressBar) findViewById(R.id.progress_bar);
        textProgressBar.setText("Loading 70%");
        textProgressBar.setProgress(70);
        textProgressBar.setTextSize(18);*/

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

	/*public class TextProgressBar extends ProgressBar {

		private String text = "";
		private int textColor = Color.BLACK;
		private float textSize = 15;

		public TextProgressBar(Context context) {
			super(context);
		}

		@Override
		protected synchronized void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			// create an instance of class Paint, set color and font size
			Paint textPaint = new Paint();
			textPaint.setAntiAlias(true);
			textPaint.setColor(textColor);
			textPaint.setTextSize(textSize);
			// In order to show text in a middle, we need to know its size
			Rect bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			// Now we store font size in bounds variable and can calculate it's
			// position
			int x = getWidth() / 2 - bounds.centerX();
			int y = getHeight() / 2 - bounds.centerY();
			// drawing text with appropriate color and size in the center
			canvas.drawText(text, x, y, textPaint);
		}

		public synchronized void setText(String text) {
			if (text != null) {
				this.text = text;
			} else {
				this.text = "";
			}
			postInvalidate();
		}
		
	    public synchronized void setTextSize(float textSize) {
	        this.textSize = textSize;
	        postInvalidate();
	    }

	}*/

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
			VotingListFragment votefragvar = new VotingListFragment();
			long id = votefragvar.songid;
			MainActivity a = (MainActivity) getActivity();
			MyApplication app = (MyApplication) a.getApplication();
			//app.sendMessage((int) id);
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
		EditText et;// = (EditText)
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
			et = (EditText) v.findViewById(R.id.songeditText);
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
					List<String> songs_sort = new ArrayList<String>();
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

		final String[] songs = new String[100];

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			for (int i = 0; i < 100; i++) {
				songs[i] = "song " + Integer.toString(i);
			}
			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, songs));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("SongListFragmentList", "Item clicked: " + id);

			MainActivity a = (MainActivity) getActivity();
			MyApplication app = (MyApplication) a.getApplication();

			//app.sendMessage((int) id);
			Intent i = new Intent(app, PlayingSong.class);
			i.putExtra("songname", songs[(int)id]);
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

				try {
					InputStream in = app.sock.getInputStream();

					// See if any bytes are available from the Middleman

					int bytes_avail = in.available();
					if (bytes_avail > 0) {

						// If so, read them in and create a sring

						byte buf[] = new byte[bytes_avail];
						in.read(buf);

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

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

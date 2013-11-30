package com.jmel.fragmentpagersupport;

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

public class SongListFragment extends ListFragment implements
		ActionBar.TabListener {
	int mNum;
	String pagename;
	long songid;
	public EditText et;

	OnSongSelectedListener mCallback;

	// Container Activity must implement this interface
	public interface OnSongSelectedListener {
		public void OnSongSelected(String string[]);
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
	}

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
	public final String[] songs = new String[100];
	public final String[] voting = new String[100];

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		for (int i = 0; i < 100; i++) {
			songs[i] = "song " + Integer.toString(i);
		}
		mCallback.OnSongSelected(songs);

		Random gen = new Random();
		int vote1;
		int i = 0;
		while (i < 4) {
			vote1 = gen.nextInt();
			if (vote1 < 100) {
				voting[i] = songs[vote1];
				i++;
			}
		}

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

		// mCallback.OnSongSelected(id);

		// app.sendMessage((int) id);
		if (!songs_sort.isEmpty()) {
			for (int j = 0; j < songs.length && forcheck == false; j++) {
				if (songs[j].equals(songs_sort.get((int) id))) {
					id = j;
					forcheck = true;
				}
			}
		}

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
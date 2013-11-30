package com.jmel.fragmentpagersupport;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


	public class VotingListFragment extends ListFragment implements
			ActionBar.TabListener {
		int mNum;
		String pagename;

		long songid;
		String[] votinglist = new String[100];

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

			
			/*for (int i = 0; i < 4; i++) { 
				votinglist[i] = "votinglist " +Integer.toString(i); 
				}*/
				
			
			super.onActivityCreated(savedInstanceState);
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_single_choice, votinglist));
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
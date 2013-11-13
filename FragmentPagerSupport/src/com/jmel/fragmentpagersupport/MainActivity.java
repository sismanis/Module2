package com.jmel.fragmentpagersupport;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
    static final int NUM_ITEMS = 3;

    MyAdapter mAdapter;

    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.goto_first);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });
        
        button = (Button)findViewById(R.id.goto_last);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(NUM_ITEMS-1);
            }
        });
        
        
        ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create Home Tab
        Tab tab = actionBar.newTab().setTabListener(new HomePageFragment());
        // Set Tab Title
        tab.setText("Home");
        actionBar.addTab(tab);

        // Create first Tab
        tab = actionBar.newTab().setTabListener(new VotingListFragment());
        // Set Tab Title
        tab.setText("Vote");
        actionBar.addTab(tab);

        // Create Second Tab
        tab = actionBar.newTab().setTabListener(new SongListFragment());
        // Set Tab Title
        tab.setText("Songs");
        actionBar.addTab(tab);
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        
        @Override
        public Fragment getItem(int position) {
        
        	if(position == 0){
        		//home page fragment
        		return HomePageFragment.newInstance(position);
        	}
        	else if(position == 1){
        		//voting page fragment
        		return VotingListFragment.newInstance(position);
        	}
        	else if(position == 2){
        		return SongListFragment.newInstance(position);
        	}
        	else
        		return HomePageFragment.newInstance(position);
        }
    }
    
    public static class HomePageFragment extends ListFragment implements ActionBar.TabListener {
        int mNum;
        String pagename;
        private Fragment mFragment;
        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
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
            if(mNum == 0){
                 pagename = "Homepage";
            }
            else if(mNum == 1){
                 pagename = "Voting Page";
            }
            else if(mNum == 2){
                 pagename = "Song List";
            }
            
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText( pagename );
            return v;
        }

        //private static String[] homepage = {"American", "Cheddar", "Jack", "Gamonedo", "Lancashire", "Limburger", "Pepperjack", "Skyr", "Feta", "Asiago", "American", "Cheddar", "Jack", "Gamonedo", "Lancashire", "Limburger", "Pepperjack", "Skyr", "Feta", "Asiago"};
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            final String[] homepage = new String[20];

            for(int i = 0; i < 20; i++){
            	homepage[i] = "homepage " + Integer.toString(i);
            }
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, homepage));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("HomePageFragmentList", "Item clicked: " + id);
        }

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
            // When the tab is selected, switch to the
            // corresponding page in the ViewPager.
            mPager.setCurrentItem(tab.getPosition());
	        //mFragment = new SongListFragment();
	        // Attach tab0fragment.xml layout
	        //ft.add(android.R.id.content, mFragment);
	        //ft.attach(mFragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
	        ft.remove(mFragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
    }

    
    
    public static class VotingListFragment extends ListFragment implements ActionBar.TabListener {
        int mNum;
        String pagename;
        private Fragment mFragment;
        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
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
            if(mNum == 0){
                 pagename = "Homepage";
            }
            else if(mNum == 1){
                 pagename = "Voting Page";
            }
            else if(mNum == 2){
                 pagename = "Song List";
            }
            
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText( pagename );
            return v;
        }

        //private static String[] votinglist = {"American", "Cheddar", "Jack", "Gamonedo", "Lancashire", "Limburger", "Pepperjack", "Skyr", "Feta", "Asiago", "American", "Cheddar", "Jack", "Gamonedo", "Lancashire", "Limburger", "Pepperjack", "Skyr", "Feta", "Asiago"};
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            final String[] votinglist = new String[5];

            for(int i = 1; i < 5; i++){
            	votinglist[i] = "votinglist " + Integer.toString(i);
            }
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, votinglist));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("VotingPageFragmentList", "Item clicked: " + id);
        }

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
	        mFragment = new SongListFragment();
	        // Attach tab0fragment.xml layout
	        ft.add(android.R.id.content, mFragment);
	        ft.attach(mFragment);
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
	        ft.remove(mFragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
    }
    
    
    public static class SongListFragment extends ListFragment implements ActionBar.TabListener {
        int mNum;
        String pagename;
        private Fragment mFragment;
        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
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
            if(mNum == 0){
                 pagename = "Homepage";
            }
            else if(mNum == 1){
                 pagename = "Voting Page";
            }
            else if(mNum == 2){
                 pagename = "Song List";
            }
            
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText( pagename );
            return v;
        }

        //private static String[] Cheeses = {"American", "Cheddar", "Jack", "Gamonedo", "Lancashire", "Limburger", "Pepperjack", "Skyr", "Feta", "Asiago", "American", "Cheddar", "Jack", "Gamonedo", "Lancashire", "Limburger", "Pepperjack", "Skyr", "Feta", "Asiago"};

        
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            final String[] songs = new String[20];

            for(int i = 0; i < 20; i++){
            	songs[i] = "song " + Integer.toString(i);
            }
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, songs));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("SongListFragmentList", "Item clicked: " + id);
        }

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
	        mFragment = new SongListFragment();
	        // Attach tab0fragment.xml layout
	        ft.add(android.R.id.content, mFragment);
	        ft.attach(mFragment);
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.remove(mFragment);
		}
		
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
    }    

}

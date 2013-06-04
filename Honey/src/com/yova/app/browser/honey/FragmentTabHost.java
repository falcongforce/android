package com.yova.app.browser.honey;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.yova.app.browser.honey.WebTab.OnWebViewCreated;

/**
 * Special TabHost that allows the use of {@link Fragment} objects for its tab
 * content. When placing this in a view hierarchy, after inflating the hierarchy
 * you must call {@link #setup(Context, FragmentManager, int)} to complete the
 * initialization of the tab host.
 * 
 */
public class FragmentTabHost extends TabHost implements
		TabHost.OnTabChangeListener {

	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	private FrameLayout mRealTabContent;
	private Context mContext;
	private FragmentManager mFragmentManager;
	private int mContainerId;
	private TabHost.OnTabChangeListener mOnTabChangeListener;
	private OnWebViewCreated onWebViewCreated;


	private TabInfo mLastTab;
	private boolean mAttached;

	static final class TabInfo {
		private final String tag;
		private final Class<?> clss;
		private final Bundle args;
		private Fragment fragment;

		TabInfo(String _tag, Class<?> _class, Bundle _args) {
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	static class SavedState extends BaseSavedState {
		String curTab;
		String[] tabText;
		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			curTab = in.readString();
			tabText = in.createStringArray();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeString(curTab);
			out.writeStringArray(tabText);
		}

		@Override
		public String toString() {
			return "FragmentTabHost.SavedState{"
					+ Integer.toHexString(System.identityHashCode(this))
					+ " curTab=" + curTab + "}";
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public FragmentTabHost(Context context) {
		// Note that we call through to the version that takes an AttributeSet,
		// because the simple Context construct can result in a broken object!
		super(context, null);
		initFragmentTabHost(context, null);
	}

	public FragmentTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFragmentTabHost(context, attrs);
	}

	private void initFragmentTabHost(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				new int[] { android.R.attr.inflatedId }, 0, 0);
		mContainerId = a.getResourceId(0, 0);
		a.recycle();

		super.setOnTabChangedListener(this);

		/*** REMOVE THE REST OF THIS FUNCTION ***/
		/*** findViewById(android.R.id.tabs) IS NULL EVERY TIME ***/
	}

//	/**
//	 * @deprecated Don't call the original TabHost setup, you must instead call
//	 *             {@link #setup(Context, FragmentManager)} or
//	 *             {@link #setup(Context, FragmentManager, int)}.
//	 */
	// @Override
	// @Deprecated
	// public void setup() {
	// throw new IllegalStateException(
	// "Must call setup() that takes a Context and FragmentManager");
	// }

	public void setup(Context context, FragmentManager manager) {
		super.setup();
		mContext = context;
		mFragmentManager = manager;
		ensureContent();
	}

	public void setup(Context context, FragmentManager manager, int containerId) {
		super.setup();
		mContext = context;
		mFragmentManager = manager;
		mContainerId = containerId;
		ensureContent();
		mRealTabContent.setId(containerId);

		// We must have an ID to be able to save/restore our state. If
		// the owner hasn't set one at this point, we will set it ourself.
		if (getId() == View.NO_ID) {
			setId(android.R.id.tabhost);
		}
	}

	private void ensureContent() {
		if (mRealTabContent == null) {
			mRealTabContent = (FrameLayout) findViewById(mContainerId);
			if (mRealTabContent == null) {
				throw new IllegalStateException(
						"No tab content FrameLayout found for id "
								+ mContainerId);
			}
		}
	}

	@Override
	public void setOnTabChangedListener(OnTabChangeListener l) {
		mOnTabChangeListener = l;
	}

	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
		tabSpec.setContent(new DummyTabFactory(mContext));
		String tag = tabSpec.getTag();

		TabInfo info = new TabInfo(tag, clss, args);

		if (mAttached) {
			// If we are already attached to the window, then check to make
			// sure this tab's fragment is inactive if it exists. This shouldn't
			// normally happen.
			info.fragment = mFragmentManager.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}
		}
		
		mTabs.add(info);
		addTab(tabSpec);
	}
	private void reBuildTab(TabHost.TabSpec tabSpec, TabInfo info ) {
		
		tabSpec.setContent(new DummyTabFactory(mContext));
		String tag = tabSpec.getTag();
		
		if (mAttached) {
			// If we are already attached to the window, then check to make
			// sure this tab's fragment is inactive if it exists. This shouldn't
			// normally happen.
			info.fragment = mFragmentManager.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}
		}
		
		mTabs.add(info);
		addTab(tabSpec);
	}
	public void removeCurrentTab(){
		if(mTabs.size() == 1){
			return;
		}
		ArrayList<TabInfo> tempTabs = new ArrayList<TabInfo>();
		int removeTab = getCurrentTab();
		for(int x =0;x<mTabs.size();x++){
			if(removeTab != x){
				tempTabs.add(mTabs.get(x));
			}
		}
		
		clearAllTabs();
		mTabs.clear();
		
		for (int i = 0; i < tempTabs.size(); i++) {
			TabInfo info = tempTabs.get(i);
			String id = info.tag;
			WebTab webtab =  (WebTab) info.fragment;
			String tag = null; 
			if(webtab != null && webtab.title != null){
				tag = ((WebTab) info.fragment).title.getText().toString();
			}
			
			View tabView = createTabView(tag);
			TextView tv = (TextView) tabView.findViewById(R.id.tab_text);
			if(webtab != null){
				//for current tab
				if(webtab.webView != null){
					webtab.webView.title =tv;
				}
				//for when the tab is changed
				webtab.title = tv;
			}
			
			TabHost.TabSpec tabSpec = newTabSpec(id).setIndicator(tabView);

			reBuildTab(tabSpec, info);
			
		}
//		if(tempTabs.size() == 0){
//			TabHost.TabSpec tabSpec = newTabSpec(String.valueOf(System.currentTimeMillis() + 1)).setIndicator(createTabView(null));
//			Bundle bundle = new Bundle();
//			addTab(tabSpec, WebTab.class, bundle);
//		}
		setCurrentTab(mTabs.size()-1);
		
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		String currentTab = getCurrentTabTag();

		// Go through all tabs and make sure their fragments match
		// the correct state.
		FragmentTransaction ft = null;
		for (int i = 0; i < mTabs.size(); i++) {
			TabInfo tab = mTabs.get(i);
			tab.fragment = mFragmentManager.findFragmentByTag(tab.tag);
			if (tab.fragment != null && !tab.fragment.isDetached()) {
				if (tab.tag.equals(currentTab)) {
					// The fragment for this tab is already there and
					// active, and it is what we really want to have
					// as the current tab. Nothing to do.
					mLastTab = tab;
				} else {
					// This fragment was restored in the active state,
					// but is not the current tab. Deactivate it.
					if (ft == null) {
						ft = mFragmentManager.beginTransaction();
					}
					ft.detach(tab.fragment);
				}
			}
		}

		// We are now ready to go. Make sure we are switched to the
		// correct tab.
		mAttached = true;
		ft = doTabChanged(currentTab, ft);
		if (ft != null) {
			ft.commit();
			mFragmentManager.executePendingTransactions();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mAttached = false;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.curTab = getCurrentTabTag();
		ss.tabText = new String[mTabs.size()];
		for(int x =0;x<mTabs.size();x++){
			ss.tabText[x]= (String)mTabs.get(x).tag;
		}
		
		
		return ss;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		String[] tabs = ss.tabText;
		for(int x =0;x<tabs.length;x++){
			String tabName = tabs[x];
			Fragment f = mFragmentManager.findFragmentByTag(tabName);
			TabInfo ti = new TabInfo(tabName, WebTab.class, f.getArguments());
			ti.fragment = f;
			String title = null;
			WebTab tw = null;
			if(f!=null){
				tw =((WebTab)f);
				tw.setOnWebViewCreated(onWebViewCreated);
				if(tw.savedTitle != null){
					title = tw.savedTitle;
				}
			}
			View tabView = createTabView(title);
			TextView tv = (TextView) tabView.findViewById(R.id.tab_text);
			if(tw != null){
				//for current tab
				if(tw.webView != null){
					tw.webView.title =tv;
				}
				//for when the tab is changed
				tw.title = tv;
			}
			TabHost.TabSpec tabSpec = newTabSpec(tabName).setIndicator(tabView);
			tabSpec.setContent(new DummyTabFactory(mContext));
			mTabs.add(ti);
			addTab(tabSpec);
		}
		super.onRestoreInstanceState(ss.getSuperState());
		setCurrentTabByTag(ss.curTab);
//		positionTabs();
	}

	@Override
	public void onTabChanged(String tabId) {
		if (mAttached) {
			FragmentTransaction ft = doTabChanged(tabId, null);
			if (ft != null) {
				ft.commit();
			}
		}

		if (mOnTabChangeListener != null) {
			mOnTabChangeListener.onTabChanged(tabId);
		}
	}

	private FragmentTransaction doTabChanged(String tabId,
			FragmentTransaction ft) {
		TabInfo newTab = null;
		for (int i = 0; i < mTabs.size(); i++) {
			TabInfo tab = mTabs.get(i);
			if (tab.tag.equals(tabId)) {
				newTab = tab;
			}
		}
		if (newTab == null) {
			// throw new IllegalStateException("No tab known for tag " + tabId);
		}
		if (mLastTab != newTab) {
			if (ft == null) {
				ft = mFragmentManager.beginTransaction();
			}
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(mContext,
							newTab.clss.getName(), newTab.args);
					TextView tv =  (TextView)getCurrentTabView().findViewById(R.id.tab_text);
					((WebTab)newTab.fragment).title = tv;
					((WebTab)newTab.fragment).setOnWebViewCreated(onWebViewCreated);
					
					ft.add(mContainerId, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
		}
		return ft;
	}

	public View createTabView(String text) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.tabs_icon, null);

		view.setOnTouchListener(new OnTouchListener() {
			boolean selected = false;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN && v.isSelected()){
					selected = v.isSelected();
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					if(selected){
						removeCurrentTab();
						selected = false;
					}else{
						selected = false;
					}
				}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					selected = false;
				}
				return false;
			}
		});
		
		
		if (text == null || "".equals(text)) {
			return view;
		}
		TextView tv = (TextView) view.findViewById(R.id.tab_text);

		tv.setText(text);
		return view;
	}
	public OnWebViewCreated getOnWebViewCreated() {
		return onWebViewCreated;
	}

	public void setOnWebViewCreated(OnWebViewCreated onWebViewCreated) {
		this.onWebViewCreated = onWebViewCreated;
	}
	public void positionTabs(){
		Point point = new Point();
		int position = getCurrentTab();
		final TabWidget tabWidget = getTabWidget();
		((MasterActivity)mContext).getWindowManager().getDefaultDisplay().getSize(point);
		
		final int screenWidth = point.x;
		
		View selected = tabWidget.getChildAt(position);
		int newX = 0;
		int leftX = 0;
		if(selected != null){
			leftX = selected.getLeft();
			newX = leftX + (selected.getWidth() / 2)
					- (screenWidth / 2);
		}
		if (newX < 0) {
			newX = 0;
		}
		HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		scroll.smoothScrollTo(newX, 0);
	}
}
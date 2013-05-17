package com.yova.app.browser.honey;

import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
/**
 * The <code>PagerAdapter</code> serves the fragments when paging.
 * @author mwho
 */
public class PagerAdapter extends FragmentPagerAdapter {
 
    private List<Tab> fragments;
    /**
     * @param fm
     * @param fragments
     */
    public PagerAdapter(FragmentManager fm, List<Tab> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Tab getItem(int position) {
        return this.fragments.get(position);
    }
 
    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return this.fragments.size();
    }
    public void addItem(Tab tab){
    	fragments.add(tab);
    }
}
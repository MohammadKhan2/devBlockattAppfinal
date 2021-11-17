package com.app.blockaat.home.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.blockaat.home.fragment.HomeCateoryFragment;
import com.app.blockaat.home.model.RootCategoriesData;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList arrListTitle ;
    private List<RootCategoriesData> arrListCategories;

    public ViewPagerAdapter(FragmentManager manager, List<RootCategoriesData> arrListCategories) {
        super(manager);
        this.arrListCategories = arrListCategories;
        this.arrListTitle = new ArrayList();
        for(RootCategoriesData categories: this.arrListCategories)
        {
            arrListTitle.add(categories.getName());
        }
    }

    @Override
    public Fragment getItem(int position) {
        return HomeCateoryFragment.newInstance(arrListCategories.get(position).getId(),"");
    }

    @Override
    public int getCount() {
        return arrListTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arrListTitle.get(position).toString();
    }
}

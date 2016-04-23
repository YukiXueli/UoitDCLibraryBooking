package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.calendarloaded;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.common.CalendarPagerAdapter;

public class CalendarLoaded extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View calendarLoadedView = inflater.inflate(R.layout.calendar, container, false);

        ViewPager _mViewPager = (ViewPager) calendarLoadedView.findViewById(R.id.calendar_view_pager);
        TabLayout _mTabLayout = (TabLayout) calendarLoadedView.findViewById(R.id.calendar_tab_layout);

        _mTabLayout.addTab(_mTabLayout.newTab().setText("Hello"));
        //_mTabLayout.setupWithViewPager(_mViewPager);

        // Will supply the ViewPager with what should be displayed
        PagerAdapter _mPagerAdapter = new CalendarPagerAdapter(getFragmentManager());

        _mViewPager.setAdapter(_mPagerAdapter);

        return calendarLoadedView;
    }
}

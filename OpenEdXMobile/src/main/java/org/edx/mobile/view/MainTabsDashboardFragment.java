package org.edx.mobile.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import org.edx.mobile.R;
import org.edx.mobile.event.FragmentSelectionEvent;
import org.edx.mobile.event.MoveToDiscoveryTabEvent;
import org.edx.mobile.event.ScreenArgumentsEvent;
import org.edx.mobile.model.FragmentItemModel;
import org.edx.mobile.module.analytics.Analytics;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainTabsDashboardFragment extends TabsBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.viewPager2.setUserInputEnabled(false);
    }

    @NonNull
    @Override
    public List<FragmentItemModel> getFragmentItems() {
        ArrayList<FragmentItemModel> items = new ArrayList<>();
        if (environment.getConfig().getDiscoveryConfig().isDiscoveryEnabled()) {
            items.add(new FragmentItemModel(MainDiscoveryFragment.class,
                    getResources().getString(R.string.label_discover),
                    R.drawable.ic_search, getArguments(),
                    () -> environment.getAnalyticsRegistry().trackScreenView(Analytics.Screens.FIND_COURSES)));
        }
        items.add(new FragmentItemModel(LearnFragment.class,
                getResources().getString(R.string.label_learn),
                R.drawable.ic_bookmark_border, getArguments(),
                () -> EventBus.getDefault().post(new FragmentSelectionEvent())));
        items.add(new FragmentItemModel(AccountFragment.class,
                getResources().getString(R.string.profile_title),
                R.drawable.ic_settings, getArguments(),
                () -> environment.getAnalyticsRegistry().trackScreenViewEvent(
                        Analytics.Events.PROFILE_PAGE_VIEWED,
                        Analytics.Screens.PROFILE
                )));
        return items;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull MoveToDiscoveryTabEvent event) {
        if (!environment.getConfig().getDiscoveryConfig().isDiscoveryEnabled()) {
            return;
        }
        if (binding != null) {
            binding.viewPager2.setCurrentItem(0);
            if (event.getScreenName() != null) {
                EventBus.getDefault().post(ScreenArgumentsEvent.Companion.getNewInstance(event.getScreenName()));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

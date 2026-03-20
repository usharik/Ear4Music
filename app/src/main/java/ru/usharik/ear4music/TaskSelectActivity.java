package ru.usharik.ear4music;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdView;
import ru.usharik.ear4music.adapter.SubTaskAdapter;
import ru.usharik.ear4music.adapter.TaskAdapter;
import ru.usharik.ear4music.databinding.TaskSelectActivityBinding;
import ru.usharik.ear4music.framework.BannerAdLoader;
import ru.usharik.ear4music.framework.ViewActivity;
import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.Task;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskSelectActivity extends ViewActivity<TaskSelectViewModel> {

    private TaskSelectActivityBinding binding;
    private AdView bannerAdView;

    @Override
    protected Class<TaskSelectViewModel> getViewModelClass() {
        return TaskSelectViewModel.class;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding = DataBindingUtil.setContentView(this, R.layout.task_select_activity);
        setSupportActionBar(binding.toolbar);
        binding.setViewModel(getViewModel());

        applySystemBarInsets(binding.toolbar, true, true, true, false);
        applySystemBarInsets(binding.tabLayout, true, false, true, false);
        applySystemBarInsets(binding.taskList, true, false, true, true);
        applySystemBarInsets(binding.favouriteTaskList, true, false, true, true);
        applySystemBarInsets(binding.bannerContainer, true, false, true, true);

        setupTabs();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.taskList.setLayoutManager(mLayoutManager);
        binding.taskList.setItemAnimator(new DefaultItemAnimator());
        binding.taskList.getLayoutManager().scrollToPosition(getViewModel().getTaskListPosition());

        TaskAdapter taskAdapter = getViewModel().getTaskAdapter();
        binding.taskList.setAdapter(taskAdapter);
        taskAdapter.getItemClickObservable().subscribe(this::onTaskSelect);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.favouriteTaskList.setLayoutManager(mLayoutManager);
        binding.favouriteTaskList.setItemAnimator(new DefaultItemAnimator());
        binding.favouriteTaskList.getLayoutManager().scrollToPosition(getViewModel().getFavouriteTaskListPosition());

        SubTaskAdapter subTaskAdapter = getViewModel().getFavouriteSubTaskAdapter();
        binding.favouriteTaskList.setAdapter(subTaskAdapter);
        subTaskAdapter.getItemClickObservable().subscribe(this::onSubTaskSelect);

        setTitle(getResources().getString(R.string.select_notes));
        setVersionBanner();
        loadBanner(binding.bannerContainer);
    }

    private void setVersionBanner() {
        String buildDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date(BuildConfig.TIMESTAMP));
        String versionWithHash = BuildConfig.VERSION_NAME + "." + BuildConfig.COMMIT_HASH;
        binding.versionBanner.setText(
                getString(R.string.version_banner, versionWithHash, buildDate));
    }

    private void loadBanner(FrameLayout container) {
        container.post(() -> bannerAdView = BannerAdLoader.loadAnchoredBanner(
                this,
                container,
                BuildConfig.ADMOB_TASK_SELECT_BANNER_AD_UNIT_ID,
                bannerAdView));
    }

    private void setupTabs() {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.all_tasks));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.favourite_tasks));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTab = tab.getPosition();
                getViewModel().setCurrentTab(selectedTab);
                showSelectedTab(selectedTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        int selectedTab = Math.max(0, Math.min(getViewModel().getCurrentTab(), binding.tabLayout.getTabCount() - 1));
        TabLayout.Tab tab = binding.tabLayout.getTabAt(selectedTab);
        if (tab != null) {
            tab.select();
        }
        showSelectedTab(selectedTab);
    }

    private void showSelectedTab(int selectedTab) {
        binding.taskList.setVisibility(selectedTab == 0 ? View.VISIBLE : View.GONE);
        binding.favouriteTaskList.setVisibility(selectedTab == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onPause() {
        getViewModel().setTaskListPosition(Utilities.getScrollPosition(binding.taskList));
        getViewModel().setFavouriteTaskListPosition(Utilities.getScrollPosition(binding.favouriteTaskList));
        BannerAdLoader.destroy(binding.bannerContainer, bannerAdView);
        bannerAdView = null;
        super.onPause();
    }

    public void onTaskSelect(Task task) {
        Intent intent = new Intent(getApplicationContext(), SubTaskSelectActivity.class);
        getViewModel().setTask(task);
        if (task.getId() != null) {
            intent.putExtra(SubTaskSelectActivity.EXTRA_TASK_ID, task.getId());
        }
        startActivity(intent);
    }

    public void onSubTaskSelect(SubTask subTask) {
        Intent intent = new Intent(getApplicationContext(), ExecuteTaskActivity.class);
        getViewModel().setSubTask(subTask);
        if (subTask.getId() != null) {
            intent.putExtra(ExecuteTaskActivity.EXTRA_SUB_TASK_ID, subTask.getId());
        }
        startActivity(intent);
    }
}

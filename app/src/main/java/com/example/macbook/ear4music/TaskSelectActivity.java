package com.example.macbook.ear4music;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.example.macbook.ear4music.adapter.SubTaskAdapter;
import com.example.macbook.ear4music.adapter.TaskAdapter;
import com.example.macbook.ear4music.databinding.TaskSelectActivityBinding;
import com.example.macbook.ear4music.framework.ViewActivity;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;
import com.google.android.material.tabs.TabLayout;

public class TaskSelectActivity extends ViewActivity<TaskSelectViewModel> {

    private TaskSelectActivityBinding binding;

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

        setupTabs();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.taskList.setLayoutManager(mLayoutManager);
        binding.taskList.setItemAnimator(new DefaultItemAnimator());
        binding.taskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.taskList.getLayoutManager().scrollToPosition(getViewModel().getTaskListPosition());

        TaskAdapter taskAdapter = getViewModel().getTaskAdapter();
        binding.taskList.setAdapter(taskAdapter);
        taskAdapter.getItemClickObservable().subscribe(this::onTaskSelect);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.favouriteTaskList.setLayoutManager(mLayoutManager);
        binding.favouriteTaskList.setItemAnimator(new DefaultItemAnimator());
        binding.favouriteTaskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.favouriteTaskList.getLayoutManager().scrollToPosition(getViewModel().getFavouriteTaskListPosition());

        SubTaskAdapter subTaskAdapter = getViewModel().getFavouriteSubTaskAdapter();
        binding.favouriteTaskList.setAdapter(subTaskAdapter);
        subTaskAdapter.getItemClickObservable().subscribe(this::onSubTaskSelect);

        setTitle(getResources().getString(R.string.select_notes));
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

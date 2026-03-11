package com.example.macbook.ear4music;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TabHost;

import com.example.macbook.ear4music.adapter.SubTaskAdapter;
import com.example.macbook.ear4music.adapter.TaskAdapter;
import com.example.macbook.ear4music.databinding.TaskSelectActivityBinding;
import com.example.macbook.ear4music.framework.ViewActivity;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;

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

        binding.tabHost.setup();
        TabHost.TabSpec tabSpec = binding.tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getResources().getString(R.string.all_tasks));
        tabSpec.setContent(binding.tab1.getId());
        binding.tabHost.addTab(tabSpec);

        tabSpec = binding.tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getResources().getString(R.string.favourite_tasks));
        tabSpec.setContent(binding.tab2.getId());
        binding.tabHost.addTab(tabSpec);

        binding.setViewModel(getViewModel());

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

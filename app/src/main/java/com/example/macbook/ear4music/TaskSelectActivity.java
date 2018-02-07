package com.example.macbook.ear4music;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        TaskAdapter taskAdapter = getViewModel().getTaskAdapter();
        binding.recyclerView.setAdapter(taskAdapter);
        taskAdapter.getItemClickObservable().subscribe(this::onTaskSelect);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView1.setLayoutManager(mLayoutManager);
        binding.recyclerView1.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SubTaskAdapter subTaskAdapter = getViewModel().getFavouriteSubTaskAdapter();
        binding.recyclerView1.setAdapter(subTaskAdapter);
        subTaskAdapter.getItemClickObservable().subscribe(this::onSubTaskSelect);

        setTitle(getResources().getString(R.string.select_notes));
    }

    public void onTaskSelect(Task task) {
        Intent intent = new Intent(getApplicationContext(), SubTaskSelectActivity.class);
        getViewModel().setTask(task);
        startActivity(intent);
    }

    public void onSubTaskSelect(SubTask subTask) {
        Intent intent = new Intent(getApplicationContext(), ExecuteTaskActivity.class);
        getViewModel().setTask(subTask.getTask());
        getViewModel().setSubTask(subTask);
        startActivity(intent);
    }
}

package com.example.macbook.ear4music;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.macbook.ear4music.adapter.SubTaskAdapter;
import com.example.macbook.ear4music.databinding.SubTaskSelectActivityBinding;
import com.example.macbook.ear4music.framework.ViewActivity;
import com.example.macbook.ear4music.model.SubTask;

import io.reactivex.disposables.Disposable;

public class SubTaskSelectActivity extends ViewActivity<SubTaskSelectViewModel> {

    private SubTaskSelectActivityBinding binding;
    private Disposable itemClickDisposable;

    @Override
    protected Class<SubTaskSelectViewModel> getViewModelClass() {
        return SubTaskSelectViewModel.class;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding = DataBindingUtil.setContentView(this, R.layout.sub_task_select_activity);

        getViewModel().syncWithAppState();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.subTaskList.setLayoutManager(mLayoutManager);
        binding.subTaskList.setItemAnimator(new DefaultItemAnimator());
        binding.subTaskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.subTaskList.getLayoutManager().scrollToPosition(getViewModel().getSubTaskListPosition());

        binding.setViewModel(getViewModel());
        SubTaskAdapter subTaskAdapter = getViewModel().getTaskAdapter();
        binding.subTaskList.setAdapter(subTaskAdapter);
        itemClickDisposable = subTaskAdapter.getItemClickObservable().subscribe(this::onSubTaskSelect);

        setTitle(getResources().getString(R.string.select_sub_task));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getViewModel().setSubTaskListPosition(Utilits.getScrollPosition(binding.subTaskList));
        if (itemClickDisposable != null) {
            itemClickDisposable.dispose();
        }
    }

    public void onSubTaskSelect(SubTask subTask) {
        Intent intent = new Intent(getApplicationContext(), ExecuteTaskActivity.class);
        getViewModel().setSubTask(subTask);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

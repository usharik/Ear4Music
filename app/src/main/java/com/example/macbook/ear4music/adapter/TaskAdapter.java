package com.example.macbook.ear4music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.macbook.ear4music.TaskListRowViewModel;
import com.example.macbook.ear4music.databinding.TaskListRowBinding;
import com.example.macbook.ear4music.model.Task;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> taskList;
    private final PublishSubject<Task> onClickSubject = PublishSubject.create();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TaskListRowBinding binding;

        public MyViewHolder(TaskListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TaskListRowViewModel viewModel) {
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TaskListRowBinding itemBinding = TaskListRowBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Task task = taskList.get(position);
        TaskListRowViewModel viewModel = new TaskListRowViewModel(task);
        holder.bind(viewModel);
        holder.itemView.setOnClickListener((v) -> onClickSubject.onNext(task));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Observable<Task> getItemClickObservable(){
        return onClickSubject;
    }
}
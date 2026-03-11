package ru.usharik.ear4music.adapter;

import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ru.usharik.ear4music.SubTaskListRowViewModel;
import ru.usharik.ear4music.repository.SubTaskRepository;
import ru.usharik.ear4music.service.Utils;
import ru.usharik.ear4music.databinding.SubTaskListRowBinding;
import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.Task;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Created by macbook on 18.02.2018.
 */

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.MyViewHolder> {

    private final List<SubTask> subTaskList;
    private final PublishSubject<SubTask> onClickSubject = PublishSubject.create();
    private final SubTaskRepository subTaskRepository;
    private final Map<Long, Task> tasksById;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final SubTaskListRowBinding binding;

        public MyViewHolder(SubTaskListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SubTaskListRowViewModel viewModel) {
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }

        public SubTaskListRowBinding getBinding() {
            return binding;
        }
    }

    public SubTaskAdapter(List<SubTask> subTaskList,
                          List<Task> tasks,
                          SubTaskRepository subTaskRepository) {
        this.subTaskList = subTaskList;
        this.subTaskRepository = subTaskRepository;
        this.tasksById = createTaskIndex(tasks);
    }

    private Map<Long, Task> createTaskIndex(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Task> taskIndex = new HashMap<>();
        for (Task task : tasks) {
            if (task != null && task.getId() != null) {
                taskIndex.put(task.getId(), task);
            }
        }
        return taskIndex;
    }

    @Override
    public SubTaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SubTaskListRowBinding itemBinding = SubTaskListRowBinding.inflate(layoutInflater, parent, false);
        return new SubTaskAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(SubTaskAdapter.MyViewHolder holder, int position) {
        final SubTask subTask = subTaskList.get(position);

        Resources res = holder.getBinding().getRoot().getResources();
        Task task = tasksById.get(subTask.getTaskId());
        SubTaskListRowViewModel viewModel = new SubTaskListRowViewModel(subTask, Utils.getSubTaskDescription(res, subTask), task, subTaskRepository);
        holder.bind(viewModel);
        holder.itemView.setOnClickListener((v) -> onClickSubject.onNext(subTask));
    }

    @Override
    public int getItemCount() {
        return subTaskList.size();
    }

    public Observable<SubTask> getItemClickObservable(){
        return onClickSubject;
    }
}

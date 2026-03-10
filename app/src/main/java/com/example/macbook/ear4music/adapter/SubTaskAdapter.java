package com.example.macbook.ear4music.adapter;

import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.macbook.ear4music.SubTaskListRowViewModel;
import com.example.macbook.ear4music.service.DbService;
import com.example.macbook.ear4music.service.Utils;
import com.example.macbook.ear4music.databinding.SubTaskListRowBinding;
import com.example.macbook.ear4music.model.SubTask;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by macbook on 18.02.2018.
 */

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.MyViewHolder> {

    private final List<SubTask> subTaskList;
    private final PublishSubject<SubTask> onClickSubject = PublishSubject.create();
    private final DbService dbService;

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

    public SubTaskAdapter(List<SubTask> subTaskList, DbService dbService) {
        this.subTaskList = subTaskList;
        this.dbService = dbService;
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
        SubTaskListRowViewModel viewModel = new SubTaskListRowViewModel(subTask, Utils.getSubTaskDescription(res, subTask), dbService.getDaoSession());
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

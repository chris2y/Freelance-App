package com.example.freelancerapp10.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.freelancerapp10.model.HomeModel;

import java.util.List;

public class HomeDiffCallback extends DiffUtil.Callback {

    private List<HomeModel> oldList;
    private List<HomeModel> newList;

    public HomeDiffCallback(List<HomeModel> oldList, List<HomeModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        HomeModel oldItem = oldList.get(oldItemPosition);
        HomeModel newItem = newList.get(newItemPosition);
        // Compare unique identifiers of the Home, e.g.,
        return oldItem.getKey().equals(newItem.getKey());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        HomeModel oldItem = oldList.get(oldItemPosition);
        HomeModel newItem = newList.get(newItemPosition);
        // Compare the content of the Home to check if they are the same
        return oldItem.equals(newItem);
    }

}
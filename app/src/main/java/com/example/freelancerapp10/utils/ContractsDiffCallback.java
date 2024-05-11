package com.example.freelancerapp10.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.freelancerapp10.model.ContractModel;

import java.util.List;

public class ContractsDiffCallback extends DiffUtil.Callback {

    private List<ContractModel> oldList;
    private List<ContractModel> newList;

    public ContractsDiffCallback(List<ContractModel> oldList, List<ContractModel> newList) {
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
        ContractModel oldItem = oldList.get(oldItemPosition);
        ContractModel newItem = newList.get(newItemPosition);
        // Compare unique identifiers of the contracts, e.g., contract IDs
        return oldItem.getKey().equals(newItem.getKey());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ContractModel oldItem = oldList.get(oldItemPosition);
        ContractModel newItem = newList.get(newItemPosition);
        // Compare the content of the contracts to check if they are the same
        return oldItem.equals(newItem);
    }
}
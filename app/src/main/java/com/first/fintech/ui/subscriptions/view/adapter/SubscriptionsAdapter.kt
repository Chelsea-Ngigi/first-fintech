package com.first.fintech.ui.subscriptions.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.first.fintech.data.model.subscriptions.Subscription
import com.first.fintech.databinding.ItemSubscriptionBinding


class SubscriptionsAdapter : ListAdapter<Subscription, SubscriptionsAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Subscription>() {
        override fun areItemsTheSame(old: Subscription, new: Subscription) = old.id == new.id
        override fun areContentsTheSame(old: Subscription, new: Subscription) = old == new
    }

    inner class ViewHolder(val binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription) {
            binding.tvServiceName.text        = subscription.serviceName.replaceFirstChar { it.uppercase() }
            binding.tvPrice.text              = "KES ${subscription.amountPaid}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}
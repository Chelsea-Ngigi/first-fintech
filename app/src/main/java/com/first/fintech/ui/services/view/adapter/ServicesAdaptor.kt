package com.first.fintech.ui.services.view.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.graphics.Paint
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.first.fintech.data.model.services.Service
import com.first.fintech.databinding.ItemServiceBinding


class ServicesAdapter(
    private val onSubscribeClick: (Service) -> Unit
) : ListAdapter<Service, ServicesAdapter.ServiceViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(old: Service, new: Service) = old.id == new.id
        override fun areContentsTheSame(old: Service, new: Service) = old == new
    }

    inner class ServiceViewHolder(val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(service: Service) {
            binding.tvServiceName.text        = service.serviceName.replaceFirstChar { it.uppercase() }

            if (service.isDiscounted && service.discountedPrice != null) {
                binding.tvOriginalPrice.apply {
                    text            = "KES ${service.pricing}"
                    paintFlags      = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    isVisible       = true
                }
                binding.tvPrice.text        = "KES ${service.discountedPrice}"
                binding.tvDiscountBadge.apply {
                    text = "${service.discountPercent.toInt()}% OFF"
                    isVisible = true
                }
            } else {
                binding.tvOriginalPrice.isVisible = false
                binding.tvDiscountBadge.isVisible = false
                binding.tvPrice.text              = "KES ${service.pricing}"
            }

            binding.btnSubscribe.setOnClickListener { onSubscribeClick(service) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ServiceViewHolder(
            ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) =
        holder.bind(getItem(position))
}
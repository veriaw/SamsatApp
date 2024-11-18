package com.veriaw.samsatapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.veriaw.samsatapp.R
import com.veriaw.samsatapp.data.entity.SubmissionEntity
import com.veriaw.samsatapp.databinding.ListAssignBinding
import com.veriaw.samsatapp.ui.DetailActivity

class SubmissionAdapter: ListAdapter<SubmissionEntity, SubmissionAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(val binding: ListAssignBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(submission: SubmissionEntity){
            binding.tvStatus.text=submission.status
            binding.tvPengajuan.text=submission.submissiontype
            binding.tvTimeStamp.text=submission.timestamp
            if(submission.status=="Sedang Diproses"){
                binding.imageIndicator.setImageResource(R.drawable.circle_grey)
            }else if(submission.status=="Menunggu Pembayaran"){
                binding.imageIndicator.setImageResource(R.drawable.circle_yellow)
            }else if(submission.status=="Selesai"){
                binding.imageIndicator.setImageResource(R.drawable.circle_green)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SubmissionEntity>() {
            override fun areItemsTheSame(oldItem: SubmissionEntity, newItem: SubmissionEntity): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: SubmissionEntity, newItem: SubmissionEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListAssignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val submission = getItem(position)
        holder.bind(submission)
        holder.itemView.setOnClickListener {
            Log.d("ADAPTER CHOOSEN ID","${submission.id}")
            val moveWithObjectIntent = Intent(holder.itemView.context,DetailActivity::class.java)
            moveWithObjectIntent.putExtra("SubmissionId", submission.id)
            holder.itemView.context.startActivity(moveWithObjectIntent)
        }
    }
}
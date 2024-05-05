package com.apps.arbaelbarca.omrscanner.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps.arbaelbarca.omrscanner.data.model.response.ResponseGetLjk
import com.apps.arbaelbarca.omrscanner.databinding.LayoutItemListLjkBinding
import com.apps.arbaelbarca.omrscanner.utils.ViewBindingVH

class ListCheckLjkAdapter : RecyclerView.Adapter<ViewBindingVH>() {

    val listCheckLjk: MutableList<ResponseGetLjk.DataResponseLjk?> = mutableListOf()

    fun addListLjk(getListLjk: List<ResponseGetLjk.DataResponseLjk?>) {
        listCheckLjk.clear()
        listCheckLjk.addAll(getListLjk)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewBindingVH {
        return ViewBindingVH.create(p0, LayoutItemListLjkBinding::inflate)
    }

    override fun getItemCount(): Int {
        return listCheckLjk.size
    }

    override fun onBindViewHolder(p0: ViewBindingVH, p1: Int) {
        val dataItem = listCheckLjk[p1]

        (p0.binding as LayoutItemListLjkBinding).apply {
            dataItem?.apply {
                tvNikItemLjk.text = "Nik : $nIK"
                tvNameItemLjk.text = "Nama : $nama"

                tvDateItemLjk.text = dateCreated
                tvScoreItemLjk.text = "Score: $score"

                tvItemTotalAnswerTrue.text = answerTrue
                tvItemTotalAnswerFalse.text = answerFalse
            }

        }
    }
}
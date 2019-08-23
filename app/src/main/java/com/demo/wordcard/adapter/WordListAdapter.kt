package com.demo.wordcard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.wordcard.OnAvoidDoubleClickListener
import com.demo.wordcard.R
import com.demo.wordcard.activity.WordDetailActivity
import com.demo.wordcard.room.entity.Word
import com.demo.wordcard.util.DataUtil
import kotlinx.android.synthetic.main.item_word_list.view.*


class WordListAdapter(var context: Context, var list: MutableList<Word>) :
    RecyclerView.Adapter<WordListAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(layoutInflater.inflate(R.layout.item_word_list, parent, false))

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    fun setData(data: MutableList<Word>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(bean: Word) {

            itemView.item_word_tv.text= bean.word
            itemView.item_mean_tv.text= DataUtil.getStringDefinition(bean.wordID)

            itemView.setOnClickListener(object :OnAvoidDoubleClickListener(){
                override fun onNoDoubleClick(v: View?) {

                    WordDetailActivity.startWordDetail(context,bean.wordID)

                }

            })

        }
    }
}
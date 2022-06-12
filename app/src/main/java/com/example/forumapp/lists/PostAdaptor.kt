package com.example.forumapp.lists

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.forumapp.PostDetailsActivity
import com.example.forumapp.R
import com.squareup.picasso.Picasso
import java.util.*

class PostAdaptor (var context: Context, modelPosts: List<PostModel>) :
    RecyclerView.Adapter<PostAdaptor.MyHolder>() {

    var _modelPosts =  modelPosts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.posts_row, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        val titlee: String = _modelPosts[position].title.toString()
        val ptime: String = _modelPosts[position].time.toString()
        val pid: String? = _modelPosts[position].time

        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = ptime.toLong()
        val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()




        holder.title.text = titlee
        holder.time.text = timedate

        holder.more.setOnClickListener {
            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra("pid", pid)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return _modelPosts.size
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var time = itemView.findViewById<TextView>(R.id.uTimeTv)
        var title = itemView.findViewById<TextView>(R.id.pTitleTv)
        var more = itemView.findViewById<ImageButton>(R.id.moreBtn)

    }

}






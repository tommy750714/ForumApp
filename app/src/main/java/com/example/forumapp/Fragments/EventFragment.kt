package com.example.forumapp.Fragments

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.forumapp.R

class EventFragment : Fragment() {

    companion object {
        fun newInstance() = EventFragment ()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val eventbtn = view!!.findViewById<Button>(R.id.create_event)
        val startTime = "2022-02-1T09:00:00"
        val endTime = "2022-02-1T12:00:00"

        val mSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val mStartTime = mSimpleDateFormat.parse(startTime)
        val mEndTime = mSimpleDateFormat.parse(endTime)

        eventbtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_EDIT)
            intent.type = "vnd.android.cursor.item/event"
            intent.putExtra("beginTime", mStartTime.time)
            intent.putExtra("time", true)
            intent.putExtra("rule", "FREQ=YEARLY")
            intent.putExtra("endTime", mEndTime.time)
            intent.putExtra("title", "Cat Event")
            startActivity(intent)
        }

    }

}
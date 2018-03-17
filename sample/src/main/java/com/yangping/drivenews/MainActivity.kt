package com.yangping.drivenews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.yangping.newsmodule.DriveNews
import com.yangping.newsmodule.DriveNewsUril
import com.yangping.newsmodule.OnGetDriveNewsCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnGetDriveNewsCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onStart() {
        super.onStart()
        //取資料
        DriveNewsUril.setCallback(this).start()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(newsList: List<DriveNews>) {
        recycler_view.adapter = MySampleAdapter(newsList)
    }

    class MySampleAdapter constructor(val mDataList: List<DriveNews>) : RecyclerView.Adapter<MySampleAdapter.SampleViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SampleViewHolder {
            val textView = TextView(parent!!.context)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.layoutParams = lp
            return SampleViewHolder(textView)
        }

        override fun getItemCount(): Int = mDataList.size

        override fun onBindViewHolder(holder: SampleViewHolder?, position: Int) {
            if (holder != null) {
                (holder.itemView as TextView).text = mDataList[position].modDttm
            }
        }

        class SampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }
    }
}

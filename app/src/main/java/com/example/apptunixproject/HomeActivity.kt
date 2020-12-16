package com.example.apptunixproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {
    private lateinit var btnSelectVideos: Button
    private lateinit var selectedVideos: List<String>
    private lateinit var recyclerView: RecyclerView
    private var videoAdapter: VideoAdapter? = null
    private lateinit var tvNoVideoSelected: TextView
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnSelectVideos = findViewById(R.id.btnSelectVideos)
        tvNoVideoSelected = findViewById(R.id.tvNoVideoSelected)
        selectedVideos = ArrayList()
        recyclerView = findViewById(R.id.rvVideos)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@HomeActivity)
        val snapHelper: SnapHelper = PagerSnapHelper()
        recyclerView.layoutManager = layoutManager
        snapHelper.attachToRecyclerView(recyclerView)
        btnSelectVideos.setOnClickListener {
            val videoPicker = Intent(Intent.ACTION_PICK)
            videoPicker.type = "video/*"
            videoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(videoPicker, Constants().REQ_CODE_GALLERY)
        }
    }

    override fun onResume() {
        super.onResume()
        tvNoVideoSelected.visibility = if (selectedVideos.isNotEmpty() && selectedVideos.size == Constants().NUMBER_OF_VIDEOS) View.GONE else View.VISIBLE
        if(videoAdapter != null)
            recyclerView.adapter = videoAdapter
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toasty.warning(this, R.string.back_press_exit).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, Constants().BACK_PRESS_EXIT_TIME)
    }

    /**
     * receiving the videos from gallery and setting in adapter
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            selectedVideos = getSelectedVideos(requestCode, data)
            if (selectedVideos.isEmpty() || selectedVideos.size != Constants().NUMBER_OF_VIDEOS) {
                Toasty.error(this@HomeActivity, getString(R.string.error_amount_videos)).show()
                tvNoVideoSelected.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvNoVideoSelected.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                setAdapter()
            }
        }
    }

    /**
     * function for initializing the video adapter
     */
    private fun setAdapter() {
        videoAdapter = VideoAdapter(this@HomeActivity, selectedVideos)
        recyclerView.adapter = videoAdapter
    }

    /**
     * function for getting a list of selected videos from gallery
     */
    private fun getSelectedVideos(requestCode: Int, data: Intent?): List<String> {
        val result: MutableList<String> = ArrayList()
        val clipData = data!!.clipData
        if (clipData != null) {

                for (i in 0 until clipData.itemCount) {
                    val videoItem = clipData.getItemAt(i)
                    val videoURI = videoItem.uri
                    result.add(videoURI.toString())
                }
                Log.e("videos added", "---------" + result.size)
        }
        return result
    }
}
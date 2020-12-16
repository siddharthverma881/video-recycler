package com.example.apptunixproject

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

class VideoAdapter internal constructor(private val context: Context, private val videoList: List<String>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.playVideo(position)
    }

    override fun getItemCount(): Int = videoList.size

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        private var videoView: VideoView? = null

        fun playVideo(position: Int) {
            videoView = mView.findViewById<View>(R.id.videoView) as VideoView
            videoView!!.setMediaController(null)
            videoView!!.setVideoURI(Uri.parse(videoList[position]))
            videoView!!.start()
            videoView!!.setOnPreparedListener { mp: MediaPlayer -> mp.isLooping = true }
        }

        fun stopVideo() {
            if (videoView != null && videoView!!.isPlaying) {
                videoView!!.stopPlayback()
                videoView!!.suspend()
                videoView = null
            }
        }

    }

}
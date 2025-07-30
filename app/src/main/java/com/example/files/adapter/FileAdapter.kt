package com.example.files.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.files.R
import java.io.File
import java.net.URLConnection

class FileAdapter(
    private val context: Context,
    private val fileData: MutableList<File>,
    private val fileEvents: FileEvents) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
        
    var viewType = 0

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById<TextView>(R.id.name)
        val image = itemView.findViewById<ImageView>(R.id.image)

        fun onBindCodes(file: File) {

            name.text = file.name

            var fileType = ""

            if (file.isDirectory) {
                image.setImageResource(R.drawable.folder)
            } else {

                when {

                    isImage(file.path) -> {
                        image.setImageResource(R.drawable.image)
                        fileType = "image/*"
                    }

                    isVideo(file.path) -> {
                        image.setImageResource(R.drawable.video)
                        fileType = "video/*"
                    }

                    isZip(file.path) -> {
                        image.setImageResource(R.drawable.zip)
                    }

                    isVoice(file.path) -> {
                        image.setImageResource(R.drawable.record)
                    }

                    isMusic(file.path) -> {
                        image.setImageResource(R.drawable.music)
                    }

                    isPdf(file.path) -> {
                        image.setImageResource(R.drawable.pdf)
                    }

                    isApk(file.path) -> {
                        image.setImageResource(R.drawable.apk)
                    }

                    else -> {
                        image.setImageResource(R.drawable.file)
                        fileType = "text/plain"
                    }

                }

            }

            itemView.setOnClickListener {

                if (file.isDirectory) {
                    fileEvents.onFolderClicked(file.path)
                } else {
                    fileEvents.onFileClicked(file, fileType)
                }

            }

            itemView.setOnLongClickListener {

                fileEvents.onFolderOrFileLongClicked(file, adapterPosition)

                true
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {

        val view: View

        if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_file_linear, parent, false)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_file_grid, parent, false)
        }
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {

        holder.onBindCodes(fileData[position])

    }

    override fun getItemCount(): Int {
        return fileData.size
    }

    //----------------------------------------------------------
    //Functions =>
    fun addNewFileOrFolder(newFile: File) {
        fileData.add(0, newFile)
        notifyItemInserted(0)
    }

    fun removeFileOrFolder(file: File, position: Int) {

        fileData.remove(file)
        notifyItemRemoved(position)

    }

    fun changeViewType(newViewType: Int) {

        viewType = newViewType
        notifyDataSetChanged()

    }

    //----------------------------------------------------------
    //memeTypes =>
    private fun isImage(path: String): Boolean {
        val mimeType: String? = URLConnection.guessContentTypeFromName(path)
        return mimeType?.startsWith("image") == true
    }

    private fun isVideo(path: String): Boolean {
        val mimeType: String? = URLConnection.guessContentTypeFromName(path)
        return mimeType?.startsWith("video") == true
    }

    private fun isZip(name: String): Boolean {
        return name.contains(".zip") || name.contains(".rar")
    }

    private fun isVoice(path: String): Boolean {
        val mimeType: String? = URLConnection.guessContentTypeFromName(path)
        return mimeType == "audio/amr"
    }

    private fun isMusic(path: String): Boolean {
        val mimeType: String? = URLConnection.guessContentTypeFromName(path)
        return mimeType == "audio/mpeg"
    }

    private fun isPdf(path: String): Boolean {
        val mimeType: String? = URLConnection.guessContentTypeFromName(path)
        return mimeType == "application/pdf"
    }

    private fun isApk(path: String): Boolean {
        val mimeType: String? = URLConnection.guessContentTypeFromName(path)
        return mimeType == "application/vnd.android.package-archive"
    }
//----------------------------------------------------------
}

interface FileEvents {

    fun onFolderClicked(path: String)
    fun onFileClicked(file: File, type: String)
    fun onFolderOrFileLongClicked(file: File, position: Int)

}
package com.example.files.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.files.activity.MainActivity
import com.example.files.adapter.FileAdapter
import com.example.files.adapter.FileEvents
import com.example.files.R
import com.example.files.databinding.DialogAddFileBinding
import com.example.files.databinding.DialogAddFolderBinding
import com.example.files.databinding.DialogRemoveBinding
import com.example.files.databinding.FragmentFileBinding
import java.io.File

class fileFragment(val path: String) : Fragment(), FileEvents {

    private lateinit var binding: FragmentFileBinding
    private lateinit var fileAdapter: FileAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFileBinding.inflate(layoutInflater)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (MainActivity.viewType == 0) {
            binding.icChangePosition.setImageResource(R.drawable.grid)
        } else {
            binding.icChangePosition.setImageResource(R.drawable.list)
        }

        val files = File(path)

        binding.fileName.text = files.name + " >"

        if (files.isDirectory) {

            val fileData = mutableListOf<File>()
            fileAdapter = FileAdapter(requireContext(), fileData, this)
            binding.fileRecycler.adapter = fileAdapter
            binding.fileRecycler.layoutManager = GridLayoutManager(requireContext(), MainActivity.spanCount, RecyclerView.VERTICAL, false)
            MainActivity.viewType = MainActivity.viewType

            fileData.addAll(files.listFiles()!!)
            fileData.sort()
            if (fileData.size > 0) {
                binding.fileRecycler.visibility = View.VISIBLE
                binding.image.visibility = View.GONE

            } else {
                binding.fileRecycler.visibility = View.GONE
                binding.image.visibility = View.VISIBLE
            }

        }

        binding.icAddFolder.setOnClickListener {

            val dialog = AlertDialog.Builder(requireContext()).create()
            val dialogBinding = DialogAddFolderBinding.inflate(layoutInflater)
            dialog.setView(dialogBinding.root)
            dialog.setCancelable(true)
            dialog.show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogBinding.addFolderCancel.setOnClickListener {

                dialog.dismiss()

            }

            dialogBinding.addFolderDone.setOnClickListener {

                val nameOfFolder = dialogBinding.addFolderEditText.text.toString()
                val newFolder = File(path + File.separator + nameOfFolder)

                if (!newFolder.exists()) {

                    newFolder.mkdir()
                    fileAdapter.addNewFileOrFolder(newFolder)
                    binding.fileRecycler.scrollToPosition(0)
                    dialog.dismiss()

                    binding.fileRecycler.visibility = View.VISIBLE
                    binding.image.visibility = View.GONE

                }


            }

        }

        binding.icAddFile.setOnClickListener {

            val dialog = AlertDialog.Builder(requireContext()).create()
            val dialogBinding = DialogAddFileBinding.inflate(layoutInflater)
            dialog.setView(dialogBinding.root)
            dialog.setCancelable(true)
            dialog.show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogBinding.addFileCancel.setOnClickListener {

                dialog.dismiss()

            }

            dialogBinding.addFileDone.setOnClickListener {

                val nameOfFolder = dialogBinding.addFileEditText.text.toString()
                val newFolder = File(path + File.separator + nameOfFolder)

                if (!newFolder.exists()) {

                    newFolder.createNewFile()
                    fileAdapter.addNewFileOrFolder(newFolder)
                    binding.fileRecycler.scrollToPosition(0)
                    dialog.dismiss()

                    binding.fileRecycler.visibility = View.VISIBLE
                    binding.image.visibility = View.GONE

                }


            }

        }

        binding.icChangePosition.setOnClickListener {

            if (MainActivity.viewType == 0) {

                binding.icChangePosition.setImageResource(R.drawable.list)
                MainActivity.viewType = 1
                MainActivity.spanCount = 3

                fileAdapter.changeViewType(MainActivity.viewType)

                binding.fileRecycler.layoutManager = GridLayoutManager(requireContext(),MainActivity.spanCount)


            } else {

                binding.icChangePosition.setImageResource(R.drawable.grid)
                MainActivity.viewType = 0
                MainActivity.spanCount = 1

                fileAdapter.changeViewType(MainActivity.viewType)

                binding.fileRecycler.layoutManager = GridLayoutManager(requireContext(),MainActivity.spanCount,RecyclerView.VERTICAL,false)

            }

        }
    }

    override fun onFolderClicked(path: String) {

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fileFragment(path))
        transaction.addToBackStack(null)
        transaction.commit()

    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onFileClicked(file: File, type: String) {

        val intent = Intent(Intent.ACTION_VIEW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().packageName + ".provider",
                file
            )
            intent.setDataAndType(fileProvider, type)
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)

    }

    override fun onFolderOrFileLongClicked(file: File, position: Int) {

        val dialog = AlertDialog.Builder(requireContext()).create()
        val dialogBinding = DialogRemoveBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.removeCancel.setOnClickListener {

            dialog.dismiss()

        }

        dialogBinding.removeDone.setOnClickListener {

            file.deleteRecursively()
            fileAdapter.removeFileOrFolder(file, position)
            dialog.dismiss()

        }


    }


}




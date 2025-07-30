package com.example.files.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.files.fragment.fileFragment
import com.example.files.R
import com.example.files.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindnig: ActivityMainBinding

    companion object{
        var viewType = 0
        var spanCount = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        bindnig = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bindnig.root)

        val file = getExternalFilesDir(null)!!
        val path = file.path

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainerView, fileFragment(path))
        transaction.commit()

    }
}
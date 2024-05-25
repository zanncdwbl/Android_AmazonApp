package com.example.projectworkpcparts

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectworkpcparts.databinding.ActivityMainBinding

import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        remove little actionbar that has been annoying
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productAdapter = ProductAdapter(emptyList(), this)
        binding.pcPartsOverview.layoutManager = LinearLayoutManager(this)
        binding.pcPartsOverview.adapter = productAdapter

        binding.homeIcon.setOnClickListener {
            viewModel.fetchAllProducts(this)
        }

        binding.userIcon.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }

        viewModel.productList.observe(this, Observer { productList ->
            productList?.let {
                productAdapter.refreshData(it)
            }
        })

        viewModel.fetchData(context = this)
        SQLiteManager.getInstance(this)
    }
    override fun onResume() {
        super.onResume()

        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            val searchEditText: EditText = findViewById(R.id.searchEditText)
            val searchQuery = searchEditText.text.toString()

            lifecycleScope.launch {
                viewModel.searchProductsByUrl(this@MainActivity, searchQuery)
            }
        }
    }
}
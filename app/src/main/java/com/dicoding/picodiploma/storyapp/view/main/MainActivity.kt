package com.dicoding.picodiploma.storyapp.view.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.storyapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.storyapp.view.ViewModelFactory
import com.dicoding.picodiploma.storyapp.view.adapter.LoadingStateAdapter
import com.dicoding.picodiploma.storyapp.view.adapter.StoryAdapter
import com.dicoding.picodiploma.storyapp.view.map.MapsActivity
import com.dicoding.picodiploma.storyapp.view.upload.UploadStoryActivity
import com.dicoding.picodiploma.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupRecyclerView()
                observeStories()
            }
        }

        setupAction()
    }

    private fun setupAction() {
        binding.buttonAdd.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, UploadStoryActivity::class.java))
            }
        }
        binding.actionMaps.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }
        binding.actionLocalization.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.actionLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager =
                if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(this@MainActivity, 2)
                } else {
                    LinearLayoutManager(this@MainActivity)
                }
            adapter = this@MainActivity.adapter.withLoadStateFooter(
                footer = LoadingStateAdapter { this@MainActivity.adapter.retry() }
            )
        }

        adapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            if (loadState.source.refresh is LoadState.Error) {
                Toast.makeText(
                    this,
                    "Error: ${(loadState.source.refresh as LoadState.Error).error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeStories() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.storyPagingList.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }
}
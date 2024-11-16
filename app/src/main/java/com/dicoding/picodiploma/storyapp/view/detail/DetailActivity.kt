package com.dicoding.picodiploma.storyapp.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyapp.data.ResultState
import com.dicoding.picodiploma.storyapp.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.storyapp.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        storyId?.let {
            viewModel.getDetailStory(it)
        }

        observeStoryDetail()
    }

    private fun observeStoryDetail() {
        viewModel.storyDetail.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    val story = result.data
                    binding.apply {
                        tvDetailName.text = story.name
                        tvDetailDescription.text = story.description
                        Glide.with(this@DetailActivity)
                            .load(story.photoUrl)
                            .into(ivDetailPhoto)
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}
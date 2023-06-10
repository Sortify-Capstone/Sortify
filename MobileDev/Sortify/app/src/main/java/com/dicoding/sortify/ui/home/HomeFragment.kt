package com.dicoding.sortify.ui.home

import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.sortify.MainActivity
import com.dicoding.sortify.databinding.FragmentHomeBinding
import com.dicoding.sortify.ui.viewmodel.AddClassifyViewModel
import com.dicoding.sortify.ui.viewmodel.HomeViewModel
import com.dicoding.storyappdicodingbpaai.viewModel.ViewModelFactory
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
//    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var viewModel: HomeViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(
            this,
            ViewModelFactory((activity as MainActivity))
        )[HomeViewModel::class.java]

        viewModel?.cekStatus()

        viewModel?.status?.observe(viewLifecycleOwner){ stat ->
            binding.apply {
                status.text = stat.status
            }
        }

        return binding.root
    }

    companion object {

    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true

        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
            binding.rvStory.smoothScrollToPosition(0)
        }
    }
}
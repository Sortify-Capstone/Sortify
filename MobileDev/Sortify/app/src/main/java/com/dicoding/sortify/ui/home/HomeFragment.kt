package com.dicoding.sortify.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val rvAdapter = HomeAdapter()

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
        viewModel?.loadHistoryData()

        viewModel?.status?.observe(viewLifecycleOwner){ stat ->
            binding.apply {
                status.text = stat.status
            }
        }

        viewModel?.apply {
            loading.observe(viewLifecycleOwner) { binding.loading.visibility = it }
            error.observe(
                viewLifecycleOwner
            ) { if (it.isNotEmpty()) Toast.makeText(getActivity(), it, Toast.LENGTH_SHORT).show() }

            sampahList.observe(viewLifecycleOwner) {
                rvAdapter.apply {
                    initData(it)
                    notifyDataSetChanged()
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }

        binding.rvHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = rvAdapter
        }

        return binding.root
    }

    companion object {

    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true

        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
            binding.rvHistory.smoothScrollToPosition(0)
        }
    }
}
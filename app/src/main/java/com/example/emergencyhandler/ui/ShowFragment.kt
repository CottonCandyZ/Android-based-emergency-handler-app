package com.example.emergencyhandler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergencyhandler.databinding.FragmentShowBinding
import com.example.emergencyhandler.model.MyViewModel
import com.example.emergencyhandler.showMessage
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ShowFragment : Fragment() {

    private var _binding: FragmentShowBinding? = null
    private val binding get() = _binding!!
    private val myViewModel: MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showCallInfoAdapter = ShowCallInfoAdapter()
        with(binding) {
            with(recyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = showCallInfoAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }

            swipeRefresh.setOnRefreshListener {
                myViewModel.refreshManually()
            }

            myViewModel.state.observe(viewLifecycleOwner) {
                when (it) {
                    MyViewModel.STATE.FETCH_SUCCESS -> {
                    }
                    MyViewModel.STATE.FETCH_ERROR -> {
                        showMessage(requireContext(), myViewModel.errorMessage)
                    }
                    MyViewModel.STATE.REFRESH_COMPLETE -> {
                        swipeRefresh.isRefreshing = false
                    }
                    MyViewModel.STATE.REFRESH_ERROR -> {
                        swipeRefresh.isRefreshing = false
                        showMessage(requireContext(), myViewModel.errorMessage)
                    }
                    null -> {
                    }
                }
            }
            myViewModel.call.observe(viewLifecycleOwner) {
                recyclerView.smoothScrollToPosition(0)
                showCallInfoAdapter.submitList(it)
            }
        }
    }
}
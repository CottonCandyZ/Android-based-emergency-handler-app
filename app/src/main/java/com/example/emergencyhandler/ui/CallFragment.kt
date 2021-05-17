package com.example.emergencyhandler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergencyhandler.data.entity.CallPhone
import com.example.emergencyhandler.databinding.FragmentCallBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class CallFragment : Fragment() {
    private var _binding: FragmentCallBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = arguments?.getParcelableArrayList<CallPhone>("CALL_LIST")
        val showCallInfoAdapter = CallAdapter(list!!.toList())
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = showCallInfoAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }
}
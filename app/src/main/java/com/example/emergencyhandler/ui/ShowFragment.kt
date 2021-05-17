package com.example.emergencyhandler.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergencyhandler.R
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
    private var permissionResult = true
    private val requestCallPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                showMessage(requireContext(), "请给予电话权限以联系呼救人")
                permissionResult = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.statistic) {
            findNavController().navigate(R.id.action_showFragment_to_statisticFragment)
            return super.onOptionsItemSelected(item)
        }
        myViewModel.setFilterStatus(item.title.toString())
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCallPermission()
        setHasOptionsMenu(true)
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

    private fun checkCallPermission(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("需要电话权限以联系呼救人")
                builder.setPositiveButton("确认") { _, _ ->
                }
                builder.create().show()
            }
            else -> {
                requestCallPermissionLauncher.launch(
                    Manifest.permission.CALL_PHONE
                )
            }
        }
        return permissionResult
    }
}
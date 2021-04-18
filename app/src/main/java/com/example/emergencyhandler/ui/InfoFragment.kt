package com.example.emergencyhandler.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergencyhandler.Hints
import com.example.emergencyhandler.R
import com.example.emergencyhandler.databinding.FragmentInfoBinding
import com.example.emergencyhandler.model.InfoViewModel
import com.example.emergencyhandler.showMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private val infoViewModel: InfoViewModel by viewModels()

    @Inject
    lateinit var hints: Hints
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        val check = menu.findItem(R.id.check)
        infoViewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                InfoViewModel.STATUS.CHECK_SUCCESS -> {
                    showMessage(requireContext(), "处理成功")
                }
                InfoViewModel.STATUS.CHECK_ERROR -> {
                    showMessage(requireContext(), infoViewModel.errorMessage)
                    check.isEnabled = true
                }
                null -> {
                }
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.check -> {
                infoViewModel.checkStatus()
                item.isEnabled = false
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        infoViewModel.call.observe(viewLifecycleOwner) {
            with(binding) {
                patient.text = it.patientName
                callerAccount.text = it.callerAccount
                location.text = it.locationName
                when (it.status) {
                    "呼救中" -> {
                        status.setTextColor(Color.RED)
                    }
                    "已取消" -> {
                        status.setTextColor(Color.GRAY)
                    }
                    "已处理" -> {
                        status.setTextColor(Color.BLUE)
                    }
                }
                status.text = it.status
            }
        }

        val showInfoAdapter = ShowInfoAdapter(hints.inputHints)
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = showInfoAdapter
        }
        infoViewModel.showInfo.observe(viewLifecycleOwner) {
            showInfoAdapter.updateDataList(
                it.inputInfo,
                it.emergencyNumber
            )
        }
        infoViewModel.getCall(arguments?.getString("CALL_ID")!!)
        infoViewModel.fetchInfo(arguments?.getString("INFO_ID")!!)

    }
}
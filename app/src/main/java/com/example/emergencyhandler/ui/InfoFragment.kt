package com.example.emergencyhandler.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.example.emergencyhandler.Hints
import com.example.emergencyhandler.R
import com.example.emergencyhandler.databinding.FragmentInfoBinding
import com.example.emergencyhandler.model.InfoViewModel
import com.example.emergencyhandler.showMessage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
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

    private fun convertDateToString(date: Date): String {
        val format = "yyyy.MM.dd 'at' HH:mm:ss"
        return SimpleDateFormat(format, Locale.CHINA).format(date.time)
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


        infoViewModel.call.observe(viewLifecycleOwner) {
            with(binding) {
                patient.text = it.patientName
                callerAccount.text = it.callerAccount
                handler.text = it.handler ?: "尚未处理"
                createTime.text = convertDateToString(it.createTime)
                responseTime.text =
                    if (it.responseTime == null) "尚未处理" else convertDateToString(it.responseTime)
                if (it.locationCoordinate == null) {
                    location.text = "尚未获得"
                    binding.floatingActionButton.isEnabled = false
                } else {
                    binding.floatingActionButton.isEnabled = true
                    location.text = it.locationName
                }


                when (it.status) {
                    "呼救中" -> {
                        status.setTextColor(Color.RED)
                        setHasOptionsMenu(true)
                    }
                    "已取消" -> {
                        setHasOptionsMenu(false)
                        status.setTextColor(Color.GRAY)
                    }
                    "已处理" -> {
                        setHasOptionsMenu(false)
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
        binding.floatingActionButton.setOnClickListener {
            val positionName = infoViewModel.call.value!!.locationName
            val position = infoViewModel.call.value!!.locationCoordinate!!.split(" ")
            val longitude = position[0].toDouble()
            val latitude = position[1].toDouble()
            val end = Poi(positionName, LatLng(longitude, latitude), "END")
            val params =
                AmapNaviParams(null, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE)
            AmapNaviPage.getInstance()
                .showRouteActivity(requireActivity().applicationContext, params, null)
        }





        infoViewModel.getCall(arguments?.getString("CALL_ID")!!)
        infoViewModel.fetchInfo(arguments?.getString("INFO_ID")!!)

    }
}
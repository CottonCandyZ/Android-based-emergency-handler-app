package com.example.emergencyhandler.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.emergencyhandler.databinding.FragmentStatisticBinding
import com.example.emergencyhandler.model.StaticViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private val staticViewModel: StaticViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        with(binding) {
            staticViewModel.state.observe(viewLifecycleOwner) {
                when (it) {
                    StaticViewModel.STATE.TODAY -> {
                        state.text = "今日"
                    }
                    StaticViewModel.STATE.WEEK -> {
                        state.text = "本周"
                    }
                    StaticViewModel.STATE.MONTH -> {
                        state.text = "本月"
                    }
                    StaticViewModel.STATE.YEAR -> {
                        state.text = "本年"
                    }
                    StaticViewModel.STATE.CUSTOM -> {
                        state.text = convertDateToString(
                            staticViewModel.customDate.first
                        ) + "\n至\n" + convertDateToString(staticViewModel.customDate.second)
                    }
                    null -> {

                    }
                }
            }
            staticViewModel.number.observe(viewLifecycleOwner) {
                number.text = it.toString()
            }


            todayButton.setOnClickListener {
                staticViewModel.setState(StaticViewModel.STATE.TODAY)
            }
            currentWeedButton.setOnClickListener {
                staticViewModel.setState(StaticViewModel.STATE.WEEK)
            }
            currentMonthButton.setOnClickListener {
                staticViewModel.setState(StaticViewModel.STATE.MONTH)
            }
            currentYearButton.setOnClickListener {
                staticViewModel.setState(StaticViewModel.STATE.YEAR)
            }

            customDateRange.setOnClickListener {
                setupRangePickerDialog()
            }
        }
    }

    private fun setupRangePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {
            staticViewModel.inputDate = Pair(it.first!!, it.second!!)
            staticViewModel.setState(StaticViewModel.STATE.CUSTOM)
        }
    }

    private fun convertDateToString(time: Long): String {
        val format = "yyyy.MM.dd"
        return SimpleDateFormat(format, Locale.CHINA).format(time)
    }
}
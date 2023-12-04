package com.project.projecte_health.presentation.ui.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.BottomSheetStringListBinding
import com.project.projecte_health.presentation.ui.adapters.BottomSheetStringListAdapter

class BottomSheetSpeciality(private val filterType: List<String>) :
    BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetStringListBinding
    var stringData = MutableLiveData<String>()

    companion object {
        const val TAG = "Bottom Sheet Dialog"
    }


    var mBaseActivity: BaseActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            mBaseActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetStringListBinding.inflate(inflater, container, false)

        binding.heading.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BottomSheetStringListAdapter {
            stringData.postValue(it)
            dismiss()
        }

        binding.recyclerView.adapter = adapter

        adapter.submitList(filterType)
    }
}
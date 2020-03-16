package com.covid19cz.bt_tracing.ui.dash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.covid19cz.bt_tracing.R
import com.covid19cz.bt_tracing.databinding.DashMainBinding
import com.covid19cz.bt_tracing.ext.withViewModel
import com.covid19cz.bt_tracing.ui.base.BaseFragment

interface DashMainView

class DashFragment : BaseFragment(), DashMainView {
    lateinit var binding: DashMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dash_main, container, false)
        binding.lifecycleOwner = this
        binding.view = this

        activity?.withViewModel<DashViewModel>(viewModelFactory) {
            binding.viewModel = this
        }

        return binding.root
    }
}
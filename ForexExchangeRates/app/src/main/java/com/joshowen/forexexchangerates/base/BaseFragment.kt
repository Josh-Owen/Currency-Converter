package com.joshowen.forexexchangerates.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<Binding : ViewBinding> : Fragment() {

    //region Variables & Class Members

    /*
        Made this variable nullable as fragments can outlive their views
        and it's cleared in onDestroy() manually and assigned in onCreate().
     */
    private var _binding: Binding? = null

    val binding get() = _binding!!

    //endregion

    //region Fragment Life-Cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initArgs(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater)
        initViews()
        observeViewModel()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //endregion

    //region Custom Overrides

    abstract fun inflateBinding(layoutInflater: LayoutInflater): Binding

    abstract fun observeViewModel()

    open fun initArgs(arguments: Bundle) {}

    open fun initViews() {}

    //endregion

}

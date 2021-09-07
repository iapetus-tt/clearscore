package com.frogbucket.clearscore.techtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.frogbucket.clearscore.techtest.databinding.ActivityMainBinding
import com.frogbucket.clearscore.techtest.retrofit.CreditRepository
import com.frogbucket.clearscore.techtest.retrofit.CreditService
import com.frogbucket.clearscore.techtest.viewmodel.DonutViewModel
import com.frogbucket.clearscore.techtest.viewmodel.DonutViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: DonutViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = DonutViewModelFactory(CreditRepository(CreditService.getInstance()))
        viewModel = ViewModelProviders.of(this,factory).get(DonutViewModel::class.java)

        observeData()
        viewModel.getCreditRating()
    }

    private fun observeData() {
        viewModel.creditRating.observe(this, Observer {
            binding.donut.creditReportInfo = it
        })
        viewModel.errorMessage.observe(this, {
            if (it == 0) {
                binding.error.visibility = View.GONE
                binding.donut.visibility = View.VISIBLE

                binding.error.setOnClickListener(null)
            } else {
                binding.error.setText(it)
                binding.error.visibility = View.VISIBLE
                binding.donut.visibility = View.GONE

                binding.error.setOnClickListener({ v -> viewModel.getCreditRating() })
            }
        })
    }
}
package com.apps.arbaelbarca.omrscanner.ui.home

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.arbaelbarca.omrscanner.AnswersActivity
import com.apps.arbaelbarca.omrscanner.MainActivity
import com.apps.arbaelbarca.omrscanner.R
import com.apps.arbaelbarca.omrscanner.adapter.ListCheckLjkAdapter
import com.apps.arbaelbarca.omrscanner.data.model.response.ResponseGetLjk
import com.apps.arbaelbarca.omrscanner.data.network.ApiClient
import com.apps.arbaelbarca.omrscanner.databinding.FragmentHomeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var isCamera = true

    lateinit var listCheckLjkAdapter: ListCheckLjkAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initial()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        initial()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initial() {
        initOnClick()
        initAdapter()
        initCallApi()

    }

    private fun initAdapter() {
        listCheckLjkAdapter = ListCheckLjkAdapter()
        binding.rvListCheckLjk.apply {
            adapter = listCheckLjkAdapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun initCallApi() {
        initCallDataLjkCheck()
    }

    private fun initCallDataLjkCheck() {
        binding.pbList.visibility = View.VISIBLE

        val callApi = ApiClient().apiService.callGetListLjk()
        callApi.enqueue(object : Callback<ResponseGetLjk> {
            override fun onResponse(call: Call<ResponseGetLjk>, response: Response<ResponseGetLjk>) {
                binding.pbList.visibility = View.GONE
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        listCheckLjkAdapter.addListLjk(response.body()?.dataItemLjk!!)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGetLjk>, t: Throwable) {
                binding.pbList.visibility = View.GONE
                t.printStackTrace()
            }

        })
    }

    private fun initOnClick() {
        binding.tvAddScanLjk.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.selection_dialog)
            dialog.findViewById<View>(R.id.camera).setOnClickListener { view: View? ->
                isCamera = true
                dialog.cancel()
                openActivity()
            }
            dialog.findViewById<View>(R.id.gallery).setOnClickListener { view: View? ->
                isCamera = false
                dialog.cancel()
                openActivity()
            }
            dialog.show()
        }

        var isExtendVisible = false

        binding.apply {
            btnAddExtendFloat.shrink()
            btnAddExtendFloat.setOnClickListener {
                if (!isExtendVisible) {
                    tvAddTheKeyAnswe.visibility = View.VISIBLE
                    tvAddScanLjk.visibility = View.VISIBLE

                    btnAddExtendFloat.extend()
                    isExtendVisible = true

                    btnAddExtendFloat.text = "Hide"
                } else {
                    tvAddTheKeyAnswe.visibility = View.GONE
                    tvAddScanLjk.visibility = View.GONE

                    btnAddExtendFloat.shrink()
                    isExtendVisible = false
                    btnAddExtendFloat.text = "Show"
                }
            }

            tvAddTheKeyAnswe.setOnClickListener {
                Dexter.withContext(requireContext())
                    .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                            if (report.areAllPermissionsGranted())
                                startActivity(Intent(requireContext(), AnswersActivity::class.java))
                        }

                        override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) { /* ... */
                        }
                    }).check()
            }
        }


    }

    fun openActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("isCamera", isCamera)
        startActivity(intent)
    }
}
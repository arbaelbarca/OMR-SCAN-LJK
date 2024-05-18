package com.apps.arbaelbarca.omrscanner.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.apps.arbaelbarca.omrscanner.R

class DialogWarningScan(
    var isFromScan: String? = "",
    val mContext: Context
) : Dialog(mContext) {

    lateinit var imgExScan: ImageView
    lateinit var btnOk: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_item_dialog_warning)

        imgExScan = findViewById(R.id.imgExScanDialog)
        btnOk = findViewById(R.id.btnOkeDialog)

        if (isFromScan.equals("camera")) {
            imgExScan.setImageResource(R.drawable.camera_ss)
        } else imgExScan.setImageResource(R.drawable.galery_ss)


        btnOk.setOnClickListener {
            dismiss()
        }

    }
}
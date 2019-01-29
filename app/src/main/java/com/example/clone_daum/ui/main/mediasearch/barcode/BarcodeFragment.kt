package com.example.clone_daum.ui.main.mediasearch.barcode

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import com.example.clone_daum.databinding.BarcodeFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.BaseDaggerFragment
import com.example.common.finish
import com.google.zxing.*
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.google.zxing.common.HybridBinarizer

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 22. <p/>
 */

class BarcodeFragment: BaseDaggerFragment<BarcodeFragmentBinding, BarcodeViewModel>()
    , BarcodeCallback {
    companion object {
        private val mLog = LoggerFactory.getLogger(BarcodeFragment::class.java)

        const val REQ_FILE_OPEN = 7912
    }

    @Inject lateinit var viewController: ViewController

    override fun initViewBinding() {
        mBinding.barcodeScanner.run {
            barcodeView.decoderFactory = DefaultDecoderFactory(
                arrayListOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39))

            decodeContinuous(this@BarcodeFragment)

            statusView.visibility = View.GONE
            viewFinder.visibility = View.INVISIBLE
        }
    }

    override fun initViewModelEvents() {
    }

    override fun onResume() {
        super.onResume()

        mBinding.barcodeScanner.resume()
    }

    override fun onPause() {
        mBinding.barcodeScanner.pause()

        super.onPause()
    }

    override fun onCommandEvent(cmd: String, data: Any?) {
        BarcodeViewModel.apply {
            when (cmd) {
                CMD_FILE_OPEN  -> fileOpen()
                CMD_INPUT_CODE -> viewController.barcodeInputFragment()
            }
        }
    }

    private fun fileOpen() {
        startActivityForResult(Intent().apply {
            setType("image/*")
            setAction(Intent.ACTION_GET_CONTENT)
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }, REQ_FILE_OPEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_FILE_OPEN -> {
                if (resultCode != Activity.RESULT_OK) {
                    mLog.error("ERROR: FILE OPEN RESULT $resultCode")
                    return
                }

                barcodeResult(parseBarcode(data))
            }
        }
    }

    private fun parseBarcode(intent: Intent?): BarcodeResult? {
        val uri = intent?.data
        if (uri == null) {
            return null
        }

        return context?.contentResolver?.openInputStream(uri)?.use {
            val bmp = BitmapFactory.decodeStream(it)
            if (bmp == null) {
                return null
            }

            val width = bmp.width
            val height = bmp.height
            val pixels: IntArray = IntArray(width * height)

            bmp.getPixels(pixels, 0, width, 0, 0, width, height)
            bmp.recycle()

            val src    = RGBLuminanceSource(width, height, pixels)
            val bitmap = BinaryBitmap(HybridBinarizer(src))
            val reader = MultiFormatReader()

            BarcodeResult(reader.decode(bitmap), null)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // BarcodeCallback
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun barcodeResult(result: BarcodeResult?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BARCODE RESULT : $result")
        }

        result?.run {
            val v = mBinding.barcodeScanner
            v.postDelayed({ finish() }, 400)

            // 단순하게 http 만 했는데, sms, vcard, pdf 등등이 존재
            if (text.substring(0, 4).toLowerCase().startsWith("http")) {
                v.postDelayed({ viewController.browserFragment(text) }, 800)
            } else if (text.startsWith("MATMSG")) {
                // MATMSG:TO:aucd29@gmail.com;SUB:hello ;BODY:world;;
                val email = text.split(";")
            } else if (text.startsWith("BEGIN:VCARD")) {
//                BEGIN:VCARD
//                VERSION:3.0
//                N:dsadf;ada
//                FN:ada dsadf
//                ORG:dddf
//                TITLE:asdf
//                ADR:;;sdf;asdf;;asdfdf;
//                TEL;WORK;VOICE:
//                TEL;CELL:
//                TEL;FAX:
//                EMAIL;WORK;INTERNET:
//                URL:
//                BDAY:
//                END:VCARD
            } else if (text.startsWith("SMSTO")) {
//                SMSTO:sdfas:dfsadf
            }

            // 히스토리 관리로 디비에 넣어야 함
            // TODO

            Unit
        }
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) { }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BarcodeFragment
    }
}
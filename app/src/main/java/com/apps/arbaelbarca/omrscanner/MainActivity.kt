package com.apps.arbaelbarca.omrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.apps.arbaelbarca.omrscanner.databinding.ActivityMainBinding
import com.apps.arbaelbarca.omrscanner.databinding.ActivityResultsBinding
import com.apps.arbaelbarca.omrscanner.ui.dialog.DialogWarningScan
import com.theartofdev.edmodo.cropper.CropImage
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    var currentPhotoPath: String? = null
    var imageFileName: String? = null
    var source: Mat? = null
    var photoFile: File? = null
    var photoURI: Uri? = null
    var isCamera = false
    var isFromScan = ""

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        System.loadLibrary("opencv_java3")
        isCamera = intent.getBooleanExtra("isCamera", false)
        val path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS + "/OMR/"
        )
        if (!path.exists()) {
            path.mkdirs()
        }
        file = File(path, "config.txt")
        var writer: PrintWriter? = null
        try {
            writer = PrintWriter(file)
            writer.print("")
            writer.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
        } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_STORAGE_PERMISSION_CODE)
        } else {
            dispatchTakePictureIntent()
        }
        binding.res.setOnClickListener(View.OnClickListener { startActivity(Intent(applicationContext, Results::class.java)) })
        binding.retry.setOnClickListener(View.OnClickListener { dispatchTakePictureIntent() })

        binding.tvPetunjukScan.setOnClickListener {
            val dialogShowWarning = DialogWarningScan(isFromScan, this)
            dialogShowWarning.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (null != grantResults && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == MY_STORAGE_PERMISSION_CODE) {
            if (null != grantResults && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "storage permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_CANCELED) finish()
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            isFromScan = "camera"
            try {
                CropImage.activity(photoURI)
                    .start(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            currentPhotoPath = data!!.data.toString()
            isFromScan = "galery"
            try {
                CropImage.activity(data.data)
                    .start(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                photoURI = result.uri
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, photoURI)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.img.setImageBitmap(bitmap)
                binding.rlRetry.setVisibility(View.VISIBLE)
                binding.res.visibility = View.VISIBLE
                //photoURI = CropImage.getCaptureImageOutputUri(this);
                //img.setImageUriAsync(photoURI);
                Log.d("IMAGE", currentPhotoPath.toString())
                //source = Imgcodecs.imread("/storage/self/primary/Pictures/sheet_1.jpg");
                source = Imgcodecs.imread(photoURI?.path)
                Util.sout("...started")
                val scanner = Scanner(source, 20)
                scanner.setLogging(false)
                try {
                    val writer = PrintWriter(file)
                    writer.print("")
                    writer.close()
                    scanner.scan()
                } catch (e: Exception) {
                    binding.res.setAlpha(0.2f)
                    binding.res.isClickable = false
                    e.printStackTrace()
                    val dialogShowWarning = DialogWarningScan(isFromScan, this)
                    dialogShowWarning.show()
                }
                Util.sout("...finished")
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageFileName = "OMR_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent() {
        binding.res.setAlpha(1f)
        binding.res.isClickable = true
        val takePictureIntent: Intent
        if (isCamera) {
            takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                photoFile = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        photoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
        } else {
            takePictureIntent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(takePictureIntent, REQUEST_GALLERY)
        }
    }

    override fun onBackPressed() {
        try {
            val writer = PrintWriter(file)
            writer.print("")
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.onBackPressed()
    }

    companion object {
        private const val MY_CAMERA_PERMISSION_CODE = 100
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_GALLERY = 2
        private const val MY_STORAGE_PERMISSION_CODE = 101

        @JvmField
        var file: File? = null
    }
}

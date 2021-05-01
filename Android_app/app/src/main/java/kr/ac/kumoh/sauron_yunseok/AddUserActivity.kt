package kr.ac.kumoh.sauron_yunseok

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import kotlinx.android.synthetic.main.activity_add_user.*
import java.io.File
import java.io.IOException

class AddUserActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        //track Choosing Image Intent
        private const val CHOOSING_IMAGE_REQUEST = 1234
    }

    private var fileUri: Uri? = null
    private var s3Client: AmazonS3Client? = null
    private var tvFileName: TextView? = null
    private var imageView: ImageView? = null
    private var edtFileName: EditText? = null
    private val KEY = "<YOUR_KEY>"
    private val SECRET = "<YOUR_SECRET_KEY>"
    private var credentials: BasicAWSCredentials? = null
    private var bitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
            }
        }

        imageView = img_file
        edtFileName = edt_file_name
        tvFileName = tv_file_name
        btn_choose_file.setOnClickListener { onClick(btn_choose_file) }
        btn_upload.setOnClickListener { onClick(btn_upload) }
        btn_download.setOnClickListener { onClick(btn_download) }
        credentials = BasicAWSCredentials(KEY, SECRET)
        s3Client = AmazonS3Client(credentials)
        AWSMobileClient.getInstance().initialize(this).execute()
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == btn_choose_file.id) {
            showChoosingFile()
        } else if (i == btn_upload.id) {
            uploadFile()
        } else if (i == btn_download.id) {
            downloadFile()
        }
    }

    private fun showChoosingFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST)
    }

    private fun uploadFile() {
        if (fileUri != null) {
            val fileName = "/sdcard/DCIM/Camera/test.jpg"
            if (!validateInputFileName(fileName)) {
                return
            }
            val file = File("/sdcard/DCIM/Camera/test.jpg"
            )
//            createFile(applicationContext, fileUri!!, file)
            val transferUtility = TransferUtility.builder()
                .context(applicationContext)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .s3Client(s3Client)
                .build()
            val uploadObserver = transferUtility.upload("sauronfaces","test.jpg",file)

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (TransferState.COMPLETED === state) {
                        Toast.makeText(applicationContext, "Upload Completed!", Toast.LENGTH_SHORT)
                            .show()
                        file.delete()
                    } else if (TransferState.FAILED === state) {
                        file.delete()
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()
                    tvFileName!!.text =
                        "ID:$id|bytesCurrent: $bytesCurrent|bytesTotal: $bytesTotal|$percentDone%"
                }

                override fun onError(id: Int, ex: Exception) {
                    ex.printStackTrace()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (bitmap != null) {
            bitmap!!.recycle()
        }
        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
//            Log.i("yunseok",fileUri.toString())
////            val img =  BitmapFactory.decodeFile(fileUri.toString())
//            val path = getRealPathFromURI(fileUri)
//            Log.i("yunseok",path.toString())
            val editable = SpannableStringBuilder( "/storage/self/primary/Pictures/KakaoTalk/1527509903075.jpg");
            edt_file_name.text = editable

            try {

                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun downloadFile() {
        if (fileUri != null) {
            val fileName = edtFileName!!.text.toString()
            if (!validateInputFileName(fileName)) {
                return
            }
            try {
                val localFile: File = File.createTempFile("images", getFileExtension(fileUri!!))
                val transferUtility = TransferUtility.builder()
                    .context(applicationContext)
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .s3Client(s3Client)
                    .build()
                val downloadObserver = transferUtility.download(
                    "sauronfaces/" + fileName + "." + getFileExtension(
                        fileUri!!
                    ), localFile
                )
                downloadObserver.setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState) {
                        if (TransferState.COMPLETED === state) {
                            Toast.makeText(
                                applicationContext,
                                "Download Completed!",
                                Toast.LENGTH_SHORT
                            ).show()
                            tvFileName!!.text = fileName + "." + getFileExtension(fileUri!!)
                            val bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath())
                            imageView!!.setImageBitmap(bmp)
                        }
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                        val percentDone = percentDonef.toInt()
                        tvFileName!!.text =
                            "ID:$id|bytesCurrent: $bytesCurrent|bytesTotal: $bytesTotal|$percentDone%"
                    }

                    override fun onError(id: Int, ex: Exception) {
                        ex.printStackTrace()
                    }
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Upload file before downloading", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateInputFileName(fileName: String): Boolean {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "Enter file name!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}

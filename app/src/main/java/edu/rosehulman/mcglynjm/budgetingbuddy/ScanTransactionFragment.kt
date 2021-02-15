package edu.rosehulman.mcglynjm.budgetingbuddy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.common.collect.Iterables.toArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.scan_transaction.view.cancel_button
import kotlinx.android.synthetic.main.scan_transaction.view.*
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

private const val RC_TAKE_PICTURE = 1

class ScanTransactionFragment(var uid: String)  : Fragment() {
    lateinit var theContext: Context
    private var receiptPath = ""
    private var categoryNames = ArrayList<String>()

    private val usersRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)

    private val transactionsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.TRANSACTIONS_COLLECTION)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.scan_transaction, container, false)

        view.cancel_button.setOnClickListener { fragmentViewer.onButtonHit(context!!.getString(R.string.home)) }

        view.scan_transaction_text_view.setOnClickListener {
            showScanDialog()
        }

        return view
    }

    private fun showScanDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage("Would you like to take a new picture?")
        builder.setPositiveButton("Take Picture") { _, _ ->
            launchCameraIntent()
        }
        builder.create().show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_TAKE_PICTURE -> {
                    Log.d(Constants.TAG, "sending picture to adapter")
                    sendCameraPhotoToAdapter()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentViewer) {
            theContext = context
            getCategoryNames()
        } else {
            throw RuntimeException(context.toString() + " must implement FragmentViewer")
        }
    }


    // Everything camera- and storage-related is from
    // https://developer.android.com/training/camera/photobasics
    private fun launchCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            Log.d(Constants.TAG, "Launching Camera")
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                Log.d(Constants.TAG, "Creating Photo File")
                val photoFile: File? = try {
                    Log.d(Constants.TAG, "Created Photo File")
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                        Log.d(Constants.TAG,"Error occurred while creating the File $ex")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    // authority declared in manifest
                    Log.d(Constants.TAG, "Have File")
                    val photoURI: Uri = FileProvider.getUriForFile(
                        theContext,
                        "edu.rosehulman.mcglynjm.budgetingbuddy",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, RC_TAKE_PICTURE)
                    Log.d(Constants.TAG, "End of photoFile.also")
                }
            }
        }
    }

    // Could save a smaller version to Storage to save time on the network.
    // But if too small, recognition accuracy can suffer.
    inner class ImageRescaleTask(val localPath: String) : AsyncTask<Void, Void, Bitmap>() {

        override fun doInBackground(vararg p0: Void?): Bitmap? {
            // Reduces length and width by a factor (currently 2).
            Log.d(Constants.TAG, "Rescaling (doInBackground())")
            val ratio = 2
            return BitmapUtils.rotateAndScaleByRatio(theContext, localPath, ratio)
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            Log.d(Constants.TAG, "Rescaling (onPostExecute())")
            scanReceipt(bitmap)
        }
    }

    private fun scanReceipt(bitmap: Bitmap?) {
        Log.d(Constants.TAG, "in scanReceipt")
        val image = FirebaseVisionImage.fromBitmap(bitmap!!)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener { result ->
                Log.d(Constants.TAG, "Text recognized ${result.text}")
                var total = 0.toDouble()
                var items = ArrayList<String>()
                total += getTotal(result.text)
                //TODO add a dialog here for them to type in the category (maybe items too)
                var category = ""
                val builder = AlertDialog.Builder(theContext)
                builder.setTitle(getString(R.string.choose_type))
                builder.setItems(toArray(categoryNames, String::class.java))
                { _, which ->
                    category = categoryNames[which]
                    Log.d(Constants.TAG, "item $which selected: ${category}")

                }
                builder.create()
                    .show()
                

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                val formatted = current.format(formatter)
                Log.d(Constants.TAG, "adding transaction for $total to category $category")
                val transaction = ManualTransaction(total, category, "", Renews.NEVER, formatted)
                usersRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
                    var monthlyRemaining =
                        (snapshot.getDouble("monthlyRemaining") ?: 0.00) as Double
                    var totalRemaining = (snapshot.getDouble("remainingFunds") ?: 0.00) as Double
                    Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
                    monthlyRemaining -= transaction.amount
                    totalRemaining -= transaction.amount
                    Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
                    usersRef.update("monthlyRemaining", monthlyRemaining)
                    usersRef.update("remainingFunds", totalRemaining)
                }
                transactionsRef.add(transaction)
            }
            .addOnFailureListener { e ->
                Log.d(Constants.TAG, "Failure recognizing text ")
            }
    }

    //https://medium.com/@teresa.wu/googles-ml-kit-text-recognition-with-sample-app-of-receipts-reading-7fe6dc68ada3
    fun String.findFloat(): ArrayList<Float> {
        Log.d(Constants.TAG, "String.findFloat()")
        //get digits from result
        if (this == null || this.isEmpty()) return ArrayList<Float>()
        val originalResult = ArrayList<Float>()
        val matchedResults = Regex(pattern = "[+-]?([0-9]*[.])?[0-9]+").findAll(this)
        if (matchedResults != null)
            for (txt in matchedResults) {
                if (txt.value.isFloatAndWhole()) originalResult.add(txt.value.toFloat())
            }
        return originalResult
    }

    private fun String.isFloatAndWhole() = this.matches("\\d*\\.\\d*".toRegex())

    fun getTotal(text: String): Double {
        Log.d(Constants.TAG, "Get total")
        val originalResult = text.findFloat()
        if (originalResult == null || originalResult.isEmpty()) return 0.toDouble()
        else {
            val totalF = Collections.max(originalResult)
            return totalF.toDouble()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        Log.d(Constants.TAG, "in createImageFile()")
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            receiptPath = absolutePath
        }
        Log.d(Constants.TAG, "Created temp file")
    }

    private fun sendCameraPhotoToAdapter() {
        Log.d(Constants.TAG, "sendCameraPhotoToAdapter")
        addPhotoToGallery()
        Log.d(Constants.TAG, "Sending to adapter this photo: $receiptPath")
        //adapter.add(receiptPath)
        ImageRescaleTask(receiptPath).execute()
    }

    // Works Not working on phone
    private fun addPhotoToGallery() {
        Log.d(Constants.TAG, "addPhotoToGallery")
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(receiptPath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity!!.sendBroadcast(mediaScanIntent)
        }
    }

    fun getCategoryNames() {
        categoryNames.clear()
        usersRef.collection(Constants.CATEGORIES_COLLECTION)
            .get()
            .addOnSuccessListener { categories ->
                for (category in categories.documents!!) {
                    categoryNames.add((category.data?.get("name") as String))
                }
            }
    }
}
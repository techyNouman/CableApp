package com.cableapp.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cableapp.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnSend.setOnClickListener {
            if (checkAllFields()) {
                val date = Calendar.getInstance().time
                val dateAndTime =
                    SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()).format(date)

                val name = binding.edtName.text.toString().trim()
                val doorNum = binding.edtDoorNum.text.toString().trim()
                val cross = binding.edtCross.text.toString().trim()
                val area = binding.edtArea.text.toString().trim()
                val pincode = binding.edtPincode.text.toString().trim()
                val amount = binding.edtAmount.text.toString().trim()
                val text =
                    "Name: $name \nDoor Num: $doorNum \nCross: $cross \nArea: $area \nPincode: $pincode \nAmount: $amount \nDate And Time: $dateAndTime"
                saveDataToFile(
                    requireActivity(),
                    text,
                    binding.edtName.text.toString().trim(),
                    dateAndTime
                )
            }
        }
        binding.btnClear.setOnClickListener {
            binding.edtName.setText("")
            binding.edtDoorNum.setText("")
            binding.edtCross.setText("")
            binding.edtArea.setText("")
            binding.edtPincode.setText("")
            binding.edtAmount.setText("")
            binding.edtName.requestFocus()
        }
        return root
    }

    private fun checkAllFields(): Boolean {
        if (binding.edtName.text.isEmpty()) {
            binding.edtName.error = "This field is required"
            return false
        }
        if (binding.edtDoorNum.text.isEmpty()) {
            binding.edtDoorNum.error = "This field is required"
            return false
        }
        if (binding.edtCross.text.isEmpty()) {
            binding.edtCross.error = "This field is required"
            return false
        }
        if (binding.edtArea.text.isEmpty()) {
            binding.edtArea.error = "This field is required"
            return false
        }
        if (binding.edtPincode.text.isEmpty()) {
            binding.edtPincode.error = "This field is required"
            return false
        }
        if (binding.edtAmount.text!!.isEmpty()) {
            binding.edtAmount.error = "This field is required"
            return false
        }

        return true
    }

    private fun saveDataToFile(
        context: Context,
        text: String,
        name: String,
        dateAndTime: String
    ) {
        val fileName = "$name - $dateAndTime"
        val externalFilesDirectory: File =
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
        val textFile = File(externalFilesDirectory, fileName)
        try {
            val writer = FileWriter(textFile)
            writer.append(text)
            writer.flush()
            writer.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e("FileNotFoundException", "" + e)
        } catch (e: java.lang.Exception) {
            e.localizedMessage
            Log.e("lang.Exceptio", "" + e)
        }
        // export data
        sendEmail(context, textFile, fileName)
    }

    private fun sendEmail(context: Context, file: File, fileName: String) {

        val fileUri: Uri =
            FileProvider.getUriForFile(context, context.packageName.toString() + ".provider", file)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // set the type to 'email'
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("vasucableapp@gmail.com")
//    emailIntent.data = Uri.parse("mailto:") // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, fileName)
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, Please find attached file. Thank you.")
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
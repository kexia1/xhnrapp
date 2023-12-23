package top.kk1.xhnr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 * 1145141919810
 *
 */
class MainActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkStoragePermission()
        val jumpButton = findViewById<Button>(R.id.jump_button)
        val generateButton = findViewById<Button>(R.id.generate_button)
        generateButton.setOnClickListener {
            performFileOperations()
        }
        jumpButton.setOnClickListener {
            val directory = getExternalFilesDir(null)
            if (directory != null && directory.exists()) {
                val directorys = getExternalFilesDir(null)
                if (directorys != null) {
                    if (!directorys.exists()) {
                        directorys.mkdirs()
                    }
                }


                val file = File(directorys, ".")
                val oss = StringBuilder().toString()
                val result = oss
                try {
                    FileOutputStream(file).use { stream ->
                        stream.write(result.toByteArray())
                        stream.flush()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()

                }
                val uri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(directory.absolutePath))
                intent.setDataAndType(uri, "text/plain")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            } else {
                Toast.makeText(this, "目录不存在", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            performFileOperations()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performFileOperations()
            } else {
                Toast.makeText(this, "需要存储权限才能进行文件操作", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performFileOperations() {
        val numEditText = findViewById<EditText>(R.id.num_edit_text)
        val contentEditText = findViewById<EditText>(R.id.content_edit_text)
        val filenameEditText = findViewById<EditText>(R.id.filename_edit_text)
        val showLogNumberCheckBox = findViewById<CheckBox>(R.id.show_log_number_checkbox)
        val openFileCheckBox = findViewById<CheckBox>(R.id.open_file_checkbox)
        val openFile = openFileCheckBox.isChecked
        val n = numEditText.text.toString().toIntOrNull() ?: return
        val content = contentEditText.text.toString()
        val filename = filenameEditText.text.toString()
        val showLogNumber = showLogNumberCheckBox.isChecked

        val oss = StringBuilder()
        for (i in 1..n) {
            val si = if (showLogNumber) "$i." else ""
            oss.append(si).append(content).append("\n")
        }
        val result = oss.toString()

        val directory = getExternalFilesDir(null)
        if (directory != null) {
            if (!directory.exists()) {
                directory.mkdirs()
            }
        }


        val file = File(directory, filename)
        if (file.exists()) {
            Toast.makeText(
                this,
                "文件${filename}已存在",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val success: Boolean = try {
            FileOutputStream(file).use { stream ->
                stream.write(result.toByteArray())
                stream.flush()
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

        if (success) {
            Toast.makeText(
                this,
                "文件已生成在 ${file.absolutePath}",
                Toast.LENGTH_LONG
            ).show()

            if (openFile) {
                // 调用文件管理器打开刚生成的文件

                val uri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "text/plain")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "文件写入失败", Toast.LENGTH_SHORT).show()
        }
    }
}

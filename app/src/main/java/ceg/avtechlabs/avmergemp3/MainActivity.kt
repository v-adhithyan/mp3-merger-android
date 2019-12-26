package ceg.avtechlabs.avmergemp3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import android.os.Environment.getExternalStorageDirectory
import android.provider.OpenableColumns
import android.util.Log
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.annotation.TargetApi
import android.content.Context
import android.widget.Toast
import java.io.*


class MainActivity : AppCompatActivity() {
    val SELECT_MP3 = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun selectMp3(v: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Choose mp3's"), SELECT_MP3)
    }

    @TargetApi(19)
    private fun generatePath(uri: Uri, context: Context): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val wholeID = DocumentsContract.getDocumentId(uri)


            //val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val id = wholeID

            val column = arrayOf(MediaStore.Audio.Media.DATA)
            val sel = MediaStore.Video.Media._ID + "=?"

            val cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )


            val columnIndex = cursor.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }

            cursor.close()
        }
        return filePath
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode) {
            SELECT_MP3 -> {
                if(resultCode == Activity.RESULT_OK) {
                    doAsync {
                        val mp3Count = data?.clipData?.itemCount
                        val mergedFile = Environment.getExternalStorageDirectory().absolutePath + "/AVMp3Merger/mymerge1.mp3"
                        val outputStream = FileOutputStream(File(mergedFile), false)
                        val buffer = ByteArray(1048576)
                        for(i in 0 until mp3Count!!) {
                            val uri = data.clipData?.getItemAt(i)?.uri!!

                            val inputStream = contentResolver.openInputStream(uri)
                            while(true){
                                val count = inputStream?.read(buffer)!!
                                if(count == -1)
                                    break
                                outputStream.write(buffer, 0, count)
                                outputStream.flush()
                            }
                            inputStream.close()
                        }
                        outputStream.close()
                        toast("Done.")
                    }
                }
            }
        }
    }
}

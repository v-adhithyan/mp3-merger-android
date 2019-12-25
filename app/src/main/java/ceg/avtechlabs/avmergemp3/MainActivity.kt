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
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.*
import kotlin.collections.ArrayList
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileOutputStream


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode) {
            SELECT_MP3 -> {
                if(resultCode == Activity.RESULT_OK) {
                    doAsync {
                        val mp3Count = data?.clipData?.itemCount
                        val mp3FilesUri = ArrayList<Uri>()
                        for(i in 0 until mp3Count!!) {
                            val uri = data.clipData?.getItemAt(i)!!.uri ?: continue
                            if(uri.scheme.equals("content")) {
                                val cursor = contentResolver.query(
                                    uri,
                                    arrayOf(android.provider.MediaStore.Images.ImageColumns.DATA),
                                    null,
                                    null,
                                    null
                                )
                                cursor.moveToFirst()
                                Log.d("ADHI", cursor.getString(0))
                                cursor.close()
                            } else {
                                Log.d("ADHI", uri.path)
                            }
                            Log.d("ADHI", data.clipData?.getItemAt(i)!!.uri.toString())
                            mp3FilesUri.add(data.clipData?.getItemAt(i)!!.uri)
                        }

                        val fname = Environment.getExternalStorageDirectory().absolutePath + "/AVMp3Merger/mymerge.mp3"

                    }
                }
            }
        }
    }
}

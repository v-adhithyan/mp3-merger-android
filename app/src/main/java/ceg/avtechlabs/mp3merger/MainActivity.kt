package ceg.avtechlabs.mp3merger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
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
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    val SELECT_MP3 = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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
                    val mp3Count = data?.clipData?.itemCount
                    val mp3FilesUri = ArrayList<Uri>()
                    for(i in 0..mp3Count!!-1) {
                        mp3FilesUri.add(data.clipData?.getItemAt(i)!!.uri)
                    }

                    val fileStreams = LinkedList<InputStream>()
                    for(uri in mp3FilesUri) {
                        if(uri.scheme.equals("content")) {
                            val inputStream = contentResolver.openInputStream(uri)
                            fileStreams.add(inputStream)
                        } else {

                        }
                    }

                    val sequenceStream = SequenceInputStream(Collections.enumeration(fileStreams))
                    //val file = File(Environment.getExternalStorageDirectory().absolutePath + "/mymerge.mp3")
                    val fos = FileOutputStream(Environment.getExternalStorageDirectory().absolutePath + "/mymerge.mp3")
                    var temp: Int
                    while(true) {
                        temp = sequenceStream.read()
                        if(temp == -1)
                            break
                        fos.write(temp)
                    }
                    fos.close()
                }
            }
        }
    }
}

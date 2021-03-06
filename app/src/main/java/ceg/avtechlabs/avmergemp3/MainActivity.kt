package ceg.avtechlabs.avmergemp3

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setRecentFiles()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun openChooseFilesActivity(v: View){
        startActivity(intentFor<ChooseFileActivity>())
    }

    private fun setRecentFiles() {
        val rootFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/AVMp3Merger")
        val files = rootFolder.listFiles()
        val fileNames = ArrayList<String>()
        val filePaths = ArrayList<String>()
        for(file in files) {
            fileNames.add(file.name)
            filePaths.add(file.path)
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames)
        recent_files_list.adapter = adapter
        recent_files_list.setOnItemClickListener { parent, view, position, id ->
            val file = File(filePaths[position])
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(Uri.fromFile(file), "audio/*")
            startActivity(intent)
        }

    }
}

package ceg.avtechlabs.avmergemp3

import android.app.ProgressDialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_choose_file.*
import org.jetbrains.anko.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.merge_file_name_input.*


class ChooseFileActivity : AppCompatActivity() {
    var filesToMerge = ArrayList<File>()
    val mp3Names = ArrayList<String?>()
    val mp3Paths = ArrayList<String?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_file)

        val dialog = indeterminateProgressDialog("Fetching mp3 files", "Please wait")
        dialog.show()

        doAsync {
            val songList = getPlayList(Environment.getExternalStorageDirectory().absolutePath)
            if (songList != null) {
                for (i in 0 until songList.size) {
                    val fileName = songList[i]["file_name"]
                    val filePath = songList[i]["file_path"]
                    mp3Names.add(fileName)
                    mp3Paths.add(filePath)
                }
            }
            dialog.dismiss()

            runOnUiThread {
                val adapter = ArrayAdapter<String>(this@ChooseFileActivity, android.R.layout.simple_list_item_1, mp3Names)
                files_list_view.adapter = adapter
                //files_list_view.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                files_list_view.setOnItemClickListener { adapterView, view, i, l ->
                    toast(mp3Paths[i]!!)
                    filesToMerge.add(File(mp3Paths[i]!!))
                }
            }
        }

    }

    fun getPlayList(rootPath: String): ArrayList<HashMap<String, String>>? {

        val fileList = ArrayList<HashMap<String, String>>()

        try {
            val rootFolder = File(rootPath)
            val files =
                rootFolder.listFiles() //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (file in files) {
                if (file.isDirectory) {
                    if (getPlayList(file.absolutePath) != null) {
                        fileList.addAll(getPlayList(file.absolutePath)!!.asIterable())
                    } else {
                        break
                    }
                } else if (file.name.endsWith(".mp3")) {
                    val song = HashMap<String, String>()
                    song["file_path"] = file.absolutePath
                    song["file_name"] = file.name
                    fileList.add(song)
                }
            }
            return fileList
        } catch (e: Exception) {
            return null
        }
    }

    fun merge(fileName: String) {
        val progressDialog = progressDialog("MP3 Merge", "Please wait")
        progressDialog.isIndeterminate = false
        progressDialog.max = 100
        progressDialog.progress = 0
        progressDialog.show()

        doAsync {
            val uuid = UUID.randomUUID().toString()
            var fn = fileName
            if(fileName.isEmpty()) {
                fn = "mymerge1-$uuid.mp3"
            } else {
                fn = fn.replace(".mp3", "")
                fn = "$fn.mp3"
            }
            val mergedFile = Environment.getExternalStorageDirectory().absolutePath + "/AVMp3Merger/$fn"
            val f = File(mergedFile)
            if(!f.exists()) {
                f.createNewFile()
            }
            val outputStream = FileOutputStream(f, true)
            val buffer = ByteArray(1048576)
            var totalSize = 0L
            for(file in filesToMerge) {
                totalSize += file.length()
            }
            var currentSize = 0
            for(file in filesToMerge) {
                val inputStream = FileInputStream(file)
                while(true){
                    val count = inputStream.read(buffer)
                    if(count == -1)
                        break
                    else {
                        currentSize += count
                        progressDialog.progress = ((currentSize.toFloat() / totalSize.toFloat()) * 100).toInt()
                    }
                    outputStream.write(buffer, 0, count)
                    outputStream.flush()
                }
                inputStream.close()
            }
            outputStream.flush()
            outputStream.close()

            filesToMerge = ArrayList<File>()
            progressDialog.dismiss()
            runOnUiThread { toast("Done $mergedFile") }
        }
    }

    fun showFileDialog(v: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.merge_file_name_input, null)
        val mergeFileText = dialogView.find<EditText>(R.id.merge_file_name)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setMessage("Enter file name for new file")
        dialogBuilder.setPositiveButton("Done", DialogInterface.OnClickListener { dialog, whichButton ->
            val fileName = mergeFileText.text.toString().trim()
            merge(fileName)
        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, whichButton ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }

}

package ceg.avtechlabs.avmergemp3


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomAdapter(private var context: Context, private var logos: Array<Int>, private var names: Array<String>) : BaseAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return logos.size
    }

    override fun getItem(i: Int): Any? {
        return names[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, v: View, viewGroup: ViewGroup): View {
        val view = inflater.inflate(R.layout.main_menu_layout, viewGroup) // inflate the layout
        val icon = view.findViewById<View>(R.id.icon) as ImageView // get the reference of ImageView
        val name = view.findViewById<View>(R.id.icon_text) as TextView
        icon.setImageResource(logos[i]) // set logo images
        name.text = names[i]
        return view
    }
}
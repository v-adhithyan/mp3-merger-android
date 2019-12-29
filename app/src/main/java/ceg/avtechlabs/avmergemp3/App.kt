package ceg.avtechlabs.avmergemp3

import android.app.Application
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import android.os.StrictMode



class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val font = CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/Myriad Pro Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build()
        CalligraphyConfig.initDefault(font)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}
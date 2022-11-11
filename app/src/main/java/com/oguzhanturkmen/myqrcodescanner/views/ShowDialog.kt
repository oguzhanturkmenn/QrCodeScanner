package com.oguzhanturkmen.myqrcodescanner.views

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.oguzhanturkmen.myqrcodescanner.R
import kotlinx.android.synthetic.main.show_dialog.*
import org.jsoup.Jsoup
import java.util.concurrent.Executors

class ShowDialog :DialogFragment(){
    override fun onCreateView
            (inflater: LayoutInflater,
             container: ViewGroup?,
             savedInstanceState: Bundle?): View? = inflater
            .inflate(R.layout.show_dialog,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    fun updataUrl(url:String)
    {
        fetchUrlMetaData(url){
            title,desc ->
            text_view_title.text = title
            text_view_desc.text = desc
            text_view_link.text = url
            text_view_visit_link.setOnClickListener {
                Intent(Intent.ACTION_VIEW).also {
                    it.data = Uri.parse(url)
                    startActivity(it)
                }
            }

        }
    }

    private fun fetchUrlMetaData(url: String,
                                 callBack:(title:String,desc:String)-> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val doc = Jsoup.connect(url).get()
            val desc = doc.select(
                    "meta[name=description]"
            )[0].attr("content")
            handler.post {
                callBack(doc.title(),desc)
            }
        }

    }

}
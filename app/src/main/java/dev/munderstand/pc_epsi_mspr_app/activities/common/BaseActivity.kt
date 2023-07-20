package dev.munderstand.pc_epsi_mspr_app.activities.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import dev.munderstand.pc_epsi_mspr_app.R

open class BaseActivity: AppCompatActivity() {


    fun setHeaderTxt(txt: String){
        val textView=findViewById<TextView>(R.id.tv_title)
        textView.text = txt
    }

    fun showBack(){
        val imageViewBack=findViewById<ImageView>(R.id.iv_Left_Arrow)
        imageViewBack.visibility=View.VISIBLE
        imageViewBack.setOnClickListener(View.OnClickListener {
            finish()
        })
    }



}
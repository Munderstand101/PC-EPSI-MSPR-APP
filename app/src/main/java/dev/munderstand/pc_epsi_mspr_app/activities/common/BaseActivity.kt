package dev.munderstand.pc_epsi_mspr_app.activities.common

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.account.ProfilViewActivity
import dev.munderstand.pc_epsi_mspr_app.activities.account.SignUpActivity

open class BaseActivity: AppCompatActivity() {

    fun setHeaderTxt(txt:String) {
        val textViewTitle = findViewById<TextView>(R.id.tv_title)
        textViewTitle.text = txt
    }

    fun showBack(){
        val imageViewBack=findViewById<ImageView>(R.id.iv_Left_Arrow)
        imageViewBack.visibility=View.VISIBLE
        imageViewBack.setOnClickListener(View.OnClickListener {
         //   finish()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        })
    }

//    fun showRight() {
//        val imageViewBack = findViewById<ImageView>(R.id.iv_Right_Arrow)
//        imageViewBack.visibility = View.VISIBLE
//        imageViewBack.setOnClickListener {
//            val intent = Intent(this, ProfilViewActivity::class.java)
//            startActivity(intent)
//        }
//    }



}
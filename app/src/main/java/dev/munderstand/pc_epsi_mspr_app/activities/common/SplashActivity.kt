package dev.munderstand.pc_epsi_mspr_app.activities.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.MainActivity
import dev.munderstand.pc_epsi_mspr_app.activities.account.SignInActivity
//import io.jsonwebtoken.Claims
//import io.jsonwebtoken.JwtException
//import io.jsonwebtoken.Jwts

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Get the SharedPreferences instance
        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)

        // Retrieve the token from SharedPreferences
        val token = sharedPreferences.getString("token", null)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val newIntent = if (token != null ) { //&& isTokenValid(token)
                Intent(application, MainActivity::class.java)
            } else {
                Intent(application, SignInActivity::class.java)
            }

            startActivity(newIntent)
            finish()
        }, 2000)

        // Change the logo for dark mode
        val logo = findViewById<ImageView>(R.id.splash_logo)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            logo.setImageResource(R.drawable.logo_base)
        }
    }

/*
    private fun isTokenValid(token: String): Boolean {
        try {
            // Parse the JWT token
            val claims: Claims = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .body

            // Get the expiration time from the claims
            val expirationTime = claims.expiration.time

            // Get the current time
            val currentTime = System.currentTimeMillis()

            // Compare the current time with the expiration time
            return currentTime < expirationTime
        } catch (e: JwtException) {
            // An error occurred while parsing the token or the token is invalid
            e.printStackTrace()
        }

        return false
    }*/

}

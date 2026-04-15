package com.first.fintech.util

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.first.fintech.R
import com.first.fintech.ui.auth.login.view.LoginActivity
import com.first.fintech.ui.services.view.ServicesActivity
import com.first.fintech.ui.subscriptions.view.SubscriptionsActivity

object DrawerHelper {

    fun setup(
        activity: AppCompatActivity,
        drawerLayout: DrawerLayout,
        btnMenu: ImageView,
        tvNavUserName: TextView,
        tvNavUserEmail: TextView,
        navServices: LinearLayout,
        navSubscriptions: LinearLayout,
        navLogout: LinearLayout
    ) {
        // populate user info
        tvNavUserName.text  = SessionManager.getName(activity)
        tvNavUserEmail.text = SessionManager.getEmail(activity)

        // open drawer on hamburger click
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // close drawer on backdrop tap
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
        })

        navServices.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (activity !is ServicesActivity) {
                activity.startActivity(Intent(activity, ServicesActivity::class.java))
                activity.finish()
            }
        }

        navSubscriptions.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (activity !is SubscriptionsActivity) {
                activity.startActivity(Intent(activity, SubscriptionsActivity::class.java))
            }
        }

        navLogout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            val dialog = AlertDialog.Builder(activity)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    SessionManager.clearSession(activity)
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(activity.getColor(R.color.orange))

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(activity.getColor(R.color.orange))
        }
    }
}
package com.tutorial.weatheria

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.TypeConverter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.tutorial.weatheria.networkmodels.Day
import com.tutorial.weatheria.networkmodels.Hour
import dagger.hilt.android.qualifiers.ApplicationContext

class cb(val context: Context,val application: AppCompatActivity){
     fun checkPerms(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val is1 = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val is2 = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            return is1 && is2
        } else {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
     val requestLauncher =
        application.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    requestLocationPermission()
                }
                it.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                    requestLocationPermission()
                }
                it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    requestLocationPermission()
                }
                else -> {

                    MaterialAlertDialogBuilder(context).apply {
                        setMessage("You need to allow permission for app to work ")
                        setTitle("ACCEPT PERMISSION REQUEST")
                        setPositiveButton("OK") { dialogInterface, Int ->
                            dialogInterface.dismiss() // or you can request permission again
                        }
                        create()
                        show()
                    }
                }
            }
        }
    @RequiresApi(Build.VERSION_CODES.N)
     fun requestLocationPermission() {

        if (checkPerms()) {
            Toast.makeText(context,"Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                requestLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @TypeConverter
    fun toHourString(list:List<Hour>):String{
        return Gson().toJson(list)
    }
    @TypeConverter
    fun toHourList(value:String):List<Hour>{
     return Gson().fromJson(value,Array<Hour>::class.java).toList()
    }

    fun fromStringToDay(string: String):Day{
       return Gson().fromJson(string,Day::class.java)
    }


}
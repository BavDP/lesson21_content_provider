package com.example.lesson21_content_provider.utils

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun reqRunTimePermission(activity: Activity, launcher: ActivityResultLauncher<Array<String>>?, permission: Array<String>) {
    var should = false
    for (element in permission) {
        (should || activity.shouldShowRequestPermissionRationale(element)).also { should = it }
        if (should) break
    }
    if (should) {
        MaterialAlertDialogBuilder(activity, 0)
            .setMessage("Access to contacts need for load to this app")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok") {_, _ ->
                launcher?.launch(permission)
            }
            .show()
    } else {
        launcher?.launch(permission)
    }
}
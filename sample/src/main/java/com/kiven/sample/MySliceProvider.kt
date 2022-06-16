package com.kiven.sample

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import android.content.Intent
import android.app.PendingIntent
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.builders.SliceAction
import com.kiven.sample.noti.ClickNotiActivity


/**
 * Created by wangk on 2019/5/24.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
class MySliceProvider:SliceProvider() {
    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    override fun onBindSlice(sliceUri: Uri?): Slice? {
        if (context == null) {
            return null
        }
        val activityAction = createActivityAction()
        val listBuilder = ListBuilder(context!!, sliceUri!!, ListBuilder.INFINITY)
        // Create parent ListBuilder.
        if ("/hello" == sliceUri.getPath()) {
            listBuilder.addRow(ListBuilder.RowBuilder()
                    .setTitle("Hello World")
                    .setPrimaryAction(activityAction!!)
            )
        } else {
            listBuilder.addRow(ListBuilder.RowBuilder()
                    .setTitle("URI not recognized")
                    .setPrimaryAction(activityAction!!)
            )
        }
        return listBuilder.build()
    }

    fun createSlice(sliceUri: Uri): Slice? {
        if (context == null) {
            return null
        }
        val activityAction = createActivityAction()
        return ListBuilder(context!!, sliceUri, ListBuilder.INFINITY)
                .addRow(ListBuilder.RowBuilder()
                        .setTitle("Perform action in app.")
                        .setPrimaryAction(activityAction!!)
                ).build()
    }

    fun createActivityAction(): SliceAction? {
        return if (context == null) {
            null
        } else SliceAction.create(
                PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, ClickNotiActivity::class.java),
                        0
                ),
                IconCompat.createWithResource(context!!, R.mipmap.ic_launcher_u),
                ListBuilder.ICON_IMAGE,
                "Enter app"
        )
    }
}
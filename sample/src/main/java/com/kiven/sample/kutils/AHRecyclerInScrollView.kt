package com.kiven.sample.kutils

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.databinding.AhRecyclerInScrollViewBinding
import com.kiven.sample.util.phoneImages

class AHRecyclerInScrollView : KActivityHelper() {
    private lateinit var binding: AhRecyclerInScrollViewBinding

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        binding = AhRecyclerInScrollViewBinding.inflate(activity.layoutInflater)
        setContentView(binding.root)

        binding.llRoot.children.forEach { recyclerView ->
            (recyclerView as RecyclerView).apply {
                layoutManager = GridLayoutManager(activity, 3)
                adapter = ImageRecyclerAdapter(activity)
            }
        }

        activity.phoneImages { images ->
            binding.llRoot.children.forEach { recyclerView ->
                val adapter = (recyclerView as RecyclerView).adapter as ImageRecyclerAdapter

                adapter.changeData(List(7) {
                    val id = images.random()[MediaStore.Images.Media._ID]?.toLong() ?: 0L
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                })
            }
        }
    }
}
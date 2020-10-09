package com.kiven.sample.media

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.*
import com.kiven.sample.R
import com.kiven.sample.util.Const
import com.kiven.sample.util.Const.IMAGE_DIR
import com.kiven.sample.util.showSnack
import com.kiven.sample.util.snackbar
import java.io.File
import java.text.DateFormat
import java.util.*

/**
 *
 * Created by wangk on 2018/2/4.
 */
open class AHMediaList : KActivityDebugHelper() {

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_media_list)

        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        val permissionInfos = arrayOf("存储空间", "相机")
        KGranting.requestPermissions(mActivity, 345, permissions, permissionInfos) { isSuccess ->
            if (!isSuccess) {
                finish()
            }
        }
    }

    private var cameraPath: String = ""
    private var selPath = ""
    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.item_mp4 -> VideoSurfaceDemo().startActivity(mActivity)
            R.id.item_gif -> AHGif().startActivity(mActivity)
            R.id.item_open_image_picker -> mActivity.startActivityForResult(
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 345)
            R.id.item_open_vidio_picker -> mActivity.startActivityForResult(
                    Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), 345)
            R.id.item_open_content_picker -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)//4.4及以上最好使用 ACTION_OPEN_DOCUMENT
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                //intent.setType("image/jpeg");
                intent.type = "video/*;image/*"
                mActivity.startActivityForResult(intent, 345)
            }
            // https://developer.android.google.cn/training/camera/photobasics
            R.id.item_camera_image -> {
                val file = getFile(System.currentTimeMillis().toString() + ".jpg")
//                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Camera/" + System.currentTimeMillis().toString() + ".jpg")
                cameraPath = file.absolutePath

                val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //Intent camera = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (Build.VERSION.SDK_INT < 24) {
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
                } else {
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mActivity, Const.FILEPROVIDER_AUTHORITY, file))
                }
                mActivity.startActivityForResult(camera, 346)
            }
            // https://developer.android.google.cn/training/camera/photobasics
            R.id.item_camera_image2 -> {
                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePhotoIntent.resolveActivity(mActivity.packageManager) != null) {
                    mActivity.startActivityForResult(takePhotoIntent, 351)
                } else {
                    mActivity.snackbar("没有相机")
                }
            }
            R.id.item_camera_video -> mActivity.startActivityForResult(Intent(MediaStore.ACTION_VIDEO_CAPTURE),
                    347)
            R.id.item_crop_image -> mActivity.startActivityForResult(
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 348)
            R.id.item_video_thumb -> mActivity.startActivityForResult(
                    Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), 350)
            R.id.item_camerax -> AHCameraxTest().startActivity(mActivity)
            R.id.item_exoplayer -> {
                /// https://github.com/google/ExoPlayer
                /// https://blog.csdn.net/dianziagen/article/details/82258356
                /// https://juejin.im/post/5cc5c2ec6fb9a032414f65c0
                val bandwidthMeter = DefaultBandwidthMeter()
                var player = ExoPlayerFactory.newSimpleInstance(mActivity, DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter)))

                val dataSourceFactory = DefaultHttpDataSourceFactory("userAgent")
                player.prepare(ExtractorMediaSource(Uri.parse("https://raw.githubusercontent.com/WangKiven/mygit/master/company.mp4"), dataSourceFactory, DefaultExtractorsFactory(), null, null))

                val dialog = object : Dialog(mActivity) {
                    init {
                        setContentView(PlayerView(mActivity).apply {
                            setPlayer(player)
                            player.playWhenReady = true
                        })
                    }

                    override fun dismiss() {
                        super.dismiss()
                        player.stop()
                        player.release()
                        player = null
                    }
                }

                dialog.show()
            }
            R.id.item_exif_interface_1 -> {
                if (KString.isBlank(selPath)) {
                    mActivity.showSnack("先选择图片")
                    return
                }

                val exifInterface = ExifInterface(selPath)
                KLog.i("时间：\n${exifInterface.getAttribute(ExifInterface.TAG_DATETIME)}" +
                        "\n${exifInterface.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)}" +
                        "\n${exifInterface.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)}" +
                        "\n${exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)}" +
                        "\n${exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)}" +
                        "\n${exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME)}" +
                        "\n${exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED)}" +
                        "\n${exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL)}" +
                        "\n${DateFormat.getInstance().format(Date(File(selPath).lastModified()))}")
            }

            R.id.item_exif_interface_2 -> {
                if (KString.isBlank(selPath)) {
                    mActivity.showSnack("先选择图片")
                    return
                }

                val exifInterface = ExifInterface(selPath)

                // 打印更多信息
                val buildFields = ExifInterface::class.java.fields
                val builder = StringBuilder("图片信息：\n")
                for (field in buildFields) {
                    try {
                        val value = field.get(Build::class.java)

                        if (value != null && value.javaClass == Class.forName("[Ljava.lang.String;")) {
                            val ss = "\n" + field.name + ": " + (value as Array<Any>).contentToString()
                            builder.append(ss)
                        } else {
                            val `as` = "\n" + field.name + ": " + value
                            builder.append(`as`)

                            if (value != null && value is String) {
                                if (field.name.startsWith("TAG_"))
                                    builder.append(" ${exifInterface.getAttribute(value)}")
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                KLog.i(builder.toString())
                KLog.i(buildFields.joinToString { it.name })
            }

            R.id.item_exif_interface_3 -> {
                if (KString.isBlank(selPath)) {
                    mActivity.showSnack("先选择图片")
                    return
                }

                val exifInterface = ExifInterface(selPath)

                exifInterface.setAttribute(ExifInterface.TAG_DATETIME, "2019:11:11 01:55:33")
                exifInterface.saveAttributes()
            }
        }
    }

    private fun getFile(fileName: String): File {
        val dir = File(Environment.getExternalStorageDirectory(), IMAGE_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            345 -> {
                val uri = data?.data ?: return

                selPath = KPath.getPath(mActivity, uri)
                KLog.i(selPath)
                KLog.i(uri.toString())

                if (selPath.endsWith(".mp4")) {

                    val video = VideoSurfaceDemo()
                    video.putExtra("mp4Path", uri)
                    video.startActivity(mActivity)
                } else {
                    showImage(selPath)
                }
                /*if (uri.toString().contains("video")) {
                    val video = VideoSurfaceDemo()
                    video.putExtra("mp4Path", uri)
                    video.startActivity(mActivity)
                } else {
                    val parceFileDescriptor = mActivity.contentResolver.openFileDescriptor(uri, "r")
                    val fileDescriptor = parceFileDescriptor!!.fileDescriptor
                    val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                    parceFileDescriptor.close()
                    showImage(path, image)
                }*/
            }
            346 -> {
                showImage(cameraPath)
                KUtil.addPicture(cameraPath) { _, _ ->
                }
                /*Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    val f = File(cameraPath)
                    mediaScanIntent.data = Uri.fromFile(f)
                    mActivity.sendBroadcast(mediaScanIntent)
                }*/
            }
            347 -> KAlertDialogHelper.Show1BDialog(mActivity, data?.data?.path ?: "路径获取失败")
            348 -> cropImage(KPath.getPath(mActivity, data?.data))
            349 -> showImage(cropPath)
            350 -> {
                /*val retriever = MediaMetadataRetriever()
                retriever.setDataSource(mActivity, data?.data)
                val bmp = retriever.frameAtTime
                retriever.release()*/

                val path = KPath.getPath(mActivity, data?.data)
                val bmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND)
                if (bmp == null) {
                    KToast.ToastMessage("获取视频文件缩略图失败")
                } else {
                    showImage(path, bmp)
                }
            }
            351 -> {
                data?.extras?.apply {
                    showImage("获取到的似乎是缩略图", get("data") as Bitmap)
                }
                val uri = data?.data
                if (uri == null) {
                    Log.i("ULog_default", "uri = null")
                }
            }
        }
    }


    private fun showImage(imagePath: String) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        if (bitmap != null)
            showImage(imagePath, bitmap)
        else
            mActivity.snackbar("获取图片失败：$imagePath")
    }


    private fun showImage(title: String, bitmap: Bitmap) {
        val dialog = object : Dialog(mActivity) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val imageView = ImageView(context)
                setContentView(imageView)
                imageView.setImageBitmap(bitmap)
                setTitle("已选图片")
            }
        }
        dialog.setTitle(title)
        dialog.show()
    }

    var cropPath = ""
    private fun cropImage(cameraPath: String) {
        cropPath = getFile(System.currentTimeMillis().toString() + ".jpg").absolutePath

        val `in` = Intent("com.android.camera.action.CROP")

        `in`.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(File(cropPath)))
        `in`.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // 需要裁减的图片格式
        val f = File(cameraPath)
        if (Build.VERSION.SDK_INT >= 24) {
            `in`.setDataAndType(getImageContentUri(mActivity, f), "image/*")
        } else {
            `in`.setDataAndType(Uri.fromFile(f), "image/*")
        }
        // 允许裁减
        `in`.putExtra("crop", "true")
        // 剪裁后ImageView显时图片的宽
        `in`.putExtra("outputX", 200)
        // 剪裁后ImageView显时图片的高
        `in`.putExtra("outputY", 200)
        // 设置剪裁框的宽高比例
        `in`.putExtra("aspectX", 1)
        `in`.putExtra("aspectY", 1)
        `in`.putExtra("return-data", false)
        `in`.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        `in`.putExtra("noFaceDetection", true)
        mActivity.startActivityForResult(`in`, 349)
    }

    private fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ",
                arrayOf(filePath), null)

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")

            cursor.close()
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                return null
            }
        }
    }
}
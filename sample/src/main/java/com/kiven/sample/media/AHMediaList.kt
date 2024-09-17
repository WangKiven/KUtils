package com.kiven.sample.media

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.exifinterface.media.ExifInterface
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.*
import com.kiven.sample.R
import com.kiven.sample.util.Const
import com.kiven.sample.util.Const.IMAGE_DIR
import com.kiven.sample.util.phoneImages
import com.kiven.sample.util.pickPhoneImage
import com.kiven.sample.util.showImageDialog
import com.kiven.sample.util.showListDialog
import com.kiven.sample.util.showSnack
import com.kiven.sample.util.showToast
import java.io.File
import java.text.DateFormat
import java.util.*

/**
 *
 * Created by wangk on 2018/2/4.
 */
class AHMediaList : KActivityHelper() {

    private lateinit var pickerLaunch: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var pickerMultipleLaunch: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_media_list)

        pickerLaunch = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it ?: return@registerForActivityResult

            // 持有保留访问权限，默认情况下，系统会授予应用对媒体文件的访问权限，直到设备重启或应用停止运行。如果您的应用执行长时间运行的工作（例如在后台上传大型文件），您可能需要将此访问权限保留更长时间。
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            mActivity.contentResolver.takePersistableUriPermission(it, flag)

            // 显示
            showImage(it)
        }

        pickerMultipleLaunch = activity.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            if (it != null && it.isNotEmpty()) showImage(it[0])
        }

        val permissions =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        val permissionInfos = arrayOf("存储空间", "相机")
        KGranting.requestPermissions(mActivity, permissions, permissionInfos) { isSuccess ->
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
            R.id.item_open_image_picker_13 -> {
                // https://developer.android.google.cn/about/versions/13/features/photopicker?hl=zh-cn
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                    return mActivity.showSnack("系统版本太低")

                mActivity.showListDialog(listOf("单张", "多张")) {i, _ ->
                    when(i) {
                        0 -> {
                            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                            intent.type = "video/*"// 没有这个的话，视频图片都可选
                            mActivity.startActivityForResult(intent, 345)
                        }
                        1 -> {
                            val maxNumPhotosAndVideos = 10
                            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxNumPhotosAndVideos)
                            intent.type = "image/*"// 没有这个的话，视频图片都可选
                            mActivity.startActivityForResult(intent, 345)
                        }
                    }
                }
            }
            R.id.item_open_image_picker_13_jitpack -> {
                // https://developer.android.google.cn/training/data-storage/shared/photopicker?hl=zh-cn
                mActivity.showListDialog(listOf("单张（并保留访问权限）", "多张", "gif")) { i, _ ->
                    when(i) {
                        0 -> pickerLaunch.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        1 -> pickerMultipleLaunch.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        2 -> {
                            val mimeType = "image/gif"
                            pickerMultipleLaunch.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)))
                        }
                    }
                }
            }
            R.id.item_open_image_picker -> mActivity.startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 345
            )
            R.id.item_open_vidio_picker -> mActivity.startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), 345
            )
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
                    camera.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(mActivity, Const.FILEPROVIDER_AUTHORITY, file)
                    )
                }
                mActivity.startActivityForResult(camera, 346)
            }
            // https://developer.android.google.cn/training/camera/photobasics
            R.id.item_camera_image2 -> {
                val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePhotoIntent.resolveActivity(mActivity.packageManager) != null) {
                    mActivity.startActivityForResult(takePhotoIntent, 351)
                } else {
                    mActivity.showSnack("没有相机")
                }
            }
            R.id.item_camera_video -> mActivity.startActivityForResult(
                Intent(MediaStore.ACTION_VIDEO_CAPTURE),
                347
            )
            R.id.item_crop_image -> mActivity.startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 348
            )
            R.id.item_video_thumb -> mActivity.startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), 350
            )
            R.id.item_manage_file -> {
                AHMediaManager().startActivity(mActivity)
            }
            R.id.item_camerax -> AHCameraxTest().startActivity(mActivity)
            R.id.item_exoplayer -> {
                /// https://github.com/google/ExoPlayer
                /// https://blog.csdn.net/dianziagen/article/details/82258356
                /// https://juejin.im/post/5cc5c2ec6fb9a032414f65c0
                val player = SimpleExoPlayer.Builder(mActivity)
                    .setTrackSelector(DefaultTrackSelector(mActivity))
                    .setBandwidthMeter(DefaultBandwidthMeter.Builder(mActivity).build())
                    .build()


                val dataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent("userAgent")

                player.setMediaSource(
                    ProgressiveMediaSource.Factory(dataSourceFactory, DefaultExtractorsFactory())
                        .createMediaSource(
                            MediaItem.Builder()
                                .setUri(Uri.parse("https://www.yimizi.xyz/wedding/resource/1.mp4"))
                                .build()
                        )
                )

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
                KLog.i(
                    "时间：\n${exifInterface.getAttribute(ExifInterface.TAG_DATETIME)}" +
                            "\n${exifInterface.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)}" +
                            "\n${exifInterface.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)}" +
                            "\n${exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)}" +
                            "\n${exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)}" +
                            "\n${exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME)}" +
                            "\n${exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED)}" +
                            "\n${exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL)}" +
                            "\n${
                                DateFormat.getInstance().format(Date(File(selPath).lastModified()))
                            }"
                )
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
                            val ss =
                                "\n" + field.name + ": " + (value as Array<Any>).contentToString()
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
        val dir = File(mActivity.externalCacheDir, IMAGE_DIR)

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
                var uri = data?.data
                if (uri == null) {
                    val cd = data?.clipData
                    if (cd != null && cd.itemCount > 0)
                        uri = cd.getItemAt(0).uri
                }
                if (uri == null) return mActivity.showSnack("uri = null")
                showImage(uri)
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
            348 -> data?.data?.apply {
                cropImage(this)
            }
            349 -> showImage(cropPath)
//            349 -> mActivity.showImageDialog(data!!.extras!!.getParcelable<Bitmap>("data")!!)
            350 -> {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(mActivity, data?.data)
                val bmp = retriever.frameAtTime ?: return
                retriever.release()

                mActivity.showImageDialog(bmp)

                /*val bmp = mActivity.contentResolver.loadThumbnail(data!!.data!!, Size(300, 300), null)
                mActivity.showImageDialog(bmp)*/


                /*val uri = data?.data ?: return

                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ThumbnailUtils.createVideoThumbnail(uri.toFile(), Size(100, 100), null)
                } else {
                    ThumbnailUtils.createVideoThumbnail(KPath.getPath(uri), MediaStore.Video.Thumbnails.MINI_KIND)
                }

                if (bmp == null) {
                    KToast.ToastMessage("获取视频文件缩略图失败")
                } else {
                    mActivity.showImageDialog(bmp)
                }*/
            }
            351 -> {
                data?.extras?.apply {
                    showImage("获取到的似乎是缩略图", get("data") as Bitmap)
                }
                val uri = data?.data
                if (uri == null) {
                    Log.i(KLog.getTag(), "uri = null")
                }
            }
        }
    }


    private fun showImage(uri: Uri) {
        selPath = KPath.getPath(uri) ?: return mActivity.showSnack("uri解析失败：$uri")
        KLog.i(selPath)
        KLog.i(uri.toString())

        if (selPath.endsWith(".mp4")) {

            val video = VideoSurfaceDemo()
            video.putExtra("mp4Path", uri)
            video.startActivity(mActivity)
        } else {
//                    showImage(selPath)
            mActivity.showImageDialog(uri)
        }
    }


    private fun showImage(imagePath: String) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        if (bitmap != null)
            showImage(imagePath, bitmap)
        else
            mActivity.showSnack("获取图片失败：$imagePath")
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
    private fun cropImage(cameraPath: Uri) {
        val outFile = getFile(System.currentTimeMillis().toString() + ".jpg")
        cropPath = outFile.absolutePath

        val `in` = Intent("com.android.camera.action.CROP")
        // 需要裁减的图片格式
        `in`.setDataAndType(cameraPath, "image/*")

        if (Build.VERSION.SDK_INT < 24) {
            `in`.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile))
        } else {
            `in`.putExtra(
                MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(mActivity, Const.FILEPROVIDER_AUTHORITY, outFile)
            )
        }
        `in`.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        `in`.putExtra("return-data", false)

        // 允许裁减
        `in`.putExtra("crop", "true")
        // 剪裁后ImageView显时图片的宽
        `in`.putExtra("outputX", 200)
        // 剪裁后ImageView显时图片的高
        `in`.putExtra("outputY", 200)
        // 设置剪裁框的宽高比例
        `in`.putExtra("aspectX", 1)
        `in`.putExtra("aspectY", 1)
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
            arrayOf(filePath), null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(
                cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID)
            )
            val baseUri = Uri.parse("content://media/external/images/media")

            cursor.close()
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                try {
                    return context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    )
                } catch (e: Throwable) {
                    KLog.e(e)
                    showToast("数据处理异常：${e.message}")
                    return null
                }
            } else {
                return null
            }
        }
    }
}
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
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.ImageView
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.*
import com.kiven.sample.R
import java.io.File

/**
 *
 * Created by wangk on 2018/2/4.
 */
open class AHMediaList : KActivityHelper() {
    private val FILEPROVIDER_AUTHORITY = "com.kiven.sample.fileprovider"
    private val IMAGE_DIR = "KUtilSampleFile" + File.separator + "testImage"

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_media_list)

        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        val permissionInfos = arrayOf("存储空间", "相机")
        KGranting.requestPermissions(mActivity, 345, permissions, permissionInfos, { isSuccess ->
            if (!isSuccess) {
                finish()
            }
        })
    }

    private var cameraPath: String = ""
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
            R.id.item_camera_image -> {
                val file = getFile(System.currentTimeMillis().toString() + ".jpg")
                cameraPath = file.absolutePath

                val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //Intent camera = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (Build.VERSION.SDK_INT < 24) {
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
                } else {
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mActivity, FILEPROVIDER_AUTHORITY, file))
                }
                mActivity.startActivityForResult(camera, 346)
            }
            R.id.item_camera_video -> mActivity.startActivityForResult(Intent(MediaStore.ACTION_VIDEO_CAPTURE),
                    347)
            R.id.item_crop_image -> mActivity.startActivityForResult(
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 348)
            R.id.item_video_thumb -> mActivity.startActivityForResult(
                    Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), 350)
        }
    }

    private fun getFile(fileName: String): File {
        val dir = File(Environment.getExternalStorageDirectory(), IMAGE_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            345 -> {
                val path = KPath.getPath(mActivity, data?.data)
                KLog.i(path)

                if (path.endsWith(".mp4")) {

                    val video = VideoSurfaceDemo()
                    video.putExtra("mp4Path", path)
                    video.startActivity(mActivity)
                } else
                    showImage(path)
            }
            346 -> {
                showImage(cameraPath)
                KUtil.addPicture(cameraPath, {_,_ ->
                })
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
                    showImage(bmp)
                }
            }
        }
    }


    private fun showImage(imagePath: String) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        showImage(bitmap)
    }


    private fun showImage(bitmap: Bitmap) {
        val dialog = object : Dialog(mActivity) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                val imageView = ImageView(context)
                setContentView(imageView)
                imageView.setImageBitmap(bitmap)
                setTitle("已选图片")
            }
        }
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
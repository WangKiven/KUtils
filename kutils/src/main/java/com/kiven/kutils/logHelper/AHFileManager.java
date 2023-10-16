package com.kiven.kutils.logHelper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kiven.kutils.R;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.tools.KFile;
import com.kiven.kutils.tools.KAlertDialogHelper;
import com.kiven.kutils.tools.KGranting;
import com.kiven.kutils.tools.KString;
import com.kiven.kutils.tools.KUtil;
import com.kiven.kutils.tools.KView;
import com.kiven.kutils.widget.UIGridView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AHFileManager extends KActivityHelper {
    private GridViewAdapter gridViewAdapter = new GridViewAdapter();

    private final ArrayList<LFile> modules = new ArrayList<>();

    // 选择的文件路径
    private final ArrayList<LFile> selDir = new ArrayList<>();

    private final MyAdapter childAdapter = new MyAdapter();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!KGranting.useFragmentRequest)
            KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCreate(@NonNull KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        activity.setTheme(R.style.KTheme);

        setContentView(R.layout.k_ah_file_manager);
        initBackToolbar(R.id.toolbar, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            childAdapter.notifyDataSetChanged();
        } else {
            KGranting.requestPermissions(mActivity, 345, Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间", new KGranting.GrantingCallBack() {
                @Override
                public void onGrantSuccess(boolean isSuccess) {
                    if (isSuccess) {
                        // 刷新列表
                        childAdapter.notifyDataSetChanged();
                    }
                }
            });
        }


        modules.add(new LFile("应用内部", mActivity.getFilesDir().getParentFile()));
        File cf = mActivity.getExternalCacheDir().getParentFile();
        if (cf != null) {
            modules.add(new LFile("应用外部", cf));
        }
        modules.add(new LFile("/system", Environment.getRootDirectory()));
        modules.add(new LFile("/data", Environment.getDataDirectory()));
        modules.add(new LFile("系统根目录", new File("/")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            modules.add(new LFile("/storage", Environment.getStorageDirectory()));
        }
        modules.add(new LFile("系统内部存储", Environment.getExternalStorageDirectory()));
        modules.add(new LFile("相册", new File(Environment.getExternalStorageDirectory(), "Pictures")));

        modules.add(new LFile("sourceDir", new File(mActivity.getApplicationInfo().sourceDir)));
        modules.add(new LFile("publicSourceDir", new File(mActivity.getApplicationInfo().publicSourceDir)));
        modules.add(new LFile("nativeLibraryDir", new File(mActivity.getApplicationInfo().nativeLibraryDir)));
        modules.add(new LFile("dataDir", new File(mActivity.getApplicationInfo().dataDir)));


        UIGridView gridView = findViewById(R.id.uiGridView);
        gridView.setAdapter(gridViewAdapter);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        {
            try {
                Class.forName("com.google.android.flexbox.FlexboxLayoutManager");

                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mActivity);
                layoutManager.setFlexDirection(FlexDirection.ROW);
                layoutManager.setJustifyContent(JustifyContent.CENTER);
                recyclerView.setLayoutManager(layoutManager);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                recyclerView.setLayoutManager(new GridLayoutManager(mActivity, KUtil.getScreenWith() / KUtil.dip2px(50f)));
            }
        }
        recyclerView.setAdapter(childAdapter);

        onSelectedDir();
    }

    private void onSelectedDir() {
        if (selDir.size() > 0) {
            LFile file = selDir.get(selDir.size() - 1);

            File[] childs = file.listFiles();
            if (childs != null && childs.length > 1)
                Arrays.sort(childs, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        boolean dir1 = o1.isDirectory();
                        boolean dir2 = o2.isDirectory();
                        if (dir1 == dir2) {
                            return o1.compareTo(o2);
                        } else {
                            if (dir1) return -1;
                        }
                        return 0;
                    }
                });

            childAdapter.refreshData(childs);
        } else {
            childAdapter.refreshData(modules);
        }

        gridViewAdapter.notifyDataSetChanged();
    }

    /**
     * 点击系统返回按钮时
     */
    @Override
    public boolean onBackPressed() {
        if (selDir.size() > 0) {
            selDir.remove(selDir.size() - 1);
            onSelectedDir();
            return false;
        }
        return super.onBackPressed();
    }

    private class GridViewAdapter extends UIGridView.Adapter implements View.OnClickListener {
        @Override
        public int getGridViewItemCount() {
            return selDir.size() + 1;
        }

        @Override
        public View getItemView(Context context, View itemView, ViewGroup parentView, int position) {
            TextView tv;
            if (itemView == null) {
                tv = new TextView(mActivity);
                tv.setPadding(10, 10, 10, 10);
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.k_right_arrow, 0);
                tv.setCompoundDrawablePadding(10);
                tv.setOnClickListener(this);
            } else {
                tv = (TextView) itemView;
            }

            if (position < 1) {
                tv.setText("多根");
            } else {
                tv.setText(selDir.get(position - 1).name);
            }
            tv.setTag(position);
            return tv;
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            List<LFile> rf = new ArrayList<>();
            for (int i = position; i < selDir.size(); i++) {
                rf.add(selDir.get(i));
            }

            selDir.removeAll(rf);
            onSelectedDir();
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView iv_unread;
        TextView tv_num;

        void showImage() {
            ImageView iv = new ImageView(mActivity);
            iv.setAdjustViewBounds(true);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setImageBitmap(BitmapFactory.decodeFile(cFile.getAbsolutePath()));

            new AlertDialog.Builder(mActivity).setView(iv).show();
        }

        MyHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            iv_unread = itemView.findViewById(R.id.iv_unread);
            tv_num = itemView.findViewById(R.id.tv_num);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (cFile.canRead())
                        KAlertDialogHelper.Show2BDialog(mActivity, "是否删除文件？", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KUtil.deleteFile(cFile, true);
                                onSelectedDir();
                            }
                        });
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KLog.i("点击文件 = " + cFile.getAbsolutePath() + "\n修改时间 = " + KString.formatDate(cFile.lastModified()) +
                            "\n文件大小 = " + Formatter.formatFileSize(mActivity, cFile.length()));

                    if (cFile.canRead()) {
                        if (cFile.isDirectory()) {
                            selDir.add(cFile);
                            onSelectedDir();
                        } else {
                            if (KFile.checkFileType(cFile) != KFile.FileType.UNKNOWN) {
                                showImage();
                            } else
                                new MaterialAlertDialogBuilder(mActivity)
                                        .setTitle("选择打开方式").setMessage("文件大小" + Formatter.formatFileSize(mActivity, cFile.length()))
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setNeutralButton("图片", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                showImage();
                                            }
                                        })
                                        .setPositiveButton("文档", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (cFile.length() / 1024 > 100) {
//                                                    KAlertDialogHelper.Show1BDialog(mActivity, "文件太大了，会卡！");
                                                    KAlertDialogHelper.Show2BDialog(mActivity, "提示", "文件太大了，会卡！", "知道了", "导出", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            extFile();
                                                        }
                                                    });
                                                } else {
                                                    new AlertDialog.Builder(mActivity)
                                                            .setTitle("选择编码")
                                                            .setItems(new CharSequence[]{
                                                                    "UTF-8", "US_ASCII", "ISO-8859-1", "UTF-16", "UTF-16BE", "UTF-16LE"
                                                            }, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Charset charset;
                                                                    switch (which) {
                                                                        case 1:
                                                                            charset = Charset.forName("US_ASCII");
                                                                            break;
                                                                        case 2:
                                                                            charset = Charset.forName("ISO-8859-1");
                                                                            break;
                                                                        case 3:
                                                                            charset = Charset.forName("UTF-16");
                                                                            break;
                                                                        case 4:
                                                                            charset = Charset.forName("UTF-16BE");
                                                                            break;
                                                                        case 5:
                                                                            charset = Charset.forName("UTF-16LE");
                                                                            break;
                                                                        default:
                                                                            charset = Charset.forName("UTF-8");
                                                                    }

                                                                    ScrollView scrollView = new ScrollView(mActivity);
                                                                    TextView tv = new TextView(mActivity);
                                                                    scrollView.addView(tv);

                                                                    tv.setText(KFile.readFile(cFile, charset));

                                                                    new AlertDialog.Builder(mActivity)
                                                                            .setView(scrollView)
                                                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                }
                                                                            })
                                                                            .setPositiveButton("导出", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    extFile();
                                                                                }
                                                                            })
                                                                            .show();

                                                                }
                                                            }).show();
                                                }
                                            }
                                        })
                                        .show();
                        }
                    }
                }
            });
        }

        private void extFile() {
            Uri txtUri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                txtUri = Uri.fromFile(cFile);
            } else {
                if (KUtil.getConfig().fileprovider.isEmpty()) {
                    KLog.i("未配置 fileprovider");
                    return;
                }
                txtUri = FileProvider.getUriForFile(mActivity, KUtil.getConfig().fileprovider, cFile);
            }

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_STREAM, txtUri);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            mActivity.startActivity(Intent.createChooser(i, "弹出显示的标题x"));
        }

        LFile cFile;

        public void bindData(LFile file) {
            cFile = file;

            if (file.isDirectory()) {
                imageView.setImageResource(android.R.drawable.ic_dialog_email);
            } else {
                if (KFile.checkFileType(cFile) != KFile.FileType.UNKNOWN) {
//                    x.image().bind(imageView, cFile.getAbsolutePath());

                    imageView.setImageBitmap(BitmapFactory.decodeFile(cFile.getAbsolutePath()));
                } else
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            KView.setVisibility(iv_unread, !file.canRead());

            tv_num.setText(file.name);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {
        // 选择的文件夹下的所有文件
        private final ArrayList<LFile> childFiles = new ArrayList<>();

        void refreshData(@NonNull List<LFile> datas) {
            childFiles.clear();
            childFiles.addAll(datas);
            notifyDataSetChanged();
        }

        void refreshData(File[] datas) {
            childFiles.clear();
            if (datas != null && datas.length > 0)
                for (File cf : datas) {
                    childFiles.add(new LFile(cf));
                }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(mActivity).inflate(R.layout.k_item_res, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.bindData(childFiles.get(position));
        }

        @Override
        public int getItemCount() {
            return childFiles.size();
        }
    }


    private static class LFile extends File {
        String name = "";

        LFile(@NonNull String name, @NonNull File file) {
            this(file.getPath());
            this.name = name;
        }

        LFile(@NonNull File file) {
            this(file.getPath());
            name = file.getName();
        }

        LFile(@NonNull String pathname) {
            super(pathname);
        }

        LFile(String parent, @NonNull String child) {
            super(parent, child);
        }

        LFile(File parent, @NonNull String child) {
            super(parent, child);
        }

        LFile(@NonNull URI uri) {
            super(uri);
        }
    }
}

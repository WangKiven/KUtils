package com.kiven.sample.autoService

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView

import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KString

object AccessibilityUtil {
    private val TAG = "KUtils-sample"

    /**
     * 检查系统设置：是否开启辅助服务
     *
     * @param service 辅助服务
     */
    private fun isSettingOpen(service: Class<*>, cxt: Context): Boolean {
        try {
            val enable = Settings.Secure.getInt(cxt.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, 0)
            if (enable != 1)
                return false
            val services = Settings.Secure.getString(cxt.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (!TextUtils.isEmpty(services)) {
                val split = TextUtils.SimpleStringSplitter(':')
                split.setString(services)
                while (split.hasNext()) { // 遍历所有已开启的辅助服务名
                    if (split.next().equals(cxt.packageName + "/" + service.name, ignoreCase = true))
                        return true
                }
            }
        } catch (e: Throwable) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            Log.e(TAG, "isSettingOpen: " + e.message)
        }

        return false
    }

    /**
     * 跳转到系统设置：开启辅助服务
     */
    fun jumpToSetting(cxt: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        //        intent.setData(Uri.parse("package:" + cxt.getPackageName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(Intent.EXTRA_PACKAGE_NAME, cxt.packageName)
        }

        try {
            cxt.startActivity(intent)
        } catch (e: Throwable) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                cxt.startActivity(intent)
            } catch (e2: Throwable) {
                Log.e(TAG, "jumpToSetting: " + e2.message)
            }

        }

    }


    /**
     * 打印当前结构
     */
    fun printTree(nodeInfo: AccessibilityNodeInfo) {
        val sb = StringBuilder("windowId:").append(nodeInfo.windowId).append("\n")
        getTree(nodeInfo, sb, 0)

        KLog.i(sb.toString())
    }

    private fun getTree(nodeInfo: AccessibilityNodeInfo?, tree: StringBuilder, deep: Int) {
        if (nodeInfo == null) return

        val childCount = nodeInfo.childCount
        val visible = nodeInfo.isVisibleToUser
        val clickable = nodeInfo.isClickable

        tree.append(getDeepHeader(deep))
                .append(nodeInfo.windowId).append(" ")
                .append(nodeInfo.className)
        if (TextUtils.equals(nodeInfo.className, "android.widget.TextView") || TextUtils.equals(nodeInfo.className, "android.widget.Button")) {
            tree.append("(").append(nodeInfo.text).append(")")
        }
        if (TextUtils.equals(nodeInfo.className, "android.widget.EditText")) {
            tree.append("(text:").append(nodeInfo.text).append(", hint:")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tree.append(nodeInfo.hintText)
            }
            tree.append(")")
        }

        if (childCount > 0) {
            tree.append("(childCount:").append(childCount).append(")")
        }
        if (!visible) {
            tree.append("(visible:").append(false).append(")")
        }
        if (clickable) {
            tree.append("(clickable:").append(true).append(")")
        }

        if (nodeInfo.isSelected) {
            tree.append("(selected:").append(true).append(")")
        }

        if (nodeInfo.isChecked) {
            tree.append("(checked:").append(true).append(")")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (nodeInfo.viewIdResourceName != null)
                tree.append("(resourceId:").append(nodeInfo.viewIdResourceName).append(")")
        }

        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        tree.append("(boundsInScreen:").append(rect.toString()).append(")")

        tree.append("\n")

        if (visible)
            for (i in 0 until childCount) {
                getTree(nodeInfo.getChild(i), tree, deep + 1)
            }
    }

    private fun getDeepHeader(deep: Int): String {
        val sb = StringBuilder()
        for (i in 0 until deep) {
            sb.append("-")
        }
        sb.append(deep).append(" ")
        return sb.toString()
    }


    // TODO 查找安装,并模拟点击(findAccessibilityNodeInfosByText判断逻辑是contains而非equals)


    fun findTxtClick(nodeInfo: AccessibilityNodeInfo, txt: String, souceId: String? = null): Boolean {
        val nodes: List<AccessibilityNodeInfo>?
        if (KString.isBlank(souceId)) {
            nodes = nodeInfo.findAccessibilityNodeInfosByText(txt)
        } else {
            nodes = nodeInfo.findAccessibilityNodeInfosByViewId(souceId!!)
        }

        if (nodes == null || nodes.isEmpty())
            return false

        for (ni in nodes) {
            if (TextUtils.equals(ni.text, txt)) {
//                KLog.i("click: $ni")
                clickNode(ni, true)

                return true
            }
        }

        return false
    }

    fun findNodeClickById(nodeInfo: AccessibilityNodeInfo, souceId: String) {
        val nodes = nodeInfo.findAccessibilityNodeInfosByViewId(souceId)
        if (nodes != null && nodes.size > 0) {
            for (ni in nodes) {
                clickNode(ni, true)
            }
        }
    }

    fun findNodeClickByClass(nodeInfo: AccessibilityNodeInfo, className: String) {
        val node = findNodeByClass(nodeInfo, className) ?: return
        clickNode(node, true)
    }

    fun findNodesClickByClass(nodeInfo: AccessibilityNodeInfo, className: String) {
        val nodes = findNodesByClass(nodeInfo, className)
        if (nodes.size > 0) {
            for (ni in nodes) {
                clickNode(ni, true)
            }
        }
    }

    /**
     * 点击组件
     *
     * @param checkParent 该按钮不可点击的话，向上还是向下检索并点击可点击控件，true:向上（即检测父控件） false:向下（即检测子控件, 没有的话就不检测）
     * @return true 点击成功，false: 没找到点击
     */
    fun clickNode(nodeInfo: AccessibilityNodeInfo, checkParent: Boolean): Boolean {

        if (checkParent) {
            var checkNode: AccessibilityNodeInfo? = nodeInfo

            while (checkNode != null) {
                if (checkNode.isClickable) {
                    checkNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                }
                checkNode = checkNode.parent
            }
        } else {
            val length = nodeInfo.childCount
            if (length > 0) {
                for (i in 0 until length) {
                    if (clickNode(nodeInfo.getChild(i), false)) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun clickNode(nodeInfo: AccessibilityNodeInfo) {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    /**
     * 是否有文案是txt的组件，文案必须一样
     */
    fun hasTxt(nodeInfo: AccessibilityNodeInfo, txt: String): Boolean {
        val nodes = nodeInfo.findAccessibilityNodeInfosByText(txt)
        if (nodes == null || nodes.isEmpty())
            return false

        for (ni in nodes) {
            if (TextUtils.equals(ni.text, txt)) {
                return true
            }
        }

        return false
    }


    fun findTxtNode(nodeInfo: AccessibilityNodeInfo, txt: String, souceId: String? = null): AccessibilityNodeInfo? {
        val nodes: List<AccessibilityNodeInfo>? = if (KString.isBlank(souceId)) {
            nodeInfo.findAccessibilityNodeInfosByText(txt)
        } else {
            nodeInfo.findAccessibilityNodeInfosByViewId(souceId!!)
        }

        if (nodes == null || nodes.isEmpty())
            return null

        for (ni in nodes) {
            if (TextUtils.equals(ni.text, txt)) {
                return ni
            }
        }

        return null
    }

    /**
     * 根据id查找，返回找到的第一个
     */
    fun findNodeById(nodeInfo: AccessibilityNodeInfo, souceId: String): AccessibilityNodeInfo? {
        val nodes = nodeInfo.findAccessibilityNodeInfosByViewId(souceId)
        return if (nodes != null && nodes.size > 0) {
            nodes[0]
        } else null
    }

    /**
     * 根据class查找，返回找到的第一个子控件
     */
    fun <T> findNodeByClass(nodeInfo: AccessibilityNodeInfo, cl: Class<T>): AccessibilityNodeInfo? {
        return findNodeByClass(nodeInfo, cl.name)
    }

    fun findNodeByClass(nodeInfo: AccessibilityNodeInfo, className: String): AccessibilityNodeInfo? {

        if (nodeInfo.className == className)
            return nodeInfo

        val childCount = nodeInfo.childCount
        if (childCount > 0) {
            for (i in 0 until childCount) {
                val nn = nodeInfo.getChild(i)
                if (nn != null) {
                    val childNode = findNodeByClass(nn, className)
                    if (childNode != null) return childNode
                }
            }
        }

        return null
    }

    /**
     * 根据class查找
     */
    fun <T> findNodesByClass(nodeInfo: AccessibilityNodeInfo, cl: Class<T>): MutableList<AccessibilityNodeInfo> {
        return findNodesByClass(nodeInfo, cl.name)
    }

    fun findNodesByClass(nodeInfo: AccessibilityNodeInfo, className: String): MutableList<AccessibilityNodeInfo> {
        val nodes = mutableListOf<AccessibilityNodeInfo>()

        if (nodeInfo.className == className)
            nodes.add(nodeInfo)

        val childCount = nodeInfo.childCount
        if (childCount > 0) {
            for (i in 0 until childCount) {
                val nn = nodeInfo.getChild(i)
                if (nn != null) {
                    val childNodes = findNodesByClass(nn, className)
                    if (childNodes.size > 0) {
                        nodes.addAll(childNodes)
                    }
                }
            }
        }

        return nodes
    }


    private var preListPageString = ""
    /**
     * 遍历listview
     * @param idName 数据所在的控件的id名称
     * @return 是否已经到底部，通过对比上次和这次的数据
     */
    fun checkListViewById(listViewNode: AccessibilityNodeInfo, datas: MutableList<String>, idName: String): Boolean {
        val txtNodes = listViewNode.findAccessibilityNodeInfosByViewId(idName)
        if (txtNodes == null || txtNodes.isEmpty()) return true //没有数据也就不用判断滚动了

        // 去重加入记录表
        val txts = txtNodes.map { it.text.toString() }
        txts.forEach {
            if (!datas.contains(it)) {
                datas.add(it)
            }
        }

        // 对比数据，判断是否到底部了
        val curListPageString = txts.joinToString()
        return if (preListPageString == curListPageString) {
            true
        } else {
            preListPageString = curListPageString
            false
        }
    }

    /**
     * 遍历listview
     * @param cn 数据所在的控件的class类型
     * @return 是否已经到底部，通过对比上次和这次的数据
     */
    fun <T> checkListViewByClass(listViewNode: AccessibilityNodeInfo, datas: MutableList<String>, cn: Class<T>): Boolean {
        return checkListViewByClass(listViewNode, datas, cn.name)
    }

    fun checkListViewByClass(listViewNode: AccessibilityNodeInfo, datas: MutableList<String>, className: String): Boolean {
        val txtNodes = findNodesByClass(listViewNode, className)
        if (txtNodes.isEmpty()) return true //没有数据也就不用判断滚动了

        // 去重加入记录表
        val txts = txtNodes.map { it.text.toString() }
        txts.forEach {
            if (!datas.contains(it)) {
                datas.add(it)
            }
        }

        // 对比数据，判断是否到底部了
        val curListPageString = txts.joinToString()
        return if (preListPageString == curListPageString) {
            preListPageString = ""
            true
        } else {
            preListPageString = curListPageString
            false
        }
    }

    /**
     * 遍历listview, 获取TextView 中的数据
     * @return 是否已经到底部，通过对比上次和这次的数据
     */
    fun checkListViewByTextView(listViewNode: AccessibilityNodeInfo, datas: MutableList<String>): Boolean {
        return checkListViewByClass(listViewNode, datas, TextView::class.java.name)
    }

    /**
     * 遍历listview, 比较TextView 中的数据， 不获取数据
     * @return 是否已经到底部，通过对比上次和这次的数据
     */
    fun checkListViewByTextView(listViewNode: AccessibilityNodeInfo): Boolean {
        return checkListViewByClass(listViewNode, TextView::class.java.name)
    }

    /**
     * 遍历listview, 不获取数据
     * @return 是否已经到底部，通过对比上次和这次的数据
     */
    fun checkListViewByClass(listViewNode: AccessibilityNodeInfo, className: String): Boolean {
        val txtNodes = findNodesByClass(listViewNode, className)
        if (txtNodes.isEmpty()) return true //没有数据也就不用判断滚动了

        // 去重加入记录表
        val txts = txtNodes.map { it.text.toString() }

        // 对比数据，判断是否到底部了
        val curListPageString = txts.joinToString()
        return if (preListPageString == curListPageString) {
            preListPageString = ""
            true
        } else {
            preListPageString = curListPageString
            false
        }
    }
}

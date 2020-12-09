package com.foreverht.webview;

public class GetWebSnapshotJs {
    public static String JS = "\n" +
            "workplus_getShareSnapShot()\n" +
            "\n" +
            "function workplus_getShareSnapShot() {\n" +
            "    var snapShot = new Object()\n" +
            "    snapShot.title = workplus_getTitle()\n" +
            "    snapShot.coverUrl = workplus_getCoverURL()\n" +
            "    snapShot.description = workplus_getDescription()\n" +
            "\n" +
            "    return snapShot\n" +
            "\n" +
            "}\n" +
            "\n" +
            "function workplus_h1Toh5() {\n" +
            "    var title = ''\n" +
            "    var arr = ['h1', 'h2', 'h3', 'h4', 'h5']\n" +
            "    arr.some(function (el) {\n" +
            "        if (document.getElementsByTagName(el)[0]) {\n" +
            "            title = document.getElementsByTagName(el)[0].innerText\n" +
            "            if (title != '') {\n" +
            "                return true\n" +
            "            }\n" +
            "        }\n" +
            "    })\n" +
            "    return title\n" +
            "}\n" +
            "function workplus_getTitle() {\n" +
            "    if (document.title) return document.title\n" +
            "    return workplus_h1Toh5()\n" +
            "}\n" +
            "\n" +
            "function workplus_getDescription() {\n" +
            "    if (document.getElementsByTagName('description')[0]) {\n" +
            "        return document.getElementsByTagName('description')[0].innerText\n" +
            "    }\n" +
            "    return workplus_h1Toh5()\n" +
            "}\n" +
            "\n" +
            "function workplus_getCoverURL() {\n" +
            "    if (document.getElementsByTagName('img').length == 0) {\n" +
            "        return ''\n" +
            "    }\n" +
            "    var condition = 400\n" +
            "    var flag = true\n" +
            "    var count = 0\n" +
            "    while (flag) {\n" +
            "        var img = document.getElementsByTagName('img')[count]\n" +
            "        if (\n" +
            "            ((img.style.width >= condition) && (img.style.height >= condition)) ||\n" +
            "            ((img.width >= condition) && (img.height >= condition))\n" +
            "        ) {\n" +
            "            if (img.src.indexOf(';base64') == -1) {\n" +
            "                return img.src\n" +
            "            }\n" +
            "        }\n" +
            "        count += 1\n" +
            "        if (document.getElementsByTagName('img').length <= count) {\n" +
            "            count = 0\n" +
            "            condition -= 50\n" +
            "        }\n" +
            "        if (condition <= 50) {\n" +
            "            return ''\n" +
            "        }\n" +
            "    }\n" +
            "}";
}

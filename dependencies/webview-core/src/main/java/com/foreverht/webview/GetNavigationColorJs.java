package com.foreverht.webview;

public class GetNavigationColorJs {

    public static String JS = "getNavigationColor();    function getNavigationColor(){\n" +
            "    var metas = document.getElementsByTagName('meta');\n" +
            "    for(var i = 0; i< metas.length;i++){\n" +
            "\n" +
            "         if(metas[i].name == '_navigation_color') {\n" +
            "            return metas[i].content;\n" +
            "         }\n" +
            "    }\n" +
            "\n" +
            "};";

}

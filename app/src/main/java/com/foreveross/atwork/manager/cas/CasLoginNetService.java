package com.foreveross.atwork.manager.cas;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.webview.WebkitSdkUtil;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by dasunsy on 2017/10/30.
 */

public class CasLoginNetService {

    @SuppressLint("StaticFieldLeak")
    public static void login(View webView, String casTicketUrl, OnLoginListener onLoginListener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return login(webView, casTicketUrl);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                onLoginListener.onResult(result);
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public static boolean login(View webView, String casTicketUrl) {
        try {
            String response = null;

            String username = LoginUserInfo.getInstance().getLoginUserRealUserName(BaseApplicationLike.baseContext);
            String password = LoginUserInfo.getInstance().getLoginSecret(BaseApplicationLike.baseContext);

            String params = "username=" + URLEncoder.encode(username, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(casTicketUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            dos.write(params.getBytes());
            dos.flush();
            dos.close();

            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String tempResult = null;
            String result = null;

            while ((tempResult = br.readLine()) != null) {
                result = result + tempResult + "\n";
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 201) {
                response = result;

                LogUtil.e("response   ->   " + response);

                setCookies(webView, response);


            }else{
                response = connection.getResponseMessage();
                //System.out.println(response);
                LogUtil.e("response   ->   " + response);

            }




        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    private static void setCookies(View webView, String response) {
        Document doc = Jsoup.parseBodyFragment(response);
        Elements elms = doc.getElementsByTag("form");
        Element el = elms.get(0);
        String act = el.attr("action");
        String ticket = act.substring(act.indexOf("TGT"), act.length());
        String domain = ".kedachina.com.cn";

        String cookieVal = "CASTGC=" + ticket + "; Domain=" + domain;
        WebkitSdkUtil.setCookies(webView, domain + "/cas", cookieVal);
    }


    public interface OnLoginListener {
        void onResult(boolean success);
    }
}

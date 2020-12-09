package com.foreveross.atwork.infrastructure.utils.root;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 *
 * 代码来自
 * https://github.com/scottyab/rootbeer, https://blog.csdn.net/chenyuebo11/article/details/54913282
 *
 * */
public class RootSniffer {



    public static boolean isRoot(Context context) {

        long beginTime = System.currentTimeMillis();

        boolean detectRootCloakingApps = detectRootCloakingApps(context);
        boolean detectPotentiallyDangerousApps = detectPotentiallyDangerousApps(context);
        boolean detectRootManagementApps = detectRootManagementApps(context);
        boolean checkForRootReadablePaths = checkForRootReadablePaths();

        boolean isRoot = detectRootCloakingApps
                || detectPotentiallyDangerousApps
                || detectRootManagementApps
                || checkForRootReadablePaths;


        Logger.e("rootTag","RootUtil detectRootCloakingApps : " + detectRootCloakingApps
                +  "  detectPotentiallyDangerousApps: " + detectPotentiallyDangerousApps
                + " detectRootManagementApps:   " + detectRootManagementApps
                + " checkForRootReadablePaths:  " + checkForRootReadablePaths);

        LogUtil.e("RootUtil #isRoot(context) 执行时间  -> " + (System.currentTimeMillis() - beginTime));

        return isRoot;
    }




    /**
     * Using the PackageManager, check for a list of well known root cloak apps. @link {Const.knownRootAppsPackages}
     * and checks for native library read access
     * @return true if one of the apps it's installed
     */
    public static boolean detectRootCloakingApps(Context context) {
        return detectRootCloakingApps(context, null);
    }

    /**
     * Using the PackageManager, check for a list of well known root cloak apps. @link {Const.knownRootAppsPackages}
     * @param additionalRootCloakingApps - array of additional packagenames to search for
     * @return true if one of the apps it's installed
     */
    public static boolean detectRootCloakingApps(Context context, String[] additionalRootCloakingApps) {

        // Create a list of package names to iterate over from constants any others provided
        ArrayList<String> packages = new ArrayList<>();
        packages.addAll(Arrays.asList(Const.knownRootCloakingPackages));
        if (additionalRootCloakingApps!=null && additionalRootCloakingApps.length>0){
            packages.addAll(Arrays.asList(additionalRootCloakingApps));
        }
        return isAnyPackageFromListInstalled(context, packages);
    }


    /**
     * Using the PackageManager, check for a list of well known apps that require root. @link {Const.knownRootAppsPackages}
     * @return true if one of the apps it's installed
     */
    public static boolean detectPotentiallyDangerousApps(Context context) {
        return detectPotentiallyDangerousApps(context,null);
    }

    /**
     * Using the PackageManager, check for a list of well known apps that require root. @link {Const.knownRootAppsPackages}
     * @param additionalDangerousApps - array of additional packagenames to search for
     * @return true if one of the apps it's installed
     */
    public static boolean detectPotentiallyDangerousApps(Context context, String[] additionalDangerousApps) {

        // Create a list of package names to iterate over from constants any others provided
        ArrayList<String> packages = new ArrayList<>();
        packages.addAll(Arrays.asList(Const.knownDangerousAppsPackages));
        if (additionalDangerousApps!=null && additionalDangerousApps.length>0){
            packages.addAll(Arrays.asList(additionalDangerousApps));
        }

        return isAnyPackageFromListInstalled(context, packages);
    }


    /**
     * Using the PackageManager, check for a list of well known root apps. @link {Const.knownRootAppsPackages}
     * @return true if one of the apps it's installed
     */
    public static boolean detectRootManagementApps(Context context) {
        return detectRootManagementApps(context, null);
    }

    /**
     * Using the PackageManager, check for a list of well known root apps. @link {Const.knownRootAppsPackages}
     * @param additionalRootManagementApps - array of additional packagenames to search for
     * @return true if one of the apps it's installed
     */
    public static boolean detectRootManagementApps(Context context, String[] additionalRootManagementApps) {

        // Create a list of package names to iterate over from constants any others provided
        ArrayList<String> packages = new ArrayList<>();
        packages.addAll(Arrays.asList(Const.knownRootAppsPackages));
        if (additionalRootManagementApps!=null && additionalRootManagementApps.length>0){
            packages.addAll(Arrays.asList(additionalRootManagementApps));
        }

        return isAnyPackageFromListInstalled(context, packages);
    }


    /**
     * Check if any package in the list is installed
     * @param packages - list of packages to search for
     * @return true if any of the packages are installed
     */
    private static boolean isAnyPackageFromListInstalled(Context context, List<String> packages){
        boolean result = false;

        PackageManager pm = context.getPackageManager();

        for (String packageName : packages) {
            try {
                // Root app detected
                pm.getPackageInfo(packageName, 0);
                result = true;
            } catch (PackageManager.NameNotFoundException e) {
                // Exception thrown, package is not installed into the system
            }
        }

        return result;
    }



    public static boolean checkForRootReadablePaths(){
        try{
            for(int i = 0; i < Const.pathsThatShouldNotBeReadable.length; i++){
                String path = Const.pathsThatShouldNotBeReadable[i] + "su";
                if(new File(path).exists()){
                    String execResult = exec(new String[] { "ls", "-l", path });
                    if(TextUtils.isEmpty(execResult) || execResult.indexOf("root") == execResult.lastIndexOf("root")){
                        return false;
                    }
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private static String exec(String[] exec){
        String ret = "";
        ProcessBuilder processBuilder = new ProcessBuilder(exec);
        try {
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while( (line = bufferedReader.readLine()) != null){
                ret += line;
            }
            process.getInputStream().close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}

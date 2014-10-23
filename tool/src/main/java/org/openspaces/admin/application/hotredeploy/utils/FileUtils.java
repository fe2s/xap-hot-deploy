package org.openspaces.admin.application.hotredeploy.utils;

import java.io.*;

/**
 * @author Anna_Babich
 */

//This class should be substituted by apache-commons-io FileUtils

public class FileUtils {

    /**
     * Deletes directory with subdirs and subfolders
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                File f = new File(dir, aChildren);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

    public static void copyFolder(File src, File destination) throws IOException {
        if(src.isDirectory()){

            if(!destination.exists()){
                destination.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destinationFile = new File(destination, file);
                copyFolder(srcFile,destinationFile);
            }
        }else{
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }
}

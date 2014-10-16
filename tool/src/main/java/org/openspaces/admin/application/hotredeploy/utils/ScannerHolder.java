package org.openspaces.admin.application.hotredeploy.utils;

import java.util.Scanner;

/**
 * @author Anna_Babich
 */
public class ScannerHolder {
    private static Scanner sc;

    public static Scanner getScanner() {
        if (sc == null) {
            sc = new Scanner(System.in);
        }
        return sc;
    }
}

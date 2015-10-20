/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.pas.encdeczip.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import za.co.pas.encdeczip.EncDecZip;

/**
 * Generate password file
 * @author Andre Labuschagne
 */
public class GenPasswords {
    private static final Random random = new Random(System.nanoTime());
    
    private static final int MIN_PASSWORD_LENGHT = 40;
    private static final int MAX_PASSWORD_LENGHT = 80;
    /**
     * Create a file full of passwords
     * @param filePath 
     */
    public static void Gen(String filePath) {
        int[] charList = new int[95];
        StringBuilder[] passwords = new StringBuilder[62];
        for(int i = 32; i < 127; i++) {
            charList[i - 32] = i;            
        }
        for(int i = 0; i < passwords.length; i++) {
            int passwordLength = random.nextInt(MAX_PASSWORD_LENGHT - MIN_PASSWORD_LENGHT) + MIN_PASSWORD_LENGHT;
            passwords[i] = new StringBuilder();
            for(int j = 0; j < passwordLength; j++) {
                int ui = charList[random.nextInt(charList.length)];
                if(ui < 32) {
                    System.out.println("ui is less than 32: " + Integer.toString(ui));
                }
                String u = Integer.toHexString(ui).toUpperCase();
                passwords[i].append("\\u00");
                if(u.length() == 1) {
                    passwords[i].append("0");
                }
                passwords[i].append(u);
            }
        }        
        
        File file = new File(filePath);
        if(file.isDirectory()) {
            StringBuilder sbFP = new StringBuilder(filePath);
            if(!sbFP.toString().endsWith(File.separator)) {
                sbFP.append(File.separator);
            }
            sbFP.append("Passwords.txt");
            file = new File(sbFP.toString());
        }
        try (FileOutputStream fos = new FileOutputStream(file);) {
            for(StringBuilder password : passwords) {
                fos.write(password.toString().getBytes("UTF-8"));
                fos.write("\n".getBytes("UTF-8"));
            }
            if(EncDecZip.LOG.isLoggable(Level.INFO)) {
                StringBuilder sbLog = new StringBuilder("Wrote password file: ");
                sbLog.append(file.getAbsolutePath());
                EncDecZip.LOG.log(Level.INFO, sbLog.toString());
            }
        } catch(IOException e) {
            EncDecZip.LOG.log(Level.SEVERE, "Error building password file", e);
        }
    }
}

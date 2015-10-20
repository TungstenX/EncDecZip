/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.pas.encdeczip.exception;

import java.util.logging.Level;


/**
 *
 * @author Andre Labuschagne
 */
public class ProcessingException extends Exception{
    private Level logingLevel = Level.SEVERE;
    private boolean printUsage = false;
    
    public ProcessingException(Level logingLevel, String msg) {
        super(msg);
        this.logingLevel = logingLevel;
    }
    public ProcessingException(Level logingLevel, String msg, boolean printUsage) {
        super(msg);
        this.logingLevel = logingLevel;
        this.printUsage = printUsage;
    }

    /**
     * @return the logingLevel
     */
    public Level getLogingLevel() {
        return logingLevel;
    }

    /**
     * @param logingLevel the logingLevel to set
     */
    public void setLogingLevel(Level logingLevel) {
        this.logingLevel = logingLevel;
    }

    /**
     * @return the printUsage
     */
    public boolean isPrintUsage() {
        return printUsage;
    }

    /**
     * @param printUsage the printUsage to set
     */
    public void setPrintUsage(boolean printUsage) {
        this.printUsage = printUsage;
    }
    
}

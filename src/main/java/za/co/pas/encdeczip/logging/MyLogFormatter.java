/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.pas.encdeczip.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author Andre Labuschagne
 */
public class MyLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(record.getLevel()).append(":\t").append(record.getMessage()).append("\n");
        if(record.getThrown() != null) {
            sb.append("\t\t").append(record.getThrown().getMessage()).append("\n");
        }
        if(record.getParameters() != null) {
            int i = 0;
            for(Object o : record.getParameters()) {
                String needle = "{" + i + "}";
                int pos = sb.indexOf(needle);
                if(pos != -1) {
                    sb.delete(pos, pos + needle.length());
                    sb.insert(pos, o.toString());
                }
                i++;
            }
        }
        return  sb.toString();
    }
}

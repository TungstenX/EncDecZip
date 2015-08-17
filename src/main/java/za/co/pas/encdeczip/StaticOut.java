/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.pas.encdeczip;

import java.util.logging.Level;

/**
 *
 * @author Andre Labuschagne
 */
public class StaticOut {
    
    
    private static void addVersion(String ver, StringBuilder sbInOut, int startOffSet, int end) {
        for(int i = 0; i < startOffSet; i++) {
            sbInOut.append(" ");
        }
        sbInOut.append("|");
        
        int startVer = end - 2 - ver.length();
        if((startVer > 0) && (startVer >= startOffSet + 1)) {
            for(int i = startOffSet + 1; i < startVer - 1; i++) {
                sbInOut.append(" ");
            }
        }
        sbInOut.append("v").append(ver).append(" |\n");        
    }
    
    public static void PrintStart(String version) {
        if(EncDecZip.LOG.isLoggable(Level.INFO)) {
            StringBuilder sb = new StringBuilder("\n");
            sb.append("                                ____  ____  ____    \n");  
            sb.append("                               ( ___)(  _ \\(_   )   \n");  
            sb.append("                             _--)__)--)(_) )/ /_---+\n"); 
            sb.append("                            |  (____)(____/(____)  |\n");  
            sb.append("                            |                      |\n");        
            sb.append("                            |       \\~~~~~~/       |\n");       
            sb.append("                            |        )    (        |\n");       
            sb.append("                            |         |  |         |\n");
            sb.append("                            |         |  |         |\n");
            sb.append("                            |         |  |         |\n");
            sb.append("                            |     ____+  +____     |\n");
            sb.append("                            |    /            \\    |\n");
            sb.append("                            |   /   EncDecZip  \\   |\n");
            sb.append("                            |   +---___  ___---+   |\n");
            sb.append("                            |          \\/          |\n");
            addVersion(version, sb, 28, 52);
            sb.append("                            +----------------------'\n");
            //         0123456789012345678901234567890123456789012345678901
            EncDecZip.LOG.log(Level.INFO, sb.toString());
        }
    }
    
       
    public static void PrintUsage(String version) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("              ____  _  _  ___  ____  ____  ___  ____  ____  ____               \n");
        sb.append("             ( ___)( \\( )/ __)(  _ \\( ___)/ __)(_   )(_  _)(  _ \\              \n");
        sb.append(" _------------)__)--)  (( (__--)(_) ))__)( (__--/ /_--_)(_--)___/--------------+\n");
        sb.append("|            (____)(_)\\_)\\___)(____/(____)\\___)(____)(____)(__)                |\n");
        sb.append("| Usage:                                                                       |\n");
        sb.append("|   java -jar EncDecZip [-z | -u] [-l level] [-h]                              |\n");  
        sb.append("|                       [-p password | -pf passwordFile | -pg] [-in path]      |\n"); 
        sb.append("|                       -out path                                              |\n");
        sb.append("|                                                                              |\n");
        sb.append("|   -z              : Zip the file or directory, not needed for -pg            |\n");
        sb.append("|   -u              : Unzip the file or directory, not needed for -pg          |\n");
        sb.append("|   -l level        : Log / output level: ERROR, WARNING, INFO, VERBOSE        |\n");
        sb.append("|   -h              : Help                                                     |\n");
        sb.append("|   -p password     : The password to encrypt the file                         |\n");
        sb.append("|   -pf passwordFile: The path to the password file                            |\n");
        sb.append("|   -pg             : Generate a password file                                 |\n");
        sb.append("|   -in path        : The input file or directory, needed for zip or unzip     |\n");  
        sb.append("|   -out path       : [MANDATORY] The out put file or directory                |\n");
        addVersion(version, sb, 0, 80);
        sb.append("|                                                                              |\n");
        sb.append("+------------------------------------------------------------------------------'");
        
        EncDecZip.LOG.log(Level.WARNING, sb.toString());
    }
    
    public static void PrintHelp(String version) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("              ____  _  _  ___  ____  ____  ___  ____  ____  ____               \n");
        sb.append("             ( ___)( \\( )/ __)(  _ \\( ___)/ __)(_   )(_  _)(  _ \\              \n");
        sb.append(" _------------)__)--)  (( (__--)(_) ))__)( (__--/ /_--_)(_--)___/--------------+\n");
        sb.append("|            (____)(_)\\_)\\___)(____/(____)\\___)(____)(____)(__)                |\n");
        sb.append("| Help:                                                                        |\n");
        sb.append("| Usage:                                                                       |\n");
        sb.append("|   java -jar EncDecZip [-z | -u] [-l level] [-h]                              |\n");  
        sb.append("|                       [-p password | -pf passwordFile | -pg] [-in path]      |\n"); 
        sb.append("|                       -out path                                              |\n");
        sb.append("|                                                                              |\n");
        sb.append("|   -z              : Zip the file or directory, not needed for -pg            |\n");
        sb.append("|   -u              : Unzip the file or directory, not needed for -pg          |\n");
        sb.append("|   -l level        : Log / output level:                                      |\n");
        sb.append("|                       ERROR:   Log out only program error events             |\n");
        sb.append("|                       WARNING: Log out ERROR and user related warnings       |\n");
        sb.append("|                       INFO:    Log out ERROR, WARNING and informational      |\n");
        sb.append("|                                messages - this is the default logging level  |\n");
        sb.append("|                       VERBOSE: Log out all errors, warnings, info and much   |\n");
        sb.append("|                                more                                          |\n");
        sb.append("|   -h              : Help, this information o.O                               |\n");
        sb.append("|   -p password     : The password to encrypt the file, cannot contain spaces. |\n");
        sb.append("|                     Consider only using standard characters to prevent       |\n");
        sb.append("|                     platform issues                                          |\n");
        sb.append("|   -pf passwordFile: The path to the password file (including the file name)  |\n");
        sb.append("|   -pg             : Generate a password file to the output path              |\n");
        sb.append("|   -in path        : The input file or directory, needed for zip or unzip     |\n");  
        sb.append("|                       Zipping:   If the path is directory then all files and |\n"); 
        sb.append("|                                  subdirectories will be included reclusively |\n"); 
        sb.append("|                                  Wildcard characters * or ? can be used but  |\n"); 
        sb.append("|                                  subdirectories will not be traverse.        |\n");
        sb.append("|                                  Put path in \"\" when usinf wildcards         |\n");         
        sb.append("|                       Unzipping: It must be a path to a file.  The file name |\n");        
        sb.append("|                                  must start with '_'                         |\n");        
        sb.append("|   -out path       : [MANDATORY] The output file or directory.                |\n");
        sb.append("|                       Password Gen: If path is to a directory then the file  |\n");
        sb.append("|                                     'Passwords.txt' will be created in that  |\n");
        sb.append("|                                     directory                                |\n");
        sb.append("|                       Zipping:      If path is to a directory then the zip   |\n");
        sb.append("|                                     file name will be the Unix time stamp,   |\n");
        sb.append("|                                     in Hex. If the path includes a file name |\n");
        sb.append("|                                     then a sequence starting with '_' will   |\n");
        sb.append("|                                     added to the start of the file name      |\n");
        sb.append("|                       Unzipping:    The path must be a directory             |\n");
        sb.append("|                                                                              |\n");
        sb.append("|   Thank you for choosing EncDecZip, we hope you enjoy it.                    |\n");
        sb.append("|                                                    www.ParanoidAndroid.co.za |\n");
        sb.append("|                                      source @ github.com/TungstenX/EncDecZip |\n");
        addVersion(version, sb, 0, 80);
        sb.append("|                                                                              |\n");
        sb.append("+------------------------------------------------------------------------------'");

        EncDecZip.LOG.log(Level.INFO, sb.toString());
    }    
}
//Character ruler \m/ \m/
//          1         2         3         4         5         6         7         8
//012345678901234567890123456789012345678901234567890123456789012345678901234567890

/**
.(….\…………../….)
. \….\……….. /…./
…\….\………./…./
….\…./´¯.I.¯`\./
…./… I….I..(¯¯¯`\
…I…..I….I…¯¯.\…\
…I…..I´¯.I´¯.I..\…)
…\…..` ¯..¯ ´…….’
….\_________.?´
…..lo o o o o ol
…..lo o o o o ol
…..lo o o o o o|
 */
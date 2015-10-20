package za.co.pas.encdeczip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import za.co.pas.encdeczip.exception.ProcessingException;
import za.co.pas.encdeczip.gen.GenPasswords;
import za.co.pas.encdeczip.logging.MyLogFormatter;

/**
 * Very simple zip and unzip utility, not rocket-surgery, not even much OO 
 * Using Zip4j
 * @author Andre Labuschagne
 * @see <a href="http://www.lingala.net/zip4j/index.php">Lingala&#39;s Zip4j</a>
 */
public class EncDecZip {
    public static final Logger LOG = Logger.getLogger(EncDecZip.class.getName());
    private static final Random random = new Random(System.nanoTime());
    private static final Map<String, Level> LOG_LEVEL = new TreeMap<>();
    private static final ConsoleHandler LOG_HANDLER = new ConsoleHandler();
    private static final String REGEX_WILDCARDS = "[?|*]";
    private static final Pattern REGEX_PATTERN = Pattern.compile(REGEX_WILDCARDS);
    static enum ACTION { NONE, ZIP, UNZIP};
    static enum OPTION { PASSWORD_GEN, PASSWORD, PASSWORD_FILE};
    
    static {
        LOG_LEVEL.put("ERROR", Level.SEVERE);
        LOG_LEVEL.put("WARNING", Level.WARNING);
        LOG_LEVEL.put("INFO", Level.INFO);
        LOG_LEVEL.put("VERBOSE", Level.FINE);
        
        Handler[] handlers = LOG.getHandlers();
        for(Handler handler : handlers) {
            LOG.removeHandler(handler);
        }
        try {
            LOG_HANDLER.setEncoding("UTF-8");
        } catch(SecurityException | UnsupportedEncodingException e) {
            LOG.log(Level.SEVERE, "Error while setting up logging encoding", e);
        }
        LOG_HANDLER.setFormatter(new MyLogFormatter());
        LOG.addHandler(LOG_HANDLER);
        LOG.setUseParentHandlers(false);
    }
    
    /**
     * Main entry point
     * @param args 
     */
    public static void main(String[] args) {
        EncDecZip edz = new EncDecZip();
        try{
            edz.process(args);
        }catch(ProcessingException e) {
            LOG.log(e.getLogingLevel(), e.getMessage());
            if(e.isPrintUsage()) {
                StaticOut.PrintUsage(edz.getClass().getPackage().getImplementationVersion());
            }
            if((e.getLogingLevel() == Level.SEVERE) || (e.getLogingLevel() == Level.WARNING)) {
                System.exit(-1);
            }
        }
    }
    
    public EncDecZip(){}
    
    public void process(String[] args) throws ProcessingException {
        if(args.length == 0) {
            throw new ProcessingException(Level.WARNING, "No parameters supplied", true);            
        }
        
        ACTION action = ACTION.NONE;
        OPTION option = null;
        String inFilePath = null;
        String outFilePath = null;
        String password = null;
        int size = args.length;
        int index  = 0;
        for(int i = 0; i < size; i++) {
            if(args[i].startsWith("-")) {
                switch(args[i]) {
                    case "-h":
                        StaticOut.PrintHelp(getClass().getPackage().getImplementationVersion());
                        return;
                    case "-u":
                        action = ACTION.UNZIP;
                        break;                    
                    case "-z":
                        action = ACTION.ZIP;
                        break;                    
                    case "-pg":
                        option = OPTION.PASSWORD_GEN;
                        break;
                    case "-l":
                        i++;
                        if(i < args.length) {
                            if(LOG_LEVEL.containsKey(args[i].toUpperCase())) {
                               LOG.setLevel(LOG_LEVEL.get(args[i].toUpperCase()));
                               LOG_HANDLER.setLevel(LOG_LEVEL.get(args[i].toUpperCase()));
                            } else {
                                LOG.log(Level.INFO, "Level value must be one of: ERROR, WARNING, INFO or VERBOSE");
                            }                            
                        } else {
                            throw new ProcessingException(Level.WARNING, "Need a level value", true);
                        }
                        break;
                    case "-p":
                        option = OPTION.PASSWORD;
                        i++;
                        if(i < args.length) {
                            password = args[i]; 
                        } else {
                            throw new ProcessingException(Level.WARNING, "Need a password", true);
                        }
                        break;
                    case "-pf":
                        option = OPTION.PASSWORD_FILE;
                        i++;
                        if(i < args.length) {
                            password = args[i]; 
                        } else {
                            throw new ProcessingException(Level.WARNING, "Need a path to the password file", true);
                        }
                        break;
                    default: 
                        throw new ProcessingException(Level.WARNING, "Unknown parameter: " + args[i], true);
                }
            } else {
                //This is the in and out file/directory now
                if(inFilePath == null) {
                    inFilePath = args[i];
                } else if(outFilePath == null) {
                    outFilePath = args[i];                     
                }else {
                    throw new ProcessingException(Level.WARNING, "Need in and out paths already set", true);
                }
            }
        }
        
        if(StringUtils.isBlank(inFilePath)) {
            throw new ProcessingException(Level.WARNING, "Needs an out file/folder path", true);
        }
        
        StaticOut.PrintStart(getClass().getPackage().getImplementationVersion());        
        switch(action) {
            case NONE:
                if(LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "About to generate password file");
                }
                if(option == OPTION.PASSWORD_GEN) {
                    GenPasswords.Gen(inFilePath);
                } else {
                    throw new ProcessingException(Level.WARNING, "No action specified, i.e. -z or -u", true);
                }                
                break;
            case ZIP:
                if(LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "About to add files to zip file");
                }
                try{
                    addFilesWithAESEncryption(option == OPTION.PASSWORD, 
                                              password,
                                              inFilePath,
                                              outFilePath);
                } catch(Exception e) {
                    throw new ProcessingException(Level.SEVERE, e.getMessage());
                }
                break;
            case UNZIP:                
                if(LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "About to extract files from zip file");
                }
                try{
                    extractAllFiles(option == OPTION.PASSWORD, password, 
                                    inFilePath,
                                    outFilePath);
                } catch(Exception e) {
                    throw new ProcessingException(Level.SEVERE, e.getMessage());
                }
                break;
        }
        
        
        if(LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "o.O");
        }
    }
    
    /**
     * Get the character associated with the password index. 
     * 0 to 9 is '0' to '9'
     * 10 to 35 is 'A' to 'Z'
     * 36 to 61 is 'a' to 'z'
     * @param index
     * @return the associated character
     */
    private char getPasswordIndexAsLetter(int index) {
        if(index <= 9) { //0-9
            return (char)(index + 48);
        } else if((index >= 10) && (index <= 35)) { //A-Z
            return (char)(index - 10 + 65);
        } else if((index >= 36) && (index <= 61)) { //a-z
            return (char)(index - 36 + 97);
        }
        return '_';
    }
    
    /**
     * Get the password index associated with the character. 
     * '0' to '9' is 0 to 9
     * 'A' to 'Z' is 10 to 35
     * 'a' to 'z' is 36 to 61
     * @param c character 0-9, A-Z or a-z
     * @return the password index, between 0 and 61
     */
    private int getPasswordIndexFromLetter(char c) {
        if((c >= '0') && (c <= '9')) { //0-9
            return (int)(c - 48);
        } else if((c >= 'A') && (c <= 'Z')) { //10-35
            return (int)(c - 65 + 10);
        } else if((c >= 'a') && (c <= 'z')) { //36-61
            return (int)(c + 36 - 97);
        }
        return -1;
    }
        
    /**
     * Get the password from the password file
     * @param index is the password index
     * @param file the password file
     * @return an array of characters that is the password, will not be null
     */
    private char[] getPassword(int index, File file) throws IOException {        
        List<String> passwords = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                passwords.add(sCurrentLine);
            }
        } 
        if(!passwords.isEmpty() && (index < passwords.size())) {
            if(LOG.isLoggable(Level.FINE)) {
                StringBuilder sbLog = new StringBuilder("The password is ");
                sbLog.append(passwords.get(index).length()).append(" characters long");
                LOG.log(Level.FINE, sbLog.toString());
            }            
            return StringEscapeUtils.unescapeJava(passwords.get(index)).toCharArray();
        } else {
            throw new ArrayIndexOutOfBoundsException("Index problem with password file");
        }        
    }
    
    /**
     * An inner class for data management
     */
    private class FileData {
        Map<String, File> fileList = new TreeMap<>();
        private long currentIndex = 0L;
    }
    
    /**
     * Makes a file name using a long as input
     * @param l
     * @return the a file name in the dos 8.3 format 
     */
    private String makeFileName(long l) {
        StringBuilder sb = new StringBuilder(Long.toHexString(l));
        while(sb.length() < 11) {
            sb.insert(0, "0");
        }
        //put the .
        sb.insert(8, ".");
        if(LOG.isLoggable(Level.FINE)) {
            StringBuilder sbLog = new StringBuilder("The new file name is ");
            sbLog.append(sb.toString().toUpperCase());
            LOG.log(Level.FINE, sbLog.toString());
        }
        return sb.toString().toUpperCase();
    }
    
    /**
     * Get all the files that needs to be zipped
     * @param fileData
     * @param inputPath
     * @return 
     */
    private FileData prepareFilesToBeZipped(FileData fileData, String inputPath) {
        if(LOG.isLoggable(Level.FINE)) {
            StringBuilder sbLog = new StringBuilder("Path: ");
            sbLog.append(inputPath);
            LOG.log(Level.FINE, sbLog.toString());
        }
        
        if(fileData == null) {
            fileData = new FileData();
        }
        File file = new File(inputPath);
        if(file.isDirectory()) {           
            for (final File fileEntry : file.listFiles()) {
                if (fileEntry.isDirectory()) {
                    prepareFilesToBeZipped(fileData, fileEntry.getPath());
                } else {
                    if(LOG.isLoggable(Level.FINE)) {
                        StringBuilder sbLog = new StringBuilder("\tAdding: ");
                        sbLog.append(fileEntry.getPath());
                        LOG.log(Level.FINE, sbLog.toString());
                    }
                    fileData.fileList.put(makeFileName(fileData.currentIndex++), fileEntry);
                }
            }
        } else if(file.isFile()) {
            if(LOG.isLoggable(Level.FINE)) {
                StringBuilder sbLog = new StringBuilder("\tAdding: ");
                sbLog.append(file.getPath());
                LOG.log(Level.FINE, sbLog.toString());
            }
            fileData.fileList.put(makeFileName(fileData.currentIndex++), file);
        } else {
            if(LOG.isLoggable(Level.WARNING)) {
                StringBuilder sbLog = new StringBuilder("Path is neither a file nor a directory: ");
                sbLog.append(inputPath);
                LOG.log(Level.WARNING, sbLog.toString());
            }
        }        
        return fileData;
    } 
    
    private FileData prepareWildcardFilesToBeZipped(FileData fileData, String inputPath) {
        if(LOG.isLoggable(Level.FINE)) {
            StringBuilder sbLog = new StringBuilder("Wild card search path: ");
            sbLog.append(inputPath);
            LOG.log(Level.FINE, sbLog.toString());
        }
        
        if(fileData == null) {
            fileData = new FileData();
        }
        //Split path and file name
        File file = new File(inputPath);
        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator) + 1);
        String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(File.separator) + 1);
        if(LOG.isLoggable(Level.FINE)) {
            StringBuilder sbLog = new StringBuilder("Path part: ");
            sbLog.append(path).append(" File part: ").append(fileName);
            LOG.log(Level.FINE, sbLog.toString());
        }
        File dir = new File(path);
        FileFilter fileFilter = new WildcardFileFilter(fileName);
        File[] files = dir.listFiles(fileFilter);
        for(File tmpFile : files) {
            if(!tmpFile.isDirectory()) {
                if(LOG.isLoggable(Level.FINE)){
                    StringBuilder sbLog = new StringBuilder("Adding: ");
                    sbLog.append(tmpFile.getPath());
                    LOG.log(Level.FINE, sbLog.toString());
                }
                fileData.fileList.put(makeFileName(fileData.currentIndex++), tmpFile);
            } else if(LOG.isLoggable(Level.FINE)) {
                StringBuilder sbLog = new StringBuilder("Skipping directory: ");
                sbLog.append(tmpFile.getPath());
                LOG.log(Level.FINE, sbLog.toString());
            }
        }
        return fileData;        
    } 
    
    /**
     * Creates a zip file name
     * @param usePassword
     * @param passwordOrPath
     * @param zipName
     * @param zipFilePathOut
     * @return
     * @throws IOException, FileNotFoundException 
     */
    private char[] makeZipFileName(boolean usePassword, 
                                   String passwordOrPath, 
                                   String zipName, 
                                   StringBuilder zipFilePathOut) throws IOException, FileNotFoundException {
        //Create the zip file name
           
        StringBuilder zipFilePath = new StringBuilder();
        StringBuilder zipFileName = new StringBuilder();
        File temp = new File(zipName);
        //Spilt the file path and name
        if(temp.isDirectory()) {
            //This is just a path
            zipFilePath.append(temp.getAbsolutePath());
            //Create file name
            zipFileName.append(Long.toHexString(System.currentTimeMillis()).toUpperCase()).append(".zip");
            if(LOG.isLoggable(Level.FINE)) {
                StringBuilder sbLog = new StringBuilder("Received a directory path [");
                sbLog.append(zipFilePath).append("] as an output, creating file...");
                LOG.log(Level.FINE, sbLog.toString());
            }
        } else {
            //This is a file name, possible with a path, relative or absolute
            if(temp.getParentFile() != null) {
                //It seems to be a path and file name set up, e.g. abc/def.txt
                zipFilePath.append(temp.getParentFile().getAbsolutePath());
                
                if(LOG.isLoggable(Level.FINE)) {
                    StringBuilder sbLog = new StringBuilder("Received a file path as an output, directory path is [");
                    sbLog.append(zipFilePath).append("]");
                    LOG.log(Level.FINE, sbLog.toString());
                }
            } else {
                int pos = temp.getAbsolutePath().lastIndexOf(File.separator);
                if(pos == -1) {
                    throw new FileNotFoundException("Error in zip file path: " + temp.getAbsolutePath());
                }
                zipFilePath.append(temp.getAbsolutePath().substring(0, pos + 1));
                
                if(LOG.isLoggable(Level.FINE)) {
                    StringBuilder sbLog = new StringBuilder("Received a file path as an output, directory path is [");
                    sbLog.append(zipFilePath).append("]");
                    LOG.log(Level.FINE, sbLog.toString());
                }
            }
            
            zipFileName.append(temp.getPath());                
            if(zipFilePath.toString().endsWith(".")) {
                zipFilePath.deleteCharAt(zipFilePath.length() - 1);
            }
        }
        if(!zipFilePath.toString().endsWith(File.separator)) {
            zipFilePath.append(File.separator);
        }
        
        zipFileName.insert(0, "_");//the second '_'
        //Do the password
        char[] password = null;
        if(usePassword) {
            password = passwordOrPath.toCharArray();
            if(LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Using password");
            }
        } else {
            File passwordFile = new File(passwordOrPath);
             if(LOG.isLoggable(Level.FINE)) {
                StringBuilder sbLog = new StringBuilder("Using password file: ");
                sbLog.append(passwordFile.getAbsolutePath());
                LOG.log(Level.FINE, sbLog.toString());
            }
            if(passwordFile.exists()) {
                int passwordIndex = random.nextInt(62);
                password = getPassword(passwordIndex, passwordFile);
                zipFileName.insert(0, getPasswordIndexAsLetter(passwordIndex));//the password index character
            } else {
                throw new FileNotFoundException("Could not find password file: " + passwordFile.getAbsolutePath());
            }
        }            
        zipFileName.insert(0, "_");//the first '_'
            
        zipFilePathOut.append(zipFilePath).append(zipFileName);
        
        if(LOG.isLoggable(Level.INFO)) {
            StringBuilder sbLog = new StringBuilder("Zip file path: ");
            sbLog.append(zipFilePathOut);
            LOG.log(Level.INFO, sbLog.toString());
        }
        if(LOG.isLoggable(Level.FINE)) {
            StringBuilder sbLog = new StringBuilder("Password lenght: ");
            sbLog.append(password.length);
            LOG.log(Level.FINE, sbLog.toString());
        }
        return password;
    }
    
    /**
     * Trim the relative file path by removing .'s and /'s
     * @param filePath
     * @return 
     */
    private String trimFilePath(String baseFilePath, String filePath){
        File base;
        if(baseFilePath.matches(REGEX_WILDCARDS)) {
            // ..\Test*.txt
            base = new File(baseFilePath);
            String fileName = base.getAbsolutePath().substring(base.getAbsolutePath().lastIndexOf(File.separator) + 1);
            String strBase = base.getPath().substring(0, base.getPath().length() - fileName.length());            
            // ..\
            base = new File(strBase);
        } else {
            base = new File(baseFilePath);
        }
        if(base.isDirectory()) {
            //subtract the baseFilePath from the filePath
            if(baseFilePath.equals(".")) {
                //Special case, . will be ./
                return filePath.substring(2);
            } else {
                return filePath.substring(baseFilePath.length());
            }
        } else { //it is a file
            //still need to remove any absolute or relavive paths
            Path path = Paths.get(filePath);
            //Return just the file name, thus e.g.  ../zyx/abc.def -> abc.def 
            return path.getFileName().toString();
        }
    }
    
    /**
     * Add the file(s) to the zip file
     * @param usePassword
     * @param passwordOrPath
     * @param inFilePath
     * @param zipName
     * @throws FileNotFoundException 
     */
    @SuppressWarnings("unchecked") 
    private void addFilesWithAESEncryption(boolean usePassword, 
                                          String passwordOrPath, 
                                          String inFilePath, 
                                          String zipName) throws FileNotFoundException {
        try {
            long addTimeStart = System.currentTimeMillis();
            if(LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Creating zip file");
            }
            StringBuilder zipFilePath = new StringBuilder();
            char[] password = makeZipFileName(usePassword, passwordOrPath, zipName, zipFilePath);
            // Initiate ZipFile object with the path/name of the zip file.
            ZipFile zipFile = new ZipFile(zipFilePath.toString());

            // Build the list of files to be added in the array list
            // Objects of type File have to be added to the ArrayList
            // Copy files
            if(System.getProperty("java.io.tmpdir") == null) {
                throw new FileNotFoundException("No temp directory found, set up your OS' temp directory or run with -Djava.io.tmpdir=");
            }
            //Add the files
            //And the files mapping
            
            
            
            StringBuilder filesMapping = new StringBuilder();
            List<File> filesToAdd = new ArrayList();
            FileData fileData;
            if(REGEX_PATTERN.matcher(inFilePath).find()) {
                fileData = prepareWildcardFilesToBeZipped(null, inFilePath);
            } else {
                fileData = prepareFilesToBeZipped(null, inFilePath);                
            }
            if(LOG.isLoggable(Level.INFO)) {
                StringBuilder sbLog = new StringBuilder("Adding ");
                sbLog.append(fileData.fileList.keySet().size()).append(" file");
                if(fileData.fileList.keySet().isEmpty() || fileData.fileList.keySet().size() > 1) {
                    sbLog.append("s");
                }
                LOG.log(Level.INFO, sbLog.toString());
            }
            for(String newFileName : fileData.fileList.keySet()) {
                
                StringBuilder newFilePath = new StringBuilder(System.getProperty("java.io.tmpdir"));
                if(!newFilePath.toString().endsWith(File.separator)) {
                    newFilePath.append(File.separator);
                }
                newFilePath.append(newFileName);
                File newFile = new File(newFilePath.toString());
                FileUtils.copyFile(fileData.fileList.get(newFileName), newFile);
                filesToAdd.add(newFile);
                filesMapping.append(newFileName).append(":").append(trimFilePath(inFilePath, fileData.fileList.get(newFileName).getPath())).append("\n");
            }
            //Create map file and add it
            
            if(LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Creating mapping file");
            }
            StringBuilder mapFilePath = new StringBuilder(System.getProperty("java.io.tmpdir"));
            if(!mapFilePath.toString().endsWith(File.separator)) {
                mapFilePath.append(File.separator);
            }
            mapFilePath.append("ZzZzZzZz.ZzZ");
            File mapFile = new File(mapFilePath.toString()); 
            try (FileOutputStream fos = new FileOutputStream(mapFile);){
                fos.write(filesMapping.toString().getBytes("UTF-8"));
                fos.flush();
            } 
            filesToAdd.add(mapFile);
            
            // Initiate Zip Parameters which define various properties such
            // as compression method, etc. More parameters are explained in other
            // examples
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression

            // Set the compression level. This value has to be in between 0 to 9
            // Several predefined compression levels are available
            // DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
            // DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
            // DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
            // DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
            // DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            // Set the encryption flag to true
            // If this is set to false, then the rest of encryption properties are ignored
            parameters.setEncryptFiles(true);

            // Set the encryption method to AES Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            // Set AES Key strength. Key strengths available for AES encryption are:
            // AES_STRENGTH_128 - For both encryption and decryption
            // AES_STRENGTH_192 - For decryption only
            // AES_STRENGTH_256 - For both encryption and decryption
            // Key strength 192 cannot be used for encryption. But if a zip file already has a
            // file encrypted with key strength of 192, then Zip4j can decrypt this file
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

            // Set password
            parameters.setPassword(password);            

            // Now add files to the zip file
            // Note: To add a single file, the method addFile can be used
            // Note: If the zip file already exists and if this zip file is a split file
            // then this method throws an exception as Zip Format Specification does not 
            // allow updating split zip files
            
            if(LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Closing zip file, please wait...");
            }
            zipFile.addFiles((ArrayList<File>)filesToAdd, parameters);
            
            if(LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Cleaning up");
            }
            //Clean up temp files
            for(String newFileName : fileData.fileList.keySet()) {
                StringBuilder newFilePath = new StringBuilder(System.getProperty("java.io.tmpdir"));
                if(!newFilePath.toString().endsWith(File.separator)) {
                    newFilePath.append(File.separator);
                }
                newFilePath.append(newFileName);
                File newFile = new File(newFilePath.toString());
                FileUtils.forceDelete(newFile);
            }
            FileUtils.forceDelete(mapFile);
                        
            if(LOG.isLoggable(Level.INFO)) {
                long addTimeEnds = System.currentTimeMillis();
                StringBuilder sbLog = new StringBuilder("Zipping files took: ");
                sbLog.append(calcTimeLaps(addTimeEnds - addTimeStart));
                LOG.log(Level.INFO, sbLog.toString());
            }
        } catch(ZipException | IOException e) {
            LOG.log(Level.SEVERE, "Error while zipping files", e);
        }
    }
    
    /**
     * 
     * @param usePassword
     * @param passwordOrPath
     * @param zipFilePath
     * @param outPath
     * @throws FileNotFoundException 
     */
    private void extractAllFiles(boolean usePassword, 
                                 String passwordOrPath, 
                                 String zipFilePath, 
                                 String outPath) throws IOException, FileNotFoundException, MalformedURLException {
        try {
            long extraTimeStart = System.currentTimeMillis();
            if(LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Extracting file(s)...");
            }

            // Initiate ZipFile object with the path/name of the zip file.            
            File temp = new File(zipFilePath);            
            if(!temp.exists()) {
                throw new FileNotFoundException("File: " + zipFilePath + " not found.");
            } else if(temp.isDirectory()) {
                throw new FileNotFoundException("Please supply a file not a directory");
            } else if(System.getProperty("java.io.tmpdir") == null) {
                throw new FileNotFoundException("No temp directory found, set up your OS' temp directory or run with -Djava.io.tmpdir=");
            }
            File temp2 = new File(outPath);
            if(temp2.isFile()) {
                throw new MalformedURLException("The output path must be a directory");
            }
            Path path = Paths.get(zipFilePath);
            String zipFileName = path.getFileName().toString();
            if(!zipFileName.startsWith("_")) {
                throw new MalformedURLException("Error in zip file name, the zip file name must start with '_'");
            }        
            if(!usePassword && (zipFileName.charAt(1) == '_')) {
                throw new IllegalArgumentException("Zip by using a password file.  Please supply path to password file");
            } else if(usePassword && (zipFileName.charAt(1) != '_')) {
                throw new IllegalArgumentException("Zip by using a password.  Please supply a password not path to password file");                
            }
            
            char[] password = null;
            if(usePassword) {
                password = passwordOrPath.toCharArray();
            } else {
                File passwordFile = new File(passwordOrPath);
                if(passwordFile.exists()) {
                    int passwordIndex = getPasswordIndexFromLetter(zipFileName.charAt(1));
                    password = getPassword(passwordIndex, passwordFile);
                } else {
                    throw new FileNotFoundException("Could not find password file: " + passwordOrPath);
                }
            }
            
            ZipFile zipFile = new ZipFile(zipFilePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            // Extracts all files to the path specified
            StringBuilder extractPath = new StringBuilder(System.getProperty("java.io.tmpdir"));
            if(!extractPath.toString().endsWith(File.separator)) {
                extractPath.append(File.separator);
            }
            
            zipFile.extractAll(extractPath.toString());	
            //Copy files
            //Create map file and add it
            StringBuilder mapFilePath = new StringBuilder(System.getProperty("java.io.tmpdir"));
            if(!mapFilePath.toString().endsWith(File.separator)) {
                mapFilePath.append(File.separator);
            }
            mapFilePath.append("ZzZzZzZz.ZzZ");
            File mapFile = new File(mapFilePath.toString()); 
            List<String> lines = new ArrayList<>();
            try(BufferedReader br = new BufferedReader(new FileReader(mapFile))){
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    lines.add(sCurrentLine);
                }
            } 
            StringBuilder tmp = new StringBuilder(System.getProperty("java.io.tmpdir"));
            if(!tmp.toString().endsWith(File.separator)) {
                tmp.append(File.separator);
            }
            String outFilePathBase = outPath.endsWith(File.separator) ? outPath : outPath + File.separator;
            for(String line : lines) {                
                String oldFileName = line.substring(0, 12);
                String newFilePath = line.substring(13);
                File oldFile = new File(tmp.toString() + oldFileName);
                File newFile = new File(outFilePathBase + newFilePath);
                FileUtils.copyFile(oldFile, newFile);
                try {
                    FileUtils.forceDelete(oldFile);
                } catch(IOException e) {
                    StringBuilder sbLog = new StringBuilder("Error while deleting file: ");
                    sbLog.append(oldFile.getAbsolutePath()).append(" : ").append(e);
                    LOG.log(Level.WARNING, sbLog.toString());                
                }
             
                if(LOG.isLoggable(Level.INFO)) {
                    StringBuilder sbLog = new StringBuilder("File: ");
                    sbLog.append(newFile.getAbsoluteFile());
                    LOG.log(Level.INFO, sbLog.toString());
                }
            }
            try {
                FileUtils.forceDelete(mapFile);
            } catch(IOException e) {
                StringBuilder sbLog = new StringBuilder("Error while deleting map file: ");
                sbLog.append(e);
                LOG.log(Level.WARNING, sbLog.toString());                
            }
            
            if(LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Cleaning up...");
            }
            File zipToBeDeleted = new File(zipFilePath);
            try{
                FileUtils.forceDelete(zipToBeDeleted);
            } catch(IOException e) {
                StringBuilder sbLog = new StringBuilder("Error while deleting orginal zip file: ");
                sbLog.append(e);
                LOG.log(Level.WARNING, sbLog.toString());                
            }
            
            if(LOG.isLoggable(Level.INFO)) {
                long extraTimeEnds = System.currentTimeMillis();
                StringBuilder sbLog = new StringBuilder("... Extraction took: ");
                sbLog.append(calcTimeLaps(extraTimeEnds - extraTimeStart));
                LOG.log(Level.INFO, sbLog.toString());
            }
        } catch (ZipException e) {
            LOG.log(Level.SEVERE, "Error while extrating files", e);
        }	
    }
    
    private String calcTimeLaps(long msLeft) {
        Period period = new Period(msLeft);
        PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                                                .appendYears()
                                                .appendSuffix(" year", " years")
                                                .appendSeparator(", ")
                                                .appendMonths()
                                                .appendSuffix(" month", " months")
                                                .appendSeparator(", ")
                                                .appendWeeks()
                                                .appendSuffix(" week", " weeks")
                                                .appendSeparator(", ")
                                                .appendDays()
                                                .appendSuffix(" day", " days")
                                                .appendSeparator(" and ")
                                                .appendHours()
                                                .appendSuffix(" hour", " hours")
                                                .appendSeparator(", ")
                                                .appendMinutes()
                                                .appendSuffix(" minute", " minutes")
                                                .appendSeparator(", ")
                                                .appendSeconds()
                                                .appendSuffix(" second", " seconds")
                                                .appendSeparator(", ")
                                                .appendMillis()
                                                .appendSuffix(" millisecond", " milliseconds")
                                                .toFormatter();
        String ret = daysHoursMinutes.print(period.normalizedStandard());        
        return ret;
    }
}

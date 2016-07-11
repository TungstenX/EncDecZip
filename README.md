# EncDecZip
Encrypt Decrypt Zip File Tool.
---
This tool allows you to create an encrypted zip file, using the **-z** option, it does not encrypt already created zip files (although you can supply your zip file as an input source file and create a new encrypted zip file). It also allows you to decrypt a file, using the **-u** option, that was encrypted *by this tool*.  You can supply a password, using the **-p** option, or a password file, using the **-pf** option, for both encrypting and decrypting.  

You should use the same password mechanism to decrypt the zip file that was used to encrypt is, e.g. if you used **-p** to encrypt the file then you *must* use **-p** to decrypt the file.

A password file can be generated using the **-pg** option.

## Examples
|What                               |How|
|---|---|
| Generate a password file | java -jar EncDecZip -pg /path/to/save/pasword/file |
| Create encrypted zip file| java -jar EncDecZip -z -pf /path/to/pasword/file /path/to/input/dir/ /path/to/output/file.zip |
| Decrypt and unzip |  java -jar EncDecZip -u -pf /path/to/pasword/file /path/to/input/file.zip /path/to/output/dir/ |

## Usage
```	
              ____  _  _  ___  ____  ____  ___  ____  ____  ____               
             ( ___)( \( )/ __)(  _ \( ___)/ __)(_   )(_  _)(  _ \              
 _------------)__)--)  (( (__--)(_) ))__)( (__--/ /_--_)(_--)___/--------------+
|            (____)(_)\_)\___)(____/(____)\___)(____)(____)(__)                |
| Usage:                                                                       |
|   java -jar EncDecZip [-z | -u] [-l level] [-h] [-o text]                    |
|                       {-p password | -pf passwordFile | -pg} [inPath]        |
|                       <outPath>                                              |
|  Options:                                                                    |
|   -z              : Zip the file or directory, not needed for -pg            |
|   -u              : Unzip the file or directory, not needed for -pg          |
|   -l level        : Log / output level: ERROR, WARNING, INFO, VERBOSE        |
|   -h              : Help                                                     |
|   -o              : Output text before filename, e.g. http://download.from/  |
|   -p password     : The password to encrypt the file                         |
|   -pf passwordFile: The path to the password file                            |
|   -pg             : Generate a password file                                 |
|  Paths:                                                                      |
|   inPath          : The input file or directory, needed for zip or unzip     |
|   outPath         : MANDATORY - The out put file or directory                |
|                                                                       v0.1.7 |
|                                                                              |
+------------------------------------------------------------------------------'
```

## No Warranty
The tool is distributed in the hope that it will be useful, but without any warranty. It is provided "as is" without warranty of any kind, either expressed or implied, including, but not limited to, the implied warranties of fitness for a particular purpose. The entire risk as to the quality and performance of the program is with you. Should the program prove defective, you assume the cost of all necessary servicing, repair or correction. 
In no event unless required by applicable law the author will be liable to you for damages, including any general, special, incidental or consequential damages arising out of the use or inability to use the program (including but not limited to loss of data or data being rendered inaccurate or losses sustained by you or third parties or a failure of the program to operate with any other programs), even if the author has been advised of the possibility of such damages. 


### PS
There is no back-door, so, *sorry for you*, if you lose the password file or forget the password.

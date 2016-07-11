# EncDecZip
Encrypt Decrypt Zip File Tool
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


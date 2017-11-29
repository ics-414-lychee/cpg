# cpg - Critical Path Application for Team Lychee
### Quick Local Start (for Windows)

1. Pull or download this repo, and pull/download our server repo [here](https://github.com/ics-414-lychee/cpg-server).

2. Get your server side up and running.

   1. Download Microsoft SQL Server Management Studio, 7.0.25 / PHP 7.0.25 of XAMPP [here](https://www.apachefriends.org/download.html), and the SQL Server (SQLSRV40) drivers [here](https://www.microsoft.com/en-us/download/details.aspx?id=20098).

   2. Install Microsoft SQL Server Management Studio (SSMS) , and open it.

   3. Navigate to the location of our server repo, and go to MSSQLServer -> SingleSQLScriptSetup. Open "Script.sql" in SSMS. 

   4. Hit F5 to execute the script. This should create the LycheeActivityOnNode414 database.

   5. In the object explorer, add a new job: right click SQL Server Agent -> New -> Job.

      1. Under **General**, the name is MaintenanceAuthentication
      2. Go to **Steps**, and click **New...**
      3. The type should be **Transact -SQL script (T-SQL)** and the name is MaintenanceAuthentication.
      4. Select **LycheeActivityOnNode414** as the database, and enter `exec MaintenanceAuthentication_proc on LycheeActivityOnNode414 database` as the command.
      5. Click **Ok** to add.
      6. Click the **Schedules** tab, and click **New...**
      7. Give the schedule a name, and click **Jobs in Schedule **. Make sure **MaintenanceAuthentication** is selected.
      8. Under **Daily frequency**, enter **Occurs every: 30 minutes**.
      9. Click **Ok** to add.

   6. Click **Ok** to add the job. 

   7. Install the 7.0.25 / PHP 7.0.25 of XAMPP directly onto your C: drive.

   8. Start up the XAMPP control panel. Click **Start** next to the module **Apache**.

   9. Extract the SQL Server drivers to `C:\xampp\php\ext`.

   10. Navigate to the location of our server repo, and copy the folder PHPWebServer.

   11. Paste this folder in `C:\xampp\htdocs`.

   12. Go back to the XAMPP control panel, click **Config** -> **PHP**.

   13. Look for the lines: 

   ``` 
       ;extension=php_xmlrpc.dll
       ;extension=php_xsl.dll
   ```
   ​	and insert the following right under.

   ```
   extension=php_sqlsrv_7_ts_x86.dll
   extension=php_pdo_sqlsrv_7_ts_x86.dll
   ```

   14. In the XAMPP control panel, click **Stop** and then **Start** to load these drivers and files.

3. Run the client side. 

   1. Open up the command prompt and navigate to the client side repo -> out -> artifacts. 
   2. Enter `java -jar CriticalPathGrapher.jar`. 




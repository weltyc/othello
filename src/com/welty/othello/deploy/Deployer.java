package com.welty.othello.deploy;

import com.orbanova.common.os.Processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Deploys Ntest on a machine
 * <p/>
 * User: chris
 * Date: 7/13/11
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class Deployer {
    /**
     * Deploys Ntest on a machine.
     * <p/>
     * Creates nCores copies of Ntest at c:/dev/oth1...c:/dev/othN.
     * Each will have its run.bat run on startup; run.bat will merge the book located at mergeBookSource,
     * Edmundize the copy, and finally run ntest with arguments<br/>
     * <code>  d s30 1000 temp.ggf >> log.txt</code>.
     * <p/>
     * There must be an existing copy of ntest at othelloSourceDir; copies of this directory will be deployed.
     *
     * @param nCores           number of instances of ntest to deploy
     * @param othelloSourceDir Source of ntest to deploy, for instance <code>U:\dev\n64</code>
     * @param mergeBookSource  location of book to merge on startup - either a directory or a .book file. Example: <code>\\Buffalo\dev\oth1\src\resource\</code>
     * @throws Exception
     */
    static void fullDeploy(int nCores, File othelloSourceDir, File mergeBookSource) throws Exception {
        if (!mergeBookSource.toString().endsWith(".book")) {
            mergeBookSource = new File(mergeBookSource, "merge.book");
        }
        new File("c:/dev").mkdirs();
        for (int i = 1; i <= nCores; i++) {
            fullDeployInstance(i, mergeBookSource, othelloSourceDir);
        }
    }

    private static void fullDeployInstance(int i, File mergeBookSource, File othelloSourceDir) throws Exception {
        copyDirectory(i, othelloSourceDir);
        createStartupScript(i);
        deployRunScriptInstance(i, mergeBookSource);
    }

    /**
     * Copy the executable file from othelloSourceDir\o1.exe to all instances, renaming to o{instance number}.exe
     *
     * @param nCores           number of instances to deploy
     * @param othelloSourceDir directory containing o1.exe
     */
    public static void deployExe(int nCores, File othelloSourceDir) throws IOException {
        final Path sourceExe = othelloSourceDir.toPath().resolve("o1.exe");
        for (int i = 1; i <= nCores; i++) {
            final Path destExe = toDir(i).toPath().resolve("o" + i + ".exe");
            Files.copy(sourceExe, destExe, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Create the batch file 'run.bat' for each instance
     *
     * @param nCores          number of instances to create
     * @param mergeBookSource location of book to merge on startup - either a directory or a .book file. Example: <code>\\Buffalo\dev\oth1\src\resource\</code>
     * @throws IOException if unable to write to file
     */
    public static void deployRunScripts(int nCores, File mergeBookSource) throws IOException {
        for (int i=1; i<=nCores; i++) {
            deployRunScriptInstance(i, mergeBookSource);
        }
    }

    /**
     * Create the batch file 'run.bat' which will be run on startup
     *
     * @param i               instance number
     * @param mergeBookSource location of book to merge on startup - either a directory or a .book file. Example: <code>\\Buffalo\dev\oth1\src\resource\</code>
     * @throws IOException if unable to write to file
     */
    private static void deployRunScriptInstance(int i, File mergeBookSource) throws IOException {
        final File othDir = toDir(i);
        final File outFile = new File(othDir, "run.bat");
        System.out.println("deploying RunScript to " + outFile);
        PrintWriter run = new PrintWriter(new FileWriter(outFile));
        run.println("c:");
        run.println("cd " + othDir);

        // get assignments from assignmentSourceDir
        final String mergeBookDest = "src\\resource\\coefficients\\merge.book";
        run.println("del " + mergeBookDest);
        run.println("copy " + mergeBookSource + " " + mergeBookDest);
        run.println("o" + i + ".exe m s30 > log.txt");
        run.println("del " + mergeBookDest);
        run.println("o" + i + " e s30");
        run.println("o" + i + ".exe d s30 1000 temp.ggf >> log.txt");
        run.close();
    }

    /**
     * Create startup script that will execute run.bat on startup
     *
     * @param i startup instance
     * @throws Exception if startup location doesn't exist (as it won't for Windows XP - see comments).
     */
    private static void createStartupScript(int i) throws Exception {
        // make startup script
        // cd C:\dev\oth1
        // start /LOW wscript.exe "C:\dev\oth1\invisible.vbs" "C:\dev\oth1\run.bat"
        // This startup location is good for Windows 7 and Vista; XP would need to change "Users" to "Documents and Settings"
        final File startup = new File("C:\\Users\\chris\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
        if (!startup.exists()) {
            System.out.println("error accessing startup directory " + startup);
            throw new Exception("can't find startup directory : " + startup);
        }
        PrintWriter out = new PrintWriter(new FileWriter(new File(startup, "o" + i + ".bat")));
        final File othDir = toDir(i);
        out.println("cd " + othDir);
        final String q = "\"";
        final String invisibleBat = q + othDir + "\\invisible.vbs" + q;
        final String runBat = q + othDir + "\\run.bat" + q;
        out.println("start /LOW wscript.exe " + invisibleBat + " " + runBat);
        out.close();
    }

    /**
     * Make a copy of the source directory into c:/dev/oth$i
     *
     * @param i         number of copy
     * @param sourceDir location of files to copy
     * @throws Exception
     */
    private static void copyDirectory(int i, File sourceDir) throws Exception {
        File toDir = toDir(i);
        System.out.println("copying from " + sourceDir + " to " + toDir);
        toDir.mkdirs();
        final String logFileName = "/log:c:\\dev\\oth1\\deployLog.txt";
        final String[] args = {"robocopy", sourceDir.toString(), toDir.toString(), "/E", logFileName};
        final Processor processor = new Processor(args);
        System.out.println("return code : " + processor.waitFor());
        System.out.println("stdout: " + processor.outFeed().join("\n"));
        System.out.println("stderr: " + processor.errFeed().join("\n"));
        File exe = new File(toDir, "o1.exe");
        exe.renameTo(new File(toDir, "o" + i + ".exe"));
    }

    private static File toDir(int i) {
        return new File("C:\\dev\\oth" + i);
    }
}

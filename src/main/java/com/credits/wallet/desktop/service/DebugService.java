package com.credits.wallet.desktop.service;

import com.credits.wallet.desktop.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by goncharov-eg on 28.05.2018.
 */
public class DebugService {
    private static Logger LOGGER = LoggerFactory.getLogger(DebugService.class);

    String className;
    String text;

    private Process dbgProcess;
    private OutputStream dbgStdIn;
    private InputStream dbgStdOut;
    private InputStream dbgStdErr;

    public DebugService(String className, String text) {
        this.className = className;
        this.text = text;
    }

    public String compile() {
        try {
            new File(className + ".java").delete();
            new File(className + ".class").delete();
            FileWriter writer = new FileWriter(className + ".java");
            writer.write(text);
            writer.close();

            // Compile class
            Process process = Runtime.getRuntime().exec("javac -g " + className + ".java");
            InputStream stderr = process.getErrorStream();

            String error = "";
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stderr));
            while ((line = reader.readLine()) != null) {
                error = error + "+" + line + "\n";
            }

            if (error.isEmpty())
                return "";
            else
                return "Compilation errors:\n" + error;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "Error compiling smart contract " + e.toString();
        }
    }

    public String start() {
        try {
            dbgProcess = Runtime.getRuntime().exec("jdb " + className);
            dbgStdIn = dbgProcess.getOutputStream();
            dbgStdOut = dbgProcess.getInputStream();
            dbgStdErr = dbgProcess.getErrorStream();
            return "";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "Error starting debug "+e.toString();
        }
    }

    public void destroy() {
        dbgProcess.destroy();
    }

    public String execCmd(String cmd) {
        try {
            LOGGER.info("SC DEBUG: Executing jdb command " + cmd);

            dbgStdIn.write((cmd + "\n*\n").getBytes());
            dbgStdIn.flush();

            String result = "";
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(dbgStdOut));
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("unrecognized command: '*'"))
                    break;
                result = result + line + "\n";
            }

            LOGGER.info("SC DEBUG: RESULT^\n" + result);
            LOGGER.info("SC DEBUG: RESULTv");

            if (result.contains("Exception occurred"))
                Utils.showError(result);

            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "Error executing debug commang\n" + cmd + "\n" + e.toString();
        }
    }

    public Integer cursorPosition() {
        try {
            String res = execCmd("where");
            int ind = res.indexOf(".java:");
            res = res.substring(ind + 6);
            ind = res.indexOf(")");
            res = res.substring(0, ind).trim();
            return Integer.valueOf(res);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
            return null;
        }
    }
}

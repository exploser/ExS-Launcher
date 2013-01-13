package net.minecraft;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static File workDir = null;
    public static String host = "http://exsserver.ru/";
    public static String filehost = "http://dl.dropbox.com/u/39651219/exs/";

    public static int getMem() {
        return 512;
    }

    public static File getWorkingDirectory() {
        if (workDir == null) {
            workDir = getWorkingDirectory("exsserver");
        }
        return workDir;
    }

    public static Properties getOptions() {
        File f = new File(getWorkingDirectory() + File.separator + "bin");
        f.mkdirs();
        f = new File(getWorkingDirectory() + File.separator + "bin" + File.separator + "opts.properties");
        try {
            if (f.exists()) {
                InputStream optfile = new FileInputStream(f);
                Properties p = new Properties();
                p.load(optfile);
                optfile.close();
                return p;
            }
            return createOptsFile(f);
        } catch (Exception e) {
        }
        return null;
    }

    public static Properties createOptsFile(File f) throws Exception {
        OutputStream optfile = new FileOutputStream(f);
        Properties p = new Properties();
        p.setProperty("leavemods", "false");
        p.setProperty("leaveconf", "false");
        p.setProperty("maxmem", "512");
        p.store(optfile, null);
        optfile.close();
        return p;
    }

    public static void deleteOptionsFile() {
        try {
            File f = new File(getWorkingDirectory() + File.separator + "bin" + File.separator + "opts.properties");
            f.delete();
        } catch (Exception e) {
        }
    }

    public static Boolean saveOptions(Properties p) {
        try {
            File f = new File(getWorkingDirectory() + File.separator + "bin" + File.separator + "opts.properties");
            OutputStream optfile = new FileOutputStream(f);
            p.store(optfile, null);
            return Boolean.valueOf(true);
        } catch (Exception e) {
        }
        return Boolean.valueOf(false);
    }

    public static File getWorkingDirectory(String applicationName) {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (getPlatform().ordinal()) {
            case 0:
            case 1:
                workingDirectory = new File(userHome, '.' + applicationName + '/');
                break;
            case 2:
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + applicationName + '/');
                } else {
                    workingDirectory = new File(userHome, '.' + applicationName + '/');
                }
                break;
            case 3:
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;
            default:
                workingDirectory = new File(userHome, applicationName + '/');
        }
        if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs())) {
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        }
        return workingDirectory;
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OS.windows;
        }
        if (osName.contains("mac")) {
            return OS.macos;
        }
        if (osName.contains("solaris")) {
            return OS.solaris;
        }
        if (osName.contains("sunos")) {
            return OS.solaris;
        }
        if (osName.contains("linux")) {
            return OS.linux;
        }
        if (osName.contains("unix")) {
            return OS.linux;
        }
        return OS.unknown;
    }

    public static boolean deleteRecursive(File path) throws Exception{
        if (!path.exists()) throw new Exception(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }
    
    public static String excutePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();

            byte[] bytes = new byte[294];
            DataInputStream dis = new DataInputStream(Util.class.getResourceAsStream("minecraft.key"));
            dis.readFully(bytes);
            dis.close();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            StringBuffer response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String str1 = response.toString();
            return str1;
        } catch (Exception e) {
            e.printStackTrace();
            String bytes = null;
            return bytes;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static boolean isEmpty(String str) {
        return (str == null) || (str.length() == 0);
    }

    public static void openLink(URI uri) {
        try {
            Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            o.getClass().getMethod("browse", new Class[]{URI.class}).invoke(o, new Object[]{uri});
        } catch (Throwable e) {
            System.out.println("Failed to open link " + uri.toString());
        }
    }

    public static void resetVersion() {
        DataOutputStream dos = null;
        try {
            File dir = new File(getWorkingDirectory() + File.separator + "bin" + File.separator);
            File versionFile = new File(dir, "version");
            dos = new DataOutputStream(new FileOutputStream(versionFile));
            dos.writeUTF("0");
            dos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dos.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String getFakeLatestVersion() {
        try {
            File dir = new File(getWorkingDirectory() + File.separator + "bin" + File.separator);
            File file = new File(dir, "version");
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            String version = dis.readUTF();
            dis.close();
            if (version.equals("0")) {
                return "1285241960000";
            }
            return version;
        } catch (IOException ex) {
        }
        return "1285241960000";
    }

    public static enum OS {

        linux, solaris, windows, macos, unknown;
    }
}

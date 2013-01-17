 package net.minecraft;
 
 import java.io.PrintStream;
 import java.io.File;
 import java.util.ArrayList;
import java.util.prefs.Preferences;
 
 public class MinecraftLauncher
 {
   public static void main(String[] args)
     throws Exception
   {
     try
     {
       String pathToJar = MinecraftLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
       Preferences prefs = Util.getOptions();
       ArrayList params = new ArrayList();
       Util.OS os = Util.getPlatform();
       if (os == Util.OS.windows)
         params.add("javaw");
       else
         params.add("java");
       String param1 = "-Xmx" + prefs.get("maxmem", "512") + "m";
       String param2 = "-Xms" + prefs.get("maxmem", "512") + "m";
       params.add(param1);
       params.add(param2);
       params.add("-classpath");
                System.out.println(pathToJar);
       params.add(pathToJar);
       params.add("net.minecraft.LauncherFrame");
       ProcessBuilder pb = new ProcessBuilder(params);
       Process process = pb.start();
       if (process == null) throw new Exception("!");
 
       PrintStream prnt = new PrintStream(new File("C:\\launcherout.txt"));
       PrintStream err = new PrintStream(new File("C:\\launchererror.txt"));
       System.setOut(prnt);
       System.setErr(err);
     } catch (Exception e) {
       e.printStackTrace();
       LauncherFrame.main(args);
     }
   }
 }
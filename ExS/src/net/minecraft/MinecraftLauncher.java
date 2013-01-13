 package net.minecraft;
 
 import java.io.PrintStream;
 import java.io.File;
 import java.util.ArrayList;
 import java.util.Properties;
 
 public class MinecraftLauncher
 {
   public static void main(String[] args)
     throws Exception
   {
     try
     {
       String pathToJar = MinecraftLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
       Properties props = Util.getOptions();
       ArrayList params = new ArrayList();
       Util.OS os = Util.getPlatform();
       if (os == Util.OS.windows)
         params.add("javaw");
       else
         params.add("java");
       String param1 = "-Xmx" + props.getProperty("maxmem") + "m";
       String param2 = "-Xms" + props.getProperty("maxmem") + "m";
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

/* Location:           C:\Users\exploser\Downloads\ExS.jar
 * Qualified Name:     net.minecraft.MinecraftLauncher
 * JD-Core Version:    0.6.0
 */
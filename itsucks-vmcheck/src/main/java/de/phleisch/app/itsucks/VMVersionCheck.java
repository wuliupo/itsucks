package de.phleisch.app.itsucks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JOptionPane;

public class VMVersionCheck {

	private static final String PROPERTY_FILE = "/VMVersionCheck.properties";

	private Properties mConfiguration;

	private String mRequiredJavaVersion;

	private String mActualJavaVersion;

	private boolean mDisplayMessageDialog;

	private String mTargetClass;

	private String mTargetJar;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		VMVersionCheck check = new VMVersionCheck();
		check.loadProperties();
		if (check.runVMVersionCheck(check)) {
			check.delegateMain(args);
		} else {
			System.exit(1); //needed for 1.4  
		}
	}

	public void delegateMain(String[] args) throws 
			ClassNotFoundException, SecurityException, NoSuchMethodException, 
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
	
		// When no target class is defined an an target jar is defined, try to extract the main class from
		// the jar.
		if(mTargetClass == null && mTargetJar != null) {
		
			final String classpath = System.getProperty("java.class.path");
			final String pathsep = File.pathSeparator;
			
			for (StringTokenizer t = new StringTokenizer(classpath, pathsep, false); t.hasMoreTokens();) {
				String path = t.nextToken();
				System.out.println("Check if '" + path + "' ends with " + mTargetJar);
				
				if (!path.endsWith(mTargetJar)) continue;
				
				JarFile jf = new JarFile(path);
				Manifest m = jf.getManifest();
				
				if(m != null) {
					Attributes attributes = m.getMainAttributes();
					mTargetClass = attributes.getValue("Main-Class");
				}
				
				break;
			}
		}
		
		if(mTargetClass == null) {
			System.err.println("Main class not defined!");
		} else {
			Class clazz = Class.forName(mTargetClass);
			Method method = clazz.getMethod("main", new Class[] { String[].class });
			method.invoke(null, new Object[] {args});
		}
	}

	public boolean runVMVersionCheck(VMVersionCheck check) {

		boolean result = true;

		try {
			result = check.testVMVersion();
		} catch(Exception ex) {
			System.err.println("Error occured parsing VM Version String: \'" 
					+ mActualJavaVersion + "\', please report.");
		}
		
		if (!result) {

			String msg = "The installed java version ("
					+ check.getActualJavaVersion() + ") "
					+ "is too old.\nPlease install a newer version ("
					+ check.getRequiredJavaVersion() + " or higher).";

			System.err.println(msg);

			if (mDisplayMessageDialog) {
				JOptionPane.showMessageDialog(null, msg,
						"Java Version too old.", JOptionPane.ERROR_MESSAGE);
				
			}
		}

		return result;
	}

	public void loadProperties() throws IOException {

		InputStream resourceAsStream = VMVersionCheck.class
				.getResourceAsStream(PROPERTY_FILE);

		if (resourceAsStream == null) {
			throw new IllegalStateException(PROPERTY_FILE
					+ " not found in classpath!");
		}

		mConfiguration = new Properties();
		mConfiguration.load(resourceAsStream);

		mRequiredJavaVersion = mConfiguration
				.getProperty("requiredJavaVersion");
		if (mRequiredJavaVersion == null) {
			throw new IllegalArgumentException(
					"The property 'requiredJavaVersion' is not set!");
		}

		mActualJavaVersion = System.getProperty("java.version");

		String displayMessageDialog = mConfiguration
				.getProperty("displayMessageDialog");
		if (displayMessageDialog != null) {
			mDisplayMessageDialog = Boolean.valueOf(mConfiguration
					.getProperty("displayMessageDialog")).booleanValue();
		} else {
			mDisplayMessageDialog = false;
		}

		mTargetClass = mConfiguration.getProperty("targetMainClass");
		mTargetJar = mConfiguration.getProperty("targetJar");
	}

	private boolean testVMVersion() {

		VMVersion actualVersion = new VMVersion(mActualJavaVersion);
		VMVersion requiredVersion = new VMVersion(mRequiredJavaVersion);

		return (actualVersion.compareTo(requiredVersion) >= 0);
	}

	public String getActualJavaVersion() {
		return mActualJavaVersion;
	}

	public String getRequiredJavaVersion() {
		return mRequiredJavaVersion;
	}

}

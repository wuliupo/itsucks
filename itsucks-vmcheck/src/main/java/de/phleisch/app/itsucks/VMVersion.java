package de.phleisch.app.itsucks;

import java.util.StringTokenizer;

public class VMVersion implements Comparable {

	private String mCompleteVersion;

	private int mMajorVersion;

	private int mMinorVersion;

	private int mPatchLevel;

	private String mBuildNumber;

	public VMVersion(String pVersion) {
		parseVersion(pVersion);
	}

	private void parseVersion(String pVersion) {

		StringTokenizer version = new StringTokenizer(pVersion, ".");

		mCompleteVersion = pVersion;

		if (version.hasMoreTokens()) {
			mMajorVersion = Integer.parseInt(version.nextToken());
		} else {
			mMajorVersion = 0;
		}

		if (version.hasMoreTokens()) {
			mMinorVersion = Integer.parseInt(version.nextToken());
		} else {
			mMinorVersion = 0;
		}

		if (version.hasMoreTokens()) {

			// parse patch/build number
			String patchPart = version.nextToken();
			if (patchPart.indexOf('_') != -1) {
				StringTokenizer patchPartTokenizer = new StringTokenizer(
						patchPart, "_");
				mPatchLevel = Integer.parseInt(patchPartTokenizer.nextToken());
				mBuildNumber = patchPartTokenizer.nextToken();
			} else {
				mPatchLevel = Integer.parseInt(patchPart);
				mBuildNumber = "";
			}

		} else {

			mPatchLevel = 0;
			mBuildNumber = "";

		}

	}

	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append("Version: " + mCompleteVersion);
		result.append(" [Major Version: " + mMajorVersion);
		result.append(" / Minor Version: " + mMinorVersion);
		result.append(" / Patch Level: " + mPatchLevel);
		result.append(" / Build Number: " + mBuildNumber + "]");

		return result.toString();
	}

	public int compareTo(Object arg0) {

		VMVersion lhs = this;
		VMVersion rhs = (VMVersion) arg0;
		int result = 0;

		result = new Integer(lhs.mMajorVersion).compareTo(new Integer(
				rhs.mMajorVersion));

		if (result == 0) {
			result = new Integer(lhs.mMinorVersion).compareTo(new Integer(
					rhs.mMinorVersion));
		}

		if (result == 0) {
			result = new Integer(lhs.mPatchLevel).compareTo(new Integer(
					rhs.mPatchLevel));
		}

		return result;
	}

}
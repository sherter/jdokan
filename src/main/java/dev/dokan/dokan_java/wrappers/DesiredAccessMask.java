package dev.dokan.dokan_java.wrappers;

import dev.dokan.dokan_java.constants.microsoft.AccessMask;
import dev.dokan.dokan_java.constants.microsoft.DirectoryAccessMask;
import dev.dokan.dokan_java.constants.microsoft.FileAccessMask;
import dev.dokan.dokan_java.constants.microsoft.FileAttribute;
import dev.dokan.dokan_java.structure.EnumIntegerSet;

import java.util.concurrent.atomic.AtomicInteger;

public class DesiredAccessMask {

	private final boolean isDirectory;
	private final AtomicInteger accessMask;

	public DesiredAccessMask(int rawAccessMask, int rawFileAttributes) {
		this(rawAccessMask, (rawFileAttributes & FileAttribute.DIRECTORY.getMask()) != 0);
	}

	public DesiredAccessMask(int rawAccessMask, EnumIntegerSet<FileAttribute> fileAttributes) {
		this(rawAccessMask, fileAttributes.contains(FileAttribute.DIRECTORY));
	}

	//TODO Check for illegal flags if isDirectory=true
	//See https://docs.microsoft.com/en-us/windows-hardware/drivers/ddi/wdm/nf-wdm-zwcreatefile Note below first table
	public DesiredAccessMask(int rawAccessMask, boolean isDirectory) {
		this.isDirectory = isDirectory;
		this.accessMask = new AtomicInteger(rawAccessMask);
	}

	public EnumIntegerSet<AccessMask> getBasicRights() {
		return EnumIntegerSet.enumSetFromInt(this.accessMask.get(), AccessMask.values());
	}

	public EnumIntegerSet<FileAccessMask> getSpecificRights() {
		return EnumIntegerSet.enumSetFromInt(this.accessMask.get(), FileAccessMask.values());
	}

	public boolean isDirectory() {
		return this.isDirectory;
	}

	public int getAccessMask() {
		return this.accessMask.get();
	}

	public void setAccessMask(int accessMask) {
		this.accessMask.set(accessMask);
	}

	public boolean getFlag(AccessMask flag) {
		return getFlag(flag.getMask());
	}

	public boolean getFlag(FileAccessMask flag) {
		return getFlag(flag.getMask());
	}

	public boolean getFlag(DirectoryAccessMask flag) {
		if(!this.isDirectory) {
			throw new IllegalArgumentException("Not a directory!");
		}
		return silentGetFlag(flag);
	}

	public boolean silentGetFlag(DirectoryAccessMask flag) {
		return getFlag(flag.getMask());
	}

	public boolean getFlag(int flag) {
		return (this.accessMask.get() & flag) != 0;
	}

	public boolean setFlag(AccessMask flag) {
		return updateFlag(flag, true);
	}

	public boolean unsetFlag(AccessMask flag) {
		return updateFlag(flag, false);
	}

	public boolean updateFlag(AccessMask flag, boolean value) {
		return updateFlag(flag.getMask(), value);
	}

	public boolean setFlag(FileAccessMask flag) {
		return updateFlag(flag, true);
	}

	public boolean unsetFlag(FileAccessMask flag) {
		return updateFlag(flag, false);
	}

	public boolean updateFlag(FileAccessMask flag, boolean value) {
		return updateFlag(flag.getMask(), value);
	}

	public boolean setFlag(DirectoryAccessMask flag) {
		if(!this.isDirectory) {
			throw new IllegalArgumentException("Not a directory!");
		}
		return silentSetFlag(flag);
	}

	public boolean unsetFlag(DirectoryAccessMask flag) {
		if(!this.isDirectory) {
			throw new IllegalArgumentException("Not a directory!");
		}
		return silentUnsetFlag(flag);
	}

	public boolean updateFlag(DirectoryAccessMask flag, boolean value) {
		if(!this.isDirectory) {
			throw new IllegalArgumentException("Not a directory!");
		}
		return silentUpdateFlag(flag, value);
	}

	public boolean silentSetFlag(DirectoryAccessMask flag) {
		return silentUpdateFlag(flag, true);
	}

	public boolean silentUnsetFlag(DirectoryAccessMask flag) {
		return silentUpdateFlag(flag, false);
	}

	public boolean silentUpdateFlag(DirectoryAccessMask flag, boolean value) {
		return updateFlag(flag.getMask(), value);
	}

	public boolean updateFlag(int flag, boolean value) {
		int prev = this.accessMask.getAndUpdate(current -> current & (value ? flag : ~flag));
		return (prev & flag) != 0;
	}

	private FileAccessMask getFileAccessMask(DirectoryAccessMask attribute) {
		//This lookup is fine, but a different kind of mapping would be preferable
		switch(attribute) {
		case LIST_DIRECTORY: //1
			return FileAccessMask.READ_DATA;
		case TRAVERSE: //32
			return FileAccessMask.EXECUTE;
		}
		throw new IllegalStateException();
	}
}
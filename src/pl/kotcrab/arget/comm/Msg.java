
package pl.kotcrab.arget.comm;

import java.util.regex.Pattern;

public class Msg {
	// TODO switch to enum?
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int SYSTEM = 2;
	public static final int ERROR = 3;

	// ======================================================================FILES=============================================================
	@Deprecated public static final String FILE_DELIMITER = "|";
	@Deprecated public static final String FILE_DELIMITER_REGEX = Pattern.quote("|"); // safe for regex

	@Deprecated public static final String FILE_PREFIX = "_file";
	@Deprecated public static final String FILE_TRANSFER_REQUEST_TO_MEMORY = "_filereqtomem|"; // + transfer task uuid + filename +
// file size
	@Deprecated public static final String FILE_TRANSFER_REQUEST_TO_FILE = "_filereqtofile|"; // + transfer task uuid + filename +
// file size
	@Deprecated public static final String FILE_ACCEPTED = "_fileacc|";// + transfer task uuid
	@Deprecated public static final String FILE_REJECTED = "_filerej|";// + transfer task uuid
	@Deprecated public static final String FILE_DATA_BLOCK = "_filedata|"; // + transfer task uuid + data block
	@Deprecated public static final String FILE_DATA_BLOCK_RECEIVED = "_filedatagotblock|"; // + transfer task uuid
	@Deprecated public static final String FILE_ERROR = "_fileerr|"; // + transfer task uuid
	@Deprecated public static final String FILE_FINISHED = "_filedone|"; // + transfer task uuid
	@Deprecated public static final String FILE_CANCEL = "_filecancel|"; // + transfer task uuid
}

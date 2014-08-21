
package pl.kotcrab.arget.comm;

import java.util.regex.Pattern;

public class Msg {
	// TODO switch to enum?
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int SYSTEM = 2;
	public static final int ERROR = 3;

	// ====================================================================MESSAGES============================================================

	/** Standard message */
	// @Deprecated public static final String MESSAGE = "_m_";
	/** Notifies that remote started typing message */
// @Deprecated public static final String TYPING_FINISHED = "_typstared_";
	/** Notifies that remote finished (or stopped) typing message */
// @Deprecated public static final String TYPING_STARTED = "_typfinished_";
	/** Notifies that session window has been hidden (center panel was changed) */
	// @Deprecated public static final String REMOTE_ON_HIDE = "_remonhide_";
	/** Notifies that session window has been shown (center panel was changed to matching session that send this message) */
	// @Deprecated public static final String REMOTE_ON_SHOW = "_remonshow_";

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

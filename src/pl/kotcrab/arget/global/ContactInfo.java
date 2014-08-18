
package pl.kotcrab.arget.global;

public class ContactInfo {
	public String name;
	public String publicProfileKey;

	public transient ContactStatus status;
	public transient boolean unreadMessages;

	public ContactInfo () {
	}

	public ContactInfo (String name, String publicProfileKey) {
		this(name, publicProfileKey, ContactStatus.DISCONNECTED);
	}

	public ContactInfo (String name, String publicProfileKey, ContactStatus status) {
		this.name = name;
		this.publicProfileKey = publicProfileKey;
		this.status = status;
	}
}

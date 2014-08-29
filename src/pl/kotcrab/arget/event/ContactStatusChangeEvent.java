
package pl.kotcrab.arget.event;

import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.server.ContactStatus;

public class ContactStatusChangeEvent implements Event {
	public ContactInfo contact;
	public ContactStatus previousStatus;
	
	public ContactStatusChangeEvent (ContactInfo contact, ContactStatus previousStatus) {
		//we are using copy constructor because we don't want to something mess with our contact data
		this.contact = new ContactInfo(contact); 
		this.previousStatus = previousStatus;
	}

	@Override
	public boolean isExectueOnEDT () {
		return true;
	}
}

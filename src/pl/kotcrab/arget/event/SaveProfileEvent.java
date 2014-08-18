
package pl.kotcrab.arget.event;

public class SaveProfileEvent implements Event {
	@Override
	public boolean isExectueOnAWTEventQueue () {
		return true;
	}
}

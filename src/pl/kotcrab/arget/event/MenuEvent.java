
package pl.kotcrab.arget.event;

public class MenuEvent implements Event {
	public MenuEventType type;

	public MenuEvent (MenuEventType type) {
		this.type = type;
	}

	@Override
	public boolean isExectueOnAWTEventQueue () {
		return true;
	}
}


package pl.kotcrab.arget.util.idle;

public class DefaultIdleTimeCounter extends IdleTimeCounter {

	@Override
	public long getSystemIdleTime () {
		return 0;
	}

}

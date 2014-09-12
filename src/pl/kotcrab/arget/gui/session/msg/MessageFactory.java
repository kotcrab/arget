package pl.kotcrab.arget.gui.session.msg;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import pl.kotcrab.arget.Log;

public class MessageFactory
{
	public TextMessage createTextMsg()
	{
//      final AtomicReference<String> msg = new AtomicReference<String>();
//		
//		runOnEDT(new Runnable() {
//			
//			@Override
//			public void run () {
//				TextMessage msg = new TextMessage(type, text)
//			}
//		});
		
		return null;
	}
	
	private void runOnEDT(Runnable runnable)
	{
		try {
			EventQueue.invokeAndWait(runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			Log.exception(e);
		}
	}
}

package pl.kotcrab.arget.global;

import java.util.ArrayList;

public class GlobalServerInfo {
	public String motd = "";
	public String hostedBy = "";
	public ArrayList<String> publicMsg = new ArrayList<String>();

	public boolean whitelistEnabled = false;
	public ArrayList<String> whitelistKeys = new ArrayList<String>();
	public ArrayList<String> banListKeys = new ArrayList<String>();
	public ArrayList<String> banListIp = new ArrayList<String>();
	public ArrayList<String> vipKeys = new ArrayList<String>();
}

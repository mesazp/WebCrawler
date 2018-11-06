package com.main.passive;

public class NMMessage {
	String Sip;
    String Dip;
    String Sport;
    String Dport;
    public String getSport() {
		return Sport;
	}
	public void setSport(String sport) {
		Sport = sport;
	}
	public String getDport() {
		return Dport;
	}
	public void setDport(String dport) {
		Dport = dport;
	}
	String Packet;

	public String getPacket() {
		return Packet;
	}
	public void setPacket(String packet) {
		Packet = packet;
	}
	public String getSip() {
		return Sip;
	}
	public void setSip(String sip) {
		Sip = sip;
	}
	public String getDip() {
		return Dip;
	}
	public void setDip(String dip) {
		Dip = dip;
	}


}

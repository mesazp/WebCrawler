package com.main.passive;



import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;  
   
public class MyPcapPacketHandler<Object> implements PcapPacketHandler<Object>  {//抓到包后送去检测  
       
    @Override  
    public void nextPacket(PcapPacket packet, Object obj) {  
        PacketMatch packetMatch = PacketMatch.getInstance();  
        packetMatch.handlePacket(packet);  
    }  
}  
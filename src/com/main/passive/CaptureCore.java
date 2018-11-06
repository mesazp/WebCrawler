    package com.main.passive;  
       
    import java.util.ArrayList;  
    import java.util.List;  
    import javax.swing.JOptionPane;  
    import org.jnetpcap.Pcap;  
    import org.jnetpcap.PcapIf;  
       
    public class CaptureCore {  
           
        public static List<PcapIf> getDevs() {//获取机器上的网卡列表  
            List<PcapIf> devs = new ArrayList<PcapIf>();  
            StringBuilder errsb = new StringBuilder();  
            int r = Pcap.findAllDevs(devs, errsb);  
            if (r == Pcap.NOT_OK || devs.isEmpty()) {  
                JOptionPane.showMessageDialog(null,errsb.toString(),"错误",JOptionPane.ERROR_MESSAGE);  
                return null;  
            } else {  
                return devs;  
            }  
        }  
           
        public static void startCaptureAt(int num) {//选择一个网卡开启抓包  
            List<PcapIf> devs = new ArrayList<PcapIf>();  
            StringBuilder errsb = new StringBuilder();  
            int r = Pcap.findAllDevs(devs, errsb);  
            if (r == Pcap.NOT_OK || devs.isEmpty()) {  
                JOptionPane.showMessageDialog(null,errsb.toString(),"错误",JOptionPane.ERROR_MESSAGE);  
                return;  
            }  
            PcapIf device = devs.get(num);  
            int snaplen = Pcap.DEFAULT_SNAPLEN;//长度65536  
            int flags = Pcap.MODE_PROMISCUOUS;//混杂模式  
            int timeout = 10 * 1000;  
            //StringBuilder errsb = null;  
            Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errsb);  
            if (pcap == null) {  
                JOptionPane.showMessageDialog(null,errsb.toString(),"错误",JOptionPane.ERROR_MESSAGE);  
                return;  
            }  
            PacketMatch packetMatch = PacketMatch.getInstance();  
            packetMatch.loadRules();  
            MyPcapPacketHandler<Object> myhandler = new MyPcapPacketHandler<Object>();  
            pcap.loop(0, myhandler, "jnetpcap");  
            pcap.close();  
        }  
    }


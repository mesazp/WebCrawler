package com.main.passive;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Http.Request;

import kafka.producer.Producer;

public class PcapOffline {  

public static void main(String[] args) {  
    final String FILE_NAME = "test.pcap";  
    StringBuilder errbuf = new StringBuilder(); // For any error msgs  

    /*************************************************************************** 
     * 打开文件
     **************************************************************************/  
    Pcap pcap = Pcap.openOffline(FILE_NAME, errbuf);  

    if (pcap == null) {  
        System.err.printf("Error while opening file for capture: "  
            + errbuf.toString());  
        return;  
    }  

    /*************************************************************************** 
     *创建一些再loop中会使用和重用的对象
     **************************************************************************/  
    Ip4 ip = new Ip4(); 
    
    String topic="myTopic";
    KafkaProducer<String, String> procuder=initializeKafka(topic);
    Socket client = null;
    Ethernet eth = new Ethernet();  
    Http http = new Http();
    PcapHeader hdr = new PcapHeader(JMemory.POINTER);  
    JBuffer buf = new JBuffer(JMemory.POINTER);  

    /*************************************************************************** 
     * 我们必须将pcap’s data-link-type 映射到 jnetPcap‘s 协议id，scanner需要这个id数据用来判断packet的第一个header是什么
     **************************************************************************/  
    int id = JRegistry.mapDLTToId(pcap.datalink());  
    // JRegistry 是协议的注册表，包括它们的类，运行时id，和相关的绑定，这个全局的注册表包括 绑定表，header scanner表和每个header的数字化id表。同时也提供一些查找和转化功能，比如吧header class 映射为 数字化id

    /*************************************************************************** 
     * 我们同步header 和 buffer 不是copy的，而是如同指针的
     **************************************************************************/  
    while (pcap.nextEx(hdr, buf) == Pcap.NEXT_EX_OK) {  

        /************************************************************************* 
         * 我们吧header和buffer复制（指向）到新的packet对象中
         ************************************************************************/  
        PcapPacket packet = new PcapPacket(hdr, buf);  

        /************************************************************************* 
         * 扫描packet
         ************************************************************************/  
        packet.scan(id);  

        /* 
         * 使用 格式化工具吧源数据变为容易看懂的数据
         */  
        if (packet.hasHeader(http)) {  // 如果packet有ether头部
        
          if(http.fieldValue(Request.RequestUrl)!=null)
           {
        	  String url="";
        	  String protocol=http.fieldValue(Request.RequestVersion);
        	  protocol=protocol.substring(0,protocol.indexOf("/"));
        	 if(http.fieldValue(Request.RequestUrl).equals("/"))
        		{
        		 url =http.fieldValue(Request.RequestMethod)+" "+protocol.toLowerCase()+"://"+http.fieldValue(Request.Host)+" ";  
        		}
        	 else
        		{
        		url =http.fieldValue(Request.RequestMethod)+" "+protocol.toLowerCase()+"://"+http.fieldValue(Request.Host)+http.fieldValue(Request.RequestUrl)+" "; 
        		}
             try{
        		//client = new Socket("127.0.0.1",8888);
        		//String msg=url;
        		//得到socket读写流,向服务端程序发送数据 
        		//client.getOutputStream().write(msg.getBytes());
        		//byte[] datas = new byte[2048];
        		//从服务端程序接收数据
        		//client.getInputStream().read(datas);
        		//System.out.println(new String(datas));
        		}catch(Exception e){
        		e.printStackTrace();
        		}finally {
        		if (client != null) {
        		try {
        		client.close();
        		} catch (IOException e) {
        		System.out.println("systemerr:" +e);
        		}
        		}
        		}
               System.out.printf(url); 
               
             
               ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topic, url);
               procuder.send(msg);
               
               //列出topic的相关信息
               List<PartitionInfo> partitions = new ArrayList<PartitionInfo>() ;
               partitions = procuder.partitionsFor(topic);
               for(PartitionInfo p:partitions)
               {
                   System.out.println(p);
               }

               System.out.println("send message over.");
              // procuder.close(100,TimeUnit.MILLISECONDS);
           }
        }  
//        if (packet.hasHeader(eth)) {  // 如果packet有ether头部
//            String str = FormatUtils.mac(eth.source());  
//            System.out.printf("#%d: eth.src=%s\n", packet.getFrameNumber(), str);  
//        }  
//        if (packet.hasHeader(ip)) {  // 如果packet有ip头部
//            String str = FormatUtils.ip(ip.source());  
//            System.out.printf("#%d: ip.src=%s\n", packet.getFrameNumber(), str);  
//            // getFrameNumber()是帧号
//        }  
    }  

    /************************************************************************* 
     * 关闭pcap
     ************************************************************************/  
    pcap.close();  
}  


public static KafkaProducer<String, String> initializeKafka(String topic) {

    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("group.id", "hello-group");
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    //生产者发送消息 
    KafkaProducer<String, String> producer = new KafkaProducer<String,String>(props);
	return producer;
}
}

#include <iostream>
#include <string>
#include <stdexcept>
#include <cstdlib>
#include <unistd.h>
#include <tins/tins.h>


using std::cout;
using std::runtime_error;
using std::endl;

using namespace Tins;

PacketSender sender;


bool anaUDPPacket(PDU& pkt) {
    const UDP &udp = pkt.rfind_pdu<UDP>();
    // We need source/destination port to be 53
    if (udp.dport() == 8888) {
        // Interpret it as DNS. This might throw, but Sniffer catches it
        //onst IP &ip = pdu.rfind_pdu<IP>(); 
        //if(ip.src_addr() ){



        const RawPDU& raw = udp.rfind_pdu<RawPDU>();
        const RawPDU::payload_type& payload = raw.payload();

        cout << std::hex<<(int )payload[0] <<" " <<(int) payload[1] <<" " << (int)payload[2] <<" ";


        IP pkt = IP("10.177.255.115") / TCP(30111) / raw;

        


    }
    return true;
}

struct SnifferUDP {
    void startSniffUDP() {
        SnifferConfiguration config;
        //config.set_promisc_mode(true);
        config.set_filter("ip src 172.16.10.1");
        Sniffer sniffer("wlp4s0mon");
        sniffer.sniff_loop(make_sniffer_handler(this, &SnifferUDP::handle));
        // Also valid
        sniffer.sniff_loop(anaUDPPacket);
    }
    
    bool handle(PDU&) {
        // Don't process anything
        return false;
    }
};

int main() {
    SnifferUDP f;
    f.startSniffUDP();

   // 
   // 
    //sender.send(pkt);
}
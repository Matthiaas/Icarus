
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

bool anaUDPPacket(PDU& pkt) {
    const UDP &udp = pkt.rfind_pdu<UDP>();
    // We need source/destination port to be 53
    if (udp.sport() == 8080) {
        // Interpret it as DNS. This might throw, but Sniffer catches it
        cout << "ok" << endl;
    }
    return true;
}

struct SnifferUDP {
    void startSniffUDP() {
        SnifferConfiguration config;
        //config.set_promisc_mode(true);
        //config.set_filter("ip src 192.168.0.100");
        Sniffer sniffer("mon0");
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

   // PacketSender sender;
   // IP pkt = IP("192.168.0.1") / TCP(22) / RawPDU("foo");
    //sender.send(pkt);
}
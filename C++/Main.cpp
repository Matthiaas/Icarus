

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

bool doo(PDU& pkt) {
    const UDP &udp = pkt.rfind_pdu<UDP>();
    // We need source/destination port to be 53
    if (udp.sport() == 53 || udp.dport() == 53) {
        // Interpret it as DNS. This might throw, but Sniffer catches it
        DNS dns = pkt.rfind_pdu<RawPDU>().to<DNS>();
        // Just print out each query's domain name
        for (const auto &query : dns.queries()) {
            std::cout << query.dname() << std::endl;
        }
    }
    return true;
}

struct foo {
    void bar() {
        SnifferConfiguration config;
        //config.set_promisc_mode(true);
        //config.set_filter("ip src 192.168.0.100");
        Sniffer sniffer("wlp1s0");
        /* Uses the helper function to create a proxy object that
         * will call this->handle. If you're using boost or C++11,
         * you could use boost::bind or std::bind, that will also
         * work.
         */
        sniffer.sniff_loop(make_sniffer_handler(this, &foo::handle));
        // Also valid
        sniffer.sniff_loop(doo);
    }
    
    bool handle(PDU&) {
        // Don't process anything
        return false;
    }
};

int main() {
    //foo f;
    //f.bar();

    PacketSender sender;
    IP pkt = IP("192.168.0.1") / TCP(22) / RawPDU("foo");
    sender.send(pkt);
}
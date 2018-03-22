package com.syscxp.utils.network;

/**
 * Create by DCY on 2018/3/21
 */
public class NetworkTest {
    public static void main(String[] args) {
        String ipA = "192.168.1.1";
        String ipB = "192.168.2.1";
        String netmask = "255.255.255.255";
        String cidr = "192.168.1.1/32";

        //System.out.println(NetworkUtils.ipv4StringToLong("202"));
        //System.out.println(Integer.toBinaryString(202));
        //System.out.println(NetworkUtils.getNetworkAddressFromCidr("192.168.211.1/24"));

        String[] arrA = ipA.split("\\.");
        String[] arrB = ipB.split("\\.");
        String[] arrM = netmask.split("\\.");

        String[] cidrs = cidr.split("\\/");

        String[] ipResult1s = new String[4];
        String[] ipResult2s = new String[4];

        for (int i = 0; i < arrA.length; ++i) {
            ipResult1s[i] = String.valueOf(Integer.parseInt(arrA[i]) & Integer.parseInt(arrM[i]));
        }
        for (int i = 0; i < arrB.length; ++i) {
            ipResult2s[i] = String.valueOf(Integer.parseInt(arrB[i]) & Integer.parseInt(arrM[i]));
        }
        String newIp1 = ipResult1s[0] +"."+ ipResult1s[1] +"."+ ipResult1s[2] +"."+ ipResult1s[3];
        String newIp2 = ipResult2s[0] +"."+ ipResult2s[1] +"."+ ipResult2s[2] +"."+ ipResult2s[3];

        System.out.println(Integer.parseInt(arrA[0]));
        System.out.println(Integer.parseInt(arrM[0]));
        System.out.println(ipResult1s[0]);
        System.out.println(ipResult1s[1]);
        System.out.println(ipResult1s[2]);
        System.out.println(ipResult1s[3]);
        System.out.println(newIp1);
        System.out.println(newIp2);

        System.out.println(NetworkUtils.isCidr(cidr));

        System.out.println(cidrs[0]);
        System.out.println(cidrs[1]);

        if (newIp1.equals(newIp2)) {
            //System.out.println(true);
        }

        //System.out.println(false);
    }
}

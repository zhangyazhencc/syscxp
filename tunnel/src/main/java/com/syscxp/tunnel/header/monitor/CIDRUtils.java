package com.syscxp.tunnel.header.monitor;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DCY on 2017-09-26
 * 子网掩码计算器
 */
public class CIDRUtils {

    //初始化网段
    private final String cidr;
    //IP
    private InetAddress inetAddress;
    //起始网络位IP
    private InetAddress startAddress;
    //最后广播位IP
    private InetAddress endAddress;
    //掩码位
    private final int prefixLength;

    public static void main(String[] args) {

        try {
            CIDRUtils cidrUtils = new CIDRUtils("10.77.12.11/18");
            Boolean b = cidrUtils.isInRange("10.78.0.1");
            System.out.println("5---"+b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CIDRUtils(String cidr) throws UnknownHostException {

        this.cidr = cidr;

        /* 分割CIDR到地址和前缀部分 */
        if (this.cidr.contains("/")) {
            int index = this.cidr.indexOf("/");
            String addressPart = this.cidr.substring(0, index);
            String networkPart = this.cidr.substring(index + 1);

            inetAddress = InetAddress.getByName(addressPart);
            prefixLength = Integer.parseInt(networkPart);
            //System.out.println("1---"+inetAddress);
            //System.out.println("2---"+prefixLength);

            calculate();
        } else {
            throw new IllegalArgumentException("not an valid CIDR format!");
        }
    }


    private void calculate() throws UnknownHostException {

        ByteBuffer maskBuffer;
        int targetSize;
        if (inetAddress.getAddress().length == 4) {
            maskBuffer =
                    ByteBuffer
                            .allocate(4)
                            .putInt(-1);
            targetSize = 4;
        } else {
            maskBuffer = ByteBuffer.allocate(16)
                    .putLong(-1L)
                    .putLong(-1L);
            targetSize = 16;
        }

        BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

        ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
        BigInteger ipVal = new BigInteger(1, buffer.array());

        BigInteger startIp = ipVal.and(mask);
        BigInteger endIp = startIp.add(mask.not());

        byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
        byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);

        this.startAddress = InetAddress.getByAddress(startIpArr);
        this.endAddress = InetAddress.getByAddress(endIpArr);

        //System.out.println("3---"+startAddress);
        //System.out.println("4---"+endAddress);

    }

    private byte[] toBytes(byte[] array, int targetSize) {
        int counter = 0;
        List<Byte> newArr = new ArrayList<Byte>();
        while (counter < targetSize && (array.length - 1 - counter >= 0)) {
            newArr.add(0, array[array.length - 1 - counter]);
            counter++;
        }

        int size = newArr.size();
        for (int i = 0; i < (targetSize - size); i++) {

            newArr.add(0, (byte) 0);
        }

        byte[] ret = new byte[newArr.size()];
        for (int i = 0; i < newArr.size(); i++) {
            ret[i] = newArr.get(i);
        }
        return ret;
    }

    public String getNetworkAddress() {

        return this.startAddress.getHostAddress();
    }

    public String getBroadcastAddress() {
        return this.endAddress.getHostAddress();
    }

    public boolean isInRange(String ipAddress) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ipAddress);
        BigInteger start = new BigInteger(1, this.startAddress.getAddress());
        BigInteger end = new BigInteger(1, this.endAddress.getAddress());
        BigInteger target = new BigInteger(1, address.getAddress());

        int st = start.compareTo(target);
        int te = target.compareTo(end);

        return (st == -1 || st == 0) && (te == -1 || te == 0);
    }
}
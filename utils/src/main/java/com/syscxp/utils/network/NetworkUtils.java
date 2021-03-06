package com.syscxp.utils.network;

import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.ShellResult;
import com.syscxp.utils.ShellUtils;
import com.syscxp.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import com.syscxp.utils.data.Pair;
import com.syscxp.utils.logging.CLogger;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtils {
    private static final CLogger logger = Utils.getLogger(NetworkUtils.class);

    private static final Set<String> validNetmasks = new HashSet<String>();

    private static final HashMap netmaskMap = new HashMap();

    public static String intFromNetmask(String netmask){
        return (String) netmaskMap.get(netmask);
    }

    public static String netmaskFromInt(String cidr){
        return (String) netmaskMap.get(cidr);
    }


    static {
        validNetmasks.add("255.255.255.255");//32
        validNetmasks.add("255.255.255.254");//31
        validNetmasks.add("255.255.255.252");//30
        validNetmasks.add("255.255.255.248");//29
        validNetmasks.add("255.255.255.240");//28
        validNetmasks.add("255.255.255.224");//27
        validNetmasks.add("255.255.255.192");//26
        validNetmasks.add("255.255.255.128");//25
        validNetmasks.add("255.255.255.0");//24
        validNetmasks.add("255.255.254.0");//23
        validNetmasks.add("255.255.252.0");//22
        validNetmasks.add("255.255.248.0");//21
        validNetmasks.add("255.255.240.0");//20
        validNetmasks.add("255.255.224.0");//19
        validNetmasks.add("255.255.192.0");//18
        validNetmasks.add("255.255.128.0");//17
        validNetmasks.add("255.255.0.0");//16
        validNetmasks.add("255.254.0.0");//15
        validNetmasks.add("255.252.0.0");//14
        validNetmasks.add("255.248.0.0");//13
        validNetmasks.add("255.240.0.0");//12
        validNetmasks.add("255.224.0.0");//11
        validNetmasks.add("255.192.0.0");//10
        validNetmasks.add("255.128.0.0");//9
        validNetmasks.add("255.0.0.0");//8
        validNetmasks.add("254.0.0.0");//7
        validNetmasks.add("252.0.0.0");//6
        validNetmasks.add("248.0.0.0");//5
        validNetmasks.add("240.0.0.0");//4
        validNetmasks.add("224.0.0.0");//3
        validNetmasks.add("192.0.0.0");//2
        validNetmasks.add("128.0.0.0");//1
        validNetmasks.add("0.0.0.0");//0

        netmaskMap.put("255.255.255.255","32");
        netmaskMap.put("255.255.255.254","31");
        netmaskMap.put("255.255.255.252","30");
        netmaskMap.put("255.255.255.248","29");
        netmaskMap.put("255.255.255.240","28");
        netmaskMap.put("255.255.255.224","27");
        netmaskMap.put("255.255.255.192","26");
        netmaskMap.put("255.255.255.128","25");
        netmaskMap.put("255.255.255.0","24");
        netmaskMap.put("255.255.254.0","23");
        netmaskMap.put("255.255.252.0","22");
        netmaskMap.put("255.255.248.0","21");
        netmaskMap.put("255.255.240.0","20");
        netmaskMap.put("255.255.224.0","19");
        netmaskMap.put("255.255.192.0","18");
        netmaskMap.put("255.255.128.0","17");
        netmaskMap.put("255.255.0.0","16");
        netmaskMap.put("255.254.0.0","15");
        netmaskMap.put("255.252.0.0","14");
        netmaskMap.put("255.248.0.0","13");
        netmaskMap.put("255.240.0.0","12");
        netmaskMap.put("255.224.0.0","11");
        netmaskMap.put("255.192.0.0","10");
        netmaskMap.put("255.128.0.0","9");
        netmaskMap.put("255.0.0.0","8");
        netmaskMap.put("254.0.0.0","7");
        netmaskMap.put("252.0.0.0","6");
        netmaskMap.put("248.0.0.0","5");
        netmaskMap.put("240.0.0.0","4");
        netmaskMap.put("224.0.0.0","3");
        netmaskMap.put("192.0.0.0","2");
        netmaskMap.put("128.0.0.0","1");
        netmaskMap.put("0.0.0.0","0");

        netmaskMap.put("32","255.255.255.255");
        netmaskMap.put("31","255.255.255.254");
        netmaskMap.put("30","255.255.255.252");
        netmaskMap.put("29","255.255.255.248");
        netmaskMap.put("28","255.255.255.240");
        netmaskMap.put("27","255.255.255.224");
        netmaskMap.put("26","255.255.255.192");
        netmaskMap.put("25","255.255.255.128");
        netmaskMap.put("24","255.255.255.0");
        netmaskMap.put("23","255.255.254.0");
        netmaskMap.put("22","255.255.252.0");
        netmaskMap.put("21","255.255.248.0");
        netmaskMap.put("20","255.255.240.0");
        netmaskMap.put("19","255.255.224.0");
        netmaskMap.put("18","255.255.192.0");
        netmaskMap.put("17","255.255.128.0");
        netmaskMap.put("16","255.255.0.0");
        netmaskMap.put("15","255.254.0.0");
        netmaskMap.put("14","255.252.0.0");
        netmaskMap.put("13","255.248.0.0");
        netmaskMap.put("12","255.240.0.0");
        netmaskMap.put("11","255.224.0.0");
        netmaskMap.put("10","255.192.0.0");
        netmaskMap.put("9","255.128.0.0");
        netmaskMap.put("8","255.0.0.0");
        netmaskMap.put("7","254.0.0.0");
        netmaskMap.put("6","252.0.0.0");
        netmaskMap.put("5","248.0.0.0");
        netmaskMap.put("4","240.0.0.0");
        netmaskMap.put("3","224.0.0.0");
        netmaskMap.put("2","192.0.0.0");
        netmaskMap.put("1","128.0.0.0");
        netmaskMap.put("0","0.0.0.0");
    }

    public static boolean isHostname(String hostname) {
        String PATTERN = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(hostname);
        return matcher.matches();
    }

    public static boolean isIpv4Address(String ip) {
        String PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static boolean isLegalPort(int port){
        if(port < 0 || port > 65535)
            return  false;
        else
            return true;
    }

    public static boolean isNetmask(String netmask) {
        return validNetmasks.contains(netmask);
    }

    public static boolean isNetmaskExcept(String netmask, String except) {
        return validNetmasks.contains(netmask) && !netmask.equals(except);
    }

    public static boolean isCidr(String cidr) {
        Pattern pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\/(\\d|[1-2]\\d|3[0-2]))$");
        Matcher matcher = pattern.matcher(cidr);
        return matcher.find();
    }

    public static long bytesToLong(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i] & 0xff);
        }
        return value;
    }

    public static long ipv4StringToLong(String ip) {
        validateIp(ip);

        try {
            InetAddress ia = InetAddress.getByName(ip);
            byte[] bytes = ia.getAddress();
            return bytesToLong(bytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(String.format("%s is not a valid ipv4 address", ip), e);
        }
    }

    public static boolean isIpv4InRange(String ip, String startIp, String endIp) {
        long ipl = ipv4StringToLong(ip);
        long startIpl = ipv4StringToLong(startIp);
        long endIpl = ipv4StringToLong(endIp);
        return (ipl <= endIpl && ipl >= startIpl);
    }

    public static String longToIpv4String(long ip) {
        byte[] bytes = ByteBuffer.allocate(4).putInt((int) ip).array();
        try {
            InetAddress ia = InetAddress.getByAddress(bytes);
            return ia.getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(String.format("%s cannot convert to a valid ipv4 address", ip), e);
        }
    }

    public static int ipRangeLength(String startIp, String endIp) {
        validateIp(startIp);
        validateIp(endIp);
        return (int)(ipv4StringToLong(endIp) - ipv4StringToLong(startIp) + 1);
    }

    private static void validateIp(String ip) {
        if (!isIpv4Address(ip)) {
            throw new IllegalArgumentException(String.format("%s is not a valid ipv4 address", ip));
        }
    }

    private static void validateIpRange(String startIp, String endIp) {
        validateIp(startIp);
        validateIp(endIp);
        long s = ipv4StringToLong(startIp);
        long e = ipv4StringToLong(endIp);
        if (s > e) {
            throw new IllegalArgumentException(String.format("[%s, %s] is not an invalid ip range, end ip must be greater than start ip", startIp, endIp));
        }
    }

    public static boolean isIpv4RangeOverlap(String startIp1, String endIp1, String startIp2, String endIp2) {
        validateIpRange(startIp1, endIp1);
        validateIpRange(startIp2, endIp2);

        long s1 = ipv4StringToLong(startIp1);
        long e1 = ipv4StringToLong(endIp1);
        long s2 = ipv4StringToLong(startIp2);
        long e2 = ipv4StringToLong(endIp2);

        if ((s1 >= s2 && s1 <= e2) || (s1 <= s2 && s2 <= e1)) {
            return true;
        }

        return false;
    }

    public static boolean isConsecutiveRange(List<Long> allocatedIps) {
        return allocatedIps.get(allocatedIps.size() - 1) - allocatedIps.get(0) == allocatedIps.size();
    }

    private static boolean isConsecutiveRange(Long[] allocatedIps) {
        return allocatedIps[allocatedIps.length - 1] - allocatedIps[0] + 1 == allocatedIps.length;
    }

    private static String[] longToIpv4String(Long[] ips) {
        String[] ret = new String[ips.length];
        for (int i=0; i<ips.length; i++) {
            ret[i] = longToIpv4String(ips[i]);
        }
        return ret;
    }

    private static long findFirstHoleByDichotomy(Long[] allocatedIps) {
        if (isConsecutiveRange(allocatedIps)) {
            String[] ips = longToIpv4String(allocatedIps);
            List<String> dips = new ArrayList<String>(allocatedIps.length);
            Collections.addAll(dips, ips);
            String err = "You can not ask me to find a hole in consecutive range!!! " + dips;
            assert false : err;
        }

        if (allocatedIps.length == 2) {
            return allocatedIps[0] + 1;
        }

        int mIndex = allocatedIps.length / 2;
        Long[] part1 = Arrays.copyOfRange(allocatedIps, 0, mIndex);
        Long[] part2 = Arrays.copyOfRange(allocatedIps, mIndex, allocatedIps.length);
        if (part1.length == 1) {
            if (isConsecutiveRange(part2)) {
                /* For special case there are only three items like [1, 3, 4]*/
                return part1[0] + 1;
            } else {
                /* For special case there are only three items like [1, 5, 9] that are all inconsecutive */
                Long[] tmp = new Long[] { part1[0], part2[0] };
                if (!isConsecutiveRange(tmp)) {
                    return part1[0] + 1;
                }
            }
        }
        
        /*For special case that hole is in the middle of array. for example, [1, 2, 4, 5]*/
        if (isConsecutiveRange(part1) && isConsecutiveRange(part2)) {
            return part1[part1.length-1] + 1;
        }

        if (!isConsecutiveRange(part1)) {
            return findFirstHoleByDichotomy(part1);
        } else {
            return findFirstHoleByDichotomy(part2);
        }
    }

    // The allocatedIps must be sorted!
    public static Long findFirstAvailableIpv4Address(Long startIp, Long endIp, Long[] allocatedIps) {
        Long ret = null;
        if (startIp > endIp) {
            throw new IllegalArgumentException(String.format("[%s, %s] is an invalid ip range, end ip must be greater than start ip", longToIpv4String(startIp), longToIpv4String(endIp)));
        }
        if (startIp.equals(endIp) && allocatedIps.length == 0 ) {
            return startIp;
        }
        if (allocatedIps.length == 0) {
            return startIp;
        }

        long lastAllocatedIp = allocatedIps[allocatedIps.length-1];
        long firstAllocatedIp = allocatedIps[0];
        if (firstAllocatedIp < startIp || lastAllocatedIp > endIp) {
            throw new IllegalArgumentException(String.format("[%s, %s] is an invalid allocated ip range, it's not a sub range of ip range[%s, %s]", longToIpv4String(firstAllocatedIp), longToIpv4String(lastAllocatedIp), longToIpv4String(startIp), longToIpv4String(endIp)));
        }


        if (allocatedIps.length == endIp - startIp + 1) {
            /* The ip range is fully occupied*/
            return null;
        }

        if (firstAllocatedIp > startIp) {
            /* The allocatedIps doesn't begin with startIp, then startIp is first available one*/
            return startIp;
        }

        if (isConsecutiveRange(allocatedIps)) {
            /* the allocated ip range is consecutive, allocate the first one out of allocated ip range */
            ret = lastAllocatedIp + 1;
            assert ret <= endIp;
            return ret;
        }
        
        /* Now the allocated ip range is inconsecutive, we are going to find out the first *hole* in it */
        return findFirstHoleByDichotomy(allocatedIps);
    }

    public static String findFirstAvailableIpv4Address(String startIp, String endIp, Long[] allocatedIps) {
        Long ret = findFirstAvailableIpv4Address(ipv4StringToLong(startIp), ipv4StringToLong(endIp), allocatedIps);
        return ret == null ? null : longToIpv4String(ret);
    }

    public static String randomAllocateIpv4Address(Long startIp, Long endIp, List<Long> allocatedIps) {
        int total = (int)(endIp - startIp + 1);
        if (startIp.equals(endIp) && allocatedIps.size() == 0){
            return longToIpv4String(startIp);
        }
        if (total == allocatedIps.size()) {
            return null;
        }

        BitSet full = new BitSet(total);
        for (long alloc : allocatedIps) {
            full.set((int) (alloc-startIp));
        }

        Random random = new Random();
        int next = random.nextInt(total);
        int a = full.nextClearBit(next);

        if (a >= total) {
            a = full.nextClearBit(0);
        }

        return longToIpv4String(a + startIp);
    }

    public static String randomAllocateIpv4Address(String startIp, String endIp, List<Long> allocatedIps) {
        return randomAllocateIpv4Address(ipv4StringToLong(startIp), ipv4StringToLong(endIp), allocatedIps);
    }

    public static int getTotalIpInRange(String startIp, String endIp) {
        validateIpRange(startIp, endIp);
        long s = ipv4StringToLong(startIp);
        long e = ipv4StringToLong(endIp);
        return (int) (e - s + 1);
    }

    public static int getTotalIpInCidr(String cidr) {
        DebugUtils.Assert(isCidr(cidr), String.format("%s is not a cidr", cidr));
        SubnetUtils.SubnetInfo range = new SubnetUtils(cidr).getInfo();

        return getTotalIpInRange(range.getLowAddress(), range.getHighAddress());
    }


    public static String getIpAddressByName(String hostName) throws UnknownHostException {
        InetAddress ia = InetAddress.getByName(hostName);
        return ia.getHostAddress();
    }

    public static String generateMacWithDeviceId(short deviceId) {
        int seed = new Random().nextInt();
        String seedStr = Integer.toHexString(seed);
        if (seedStr.length() < 8) {
            String compensate = StringUtils.repeat("0", 8 - seedStr.length());
            seedStr = compensate + seedStr;
        }
        String octet2 = seedStr.substring(0, 2);
        String octet3 = seedStr.substring(2, 4);
        String octet4 = seedStr.substring(4, 6);
        String octet5 = seedStr.substring(6, 8);
        StringBuilder sb = new StringBuilder("fa").append(":");
        sb.append(octet2).append(":");
        sb.append(octet3).append(":");
        sb.append(octet4).append(":");
        sb.append(octet5).append(":");
        String deviceIdStr = Integer.toHexString(deviceId);
        if (deviceIdStr.length() < 2) {
            deviceIdStr = "0" + deviceIdStr;
        }
        sb.append(deviceIdStr);
        return sb.toString();
    }

    public static List<Pair<String, String>> findConsecutiveIpRange(Collection<String> ips) {
        List<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();
        if (ips.isEmpty()) {
            return ret;
        }

        TreeSet<Long> orderIps = new TreeSet<Long>();
        for (String ip : ips) {
            orderIps.add(ipv4StringToLong(ip));
        }

        Long s = null;
        Long e = null;

        for (Long ip : orderIps) {
            if (s == null || e == null) {
                s = ip;
                e = ip;
            } else if (e.equals(ip - 1)) {
                e = ip;
            } else {
                Pair<String, String> range = new Pair<String, String>();
                range.first(longToIpv4String(s));
                range.second(longToIpv4String(e));
                ret.add(range);
                s = ip;
                e = ip;
            }
        }

        Pair<String, String> range = new Pair<String, String>();
        range.first(longToIpv4String(s));
        range.second(longToIpv4String(e));
        ret.add(range);

        return ret;
    }

    public static boolean isRemotePortOpen(String ip, int port, int timeout) {
        Socket socket = null;

        socket = new Socket();
        try {
            socket.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(ip, port);
            socket.connect(sa, timeout);
            return socket.isConnected();
        } catch (SocketException e) {
            logger.debug(String.format("unable to connect remote port[ip:%s, port:%s], %s", ip, port, e.getMessage()));
            return false;
        } catch (IOException e) {
            logger.debug(String.format("unable to connect remote port[ip:%s, port:%s], %s", ip, port, e.getMessage()));
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    public static List<String> getFreeIpInRange(String startIp, String endIp, List<String> usedIps, int limit, String start) {
        long s = ipv4StringToLong(startIp);
        long e = ipv4StringToLong(endIp);
        long f = ipv4StringToLong(start);
        List<String> res = new ArrayList<String>();
        for (long i = s > f ? s : f; i<=e; i++) {
            String ip = longToIpv4String(i);
            if (!usedIps.contains(ip)) {
                res.add(ip);
            }

            if (res.size() >= limit) {
                break;
            }
        }

        return res;
    }

    public static List<String> getAllMac() {
        try {
            List<String> macs = new ArrayList<String>();
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();

                byte[] mac = iface.getHardwareAddress();
                if (mac == null) {
                    // lo device
                    continue;
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                }

                macs.add(sb.toString().toLowerCase());
            }
            return macs;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isCidrOverlap(String cidr1, String cidr2) {
        DebugUtils.Assert(isCidr(cidr1), String.format("%s is not a cidr", cidr1));
        DebugUtils.Assert(isCidr(cidr2), String.format("%s is not a cidr", cidr2));

        SubnetUtils su1 = new SubnetUtils(cidr1);
        SubnetUtils su2 = new SubnetUtils(cidr2);

        SubnetUtils.SubnetInfo info1 = su1.getInfo();
        SubnetUtils.SubnetInfo info2 = su2.getInfo();

        return isIpv4RangeOverlap(info1.getLowAddress(), info1.getHighAddress(), info2.getLowAddress(), info2.getHighAddress());
    }

    public static boolean isIpv4InCidr(String ipv4, String cidr) {
        DebugUtils.Assert(isCidr(cidr), String.format("%s is not a cidr", cidr));
        validateIp(ipv4);

        SubnetUtils.SubnetInfo info = new SubnetUtils(cidr).getInfo();
        return isIpv4InRange(ipv4, info.getLowAddress(), info.getHighAddress());
    }

    public static boolean isIpRoutedByDefaultGateway(String ip) {
        ShellResult res = ShellUtils.runAndReturn(String.format("ip route get %s | grep -q \"via $(ip route | awk '/default/ {print $3}')\"", ip));
        return res.isReturnCode(0);
    }

    public static boolean isSubCidr(String cidr, String subCidr) {
        DebugUtils.Assert(isCidr(cidr), String.format("%s is not a cidr", cidr));
        DebugUtils.Assert(isCidr(subCidr), String.format("%s is not a cidr", subCidr));

        SubnetUtils.SubnetInfo range = new SubnetUtils(cidr).getInfo();
        SubnetUtils.SubnetInfo sub = new SubnetUtils(subCidr).getInfo();
        return range.isInRange(sub.getLowAddress()) && range.isInRange(sub.getHighAddress());
    }
    
    public static String getNetworkAddressFromCidr(String cidr) {
        DebugUtils.Assert(isCidr(cidr), String.format("%s is not a cidr", cidr));
        SubnetUtils n = new SubnetUtils(cidr);
        return String.format("%s/%s", n.getInfo().getNetworkAddress(), cidr.split("\\/")[1]);
    }


    public static List<String> getIpRangeFromIps(List<String> ips){
        List<Pair<String, String>> ipRanges = findConsecutiveIpRange(ips);
        List<String> internalIpRanges = new ArrayList<String>(ipRanges.size());
        for (Pair<String, String> p : ipRanges) {
            if (p.first().equals(p.second())) {
                internalIpRanges.add(p.first());
            } else {
                internalIpRanges.add(String.format("%s-%s", p.first(), p.second()));
            }
        }
        return internalIpRanges;
    }

    public static List<String> getCidrsFromIpRange(String startIp, String endIp) {
        if (!isIpv4Address(startIp)) {
            throw new IllegalArgumentException(String.format("%s is not a valid ipv4 address", startIp));
        }
        if (!isIpv4Address(endIp)) {
            throw new IllegalArgumentException(String.format("%s is not a valid ipv4 address", endIp));
        }

        long start = ipToLong(startIp);
        long end = ipToLong(endIp);

        ArrayList<String> pairs = new ArrayList<String>();
        while (end >= start) {
            byte maxsize = 32;
            while (maxsize > 0) {
                long mask = CIDR2MASK[maxsize - 1];
                long maskedBase = start & mask;

                if (maskedBase != start) {
                    break;
                }

                maxsize--;
            }
            double x = Math.log(end - start + 1) / Math.log(2);
            byte maxdiff = (byte) (32 - Math.floor(x));
            if (maxsize < maxdiff) {
                maxsize = maxdiff;
            }
            String ip = longToIP(start);
            pairs.add(ip + "/" + maxsize);
            start += Math.pow(2, (32 - maxsize));
        }
        logger.debug(String.format("get Cidrs from startIp:[%s], endId: [%s]", startIp, endIp));
        logger.debug(String.format("cidrs: %s", pairs.toString()));
        return pairs;
    }

    public static final int[] CIDR2MASK = new int[] { 0x00000000, 0x80000000,
            0xC0000000, 0xE0000000, 0xF0000000, 0xF8000000, 0xFC000000,
            0xFE000000, 0xFF000000, 0xFF800000, 0xFFC00000, 0xFFE00000,
            0xFFF00000, 0xFFF80000, 0xFFFC0000, 0xFFFE0000, 0xFFFF0000,
            0xFFFF8000, 0xFFFFC000, 0xFFFFE000, 0xFFFFF000, 0xFFFFF800,
            0xFFFFFC00, 0xFFFFFE00, 0xFFFFFF00, 0xFFFFFF80, 0xFFFFFFC0,
            0xFFFFFFE0, 0xFFFFFFF0, 0xFFFFFFF8, 0xFFFFFFFC, 0xFFFFFFFE,
            0xFFFFFFFF };

    private static long ipToLong(String strIP) {
        long[] ip = new long[4];
        String[] ipSec = strIP.split("\\.");
        for (int k = 0; k < 4; k++) {
            ip[k] = Long.valueOf(ipSec[k]);
        }

        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    private static String longToIP(long longIP) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf(longIP >>> 24));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(longIP & 0x000000FF));

        return sb.toString();
    }

    /************************************************************************************************************************/
    public static int randomAllocateVlan(int startVlan, int endVlan, List<Integer> allocatedVlans) {
        int total = endVlan - startVlan + 1;
        if (total == allocatedVlans.size()) {
            return 0;  //not vlan
        }

        BitSet full = new BitSet(total);
        for (int alloc : allocatedVlans) {
            full.set(alloc - startVlan);
        }

        int a  = full.nextClearBit(0);

        return a + startVlan;
    }

    public static boolean isIpv4sInNetmask(String ipA, String ipB, String netmask){
        String[] arrA = ipA.split("\\.");
        String[] arrB = ipB.split("\\.");
        String[] arrM = netmask.split("\\.");

        String[] ipResult1s = new String[4];
        String[] ipResult2s = new String[4];

        for (int i = 0; i < arrA.length; ++i) {
            ipResult1s[i] = String.valueOf(Integer.parseInt(arrA[i]) & Integer.parseInt(arrM[i]));
        }
        for (int i = 0; i < arrB.length; ++i) {
            ipResult2s[i] = String.valueOf(Integer.parseInt(arrB[i]) & Integer.parseInt(arrM[i]));
        }
        String newIp1 = ipResult1s[0] + ipResult1s[1] + ipResult1s[2] + ipResult1s[3];
        String newIp2 = ipResult2s[0] + ipResult2s[1] + ipResult2s[2] + ipResult2s[3];

        return newIp1.equals(newIp2);

    }

    public static String getIpCidrFromIpv4Netmask(String ip, String netmask){
        String[] arrIp = ip.split("\\.");
        String[] arrM = netmask.split("\\.");

        String[] ipResults = new String[4];
        for (int i = 0; i < arrIp.length; ++i) {
            ipResults[i] = String.valueOf(Integer.parseInt(arrIp[i]) & Integer.parseInt(arrM[i]));
        }

        return ipResults[0] +"."+ ipResults[1] +"."+ ipResults[2] +"."+ ipResults[3]+"/"+intFromNetmask(netmask);
    }

    public static String charge(String ip) {
        StringBuilder str = new StringBuilder();
        for (int j = 0; j < ip.length(); j += 8) {
            String ip1 = ip.substring(j, j + 8);
            ip1 = Integer.valueOf(ip1, 2).toString();
            str.append(ip1).append(".");
        }
        str = new StringBuilder(str.substring(0, str.length() - 1));
        return str.toString();
    }


    public static String startIP(String ip) {
        return ip.replaceAll("1", "0");
    }


    public static String endIP(String ip) {
        return ip.replaceAll("0", "1");
    }


    public static String[] ipSplit(String ip) {
        String[] ipString = ip.split("/");
        ip = ipString[0];
        int i = Integer.parseInt(ipString[1]);
        String[] strings = ip.split("\\.");
        StringBuilder str1 = new StringBuilder();
        for (String string : strings) {
            String str = Integer.toBinaryString(Integer.parseInt(string));
            if (str.length() < 8) {
                int j = (int) Math.pow(10, 8 - str.length());
                str = ("" + j + str);
                str = str.substring(1, str.length());
            }
            str1.append(str);
        }
        ipString[0] = charge(str1.substring(0, i) + startIP(str1.substring(i, 32)));
        ipString[1] = charge(str1.substring(0, i) + endIP(str1.substring(i, 32)));
        return ipString;
    }
}


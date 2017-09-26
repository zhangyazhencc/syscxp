package org.zstack.tunnel.manage;

import org.zstack.header.AbstractService;
import org.zstack.tunnel.header.monitor.TunnelMonitorVO;
import org.zstack.utils.gson.JSONObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-26.
 * @Description: .
 */
public class TestClass  {
    public static void main(String[] args){
        Map map = new HashMap();

        List<C1> vos = new ArrayList<>();
        C1 c1 = new C1();
        c1.setAge(123);
        c1.setName("nam31");
        c1.setUuid("asdfadsf");
        vos.add(c1);

        C1 c2 = new C1();
        c2.setAge(123);
        c2.setName("nam31");
        c2.setUuid("asdfadsf");
        vos.add(c2);

        map.put("tunnel_id","525931ce23e14187a4d0a9deaf95cf0d");
        map.put("tunnelMonitor",vos);

        String jsonResult = JSONObjectUtil.toJsonString(map);
        System.out.println(jsonResult);

       /* vo1.setHostAUuid("aaaaaaaaaaaaaaaaaaa");
        vo1.setMonitorAIp("11.22.33.44");

        vo2.setHostAUuid("bbbbbbbbbbbbbbbb");
        vo2.setMonitorAIp("22.22.33.44");

        vos.add(vo1);
        vos.add(vo2);*/
    }

    static class C1{
        C1(){

        }
        private String uuid;
        private String name;
        private Integer age;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}

package com.syscxp.tunnel.network;

import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import static com.syscxp.core.Platform.argerr;

/**
 * Create by DCY on 2018/3/12
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class L3NetworkValidateBase {
    private static final CLogger logger = Utils.getLogger(L3NetworkValidateBase.class);

    @Autowired
    private DatabaseFacade dbf;

    private void validate(APICreateL3NetworkMsg msg){
        //判断同一个用户的云网络名称是否已经存在
        Q q1 = Q.New(L3NetworkVO.class)
                .eq(L3NetworkVO_.name, msg.getName())
                .eq(L3NetworkVO_.accountUuid, msg.getAccountUuid());
        if (q1.isExists()) {
            throw new ApiMessageInterceptionException(argerr("该用户云网络名称【%s】已经存在!", msg.getName()));
        }

    }

    private void validate(APIUpdateL3NetworkMsg msg){
        L3NetworkVO vo = dbf.findByUuid(msg.getUuid(), L3NetworkVO.class);
        //判断同一个用户的云网络名称是否已经存在
        if (msg.getName() != null) {
            SimpleQuery<L3NetworkVO> q = dbf.createQuery(L3NetworkVO.class);
            q.add(L3NetworkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(L3NetworkVO_.accountUuid, SimpleQuery.Op.EQ, vo.getAccountUuid());
            q.add(L3NetworkVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("该用户云网络名称【%s】已经存在!", msg.getName()));
            }
        }
    }

    private void validate(APIDeleteL3NetworkMsg msg){
        if(Q.New(L3EndPointVO.class).eq(L3EndPointVO_.l3NetworkUuid, msg.getUuid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("请先删除该云网络下的连接点!"));
        }

    }
}

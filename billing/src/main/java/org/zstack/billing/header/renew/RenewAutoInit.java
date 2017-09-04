package org.zstack.billing.header.renew;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.sql.Timestamp;
import java.util.List;

public class RenewAutoInit implements InitializingBean {

    private static final CLogger logger = Utils.getLogger(RenewAutoInit.class);

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void afterPropertiesSet() throws Exception {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        SimpleQuery<RenewVO> q = dbf.createQuery(RenewVO.class);
        q.add(RenewVO_.isRenewAuto, SimpleQuery.Op.EQ, true);
        List<RenewVO> renews = q.list();
        for(RenewVO r : renews) {
            
        }

    }
}

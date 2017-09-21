package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.account.APIQueryAccountMsg;
import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.account.AccountVO;
import org.zstack.account.header.account.AccountVO_;
import org.zstack.account.header.identity.ProxyAccountRefVO;
import org.zstack.account.header.identity.ProxyAccountRefVO_;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.header.identity.AccountType;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.query.AbstractMysqlQuerySubQueryExtension;
import org.zstack.query.QueryUtils;

import java.util.List;

/**
 */
public class AccountSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
    @Autowired
    private IdentiyInterceptor identiyInterceptor;

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession()) {
            return null;
        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
        if (!identiyInterceptor.isResourceHavingAccountReference(entityClass)) {
            return null;
        }

        //String priKey = QueryUtils.getPrimaryKeyNameFromEntityClass(entityClass);\

        if(msg instanceof APIQueryAccountMsg){

            StringBuffer uuidlist = new StringBuffer() ;
            SimpleQuery<ProxyAccountRefVO> q = dbf.createQuery(ProxyAccountRefVO.class);
            q.add(ProxyAccountRefVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
            if(q.list() != null && q.list().size() > 0){
                for(Object pr : q.list()){
                    ProxyAccountRefVO ps = (ProxyAccountRefVO)pr;
                    uuidlist.append("'" + ps.getCustomerAcccountUuid().toString() + "',");
                }
            }

            if(uuidlist.length() > 0){
                uuidlist.substring(0,uuidlist.length());
                return String.format("%s.uuid in (%s)", inventoryClass.getSimpleName().toLowerCase()
                        , uuidlist) ;
            }else{
                return String.format("%s.uuid = '%s'", inventoryClass.getSimpleName().toLowerCase()
                        , msg.getSession().getAccountUuid()) ;
            }

        }else{
            return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase()
                    , msg.getSession().getAccountUuid()) ;
        }



    }
}

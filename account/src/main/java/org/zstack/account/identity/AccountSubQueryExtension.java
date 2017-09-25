package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.account.APIQueryAccountMsg;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.query.AbstractMysqlQuerySubQueryExtension;
import org.zstack.query.QueryUtils;

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

            if (((APIQueryAccountMsg) msg).isQueryCustomer()){

                return String.format("%s.uuid in (select customerAccountUuid from ProxyAccountRefVO where accountUuid = '%s')", inventoryClass.getSimpleName().toLowerCase()
                        , msg.getSession().getAccountUuid()) ;

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

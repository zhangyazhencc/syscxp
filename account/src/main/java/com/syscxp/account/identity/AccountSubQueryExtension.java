package com.syscxp.account.identity;

import com.syscxp.account.header.account.APIQueryAccountMsg;
import com.syscxp.query.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.query.AbstractMysqlQuerySubQueryExtension;

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

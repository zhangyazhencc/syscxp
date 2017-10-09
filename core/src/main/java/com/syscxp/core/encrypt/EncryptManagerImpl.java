package com.syscxp.core.encrypt;

import com.syscxp.core.db.DatabaseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.AbstractService;
import com.syscxp.header.core.encrypt.APIUpdateEncryptKeyEvent;
import com.syscxp.header.core.encrypt.APIUpdateEncryptKeyMsg;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.Query;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Created by mingjian.deng on 16/12/28.
 */
public class EncryptManagerImpl extends AbstractService {
    private static final CLogger logger = Utils.getLogger(EncryptManagerImpl.class);
    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg instanceof APIUpdateEncryptKeyMsg) {
            handle((APIUpdateEncryptKeyMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    @Transactional
    private void handle(APIUpdateEncryptKeyMsg msg){
        Set<Method> map = Platform.encryptedMethodsMap;
        logger.debug("decrypt passwords with old key and encrypt with new key");

        EncryptRSA rsa = new EncryptRSA();

        for (Method method: map) {
            String old_key = EncryptGlobalConfig.ENCRYPT_ALGORITHM.value();
            String new_key = msg.getEncryptKey();

            Class tempClass = method.getDeclaringClass();
            String className = tempClass.getSimpleName();
            String paramName = "password";

            String sql1 = "select uuid from "+className;
            Query q1 = dbf.getEntityManager().createNativeQuery(sql1);
            List uuidList = q1.getResultList();

            for (int i=0; i<uuidList.size(); i++){
                String sql2 = "select "+paramName+" from "+className+" where uuid = \""+uuidList.get(i)+"\"";
                Query q2 = dbf.getEntityManager().createNativeQuery(sql2);
                String preEncrypttxt = q2.getResultList().get(0).toString();
                try {

                    String password = (String) rsa.decrypt1(preEncrypttxt);
                    String newencrypttxt = (String) rsa.encrypt(password,msg.getEncryptKey());
                    String sql3 = "update "+className+" set "+paramName+" = :newencrypttxt where uuid = :uuid";

                    Query query = dbf.getEntityManager().createQuery(sql3);
                    query.setParameter("newencrypttxt",newencrypttxt);
                    query.setParameter("uuid",uuidList.get(i));

                    query.executeUpdate();

                }catch (Exception e){
                    logger.debug("sql exec error");
                    logger.debug(String.format("error is : %s",e.getMessage()));
                    e.printStackTrace();
                }

            }
        }
        try {
            rsa.updateKey(msg.getEncryptKey());
        }catch (Exception e){
            logger.debug("update key in encryptrsa error");
            logger.debug(String.format("error is : %s",e.getMessage()));
            e.printStackTrace();
        }

        APIUpdateEncryptKeyEvent evt = new APIUpdateEncryptKeyEvent(msg.getId());
        bus.publish(evt);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(EncryptGlobalConfig.SERVICE_ID);
    }
}

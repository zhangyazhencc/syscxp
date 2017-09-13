package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.account.header.account.AccountVO;
import org.zstack.account.header.user.UserVO;
import org.zstack.core.Platform;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.identity.AbstractIdentityInterceptor;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.identity.*;
import org.zstack.account.header.identity.*;
import org.zstack.utils.gson.JSONObjectUtil;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class IdentiyInterceptor extends AbstractIdentityInterceptor {


    public SessionInventory initSession(AccountVO account, UserVO user) {
        int maxLoginTimes = IdentityGlobalConfig.MAX_CONCURRENT_SESSION.value(Integer.class);
        SimpleQuery<SessionVO> query = getDbf().createQuery(SessionVO.class);
        query.add(SessionVO_.accountUuid, SimpleQuery.Op.EQ, account.getUuid());
        query.add(SessionVO_.userUuid, SimpleQuery.Op.EQ, user == null ? account.getUuid():user.getUuid());
        long count = query.count();
        if (count >= maxLoginTimes) {
            String err = String.format("Login sessions hit limit of max allowed concurrent login sessions, max allowed: %s", maxLoginTimes);
            throw new BadCredentialsException(err);
        }

        int sessionTimeout = IdentityGlobalConfig.SESSION_TIMEOUT.value(Integer.class);
        SessionVO svo = new SessionVO();
        svo.setUuid(Platform.getUuid());
        svo.setAccountUuid(account.getUuid());
        svo.setUserUuid(user == null ? account.getUuid():user.getUuid());
        svo.setType(account.getType());
        long expiredTime = getCurrentSqlDate().getTime() + TimeUnit.SECONDS.toMillis(sessionTimeout);
        svo.setExpiredDate(new Timestamp(expiredTime));
        svo = getDbf().persistAndRefresh(svo);

        SessionInventory session = svo.toSessionInventory();

        if (session.isUserSession()) {
            List<PolicyStatement> ps = getUserPolicyStatements(session.getUserUuid());
            session.setPolicyStatements(ps);
        }

        sessions.put(session.getUuid(), session);
        return session;
    }

    @Transactional
    public List<PolicyStatement> getUserPolicyStatements(String userUuid){
        List<PolicyStatement> policyStatements = new ArrayList<>();
        UserVO user = getDbf().findByUuid(userUuid, UserVO.class);

        for (RoleVO policy : user.getRoleSet()) {
            for (PolicyVO permission : policy.getPolicySet()) {
                PolicyStatement p = JSONObjectUtil.toObject(permission.getPermission(), PolicyStatement.class);
                p.setUuid(permission.getUuid());
                p.setName(permission.getName());
                policyStatements.add(p);
            }
        }

        return policyStatements;
    }

    @Override
    @Transactional
    public void removeExpiredSession(List<String> sessionUuids){
        String dsql = "delete from SessionVO s where CURRENT_TIMESTAMP  >= s.expiredDate";
        Query dq = getDbf().getEntityManager().createQuery(dsql);
        dq.executeUpdate();
    }

    @Override
    protected SessionInventory logOutSessionRemove(String sessionUuid){

        SessionVO svo = getDbf().findByUuid(sessionUuid, SessionVO.class);
        SessionInventory session = svo == null ? null : svo.toSessionInventory();

        getDbf().removeByPrimaryKey(sessionUuid, SessionVO.class);

        return session;
    }

    @Override
    protected SessionInventory getSessionInventory(String sessionUuid) {

        SessionVO svo = getDbf().findByUuid(sessionUuid, SessionVO.class);
        if (svo == null) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION,
                    "Session expired"));
        }
        SessionInventory session = svo.toSessionInventory();

        if (session.isUserSession()) {
            List<PolicyStatement> ps = getUserPolicyStatements(session.getUserUuid());
            session.setPolicyStatements(ps);
        }

        return session;
    }

}

package com.syscxp.account.identity;

import com.syscxp.account.header.account.AccountVO;
import com.syscxp.account.header.identity.PolicyVO;
import com.syscxp.account.header.identity.RoleVO;
import com.syscxp.account.header.identity.SessionVO;
import com.syscxp.account.header.identity.SessionVO_;
import com.syscxp.account.header.user.UserVO;
import com.syscxp.header.identity.IdentityErrors;
import com.syscxp.header.identity.PolicyStatement;
import com.syscxp.header.identity.SessionInventory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.identity.AbstractIdentityInterceptor;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.account.header.identity.*;
import com.syscxp.utils.gson.JSONObjectUtil;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class IdentiyInterceptor extends AbstractIdentityInterceptor {


    public SessionInventory initSession(AccountVO account, UserVO user) {
        int maxLoginTimes = IdentityGlobalConfig.MAX_CONCURRENT_SESSION.value(Integer.class);
        SimpleQuery<SessionVO> query = dbf.createQuery(SessionVO.class);
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
        svo = dbf.persistAndRefresh(svo);

        SessionInventory session = svo.toSessionInventory();

        if (session.isUserSession()) {
            List<PolicyStatement> ps = getUserPolicyStatements(session.getUserUuid());
            session.setPolicyStatements(ps);
        }

        sessions.put(session.getUuid(), session);
        return session;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<PolicyStatement> getUserPolicyStatements(String userUuid){
        List<PolicyStatement> policyStatements = new ArrayList<>();
        String sql = "select p" +
                " from PolicyVO p, UserRoleRefVO ref, RolePolicyRefVO rref" +
                " where p.uuid = rref.policyUuid" +
                " and rref.roleUuid = ref.roleUuid" +
                " and ref.userUuid = :uuid";
        TypedQuery<PolicyVO> q = dbf.getEntityManager().createQuery(sql, PolicyVO.class);
        q.setParameter("uuid", userUuid);
        List<PolicyVO> policys = q.getResultList();
        for (PolicyVO permission : policys) {
            PolicyStatement p = JSONObjectUtil.toObject(permission.getPermission(), PolicyStatement.class);
            p.setUuid(permission.getUuid());
            p.setName(permission.getName());
            policyStatements.add(p);
        }
        return policyStatements;
    }

    @Override
    @Transactional
    public void removeExpiredSession(List<String> sessionUuids){
        String dsql = "delete from SessionVO s where CURRENT_TIMESTAMP  >= s.expiredDate";
        Query dq = dbf.getEntityManager().createQuery(dsql);
        dq.executeUpdate();
    }

    @Override
    protected SessionInventory logOutSessionRemove(String sessionUuid){

        SessionVO svo = dbf.findByUuid(sessionUuid, SessionVO.class);
        SessionInventory session = svo == null ? null : svo.toSessionInventory();

        dbf.removeByPrimaryKey(sessionUuid, SessionVO.class);

        return session;
    }

    @Override
    protected SessionInventory getSessionInventory(String sessionUuid) {

        SessionVO svo = dbf.findByUuid(sessionUuid, SessionVO.class);
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

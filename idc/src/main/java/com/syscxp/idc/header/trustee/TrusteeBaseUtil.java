package com.syscxp.idc.header.trustee;

import com.syscxp.header.billing.ProductChargeModel;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;

@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TrusteeBaseUtil {

//    private static final CLogger logger = Utils.getLogger(TrusteeBaseUtil.class);
//    @Autowired
//    private DatabaseFacade dbf;
//    @Autowired
//    private RESTFacade restf;

       public Timestamp getExpireDate(Timestamp oldTime, ProductChargeModel chargeModel, int duration) {
        Timestamp newTime = oldTime;
        if (chargeModel == ProductChargeModel.BY_YEAR) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusYears(duration));
        } else if (chargeModel == ProductChargeModel.BY_MONTH) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusMonths(duration));
        } else if (chargeModel == ProductChargeModel.BY_WEEK) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusWeeks(duration));
        } else if (chargeModel == ProductChargeModel.BY_DAY) {
            newTime = Timestamp.valueOf(oldTime.toLocalDateTime().plusDays(duration));
        }
        return newTime;
    }

}

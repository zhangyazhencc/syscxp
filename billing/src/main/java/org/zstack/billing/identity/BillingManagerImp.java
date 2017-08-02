package org.zstack.billing.identity;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.billing.header.identity.APIGetAccountBalanceMsg;
import org.zstack.billing.header.identity.APIGetAccountBalanceReply;
import org.zstack.billing.header.identity.AccountBalanceInventory;
import org.zstack.billing.header.identity.AccountBalanceVO_;
import org.zstack.billing.header.identity.AccountBalanceVO;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.db.HardDeleteEntityExtensionPoint;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.db.SoftDeleteEntityExtensionPoint;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.apimediator.GlobalApiMessageInterceptor;
import org.zstack.header.managementnode.PrepareDbInitialValueExtensionPoint;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;

public class BillingManagerImp extends AbstractService implements BillingManager, PrepareDbInitialValueExtensionPoint,
SoftDeleteEntityExtensionPoint, HardDeleteEntityExtensionPoint,
GlobalApiMessageInterceptor, ApiMessageInterceptor{
	
	@Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private EventFacade evtf;
    @Autowired
    private GlobalConfigFacade gcf;
	
	

	@Override
	@MessageSafe
	public void handleMessage(Message msg) {
		if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
		
	}
	
	 private void handleLocalMessage(Message msg) {
         bus.dealWithUnknownMessage(msg);
    }
	
	  private void handleApiMessage(APIMessage msg) {
	        if (msg instanceof APIGetAccountBalanceMsg) {
	            handle((APIGetAccountBalanceMsg) msg);
	        } else {
	            bus.dealWithUnknownMessage(msg);
	        }
	    }


	private void handle(APIGetAccountBalanceMsg msg) {
		if(msg.getUuid()!=null && !"".equals(msg.getUuid())) {
			SimpleQuery<AccountBalanceVO> q = dbf.createQuery(AccountBalanceVO.class);
			q.add(AccountBalanceVO_.uuid, Op.EQ, msg.getUuid());
			AccountBalanceVO a = q.find();
			AccountBalanceInventory inventory = new AccountBalanceInventory();
			inventory.setUuid(a.getUuid());
			inventory.setCashBalance(a.getCashBalance());
			inventory.setPresentBalance(a.getPresentBalance());
			inventory.setCreditPoint(a.getCreditPoint());
			APIGetAccountBalanceReply reply = new APIGetAccountBalanceReply();
			reply.setInventory(inventory);
			bus.reply(msg, reply);
		}
	}

	@Override
	public String getId() {
		 return bus.makeLocalServiceId(BillingConstant.SERVICE_ID);
	}

	@Override
	public boolean start() {
		return false;
	}

	@Override
	public boolean stop() {
		return false;
	}

	@Override
	public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
		  if (msg instanceof APIGetAccountBalanceMsg) {
	            validate((APIGetAccountBalanceMsg) msg);
	        } 
		   return msg;
	}

	private void validate(APIGetAccountBalanceMsg msg) {
         if(msg.getUuid()==null || "".equals(msg.getUuid())) {
        	 throw new ApiMessageInterceptionException(Platform.argerr("%uuid must be not null", "uuid"));
         }		
	}

	@Override
	public List<Class> getMessageClassToIntercept() {
		return null;
	}

	@Override
	public InterceptorPosition getPosition() {
		return null;
	}

	@Override
	public List<Class> getEntityClassForHardDeleteEntityExtension() {
		return null;
	}

	@Override
	public void postHardDelete(Collection entityIds, Class entityClass) {
		
	}

	@Override
	public List<Class> getEntityClassForSoftDeleteEntityExtension() {
		return null;
	}

	@Override
	public void postSoftDelete(Collection entityIds, Class entityClass) {
		
	}

	@Override
	public void prepareDbInitialValue() {
		
	}

}

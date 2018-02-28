package com.syscxp.core.rest;

import com.syscxp.core.ansible.AnsibleConstant;
import com.syscxp.core.ansible.AnsibleLogCmd;
import com.syscxp.core.logging.Log;
import com.syscxp.core.logging.LogLevel;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xing5 on 2016/6/15.
 */
@Controller
public class AnsibleLogController {

    @RequestMapping(value = AnsibleConstant.AGENT_ANSIBLE_LOG_PATH_FROMAT, method = {RequestMethod.PUT, RequestMethod.POST})
    public  @ResponseBody
    String log(@PathVariable String uuid, @RequestBody String body) {
        AnsibleLogCmd cmd = JSONObjectUtil.toObject(body, AnsibleLogCmd.class);
        if (cmd.getParameters() != null) {
            new Log(uuid).setLevel(LogLevel.valueOf(cmd.getLevel())).log(cmd.getLabel(), cmd.getParameters());
        } else {
            new Log(uuid).setLevel(LogLevel.valueOf(cmd.getLevel())).log(cmd.getLabel());
        }

        return null;
    }
}

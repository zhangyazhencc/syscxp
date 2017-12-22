package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SQL;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.vpn.vpn.VpnConstant;
import com.syscxp.header.vpn.agent.ClientInfo;
import com.syscxp.header.vpn.vpn.VpnCertVO;
import com.syscxp.header.vpn.vpn.VpnSystemVO;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.ZipUtils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

@Controller
public class DownloadController {
    private static final CLogger logger = Utils.getLogger(DownloadController.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;

    private String zipName = VpnConstant.KEYS_DIR + ".zip";

    private String charset = "GBK";

    @RequestMapping(value = VpnConstant.VPN_REPORT_PATH, method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public String report(@RequestBody String body) {
        ClientInfo info = JSONObjectUtil.toObject(body, ClientInfo.class);
        VpnSystemVO vo = dbf.findByUuid(info.getId(), VpnSystemVO.class);
        if (vo == null) {
            vo = new VpnSystemVO();
            dbf.persistAndRefresh(copy(info, vo));
        } else {
            dbf.updateAndRefresh(copy(info, vo));
        }
        return "success";
    }

    private VpnSystemVO copy(ClientInfo info, VpnSystemVO vo) {
        vo.setUuid(info.getId());
        vo.setVpn(info.getVpn());
        vo.setTap(info.getTap());
        vo.setSystem(info.getSystem());
        return vo;
    }

    @RequestMapping(value = RESTConstant.REST_API_CALL + VpnConstant.CONF_DOWNLOAD_PATH, method = {RequestMethod.GET})
    @Deferred
    public void downLoadConfFile(@PathVariable String uuid, HttpServletResponse response) throws IOException {

        String vpnUuid = valid(uuid, VpnVO.class.getSimpleName());

        File root = PathUtil.findFileOnClassPath(VpnConstant.KEYS_DIR, true);

        VpnVO vpn = dbf.findByUuid(vpnUuid, VpnVO.class);
        File clientConf = new File(root, VpnConstant.CLIENT_CONF_PATH);
        try {

            FileUtils.writeStringToFile(clientConf, vpn.getClientConf(), charset);

            download(root, response);

            Defer.defer(new Runnable() {
                @Override
                public void run() {
                    clientConf.delete();
                }
            });
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    @RequestMapping(value = RESTConstant.REST_API_CALL + VpnConstant.CERT_DOWNLOAD_PATH, method = {RequestMethod.GET})
    @Deferred
    public void downLoadCertFile(@PathVariable String uuid, HttpServletResponse response) throws IOException {

        String vpnCertUuid = valid(uuid, VpnCertVO.class.getSimpleName());

        File root = PathUtil.findFileOnClassPath(VpnConstant.KEYS_DIR, true);

        VpnCertVO cert = dbf.findByUuid(vpnCertUuid, VpnCertVO.class);

        File caCrt = new File(root, VpnConstant.CA_CRT_PATH);
        File clientCrt = new File(root, VpnConstant.CLIENT_CRT_PATH);
        File clientKey = new File(root, VpnConstant.CLIENT_KEY_PATH);
        try {
            FileUtils.writeStringToFile(caCrt, cert.getCaCert(), charset);
            FileUtils.writeStringToFile(clientCrt, cert.getClientCert(), charset);
            FileUtils.writeStringToFile(clientKey, cert.getClientKey(), charset);

            download(root, response);

            Defer.defer(new Runnable() {
                @Override
                public void run() {
                    caCrt.delete();
                    clientCrt.delete();
                    clientKey.delete();
                }
            });
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }

    private void download(File root, HttpServletResponse response) throws IOException {

        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipName);

        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        ZipUtils.doCompress(root, out);
        response.flushBuffer();
    }

    private String valid(String uuid, String type) {
        String decode = new String(Base64.decode(uuid.getBytes()));
        String[] list = decode.split(":");

        String sql = String.format("select r.accountUuid from %s r where r.uuid = :uuid ", type);
        String accountUuid = SQL.New(sql).param("uuid", list[0]).find();
        String md5 = DigestUtils.md5Hex(accountUuid + list[1] + VpnConstant.GENERATE_KEY);
        if (!md5.equals(list[2]) || System.currentTimeMillis() - Long.valueOf(list[1]) > CoreGlobalProperty.INNER_MESSAGE_EXPIRE * 1000)
            throw new CloudRuntimeException("The download link is expired");
        return list[0];
    }

    @ExceptionHandler(Exception.class)
    public void exception(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        StringBuilder sb = new StringBuilder(String.format("Error when calling %s", request.getRequestURI()));
        sb.append(String.format("\nexception message: %s", ex.getMessage()));
        logger.debug(sb.toString(), ex);
        response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, sb.toString());
    }

}

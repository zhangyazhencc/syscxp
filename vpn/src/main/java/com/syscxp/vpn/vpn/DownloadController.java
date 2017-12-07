package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.ansible.AnsibleConstant;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SQL;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.vpn.VpnCertVO;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.ZipUtils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Controller
public class DownloadController {
    private static final CLogger logger = Utils.getLogger(DownloadController.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;

    private String zipName = VpnConstant.KEYS_DIR + ".zip";

    @RequestMapping(value = "/download/conf/{uuid}", method = {RequestMethod.GET, RequestMethod.POST})
    @Deferred
    public void downLoadConfFile(@PathVariable String uuid, HttpServletResponse response) throws IOException{

        String vpnUuid = valid(uuid, VpnVO.class.getSimpleName());

        File root = PathUtil.findFileOnClassPath(VpnConstant.KEYS_DIR,true);

        VpnVO vpn = dbf.findByUuid(vpnUuid, VpnVO.class);
        File clientConf = new File(root, VpnConstant.CLIENT_CONF_PATH);
        try {

            FileUtils.writeStringToFile(clientConf, vpn.getClientConf());

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

    @RequestMapping(value = "/download/cert/{uuid}", method = {RequestMethod.GET, RequestMethod.POST})
    @Deferred
    public void downLoadCertFile(@PathVariable String uuid, HttpServletResponse response) throws IOException {

        String vpnCertUuid = valid(uuid, VpnCertVO.class.getSimpleName());

        File root = PathUtil.findFileOnClassPath(VpnConstant.KEYS_DIR, true);

        VpnCertVO cert = dbf.findByUuid(vpnCertUuid, VpnCertVO.class);

        File caCrt = new File(root, VpnConstant.CA_CRT_PATH);
        File clientCrt = new File(root, VpnConstant.CLIENT_CRT_PATH);
        File clientKey = new File(root, VpnConstant.CLIENT_KEY_PATH);
        try {
            FileUtils.writeStringToFile(caCrt, cert.getCaCert());
            FileUtils.writeStringToFile(clientCrt, cert.getClientCert());
            FileUtils.writeStringToFile(clientKey, cert.getClientKey());

            download(root, response);

            Defer.defer(new Runnable() {
                @Override
                public void run() {
                    caCrt.delete();
                    clientCrt.delete();
                    clientKey.delete();
                }
            });
        }catch (Throwable t) {
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
        HttpEntity<String> entity = restf.httpServletRequestToHttpEntity(request);
        StringBuilder sb = new StringBuilder(String.format("Error when calling %s", request.getRequestURI()));
        sb.append(String.format("\nexception message: %s", "download failed"));
        logger.debug(sb.toString(), ex);
        response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, sb.toString());
    }

}

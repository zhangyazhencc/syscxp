package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SQL;
import com.syscxp.core.defer.Defer;
import com.syscxp.core.defer.Deferred;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.vpn.agent.ClientInfo;
import com.syscxp.header.vpn.vpn.VpnCertVO;
import com.syscxp.header.vpn.vpn.VpnConstant;
import com.syscxp.header.vpn.vpn.VpnSystemVO;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.ZipUtils2;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.path.PathUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjie
 */
@Controller
public class DownloadController {
    private static final CLogger LOGGER = Utils.getLogger(DownloadController.class);

    @Autowired
    private DatabaseFacade dbf;

    private static final String KEYS_ZIP_NAME = "keys.zip";

    private static final String CONF_ZIP_NAME = "conf.zip";

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
    @ResponseBody
    @Deferred
    public ResponseEntity<byte[]> downLoadConfFile(@PathVariable String uuid) throws IOException {

        String vpnUuid = valid(uuid, VpnVO.class.getSimpleName());

        File root = PathUtil.findFileOnClassPath(VpnConstant.KEYS_DIR, true);

        VpnVO vpn = dbf.findByUuid(vpnUuid, VpnVO.class);
        File clientConf = new File(root, VpnConstant.CLIENT_CONF_PATH);

        FileUtils.writeStringToFile(clientConf, vpn.getClientConf());
        File zip = ZipUtils2.zip(root.getAbsolutePath());

        Defer.defer(() -> {
            clientConf.delete();
            zip.delete();
        });

        return download(zip, CONF_ZIP_NAME);
    }

    @RequestMapping(value = RESTConstant.REST_API_CALL + VpnConstant.CERT_DOWNLOAD_PATH, method = {RequestMethod.GET})
    @ResponseBody
    @Deferred
    public ResponseEntity<byte[]> downLoadCertFile(@PathVariable String uuid) throws IOException {

        String vpnCertUuid = valid(uuid, VpnCertVO.class.getSimpleName());

        File root = PathUtil.findFileOnClassPath(VpnConstant.KEYS_DIR, true);

        VpnCertVO cert = dbf.findByUuid(vpnCertUuid, VpnCertVO.class);
        File caCrt = new File(root, VpnConstant.CA_CRT_PATH);
        File clientCrt = new File(root, VpnConstant.CLIENT_CRT_PATH);
        File clientKey = new File(root, VpnConstant.CLIENT_KEY_PATH);
        FileUtils.writeStringToFile(caCrt, cert.getCaCert());
        FileUtils.writeStringToFile(clientCrt, cert.getClientCert());
        FileUtils.writeStringToFile(clientKey, cert.getClientKey());

        File zip = ZipUtils2.zip(root.getAbsolutePath());

        Defer.defer(() -> {
            caCrt.delete();
            clientCrt.delete();
            clientKey.delete();
            zip.delete();
        });
        return download(zip, KEYS_ZIP_NAME);

    }

    private ResponseEntity<byte[]> download(File zipFile, String zipName) throws IOException {


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", zipName);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(zipFile), headers, HttpStatus.CREATED);
    }

    private String valid(String uuid, String type) {
        String decode = new String(Base64.decode(uuid.getBytes()));
        String[] list = decode.split(":");

        String sql = String.format("select r.accountUuid from %s r where r.uuid = :uuid ", type);
        String accountUuid = SQL.New(sql).param("uuid", list[0]).find();
        String md5 = DigestUtils.md5Hex(accountUuid + list[1] + VpnConstant.URL_GENERATE_KEY);
        if (!md5.equals(list[2]) || System.currentTimeMillis() - Long.valueOf(list[1]) > TimeUnit.SECONDS.toMillis(CoreGlobalProperty.INNER_MESSAGE_EXPIRE)) {
            throw new CloudRuntimeException("The download link is expired");
        }
        return list[0];
    }

    @ExceptionHandler(Exception.class)
    public void exception(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        StringBuilder sb = new StringBuilder(String.format("Error when calling %s", request.getRequestURI()));
        sb.append(String.format("\nexception message: %s", ex.getMessage()));
        LOGGER.debug(sb.toString(), ex);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), sb.toString());
    }

}

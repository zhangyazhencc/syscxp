package com.syscxp.vpn.vpn;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SQL;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.header.vpn.vpn.VpnCertVO;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.ZipUtils;
import com.syscxp.utils.logging.CLogger;
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

    @RequestMapping(value = "/download/{type}/{uuid}", method = {RequestMethod.GET, RequestMethod.POST})
    public void downLoadZipFile(@PathVariable String type, @PathVariable String uuid, HttpServletResponse response) throws IOException {

        String zipName = "";
        List<File> fileList = new ArrayList<>();
        if ("conf".equals(type)) {
            String vpnUuid = valid(uuid, VpnVO.class.getSimpleName());
            fileList = createConfFiles(vpnUuid);
            zipName = "keys.zip";
        } else if ("crt".equals(type)) {
            String vpnCertUuid = valid(uuid, VpnCertVO.class.getSimpleName());
            fileList = createCertFiles(vpnCertUuid);
            zipName = "conf.zip";
        }

        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipName);

        try (ZipOutputStream out = new ZipOutputStream(response.getOutputStream())) {
            for (File file : fileList) {
                ZipUtils.doCompress(file.getName(), out);
                response.flushBuffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private List<File> createCertFiles(String vpnCertUuid) throws IOException {
        VpnCertVO cert = dbf.findByUuid(vpnCertUuid, VpnCertVO.class);
        List<File> fileList = new ArrayList<>();
        fileList.add(createFile("ca.crt", cert.getCaCert()));
        fileList.add(createFile("client.crt", cert.getClientCert()));
        fileList.add(createFile("client.key", cert.getClientKey()));
        return fileList;
    }

    private List<File> createConfFiles(String vpnUuid) throws IOException {
        VpnVO vpn = dbf.findByUuid(vpnUuid, VpnVO.class);
        List<File> fileList = new ArrayList<>();
        fileList.add(createFile("client.conf", vpn.getClientConf()));
        return fileList;
    }

    private File createFile(String fileName, String str) throws IOException {
        File file = new File(fileName);
        FileUtils.writeStringToFile(file, str);
        return file;
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

package scripts

import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils
import com.syscxp.header.exception.CloudRuntimeException
import com.syscxp.header.identity.SuppressCredentialCheck
import com.syscxp.header.message.*
import com.syscxp.header.query.APIQueryMessage
import com.syscxp.header.rest.APINoSee
import com.syscxp.header.rest.RestRequest
import com.syscxp.header.rest.SDK
import com.syscxp.rest.sdk.SdkTemplate
import com.syscxp.rest.sdk.SdkFile
import com.syscxp.utils.FieldUtils
import com.syscxp.utils.Utils
import com.syscxp.utils.logging.CLogger

import java.lang.reflect.Field

/**
 * Created by xing5 on 2016/12/9.
 */
class SdkApiTemplate implements SdkTemplate {
    CLogger logger = Utils.getLogger(SdkApiTemplate.class)

    Class apiMessageClass
    RestRequest requestAnnotation

    Set<Class> enumClasses = []

    String resultClassName
    boolean isQueryApi

    String contentPath

    SdkApiTemplate(Class apiMessageClass) {
        try {
            this.apiMessageClass = apiMessageClass
            this.requestAnnotation = apiMessageClass.getAnnotation(RestRequest.class)

            String baseName = requestAnnotation.responseClass().simpleName
            baseName = StringUtils.removeStart(baseName, "API")
            baseName = StringUtils.removeEnd(baseName, "Event")
            baseName = StringUtils.removeEnd(baseName, "Reply")

            resultClassName = StringUtils.capitalize(baseName)
            resultClassName = "${resultClassName}Result"

            isQueryApi = APIQueryMessage.class.isAssignableFrom(apiMessageClass)
        } catch (Throwable t) {
            throw new CloudRuntimeException(String.format("failed to make SDK for the class[%s]", apiMessageClass), t)
        }
    }

    def normalizeApiName() {
        def name = StringUtils.removeStart(apiMessageClass.getSimpleName(), "API")
        name = StringUtils.removeEnd(name, "Msg")
        return StringUtils.capitalize(name)
    }

    def generateClassName() {
        return String.format("%sAction", normalizeApiName())
    }

    def generateFields() {
        if (isQueryApi) {
            return ""
        }

        def fields = FieldUtils.getAllFields(apiMessageClass)

        APIMessage msg = (APIMessage) apiMessageClass.newInstance()

        def output = []

        OverriddenApiParams oap = apiMessageClass.getAnnotation(OverriddenApiParams.class)
        Map<String, APIParam> overriden = [:]
        if (oap != null) {
            for (OverriddenApiParam op : oap.value()) {
                overriden.put(op.field(), op.param())
            }
        }

        for (Field f : fields) {
            if (f.isAnnotationPresent(APINoSee.class)) {
                continue
            }

            APIParam apiParam = overriden.containsKey(f.name) ? overriden[f.name] : f.getAnnotation(APIParam.class)

            def annotationFields = []
            if (apiParam != null) {
                annotationFields.add(String.format("required = %s", apiParam.required()))
                if (apiParam.validValues().length > 0) {
                    annotationFields.add(String.format("validValues = {%s}", { ->
                        def vv = []
                        for (String v : apiParam.validValues()) {
                            vv.add("\"${v}\"")
                        }
                        return vv.join(",")
                    }()))
                }
                if (!apiParam.validRegexValues().isEmpty()) {
                    annotationFields.add(String.format("validRegexValues = \"%s\"", StringEscapeUtils.escapeJava(apiParam.validRegexValues())))
                }
                if (apiParam.maxLength() != Integer.MIN_VALUE) {
                    annotationFields.add(String.format("maxLength = %s", apiParam.maxLength()))
                }
                if (apiParam.minLength() != 0) {
                    annotationFields.add(String.format("minLength = %s", apiParam.minLength()))
                }
                annotationFields.add(String.format("nonempty = %s", apiParam.nonempty()))
                annotationFields.add(String.format("nullElements = %s", apiParam.nullElements()))
                annotationFields.add(String.format("emptyString = %s", apiParam.emptyString()))
                if (apiParam.numberRange().length > 0) {
                    def nr = apiParam.numberRange() as List<Long>
                    def ns = []
                    nr.forEach({ n -> return ns.add("${n}L") })

                    annotationFields.add(String.format("numberRange = {%s}", ns.join(",")))

                }

                annotationFields.add(String.format("noTrim = %s", apiParam.noTrim()))
            } else {
                annotationFields.add(String.format("required = false"))
            }

            def fieldTypeName = f.getType().getName()

            if (Enum.class.isAssignableFrom(f.getType())) {
                fieldTypeName = String.format("%s", f.getType().getSimpleName())
                if (!enumClasses.contains(f.getType())) {
                    enumClasses.add(f.getType())
                }
            }

            def fs = """\
    @Param(${annotationFields.join(", ")})
    public ${fieldTypeName.toString()} ${f.getName()}${
                { ->
                    f.accessible = true

                    Object val = f.get(msg)
                    if (val == null) {
                        return ";"
                    }

                    if (val instanceof String) {
                        return " = \"${StringEscapeUtils.escapeJava(val.toString())}\";"
                    } else {
                        return " = ${val.toString()};"
                    }
                }()
            }
"""
            output.add(fs.toString())
        }

        if (!APISyncCallMessage.class.isAssignableFrom(apiMessageClass)) {
            output.add("""\
    @NonAPIParam
    public long timeout = -1;

    @NonAPIParam
    public long pollingInterval = -1;
""")
        }

        return output.join("\n")
    }

    def generateMethods(String path) {
        def ms = []
        ms.add("""\
    private Result makeResult(ApiResult res) {
        Result ret = new Result();
        if (!Constants.RESULT_CODE.equals(res.code)) {
            ret.code = res.code;
            ret.message = res.message;
            return ret;
        }
        
        ${resultClassName} value = res.getResult(${resultClassName}.class, new SourceClassMap());
        ret.value = value == null ? new ${resultClassName}() : value; 

        return ret;
    }
""")

        ms.add("""\
    public Result call() {
        ApiResult res = ZSClient.call(this);
        return makeResult(res);
    }
""")

        ms.add("""\
    public void call(final Completion<Result> completion) {
        ZSClient.call(this, new InternalCompletion() {
            @Override
            public void complete(ApiResult res) {
                completion.complete(makeResult(res));
            }
        });
    }
""")

        ms.add("""\
    public Map<String, Parameter> getParameterMap() {
        return parameterMap;
    }
    
    public Map<String, Parameter> getNonAPIParameterMap() {
        return nonAPIParameterMap;
    }
""")

        ms.add("""\
    public RestInfo getRestInfo() {
        RestInfo info = new RestInfo();
        info.setHttpMethod("${requestAnnotation.method().name()}");
        info.setPath("${path}");
        info.setNeedSession(${!apiMessageClass.isAnnotationPresent(SuppressCredentialCheck.class)});
        info.setNeedPoll(${!APISyncCallMessage.class.isAssignableFrom(apiMessageClass)});
        info.setParameterName("${
            requestAnnotation.isAction() ? StringUtils.uncapitalize(normalizeApiName()) : requestAnnotation.parameterName()
        }");
        
        return info;
    }
""")

        return ms.join("\n")
    }

    def generateAction(String clzName, String path) {
        def f = new SdkFile()
        f.fileName = "${clzName}.java"
        f.content = """package com.syscxp.sdk.${contentPath};

import java.util.HashMap;
import java.util.Map;
import com.syscxp.sdk.common.*;

public class ${clzName} extends ${isQueryApi ? "QueryAction" : "AbstractAction"} {

    private static final HashMap<String, Parameter> parameterMap = new HashMap<>();
    
    private static final HashMap<String, Parameter> nonAPIParameterMap = new HashMap<>();

    public static class Result {
        public String code;
        public String message;
        public ${resultClassName} value;

        public Result throwExceptionIfError() {
            if (code != null) {
                throw new ApiException(
                    String.format("error[code: %s, message: %s]", code, message)
                );
            }
            
            return this;
        }
    }

${generateFields()}

${generateMethods(path)}
}
""".toString()

        return f
    }

    def resolveEnumClass() {
        def ret = []
        if (!enumClasses.isEmpty()) {
            for (Class clz : enumClasses) {
                def output = []

                for (Enum e : clz.getEnumConstants()) {
                    output.add("\t${e.name()},")
                }

                SdkFile file = new SdkFile()
                file.fileName = "${clz.getSimpleName()}.java"
                file.content = """package com.syscxp.sdk.${contentPath};

public enum ${clz.getSimpleName()} {
${output.join("\n")}
}
"""
                ret.add(file)
            }
        }
        return ret
    }

    def generateAction(String contentPath) {
        SDK sdk = apiMessageClass.getAnnotation(SDK.class)
        if (sdk != null && sdk.actionsMapping().length != 0) {
            def ret = []

            for (String ap : sdk.actionsMapping()) {
                String[] aps = ap.split("=")
                if (aps.length != 2) {
                    throw new CloudRuntimeException("Invalid actionMapping[${ap}] of the class[${apiMessageClass.name}]," +
                            "an action mapping must be in format of actionName=restfulPath")
                }

                String aname = aps[0].trim()
                String restPath = aps[1].trim()

                if (!requestAnnotation.optionalPaths().contains(restPath)) {
                    throw new CloudRuntimeException("Cannot find ${restPath} in the 'optionalPaths' of the @RestPath of " +
                            "the class[${apiMessageClass.name}]")
                }

                aname = StringUtils.capitalize(aname)

                ret.add(generateAction("${aname}Action", restPath))
            }

            return ret
        } else {
            String path = requestAnnotation.path()

            if (contentPath != "null") {
                path = contentPath
            }
            if (path == "") {
                throw new CloudRuntimeException("'path' is set to '' on the class[${apiMessageClass.name}] but " +
                        "'contentPath' is also set to ''")
            }
            if (requestAnnotation.path() != path && !requestAnnotation.optionalPaths().contains(path)) {
                throw new CloudRuntimeException("Cannot find ${path} in the 'optionalPaths' of the @RestPath of " +
                        "the class[${apiMessageClass.name}]")
            }

            return [generateAction(generateClassName(), path)]
        }
    }

    @Override
    List<SdkFile> generate(String contentPath) {
        this.contentPath = contentPath
        def ret = []
        try {
            ret.addAll(generateAction(contentPath))
            ret.addAll(resolveEnumClass())
            return ret
        } catch (Exception e) {
            logger.warn("failed to generate SDK for ${apiMessageClass.name}")
            throw e
        }
    }
}

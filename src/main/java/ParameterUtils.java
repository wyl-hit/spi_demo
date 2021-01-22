import javax.servlet.http.HttpServletRequest;

public class ParameterUtils {

    /**
     * 从HttpServletRequest中获取参数值，如果为null，那么返回默认值
     * @param request
     * @param name
     * @param nullValue
     * @return
     */
    public static String getString(HttpServletRequest request, String name, String nullValue) {
        String value = request.getParameter(name);
        return value == null ? nullValue : value;
    }
}

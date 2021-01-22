import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class AuthTokenUtil {

    public static byte[] readAsBytes(HttpServletRequest request){
        int len = request.getContentLength();
        byte[] buffer = new byte[len];
        try(ServletInputStream in = request.getInputStream()){
            in.read(buffer, 0, len);
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

    public static String genSignature(String secretKey, byte[] binaryparams){
        return DigestUtils.sha1Hex(binaryparams);
    }

    public static boolean isValidToken(String secretKey, HttpServletRequest request) {
        String token = request.getHeader("X-Open-Signature");
        String timeStamp = ParameterUtils.getString(request, "timestamp", "");
        if (Strings.isNullOrEmpty(timeStamp)) {
            return false;
        }
        if (System.currentTimeMillis() - NumberUtils.toLong(timeStamp, 0) > 60*1000) {
            return false;
        }
        String signature = genSignature(secretKey,readAsBytes(request));
        return token.equals(signature);
    }

}

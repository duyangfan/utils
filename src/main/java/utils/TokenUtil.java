package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Token 用jwt生成
 */
public class TokenUtil {

    public static final int JWT_TTL = 30*60*1000; //millisecond   30分钟
    public static final String STRINGKEY = "www.sinosoft.com";


    /**
     * 由字符串生成加密key
     * @return
     */
    public static SecretKey generalKey(){
        byte[] encodedKey = Base64.decodeBase64(STRINGKEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 创建jwt
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     * @throws Exception
     */
    public static String createJWT(String id, String subject, long ttlMillis) throws Exception {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey key = generalKey();
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(signatureAlgorithm, key);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * 解密jwt
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception{
        SecretKey key = generalKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    public static void main(String[] args) throws Exception {
        String id=Util.getUUID();
        Map<String,String> user = new HashMap<String,String>();
        user.put("userId", "zhengzhenglei");
        user.put("code", "001");
        String subject=JsonUtils.toJson(user);
        String token=TokenUtil.createJWT(id, subject, JWT_TTL);
        System.out.println(token);
        Claims claims=TokenUtil.parseJWT(token);
        System.out.println(JsonUtils.toJson(claims));
        System.out.println(claims.getSubject());
        System.out.println(claims.getId());
        System.out.println(claims.getIssuedAt());
        System.out.println(claims.getExpiration());

    }

}
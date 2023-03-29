package ru.artemchegushka.restjwtauth.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.artemchegushka.restjwtauth.exception.MyTokenException;
import ru.artemchegushka.restjwtauth.exception.UserException;
import ru.artemchegushka.restjwtauth.service.MyUserDetails;

import java.time.ZonedDateTime;
import java.util.Date;

//Класс генерации jwt токена и для того что б достать логин из токена
@Service
public class JwtService {
    @Value("${secret.jwt.key}")
    private String secret;


    // Генерация токена
    public String generateToken(String username) {
        Date liveTimeToken = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
        return JWT
                .create()
                .withSubject("User details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("springSecurityJWTApp")
                .withExpiresAt(liveTimeToken)
                .sign(Algorithm.HMAC256(secret));
    }

    // Получаем интересующие нас данные
    public String extractUserName(String token) {
//   Можно вот таким способом получить
//   return getClaimByKey(token,map->map.get("username")).asString();
        return jwtVerifier(token).getClaim("username").asString();
    }

// Просто образец, как можно сделать получение Claim
//    public <T> T getClaimByKey(String token, Function<Map<String, Claim>, T> claimsMap) {
//        final Map<String, Claim> claims = getAllClaims(token);
//        return claimsMap.apply(claims);
//    }

// Возвращает все claims
//    public Map<String, Claim> getAllClaims(String token) {
//        return jwtVerifier(token).getClaims();
//    }


    public boolean isTokenValid(String token, MyUserDetails myUserDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(myUserDetails.getUsername())) && isTokenAlive(token);

    }

    //    Проверка, истекло ли время действия токена
    private boolean isTokenAlive(String token) {
//    Если jwtVerifier(token).getExpiresAt() это дата которая больше чем дата в данное время,
//    значит срок действия токена не истек
        return jwtVerifier(token).getExpiresAt().after(new Date());
    }

    //   Метод для валидации токена
    private DecodedJWT jwtVerifier(String token) {
// Только токен с этими данными, а их мы указали при генерации токена будет проходить валидацию.
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("springSecurityJWTApp")
                .build();

        try {
            return verifier.verify(token);
        }
        catch (JWTDecodeException e) {
            throw new MyTokenException("Не верный токен, необходимо авторизоваться.");
        }catch (TokenExpiredException e){
            throw new MyTokenException("Срок действия токена истек, авторизуйтесь повторно");
        }catch (JWTVerificationException e){
            throw new MyTokenException("Ошибка токена, необходимо авторизоваться");
        }

    }
}

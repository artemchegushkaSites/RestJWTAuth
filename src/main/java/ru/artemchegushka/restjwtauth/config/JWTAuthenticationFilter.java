package ru.artemchegushka.restjwtauth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.artemchegushka.restjwtauth.exception.FilterExceptHandler;
import ru.artemchegushka.restjwtauth.service.MyUserDetails;
import ru.artemchegushka.restjwtauth.service.MyUserDetailsService;

import java.io.IOException;

// При каждом обращении к нашему серверу - клиент будет отправлять токен, мы должны его отлавливать во всех запросах,
// получать зашифрованные в нем данные для аутентификации и аутентифицировать пользователя.
// Этот класс будет отлавливать все запросы и будет проверять header, в котором и будет содержаться токен

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;
    private final FilterExceptHandler handler;

    @Autowired
    public JWTAuthenticationFilter(JwtService jwtService, MyUserDetailsService userDetailsService, FilterExceptHandler handler) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handler = handler;
    }


    @Override
    protected void doFilterInternal(
//            Запрос
            @NonNull HttpServletRequest request,
//            Ответ
            @NonNull HttpServletResponse response,
//            Цепочка фильтров, которая содержит необходимые нам фильтры
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

//Authorization - это имя параметра в заголовке, мы в эту строку получаем значение этого параметра,
// должно быть Bearer, что мы и проверяем в цикле дальше
        final String authHeader = request.getHeader("Authorization");

        final String jwtToken;
        final String username;

//     Ошибки из фильтров не обрабатываются привычным способом, те ошибки которые мы не можем обработать стандартно -
//     через контроллер, мы обрабатываем вот таким образом, при каждом запросе будет делаться проверка на ошибки,
//     и в случае обнаружения через response будет передаваться сообщение об ошибке
        try {

// после Bearer обязательно должен быть пробел
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

//  По цепочке фильтров передаем далее запрос и ответ, для дальнейшей их обработки
                filterChain.doFilter(request, response);

//   Я так понял, что если цикл выполнился, и в header нет токена, то мы передаем по цепочке фильтров
//   запрос с ответом и выходим из метода, потому что дальнейшие действия бессмысленны
                return;
            }
//       Получаем токен jwt с позиции 7, потому что Bearer + пробел это 7 символов
            jwtToken = authHeader.substring(7);

//      достаем имя пользователя из токена jwt, для этого создаем класс JwtService
            username = jwtService.extractUserName(jwtToken);

//      Проверяем равен ли логин null и проверяем прошел ли пользователь аутентификацию,
//      если не аутентифицирован, то null, если пользователь аутентифицирован,
//      то нам не нужно выполнять проверки для токена,
//      а если у него есть токен с именем пользователя, но он не аутентифицирован, то токен нужно проверить,
//      что мы сейчас и сделаем
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

//         Получаем пользователя из БД
                MyUserDetails userDetails = userDetailsService.loadUserByUsername(username);

//      Проверяем валидный ли токен, т.е. соответствуют ли имена пользователя и не просрочен ли он
                if (jwtService.isTokenValid(jwtToken, userDetails)) {

//                 Пароль null потому что токен прошел валидацию
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

//      Этот код передает в токен аутентификации(не путать с jwt) дополнительную информацию такую как ip адрес,
//      данные браузера и др.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

//        Вот эта строка аутентифицирует пользователя
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }

            }
//  По цепочке фильтров передаем далее запрос и ответ, для дальнейшей их обработки
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handler.handleJWTException(response, HttpServletResponse.SC_BAD_REQUEST, e);
        }
    }
}

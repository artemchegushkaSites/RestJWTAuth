package ru.artemchegushka.restjwtauth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Component
public class FilterExceptHandler {

    public void handleJWTException(@NonNull HttpServletResponse response, int httpServletResponse, Exception e) throws IOException {
        HashMap<String, String> map = getMessageMap(e.getMessage());
        extracted(response, httpServletResponse, map);
    }


    public void handleAuthException(@NonNull HttpServletResponse response, int httpServletResponse, String message) throws IOException {
        HashMap<String, String> map = getMessageMap(message);
        extracted(response, httpServletResponse, map);
    }


    private void extracted(HttpServletResponse response, int httpServletResponse, HashMap<String, String> map) throws IOException {
        response.setStatus(httpServletResponse);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

//      ObjectMapper - делает из сообщения json
        ObjectMapper objectMapper = new ObjectMapper();

        String resBody = objectMapper.writeValueAsString(map);

//        Получаем из ответа printWriter и в него пишем то что нужно передать
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }

    private HashMap<String, String> getMessageMap(String message) {
        HashMap<String, String> map = new HashMap<>();
        map.put("message:", message);
        return map;
    }


}

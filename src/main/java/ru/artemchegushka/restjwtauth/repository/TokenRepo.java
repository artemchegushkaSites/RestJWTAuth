package ru.artemchegushka.restjwtauth.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
//Это конечно дичь так делать, но для примера подойдет, лучше конечно создать класс токен,
// в котором будет булька которую я смогу выставлять сам и указывать стоит ли токен использовать или нет,
// а хранить все в бд или на телефоне клиента
@Repository
@Component
public class TokenRepo {

    public void saveToken(String user, String token) {
        try(FileWriter writer = new FileWriter("tokenStore.txt",true)) {
            writer.write(user + ":" + token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken(String user, String token) {

        try {
            List<String> list = Files.readAllLines(Path.of("tokenStore.txt"));
            return list.get(list.indexOf(token)).substring(user.length() + 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteToken(String user) {

        try {
            Path path = Path.of("tokenStore.txt");

            List<String> list = new ArrayList<>(Files.readAllLines(path));
            list.removeIf(s -> s.startsWith(user));

            Files.write(path, list);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTokenByToken(String token) {

        try {
            Path path = Path.of("tokenStore.txt");

            List<String> list = new ArrayList<>(Files.readAllLines(path));
            list.removeIf(s -> s.endsWith(token));

            Files.write(path, list);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

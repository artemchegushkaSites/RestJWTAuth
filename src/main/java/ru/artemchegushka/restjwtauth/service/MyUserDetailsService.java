package ru.artemchegushka.restjwtauth.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.artemchegushka.restjwtauth.entity.MyUser;
import ru.artemchegushka.restjwtauth.exception.UserException;
import ru.artemchegushka.restjwtauth.repository.UserRepo;

import java.util.Optional;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public MyUserDetails loadUserByUsername(String username) {
        Optional<MyUser> optionalMyUser = userRepo.findByUsername(username);

        return new MyUserDetails(optionalMyUser.orElseThrow(()
                -> new UserException("Пользователя с таким логином не существует!")));
    }

    @Transactional
    public void register(MyUser myUser){
        userRepo.save(myUser);
    }

    public Optional<MyUser> findByUserName(String username){
        return userRepo.findByUsername(username);
    }
}

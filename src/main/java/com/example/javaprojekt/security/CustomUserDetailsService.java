package com.example.javaprojekt.security;

import com.example.javaprojekt.entity.Uzytkownik;
import com.example.javaprojekt.repository.UzytkownikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; // Spring Security User
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UzytkownikRepository uzytkownikRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Uzytkownik uzytkownik = uzytkownikRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono uzytkownika o loginie: " + login));

        return new User(
                uzytkownik.getLogin(),
                uzytkownik.getHaslo(),
                uzytkownik.isCzyAktywny(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                mapRolesToAuthorities(uzytkownik.getRole())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<com.example.javaprojekt.entity.Rola> role) {
        return role.stream()
                .map(r -> new SimpleGrantedAuthority(r.getNazwa()))
                .collect(Collectors.toList());
    }
}
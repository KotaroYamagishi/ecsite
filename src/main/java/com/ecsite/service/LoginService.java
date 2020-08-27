package com.ecsite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import com.ecsite.domain.User;
import com.ecsite.repository.UserRepository;
import com.ecsite.security.LoginUserDetails;

@Service
@Transactional
public class LoginService implements UserDetailsService {

	@Autowired
	private UserRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		if (email == null || "".equals(email)) {
			throw new UsernameNotFoundException("mailAddress is empty");
		}
		User user = usersRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found for email: " + email);
		}
		return new LoginUserDetails(user, getAuthorities(user));
	}

	private Collection<GrantedAuthority> getAuthorities(User user) {
		return AuthorityUtils.createAuthorityList("ROLE_USER");
	}

	// public User findByMailAddressAndPassward(String email, String password) {
	// 	User user = usersRepository.findByMailAddressAndPassward(email, password);
	// 	return user;
	// }

}

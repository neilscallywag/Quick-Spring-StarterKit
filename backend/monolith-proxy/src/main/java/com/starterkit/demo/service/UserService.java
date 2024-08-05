/* (C)2024 */
package com.starterkit.demo.service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.starterkit.demo.config.SecurityConfig;
import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.event.TransactionEventListener.TransactionEvent;
import com.starterkit.demo.event.TransactionEventListener.TransactionType;
import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.exception.InvalidRequestException;
import com.starterkit.demo.exception.ResourceNotFoundException;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.processor.GenericProcessor;
import com.starterkit.demo.processor.UserProcessor;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.util.CookieUtils;
import com.starterkit.demo.util.JwtUtil;
import com.starterkit.demo.util.UserMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;
	private final UserMapper userMapper;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final CookieUtils cookieUtils;
	private final JwtUtil jwtUtil;

	@Transactional(readOnly = true)
	public Page<User> getAllUsers(
			int page,
			int size,
			String nameFilter,
			String emailFilter,
			String sortField,
			String sortOrder) {
		Pageable pageable =
				PageRequest.of(
						page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
		if (nameFilter != null && emailFilter != null) {
			return userRepository.findByNameContainingAndEmailContaining(
					nameFilter, emailFilter, pageable);
		} else if (nameFilter != null) {
			return userRepository.findByNameContaining(nameFilter, pageable);
		} else if (emailFilter != null) {
			return userRepository.findByEmailContaining(emailFilter, pageable);
		} else {
			return userRepository.findAll(pageable);
		}
	}

	@Transactional(readOnly = true)
	public long getTotalCount(String nameFilter, String emailFilter) {
		if (nameFilter != null && emailFilter != null) {
			return userRepository.countByNameContainingAndEmailContaining(nameFilter, emailFilter);
		} else if (nameFilter != null) {
			return userRepository.countByNameContaining(nameFilter);
		} else if (emailFilter != null) {
			return userRepository.countByEmailContaining(emailFilter);
		} else {
			return userRepository.count();
		}
	}

	@Transactional(readOnly = true)
	public User getUserById(UUID id) {
		return userRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id.toString()));
	}

	@Transactional(readOnly = true)
	public User getUserByUsername(String username) {
		return userRepository
				.findByUsername(username)
				.orElseThrow(
						() ->
								new ResourceNotFoundException(
										"User not found with username: " + username));
	}

	public UserResponseDTO createUser(NewUserRequestDTO userRequestDTO) {
		validateUserRequest(userRequestDTO);

		userRequestDTO.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
		User user = userMapper.toEntity(userRequestDTO);

		Role defaultRole = roleService.findRoleByName(EnumRole.ROLE_USER);
		user.getRoles().add(defaultRole);

		User validatedUser =
				GenericProcessor.of(user)
						.validate(UserProcessor.validateRoles())
						.validate(UserProcessor.validateUsername(userRepository::findByUsername))
						.validate(UserProcessor.validateEmail(userRepository::findByEmail))
						.process();

		User savedUser = userRepository.save(validatedUser);
		publishTxLogEvent(TransactionType.CREATE, savedUser.getId());
		return userMapper.toResponseDTO(savedUser);
	}

	public UserResponseDTO updateUser(UUID id, User userDetails) {
		User user =
				userRepository
						.findById(id)
						.orElseThrow(() -> new ResourceNotFoundException(id.toString()));

		userMapper.updateUserFromDetails(userDetails, user);

		User validatedUser =
				GenericProcessor.of(user).validate(UserProcessor.validateRoles()).process();

		User updatedUser = userRepository.save(validatedUser);
		publishTxLogEvent(TransactionType.UPDATE, updatedUser.getId());
		return userMapper.toResponseDTO(updatedUser);
	}

	public void deleteUser(UUID id) {
		User user =
				userRepository
						.findById(id)
						.orElseThrow(() -> new ResourceNotFoundException(id.toString()));
		userRepository.delete(user);
		publishTxLogEvent(TransactionType.DELETE, id);
	}

	public String login(String username, String password, HttpServletResponse response) {
		User user =
				userRepository
						.findByUsername(username)
						.orElseThrow(() -> new ResourceNotFoundException(username));

		if (passwordEncoder.matches(password, user.getPassword())) {
			String token = createJwtToken(user);
			addJwtCookie(response, token);
			return token;
		} else {
			throw new AuthenticationException("Invalid username or password");
		}
	}

	public void logout(HttpServletResponse response) {
		cookieUtils.clearCookie(response, SecurityConfig.AUTH_TOKEN);
	}

	private void validateUserRequest(NewUserRequestDTO userRequestDTO) {
		if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
			throw new InvalidRequestException("Username is already taken.");
		}
		if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
			throw new InvalidRequestException("Email is already taken.");
		}
	}

	private String createJwtToken(User user) {
		Map<String, Object> claims =
				Map.of(
						"roles",
						user.getRoles().stream()
								.map(role -> role.getName().name())
								.collect(Collectors.toList()));
		return jwtUtil.generateToken(claims, user.getUsername());
	}

	private void addJwtCookie(HttpServletResponse response, String token) {
		cookieUtils.createCookie(
				SecurityConfig.AUTH_TOKEN,
				token,
				jwtUtil.getExpiration().intValue(),
				response);
	}

	private void publishTxLogEvent(TransactionType type, UUID entityId) {
		if (entityId == null) {
			throw new IllegalArgumentException(
					"Entity ID cannot be null when publishing transaction event");
		}
		applicationEventPublisher.publishEvent(
				new TransactionEvent(type, "User", entityId.toString()));
	}
}

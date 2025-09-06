package com.example.jwt.Controller;

import com.example.jwt.Dto.Request.LoginRequest;
import com.example.jwt.Dto.Request.RegisterRequest;
import com.example.jwt.Dto.Response.JwtResponse;
import com.example.jwt.Dto.Response.TokenRefreshRequest;
import com.example.jwt.Enity.RefreshToken;
import com.example.jwt.Enity.Role;
import com.example.jwt.Enity.User;
import com.example.jwt.Repository.RoleRepository;
import com.example.jwt.Repository.UserRepository;
import com.example.jwt.Services.CustomerUserDetails;
import com.example.jwt.Services.JwtService;
import com.example.jwt.Services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository UserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (UserRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        // set default user role
        Role roleUser = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role Not Found"));
        user.getRoles().add(roleUser);
        userRepository.save(user);
        return ResponseEntity.ok().body("User has been registered successfully!");
    }

    // login username + password
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new RuntimeException("User Not Found"));

        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(customerUserDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(customerUserDetails.getId());

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    CustomerUserDetails customerUserDetails = new CustomerUserDetails(user); // rebuild
                    String token = jwtService.generateToken(customerUserDetails);
                    return ResponseEntity.ok(new JwtResponse(token, refreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token Not Found"));
    }

    @PostMapping("/oauth/success")
    public ResponseEntity<?> oauthSuccess(@RequestBody Authentication authentication) {
        // get user from spring security context
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        // create UserDetails from database
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));
        CustomerUserDetails customerUserDetails = new CustomerUserDetails(user);

        // create JWT
        String accessToken = jwtService.generateToken(customerUserDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(customerUserDetails.getId());

        JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken.getToken());
        return ResponseEntity.ok(jwtResponse);
    }
}

package com.example.buensaborback.service;

import com.example.buensaborback.domain.enums.Rol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class Auth0Service {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String domain;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    public String getAuth0Token() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = domain + "oauth/token";

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("audience", domain + "api/v2/");
        body.put("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return (String) response.getBody().get("access_token");
        } else {
            throw new Exception("Error obteniendo el token");
        }
    }

    public Map<String, Object> createAuth0User(String email, String name, String password, Rol rol) throws Exception {

        String token = getAuth0Token();
        RestTemplate restTemplate = new RestTemplate();
        String url = domain + "api/v2/users";

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("name", name);
        body.put("password", password);
        body.put("connection", "Username-Password-Authentication");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();
            String userId = (String) responseBody.get("user_id");

            assignRoles(userId, rol);
            return responseBody;
        } else {
            throw new Exception("Error creando el usuario en Auth0");
        }
    }

    public List<Map<String, String>> obtenerRolesDesdeAuth0() throws Exception {
        String token = getAuth0Token();
        RestTemplate restTemplate = new RestTemplate();
        String url = domain + "api/v2/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Map<String, String>> roles = new ArrayList<>();
            for (Map<String, String> roleMap : response.getBody()) {
                Map<String, String> role = new HashMap<>();
                role.put("id", roleMap.get("id"));
                role.put("name", roleMap.get("name"));
                role.put("description", roleMap.get("description"));
                roles.add(role);
            }
            return roles;
        } else {
            throw new Exception("Error obteniendo roles desde Auth0. Detalles: " + response.getBody());
        }
    }

    public List<String> getRolesOfUser(String userId) throws Exception {
        String token = getAuth0Token();
        RestTemplate restTemplate = new RestTemplate();
        String url = domain + "api/v2/users/" + userId + "/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List<String> roles = new ArrayList<>();
            for (Map<String, String> roleMap : response.getBody()) {
                roles.add(roleMap.get("id"));
            }
            return roles;
        } else {
            throw new Exception("Error obteniendo los roles del usuario desde Auth0. Detalles: " + response.getBody());
        }
    }

    public void removeRolesFromUser(String userId, List<String> roleIds) throws Exception {
        String token = getAuth0Token();
        RestTemplate restTemplate = new RestTemplate();
        String url = domain + "api/v2/users/" + userId + "/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        Map<String, Object> body = new HashMap<>();
        body.put("roles", roleIds);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Error eliminando los roles del usuario en Auth0. Detalles: " + response.getBody());
        }
    }

    public void assignRoles(String userId, Rol rol) throws Exception {
        // Obtener los roles actuales del usuario
        List<String> currentRoles = getRolesOfUser(userId);

        // Eliminar los roles actuales del usuario
        if (!currentRoles.isEmpty()) {
            removeRolesFromUser(userId, currentRoles);
        }

        // Asignar el nuevo rol
        List<Map<String, String>> rolesFromAuth0 = obtenerRolesDesdeAuth0();
        Optional<Map<String, String>> optionalRole = rolesFromAuth0.stream()
                .filter(role -> rol.name().equalsIgnoreCase(role.get("name")))
                .findFirst();

        if (optionalRole.isPresent()) {
            String roleId = optionalRole.get().get("id");
            assignRoleToUser(userId, roleId);
        } else {
            throw new Exception("No se encontró el rol '" + rol.name() + "' en Auth0.");
        }
    }

    private void assignRoleToUser(String userId, String roleId) throws Exception {
        String token = getAuth0Token();
        RestTemplate restTemplate = new RestTemplate();
        String url = domain + "api/v2/users/" + userId + "/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        Map<String, Object> body = new HashMap<>();
        body.put("roles", Collections.singletonList(roleId));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Error asignando rol al usuario en Auth0. Detalles: " + response.getBody());
        }
    }
}

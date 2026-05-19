package com.pcbuilder.ms_usuarios.controller;

import com.pcbuilder.ms_usuarios.entity.Usuario;
import com.pcbuilder.ms_usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService service;

    // endpoint pal login
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String password = credenciales.get("password");
        
        log.info("El weon con correo {} está intentando entrar al sistema", correo);
        
        try {
            Usuario usuarioLogueado = service.login(correo, password);
            log.info("opa! El loco {} entró de pana", usuarioLogueado.getNombre());
            return ResponseEntity.ok(usuarioLogueado);
        } catch (RuntimeException e) {
            log.error("Murio programa: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // CRUD basico pal 3 pelos (grande pelao Abarzua)
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(service.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUno(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario u) {
        return new ResponseEntity<>(service.guardar(u), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario u) {
        Usuario existe = service.buscarPorId(id);
        existe.setNombre(u.getNombre());
        existe.setCorreo(u.getCorreo());
        existe.setPassword(u.getPassword());
        existe.setRol(u.getRol());
        return ResponseEntity.ok(service.guardar(existe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
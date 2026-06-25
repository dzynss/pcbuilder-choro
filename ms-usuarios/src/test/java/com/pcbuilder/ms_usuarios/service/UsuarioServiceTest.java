package com.pcbuilder.ms_usuarios.service;

import com.pcbuilder.ms_usuarios.dto.LoginRequestDTO;
import com.pcbuilder.ms_usuarios.dto.UsuarioRequestDTO;
import com.pcbuilder.ms_usuarios.dto.UsuarioResponseDTO;
import com.pcbuilder.ms_usuarios.entity.Usuario;
import com.pcbuilder.ms_usuarios.exception.CredencialesInvalidasException;
import com.pcbuilder.ms_usuarios.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repo;

    @InjectMocks
    private UsuarioService service;

    @Test
    void buscarPorId_deberiaRetornarUsuarioSinPassword_cuandoExiste() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("El Bryan");
        usuario.setCorreo("bryan@pcbuilder.cl");
        usuario.setPassword("1234");
        usuario.setRol("ADMIN");
        when(repo.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        UsuarioResponseDTO resultado = service.buscarPorId(1L);

        // Then
        assertThat(resultado.nombre()).isEqualTo("El Bryan");
        assertThat(resultado.correo()).isEqualTo("bryan@pcbuilder.cl");
    }

    @Test
    void buscarPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        // Given
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void guardar_deberiaPersistirUsuarioNuevo() {
        // Given
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Kevin", "kevin@pcbuilder.cl", "1234", "USER");
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(5L);
            return u;
        });

        // When
        UsuarioResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.id()).isEqualTo(5L);
        assertThat(resultado.nombre()).isEqualTo("Kevin");
    }

    // --- Regla de negocio: validación de credenciales de login ---
    @Test
    void login_deberiaAutenticar_cuandoCorreoYPasswordSonCorrectos() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNombre("La Jhendelyn");
        usuario.setCorreo("jhen@pcbuilder.cl");
        usuario.setPassword("1234");
        usuario.setRol("USER");
        when(repo.findByCorreoAndPassword("jhen@pcbuilder.cl", "1234")).thenReturn(Optional.of(usuario));

        // When
        UsuarioResponseDTO resultado = service.login(new LoginRequestDTO("jhen@pcbuilder.cl", "1234"));

        // Then
        assertThat(resultado.nombre()).isEqualTo("La Jhendelyn");
    }

    @Test
    void login_deberiaRechazar_cuandoCredencialesSonIncorrectas() {
        // Given
        when(repo.findByCorreoAndPassword("falso@pcbuilder.cl", "wrong")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.login(new LoginRequestDTO("falso@pcbuilder.cl", "wrong")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }
}

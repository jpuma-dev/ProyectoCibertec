package com.cibertec.proyecto;

import com.cibertec.proyecto.entities.Socio;
import com.cibertec.proyecto.entities.Puesto;
import com.cibertec.proyecto.entities.Usuario;
import com.cibertec.proyecto.repositories.SocioRepository;
import com.cibertec.proyecto.repositories.PuestoRepository;
import com.cibertec.proyecto.repositories.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════
 * PRUEBAS DE LA CAPA DE ACCESO A DATOS
 *
 * Cubre el requisito de la rúbrica:
 *   "Implementa pruebas a las operaciones de insertar,
 *    actualizar, eliminar y listar."
 *
 * Se usa @DataJpaTest que configura una BD en memoria (H2)
 * sin levantar el servidor web completo.
 * ══════════════════════════════════════════════════════════
 */
@DataJpaTest
@DisplayName("Pruebas de Capa de Acceso a Datos")
class ProyectoApplicationTests {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private PuestoRepository puestoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ─────────────────────────────────────────────────────
    // SOCIOS
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("1. INSERTAR socio en la base de datos")
    void testInsertarSocio() {
        Socio socio = Socio.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .dni("12345678")
                .telefono("987654321")
                .email("juan@email.com")
                .build();

        Socio guardado = socioRepository.save(socio);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getDni()).isEqualTo("12345678");
        assertThat(guardado.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("2. LISTAR socios de la base de datos")
    void testListarSocios() {
        socioRepository.save(Socio.builder().nombre("Ana").apellido("López").dni("11111111").telefono("911111111").build());
        socioRepository.save(Socio.builder().nombre("Pedro").apellido("García").dni("22222222").telefono("922222222").build());

        var lista = socioRepository.findAll();

        assertThat(lista).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("3. ACTUALIZAR socio en la base de datos")
    void testActualizarSocio() {
        Socio socio = socioRepository.save(
                Socio.builder().nombre("Carlos").apellido("Ruiz").dni("33333333").telefono("933333333").build());

        socio.setNombre("Carlos Alberto");
        socio.setEmail("carlosalberto@email.com");
        Socio actualizado = socioRepository.save(socio);

        assertThat(actualizado.getNombre()).isEqualTo("Carlos Alberto");
        assertThat(actualizado.getEmail()).isEqualTo("carlosalberto@email.com");
    }

    @Test
    @DisplayName("4. ELIMINAR socio de la base de datos")
    void testEliminarSocio() {
        Socio socio = socioRepository.save(
                Socio.builder().nombre("Eliminar").apellido("Test").dni("44444444").telefono("944444444").build());
        Long id = socio.getId();

        socioRepository.deleteById(id);

        Optional<Socio> resultado = socioRepository.findById(id);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("5. Buscar socio por DNI")
    void testBuscarSocioPorDni() {
        socioRepository.save(
                Socio.builder().nombre("María").apellido("Torres").dni("55555555").telefono("955555555").build());

        Optional<Socio> resultado = socioRepository.findByDni("55555555");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("María");
    }

    // ─────────────────────────────────────────────────────
    // PUESTOS
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("6. INSERTAR puesto en la base de datos")
    void testInsertarPuesto() {
        Puesto puesto = Puesto.builder()
                .numero("A01")
                .descripcion("Puesto de verduras")
                .build();

        Puesto guardado = puestoRepository.save(puesto);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNumero()).isEqualTo("A01");
    }

    @Test
    @DisplayName("7. LISTAR puestos de la base de datos")
    void testListarPuestos() {
        puestoRepository.save(Puesto.builder().numero("B01").descripcion("Carnes").build());
        puestoRepository.save(Puesto.builder().numero("B02").descripcion("Lácteos").build());

        var lista = puestoRepository.findAll();

        assertThat(lista).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("8. ACTUALIZAR puesto en la base de datos")
    void testActualizarPuesto() {
        Puesto puesto = puestoRepository.save(
                Puesto.builder().numero("C01").descripcion("Sin descripción").build());

        puesto.setDescripcion("Puesto de frutas tropicales");
        Puesto actualizado = puestoRepository.save(puesto);

        assertThat(actualizado.getDescripcion()).isEqualTo("Puesto de frutas tropicales");
    }

    @Test
    @DisplayName("9. ELIMINAR puesto de la base de datos")
    void testEliminarPuesto() {
        Puesto puesto = puestoRepository.save(
                Puesto.builder().numero("D01").descripcion("Para eliminar").build());
        Long id = puesto.getId();

        puestoRepository.deleteById(id);

        assertThat(puestoRepository.findById(id)).isEmpty();
    }

    // ─────────────────────────────────────────────────────
    // USUARIOS (Spring Security + BCrypt)
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("10. INSERTAR usuario con password cifrado BCrypt")
    void testInsertarUsuarioConBCrypt() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordCifrado = encoder.encode("miPassword123");

        Usuario usuario = Usuario.builder()
                .username("admin")
                .password(passwordCifrado)
                .rol("ROLE_ADMIN")
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        // Verificar que el password está cifrado (no en texto plano)
        assertThat(guardado.getPassword()).isNotEqualTo("miPassword123");
        assertThat(encoder.matches("miPassword123", guardado.getPassword())).isTrue();
    }

    @Test
    @DisplayName("11. BUSCAR usuario por username")
    void testBuscarUsuarioPorUsername() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuarioRepository.save(Usuario.builder()
                .username("testuser")
                .password(encoder.encode("pass123"))
                .rol("ROLE_USER")
                .activo(true)
                .build());

        Optional<Usuario> resultado = usuarioRepository.findByUsername("testuser");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getRol()).isEqualTo("ROLE_USER");
    }
}

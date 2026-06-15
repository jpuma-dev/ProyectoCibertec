package com.cibertec.proyecto.services.impl;

import com.cibertec.proyecto.dtos.SocioDTO;
import com.cibertec.proyecto.dtos.SocioResponseDTO;
import com.cibertec.proyecto.entities.Socio;
import com.cibertec.proyecto.exceptions.BadRequestException;
import com.cibertec.proyecto.exceptions.ConflictException;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.mappers.SocioMapper;
import com.cibertec.proyecto.repositories.SocioRepository;
import com.cibertec.proyecto.services.ISocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocioServiceImpl implements ISocioService {

    private final SocioRepository socioRepository;
    private final SocioMapper socioMapper;

    @Override
    @Transactional
    public SocioResponseDTO crearSocio(SocioDTO dto) {
        SocioDTO normalizado = normalizar(dto);

        if (socioRepository.existsByDni(normalizado.dni())) {
            throw new ConflictException("Ya existe un socio con el DNI: " + normalizado.dni());
        }

        Socio socio = socioMapper.toEntity(normalizado);
        Socio savedSocio = socioRepository.save(socio);
        return socioMapper.toResponseDTO(savedSocio);
    }

    @Override
    @Transactional
    public void eliminarSocioPorDni(String dni) {
        Socio socio = socioRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un socio con el DNI: " + dni));

        if (socio.getPuestos() != null && !socio.getPuestos().isEmpty()) {
            throw new ConflictException("No se puede eliminar un socio con puestos asignados");
        }

        socioRepository.delete(socio);
    }

    @Override
    @Transactional
    public SocioResponseDTO actualizarSocio(String dni, SocioDTO dto) {
        Socio socio = socioRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con DNI: " + dni));
        SocioDTO normalizado = normalizar(dto);

        if (!socio.getDni().equals(normalizado.dni()) && socioRepository.existsByDni(normalizado.dni())) {
            throw new ConflictException("El DNI " + normalizado.dni() + " ya esta en uso por otro socio");
        }

        socio.setNombre(normalizado.nombre());
        socio.setApellido(normalizado.apellido());
        socio.setDni(normalizado.dni());
        socio.setTelefono(normalizado.telefono());
        socio.setEmail(normalizado.email());

        Socio updatedSocio = socioRepository.save(socio);
        return socioMapper.toResponseDTO(updatedSocio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocioResponseDTO> listarSocios() {
        return socioRepository.findAll()
                .stream()
                .map(socioMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SocioResponseDTO> listarSociosPaginado(Pageable pageable) {
        return socioRepository.findAll(pageable)
                .map(socioMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SocioResponseDTO> buscarSocios(String term, Pageable pageable) {
        String texto = term == null ? "" : term.trim();
        return socioRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrDniContainingOrTelefonoContainingOrEmailContainingIgnoreCase(
                        texto, texto, texto, texto, texto, pageable)
                .map(socioMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SocioResponseDTO obtenerPorDni(String dni) {
        return socioRepository.findByDni(dni)
                .map(socioMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con DNI: " + dni));
    }

    private SocioDTO normalizar(SocioDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del socio son obligatorios");
        }

        return new SocioDTO(
                normalizarTextoRequerido(dto.nombre(), "El nombre es obligatorio"),
                normalizarTextoRequerido(dto.apellido(), "El apellido es obligatorio"),
                normalizarTextoRequerido(dto.dni(), "El DNI es obligatorio"),
                normalizarTextoRequerido(dto.telefono(), "El telefono es obligatorio"),
                normalizarTextoOpcional(dto.email())
        );
    }

    private String normalizarTextoRequerido(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BadRequestException(mensaje);
        }
        return valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }
}

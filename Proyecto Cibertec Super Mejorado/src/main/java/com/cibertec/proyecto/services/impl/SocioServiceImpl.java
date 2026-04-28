package com.cibertec.proyecto.services.impl;

import com.cibertec.proyecto.dtos.SocioDTO;
import com.cibertec.proyecto.dtos.SocioResponseDTO;
import com.cibertec.proyecto.entities.Socio;
import com.cibertec.proyecto.exceptions.ConflictException;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.repositories.SocioRepository;
import com.cibertec.proyecto.services.ISocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocioServiceImpl implements ISocioService {

    private final SocioRepository socioRepository;

    @Override
    @Transactional
    public SocioResponseDTO crearSocio(SocioDTO dto) {
        if (socioRepository.existsByDni(dto.getDni())) {
            throw new ConflictException("Ya existe un socio con el DNI: " + dto.getDni());
        }
        Socio socio = Socio.builder()
                .nombre(dto.getNombre().trim())
                .apellido(dto.getApellido().trim())
                .dni(dto.getDni().trim())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .build();
        return toDTO(socioRepository.save(socio));
    }

    @Override
    @Transactional
    public SocioResponseDTO actualizarSocio(String dni, SocioDTO dto) {
        Socio socio = socioRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con DNI: " + dni));
        socio.setNombre(dto.getNombre().trim());
        socio.setApellido(dto.getApellido().trim());
        socio.setDni(dto.getDni().trim());
        socio.setTelefono(dto.getTelefono());
        socio.setEmail(dto.getEmail());
        return toDTO(socioRepository.save(socio));
    }

    @Override
    @Transactional
    public void eliminarSocioPorDni(String dni) {
        if (!socioRepository.existsByDni(dni)) {
            throw new ResourceNotFoundException("Socio no encontrado con DNI: " + dni);
        }
        socioRepository.deleteByDni(dni);
    }

    @Override
    public List<SocioResponseDTO> listarSocios() {
        return socioRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SocioResponseDTO> listarSociosPaginado(Pageable pageable) {
        return socioRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public Page<SocioResponseDTO> buscarSocios(String term, Pageable pageable) {
        return socioRepository
                .findByNombreContainingIgnoreCaseOrDniContaining(term, term, pageable)
                .map(this::toDTO);
    }

    @Override
    public SocioResponseDTO obtenerPorDni(String dni) {
        Socio socio = socioRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con DNI: " + dni));
        return toDTO(socio);
    }

    // ── Mapper privado ─────────────────────────────────────
    private SocioResponseDTO toDTO(Socio s) {
        return SocioResponseDTO.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .apellido(s.getApellido())
                .dni(s.getDni())
                .telefono(s.getTelefono())
                .email(s.getEmail())
                .build();
    }
}

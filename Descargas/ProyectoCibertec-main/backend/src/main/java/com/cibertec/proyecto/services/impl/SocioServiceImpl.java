package com.cibertec.proyecto.services.impl;

import com.cibertec.proyecto.dtos.SocioDTO;
import com.cibertec.proyecto.dtos.SocioResponseDTO;
import com.cibertec.proyecto.entities.Socio;
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
        if (socioRepository.existsByDni(dto.dni())) {
            throw new ConflictException("Ya existe un socio con el DNI: " + dto.dni());
        }

        Socio socio = socioMapper.toEntity(dto);
        Socio savedSocio = socioRepository.save(socio);
        return socioMapper.toResponseDTO(savedSocio);
    }

    @Override
    @Transactional
    public void eliminarSocioPorDni(String dni) {
        Socio socio = socioRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un socio con el DNI: " + dni));
        socioRepository.delete(socio);
    }

    @Override
    @Transactional
    public SocioResponseDTO actualizarSocio(String dni, SocioDTO dto) {
        Socio socio = socioRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con DNI: " + dni));

        if (!socio.getDni().equals(dto.dni()) && socioRepository.existsByDni(dto.dni())) {
            throw new ConflictException("El DNI " + dto.dni() + " ya esta en uso por otro socio");
        }

        socio.setNombre(dto.nombre());
        socio.setApellido(dto.apellido());
        socio.setDni(dto.dni());
        socio.setTelefono(dto.telefono());
        socio.setEmail(dto.email());

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
        return socioRepository.findByNombreContainingIgnoreCaseOrDniContaining(term, term, pageable)
                .map(socioMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SocioResponseDTO obtenerPorDni(String dni) {
        return socioRepository.findByDni(dni)
                .map(socioMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con DNI: " + dni));
    }
}

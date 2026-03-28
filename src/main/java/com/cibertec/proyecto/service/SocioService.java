package com.cibertec.proyecto.service;

import com.cibertec.proyecto.dto.SocioDTO;
import com.cibertec.proyecto.dto.SocioResponseDTO;
import com.cibertec.proyecto.entity.Socio;
import com.cibertec.proyecto.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SocioService {

    private final SocioRepository socioRepository;

    public Socio crearSocio(SocioDTO dto) {

        boolean existe = socioRepository.existsByDni(dto.getDni());
        if (existe) {
            throw new RuntimeException("Ya existe un socio con ese DNI");
        }

        Socio socio = new Socio();
        socio.setNombre(dto.getNombre());
        socio.setDni(dto.getDni());
        socio.setTelefono(dto.getTelefono());

        return socioRepository.save(socio);
    }

    public void eliminarSocioPorDni(String dni) {
        boolean existe = socioRepository.existsByDni(dni);
        if (!existe) {
            throw new RuntimeException("No existe un socio con ese DNI");
        }

        socioRepository.deleteByDni(dni);
    }

    public List<SocioResponseDTO> listarSocios() {
        return socioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private SocioResponseDTO toResponseDTO(Socio socio) {
        SocioResponseDTO dto = new SocioResponseDTO();
        dto.setId(socio.getId());
        dto.setNombre(socio.getNombre());
        dto.setDni(socio.getDni());
        dto.setTelefono(socio.getTelefono());
        return dto;
    }
}

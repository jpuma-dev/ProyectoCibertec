package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.PuestoDTO;
import com.cibertec.proyecto.dtos.PuestoResponseDTO;
import com.cibertec.proyecto.entities.Puesto;
import com.cibertec.proyecto.entities.Socio;
import com.cibertec.proyecto.exceptions.ConflictException;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.repositories.PuestoRepository;
import com.cibertec.proyecto.repositories.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuestoService {

    private final PuestoRepository puestoRepository;
    private final SocioRepository socioRepository;

    @Transactional
    public PuestoResponseDTO crearPuesto(PuestoDTO dto) {

        boolean existe = puestoRepository.existsByNumero(dto.getNumero());
        if (existe) {
            throw new ConflictException("El número de puesto ya existe: " + dto.getNumero());
        }

        Socio socio = null;

        if (dto.getSocioId() != null) {
            socio = socioRepository.findById(dto.getSocioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con ID: " + dto.getSocioId()));
        }

        Puesto puesto = new Puesto();
        puesto.setNumero(dto.getNumero());
        puesto.setDescripcion(dto.getDescripcion());
        puesto.setSocio(socio);

        Puesto savedPuesto = puestoRepository.save(puesto);
        return toResponseDTO(savedPuesto);
    }

    @Transactional
    public PuestoResponseDTO actualizarPuesto(Long id, PuestoDTO dto) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puesto no encontrado con ID: " + id));

        puesto.setNumero(dto.getNumero());
        puesto.setDescripcion(dto.getDescripcion());

        if (dto.getSocioId() != null) {
            Socio socio = socioRepository.findById(dto.getSocioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con ID: " + dto.getSocioId()));
            puesto.setSocio(socio);
        } else {
            puesto.setSocio(null);
        }

        Puesto updatedPuesto = puestoRepository.save(puesto);
        return toResponseDTO(updatedPuesto);
    }

    @Transactional
    public void eliminarPuesto(Long id) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puesto no encontrado con ID: " + id));
        
        if (puesto.getDeudas() != null && !puesto.getDeudas().isEmpty()) {
            throw new ConflictException("No se puede eliminar un puesto que tiene deudas registradas");
        }
        
        puestoRepository.delete(puesto);
    }

    public List<PuestoResponseDTO> listarTodos() {
        return puestoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private PuestoResponseDTO toResponseDTO(Puesto puesto) {
        PuestoResponseDTO dto = new PuestoResponseDTO();
        dto.setId(puesto.getId());
        dto.setNumero(puesto.getNumero());
        dto.setDescripcion(puesto.getDescripcion());
        if (puesto.getSocio() != null) {
            dto.setSocioNombreCompleto(puesto.getSocio().getNombre() + " " + puesto.getSocio().getApellido());
        }
        return dto;
    }
}

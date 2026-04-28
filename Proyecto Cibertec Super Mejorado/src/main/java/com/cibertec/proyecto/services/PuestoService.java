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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuestoService {

    private final PuestoRepository puestoRepository;
    private final SocioRepository socioRepository;

    @Transactional
    public PuestoResponseDTO crearPuesto(PuestoDTO dto) {
        if (puestoRepository.existsByNumero(dto.getNumero())) {
            throw new ConflictException("El número de puesto ya existe: " + dto.getNumero());
        }
        Puesto puesto = new Puesto();
        puesto.setNumero(dto.getNumero().trim().toUpperCase());
        puesto.setDescripcion(dto.getDescripcion());
        asignarSocio(puesto, dto.getSocioId());
        return toDTO(puestoRepository.save(puesto));
    }

    @Transactional
    public PuestoResponseDTO actualizarPuesto(Long id, PuestoDTO dto) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puesto no encontrado con ID: " + id));
        puesto.setNumero(dto.getNumero().trim().toUpperCase());
        puesto.setDescripcion(dto.getDescripcion());
        asignarSocio(puesto, dto.getSocioId());
        return toDTO(puestoRepository.save(puesto));
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
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────
    private void asignarSocio(Puesto puesto, Long socioId) {
        if (socioId != null) {
            Socio socio = socioRepository.findById(socioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con ID: " + socioId));
            puesto.setSocio(socio);
        } else {
            puesto.setSocio(null);
        }
    }

    private PuestoResponseDTO toDTO(Puesto p) {
        PuestoResponseDTO dto = new PuestoResponseDTO();
        dto.setId(p.getId());
        dto.setNumero(p.getNumero());
        dto.setDescripcion(p.getDescripcion());
        if (p.getSocio() != null) {
            dto.setSocioId(p.getSocio().getId());
            dto.setSocioNombreCompleto(p.getSocio().getNombre() + " " + p.getSocio().getApellido());
        }
        return dto;
    }
}

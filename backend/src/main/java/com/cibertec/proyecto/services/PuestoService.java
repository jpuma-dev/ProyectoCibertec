package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.PuestoDTO;
import com.cibertec.proyecto.dtos.PuestoResponseDTO;
import com.cibertec.proyecto.entities.Puesto;
import com.cibertec.proyecto.entities.Socio;
import com.cibertec.proyecto.exceptions.BadRequestException;
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
        String numero = normalizarNumero(dto);

        if (puestoRepository.existsByNumero(numero)) {
            throw new ConflictException("El numero de puesto ya existe: " + numero);
        }

        Puesto puesto = new Puesto();
        puesto.setNumero(numero);
        puesto.setDescripcion(normalizarTextoOpcional(dto.getDescripcion()));
        puesto.setSocio(buscarSocioOpcional(dto.getSocioId()));

        return toResponseDTO(puestoRepository.save(puesto));
    }

    @Transactional
    public PuestoResponseDTO actualizarPuesto(Long id, PuestoDTO dto) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puesto no encontrado con ID: " + id));
        String numero = normalizarNumero(dto);

        if (puestoRepository.existsByNumeroAndIdNot(numero, id)) {
            throw new ConflictException("El numero de puesto ya existe: " + numero);
        }

        puesto.setNumero(numero);
        puesto.setDescripcion(normalizarTextoOpcional(dto.getDescripcion()));
        puesto.setSocio(buscarSocioOpcional(dto.getSocioId()));

        return toResponseDTO(puestoRepository.save(puesto));
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

    @Transactional(readOnly = true)
    public List<PuestoResponseDTO> listarTodos() {
        return puestoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PuestoResponseDTO buscarPorId(Long id) {
        return puestoRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Puesto no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<PuestoResponseDTO> listarDisponibles() {
        return puestoRepository.findAll().stream()
                .filter(puesto -> puesto.getSocio() == null)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PuestoResponseDTO> listarPorSocio(Long socioId) {
        if (!socioRepository.existsById(socioId)) {
            throw new ResourceNotFoundException("Socio no encontrado con ID: " + socioId);
        }

        return puestoRepository.findBySocioId(socioId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private Socio buscarSocioOpcional(Long socioId) {
        if (socioId == null) {
            return null;
        }

        return socioRepository.findById(socioId)
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado con ID: " + socioId));
    }

    private PuestoResponseDTO toResponseDTO(Puesto puesto) {
        PuestoResponseDTO dto = new PuestoResponseDTO();
        dto.setId(puesto.getId());
        dto.setNumero(puesto.getNumero());
        dto.setDescripcion(puesto.getDescripcion());
        if (puesto.getSocio() != null) {
            dto.setSocioId(puesto.getSocio().getId());
            dto.setSocioNombreCompleto(puesto.getSocio().getNombre() + " " + puesto.getSocio().getApellido());
        }
        return dto;
    }

    private String normalizarNumero(PuestoDTO dto) {
        if (dto == null || dto.getNumero() == null || dto.getNumero().trim().isEmpty()) {
            throw new BadRequestException("El numero de puesto es obligatorio");
        }
        return dto.getNumero().trim().toUpperCase();
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim().replaceAll("\\s+", " ");
    }
}

package com.vegstore.service;

import com.vegstore.entity.Supplier;
import com.vegstore.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        log.info("Creating supplier: {}", supplier.getName());

        // Ensure ID is null for new entity
        supplier.setId(null);

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Supplier saved with ID: {}", savedSupplier.getId());

        return savedSupplier;
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplier) {
        log.info("Updating supplier with ID: {}", id);

        Supplier existing = getSupplierById(id);
        existing.setName(supplier.getName());
        existing.setContactPerson(supplier.getContactPerson());
        existing.setPhone(supplier.getPhone());
        existing.setEmail(supplier.getEmail());
        existing.setAddress(supplier.getAddress());

        return supplierRepository.save(existing);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with id: {}", id);

        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }

        supplierRepository.deleteById(id);
    }
}

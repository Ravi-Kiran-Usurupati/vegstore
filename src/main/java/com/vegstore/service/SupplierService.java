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
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplier) {
        Supplier existing = getSupplierById(id);
        existing.setName(supplier.getName());
        existing.setContactPerson(supplier.getContactPerson());
        existing.setPhone(supplier.getPhone());
        existing.setEmail(supplier.getEmail());
        existing.setAddress(supplier.getAddress());

        log.info("Updating supplier: {}", supplier.getName());
        return supplierRepository.save(existing);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with id: {}", id);
        supplierRepository.deleteById(id);
    }
}
